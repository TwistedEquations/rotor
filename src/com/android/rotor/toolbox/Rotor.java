

package com.android.rotor.toolbox;

import android.os.*;
import android.os.Process;

import com.android.rotor.Action;
import com.android.rotor.Player;
import com.android.rotor.Playlist;
import com.android.rotor.RotorAsync;
import com.android.rotor.RotorTask;
import com.android.rotor.StateListener;

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
    public static final int STATE_PAUSED = 3;
    public static final int STATE_BUFFERING = 4;
    public static final int STATE_WAITING = 5;
    public static final int STATE_ERROR = 6;

    public static final int ACTION_PLAY = 7;
    public static final int ACTION_PAUSE = 8;
    public static final int ACTION_RESET = 9;
    public static final int ACTION_NEXT = 10;
    public static final int ACTION_PREV = 12;
    public static final int ACTION_SEEK = 13;

    private Player player;
    private Set<StateListener> listeners = Collections.synchronizedSet(new HashSet<StateListener>());
    private RotorAsync rotorAsync = new RotorAsync();

    private final Object lock = new Object();
    private boolean run;
    private boolean paused;
    private AtomicInteger currentState = new AtomicInteger(STATE_WAITING);
    private Handler handler = new Handler(Looper.getMainLooper());

    public Rotor(Player player) {
        this.player = player;
        this.player.addListener(playerListener);
        rotorAsync.start();
    }

    public void addListener(StateListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(StateListener listener) {
        this.listeners.remove(listener);
    }

    public void asyncAction(Action action, ActionFinishedListener actionFinishedListener) {
        RotorTask rotorTask = new RotorTask(player, action, new RotorTask.TaskListener(actionFinishedListener) {
            @Override
            public void onTaskFinished(Action action, ActionFinishedListener finishedListener) {
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

    public void asyncAction(Action action) {
        RotorTask rotorTask = new RotorTask(player, action, new RotorTask.TaskListener(null) {
            @Override
            public void onTaskFinished(Action action, ActionFinishedListener finishedListener) {
                if(finishedListener != null) {
                    finishedListener.onActionFinished(action);
                }
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
        listeners.clear();
        run = false;
        rotorAsync.stop();
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

    public void updateState() {
        synchronized (lock) {
            if(paused) {
                lock.notifyAll();
            }
        }
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
            //wait until an action has been preformed or 1 second passes
            synchronized (lock) {
                try {
                    paused = true;
                    lock.wait(1000);
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
                for(StateListener listener : listeners) {
                    listener.onStateChange(state);
                }
            }
        });
    }

    private StateListener playerListener = new StateListener() {
        @Override
        public void onStateChange(int state) {
            updateState();
        }
    };

    public interface ActionFinishedListener {
        public void onActionFinished(Action action);
    }

}
