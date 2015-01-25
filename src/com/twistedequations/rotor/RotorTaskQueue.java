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

package com.twistedequations.rotor;

import java.util.concurrent.LinkedBlockingQueue;

public class RotorTaskQueue implements Runnable {

    private LinkedBlockingQueue<RotorTask> taskQueue = new LinkedBlockingQueue<>();
    private boolean run;

    public void add(RotorTask rotorTask) {
        taskQueue.add(rotorTask);
    }

    public void start() {
        run = true;
        Thread thread = new Thread(this, "Rotor tasks queue");
        thread.start();
    }

    public void stop() {
        run = false;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        while (run) {
            try {
                RotorTask rotorTask = taskQueue.take();
                rotorTask.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
