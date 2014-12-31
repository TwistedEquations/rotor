/**
 * Copyright (C) 31/12/2014 Patrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.rotor;

import android.os.Handler;
import android.os.Looper;

import com.android.rotor.toolbox.Rotor;


public class RotorTask implements Runnable {

    private Player player;
    private int action;
    private TaskListener actionFinishedListener;
    private Handler handler = new Handler(Looper.getMainLooper());

    public RotorTask(Player player, int action, TaskListener actionFinishedListener) {
        this.player = player;
        this.action = action;
        this.actionFinishedListener = actionFinishedListener;
    }

    @Override
    public void run() {
        player.preformAction(action);
        handler.post(new Runnable() {
            @Override
            public void run() {
                actionFinishedListener.onTaskFinished(action, actionFinishedListener.getActionFinishedListener());
            }
        });
    }

    public static abstract class TaskListener {

        private Rotor.ActionFinishedListener actionninishedListener;

        protected TaskListener(Rotor.ActionFinishedListener actionninishedListener) {
            this.actionninishedListener = actionninishedListener;
        }

        private Rotor.ActionFinishedListener getActionFinishedListener() {
            return actionninishedListener;
        }

        public abstract void onTaskFinished(int action, Rotor.ActionFinishedListener actionFinishedListener1);
    }
}
