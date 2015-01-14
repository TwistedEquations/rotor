

package com.twistedequations.rotor.toolbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.os.Process;

import com.twistedequations.rotor.Action;
import com.twistedequations.rotor.MediaControl;
import com.twistedequations.rotor.MediaControlListener;
import com.twistedequations.rotor.Player;
import com.twistedequations.rotor.RotorAsync;
import com.twistedequations.rotor.RotorTask;
import com.twistedequations.rotor.StateListener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Overall class for controlling the player an listening to events
 *
 */
public class Rotor implements Runnable {

    public static final String INTENT_ACTION = "com.twistedequations.rotor";
    public static final String INTENT_KEY = "com.twistedequations.rotor.ACTION";

    public static final int STATE_PLAYING = 2;
    public static final int STATE_PAUSED = 3;
    public static final int STATE_BUFFERING = 4;
    public static final int STATE_WAITING = 5;
    public static final int STATE_ERROR = 6;

    public static final int ACTION_PLAY = 7;
    public static final int ACTION_PAUSE = 8;
    public static final int ACTION_STOP = 9;
    public static final int ACTION_NEXT = 10;
    public static final int ACTION_PREV = 12;
    public static final int ACTION_SEEK = 13;

    private Context context;
    private Player player;
    private Set<StateListener> listeners = Collections.synchronizedSet(new HashSet<StateListener>());
    private RotorAsync rotorAsync = new RotorAsync();

    private final Object lock = new Object();
    private boolean run;
    private boolean paused;
    private AtomicInteger currentState = new AtomicInteger(STATE_WAITING);
    private Handler handler = new Handler(Looper.getMainLooper());
    private Set<MediaControl> mediaControls = new HashSet<>();

    public Rotor(Context context, Player player) {
        this.context = context.getApplicationContext();
        this.player = player;
        this.player.addListener(playerListener);
    }

    public void addListener(StateListener listener) {
        synchronized (listeners) {
            this.listeners.add(listener);
        }
    }

    public void removeListener(StateListener listener) {
        synchronized (listeners) {
            this.listeners.remove(listener);
        }
    }

    public void addControl(MediaControl control) {
        synchronized (mediaControls) {
            this.mediaControls.add(control);
        }
    }

    public void removeControl(MediaControl control) {
        synchronized (mediaControls) {
            this.mediaControls.remove(control);
        }
    }

    public void asyncAction(Action action, ActionFinishedListener actionFinishedListener) {
        RotorTask rotorTask = new RotorTask(player, action, new RotorTask.TaskListener(actionFinishedListener) {
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

    public void asyncAction(Action action) {
        asyncAction(action, null);
    }

    public void start() {
        run = true;
        Thread thread = new Thread(this, "Rotor state thread");
        thread.start();
        rotorAsync.start();

        IntentFilter intentFilter = new IntentFilter(Rotor.INTENT_ACTION);
        context.registerReceiver(broadcastReceiver, intentFilter);

        synchronized (mediaControls) {
            for(MediaControl mediaControl : mediaControls) {
                mediaControl.create();
            }
        }
    }

    public void destroy() {
        listeners.clear();
        run = false;
        rotorAsync.stop();
        context.unregisterReceiver(broadcastReceiver);
        synchronized (lock) {
            if(paused) {
                lock.notifyAll();
            }
            paused = false;
        }
        currentState.set(STATE_WAITING);
        synchronized (listeners) {
            this.listeners.clear();
        }
        synchronized (mediaControls) {
            for(MediaControl mediaControl : mediaControls) {
                mediaControl.destroy();
            }
            mediaControls.clear();
        }
    }

    public int getState() {
        return currentState.get();
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
                synchronized (listeners) {
                    for(StateListener listener : listeners) {
                        listener.onStateChange(state);
                    }
                }

                synchronized (mediaControls) {
                    for(MediaControl mediaControl : mediaControls) {
                        mediaControl.updateState(state);
                    }
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Action action = intent.getParcelableExtra(Rotor.INTENT_KEY);
            asyncAction(action);
        }
    };

    public interface ActionFinishedListener {
        public void onActionFinished(Action action);
    }
}
