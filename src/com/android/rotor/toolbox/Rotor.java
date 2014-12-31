

package com.android.rotor.toolbox;

import android.os.*;
import android.os.Process;

import com.android.rotor.Player;
import com.android.rotor.Playlist;
import com.android.rotor.RotorAsync;
import com.android.rotor.RotorTask;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Overall class for contolling the player an listening to events
 *
 */
public class Rotor implements Runnable {

    public static final int STATE_PLAYING = 2;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_BUFFERING = 8;
    public static final int STATE_WAITING = 16;
    public static final int STATE_ERROR = 32;

    public static final int ACTION_PLAY = 64;
    public static final int ACTION_PAUSE = 128;
    public static final int ACTION_RESET = 256;
    public static final int ACTION_NEXT = 1024;
    public static final int ACTION_PREV = 2048;

    private Player player;
    private Playlist playlist;
    private Set<Listener> listeners = Collections.synchronizedSet(new HashSet<Listener>());
    private RotorAsync rotorAsync = new RotorAsync();

    private final Object lock = new Object();
    private boolean run;
    private boolean paused;
    private AtomicInteger currentState = new AtomicInteger(STATE_WAITING);
    private Handler handler = new Handler(Looper.getMainLooper());

    public Rotor(Player player, Playlist playlist) {
        this.player = player;
        this.playlist = playlist;
        rotorAsync.start();
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void asyncAction(int action, ActionFinishedListener actionFinishedListener) {
        RotorTask rotorTask = new RotorTask(player, action, new RotorTask.TaskListener(actionFinishedListener) {
            @Override
            public void onTaskFinished(int action, ActionFinishedListener finishedListener) {
                finishedListener.onActionFinished(action);
                synchronized (lock) {
                    if(paused) {
                        lock.notifyAll();
                    }
                }
            }
        });
        rotorAsync.add(rotorTask);
    }

    public void stop() {
        run = false;
        synchronized (lock) {
            if(paused) {
                lock.notifyAll();
            }
            paused = false;
        }
        currentState.set(STATE_WAITING);
    }

    public int getState() {
        return currentState.get();
    }

    public void start() {
        run = true;
        Thread thread = new Thread(this, "Rotor state thread");
        thread.start();
    }

    @Override
    public void run() {
        //Start of the thread for the state
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while(run) {
            int state = player.getState();
            if(currentState.get() != state) {
                onStateChange(state);
            }
            currentState.set(state);
            //wait until an action has been preformed or 3 seconds pass
            synchronized (lock) {
                try {
                    paused = true;
                    lock.wait(3000);
                    paused = false;
                } catch (InterruptedException e) {
                    paused = false;
                    break;
                }
            }
        }
    }

    private void onStateChange(final int state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(Listener listener : listeners) {
                    listener.onStateChange(state);
                }
            }
        });
    }

    public interface Listener {
        public void onStateChange(int state);
    }

    public interface ActionFinishedListener {
        public void onActionFinished(int action);
    }

}
