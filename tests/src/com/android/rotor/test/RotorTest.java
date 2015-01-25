/**
 * Copyright (C) 25/01/2015 Patrick
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

package com.android.rotor.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.rotor.test.mock.StubPlayer;
import com.twistedequations.rotor.Action;
import com.twistedequations.rotor.Rotor;
import com.twistedequations.rotor.StateListener;

public class RotorTest extends AndroidTestCase {

    int currentState;
    int count;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        currentState = 0;
        count = 0;
    }

    @LargeTest
    public void testRotorState() throws InterruptedException {
        StubPlayer player = new StubPlayer() {
            @Override
            public void preformAction(Action action) {

            }
        };

        Rotor rotor = new Rotor(getContext(), player);
        rotor.addListener(new StateListener() {
            @Override
            public void onStateChange(int state) {
                count++;
            }
        });
        rotor.start();

        currentState = Rotor.STATE_PLAYING;
        player.pushSate(currentState);
        Thread.sleep(1100);
        assertEquals(count, 1);

        currentState = Rotor.STATE_BUFFERING;
        player.pushSate(currentState);
        Thread.sleep(1100);
        assertEquals(count, 2);

        currentState = Rotor.STATE_BUFFERING;
        player.pushSate(currentState);
        Thread.sleep(1100);
        assertEquals(count, 2);

        currentState = Rotor.STATE_PAUSED;
        player.pushSate(currentState);
        Thread.sleep(1100);
        assertEquals(count, 3);
        rotor.stop();
    }

    @SmallTest
    public void testRotorActions() throws InterruptedException {

        StubPlayer player = new StubPlayer() {
            @Override
            public void preformAction(Action action) {
                count++;
            }
        };

        Rotor rotor = new Rotor(getContext(), player);
        rotor.start();

        rotor.asyncAction(Action.NEXT);
        Thread.sleep(1100);
        assertEquals(count, 1);

        rotor.asyncAction(Action.PREV);
        Thread.sleep(1100);
        assertEquals(count, 2);

        rotor.asyncAction(Action.PAUSE);
        Thread.sleep(1100);
        assertEquals(count, 3);

        rotor.asyncAction(Action.PLAY);
        Thread.sleep(1100);
        assertEquals(count, 4);
        rotor.stop();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        currentState = 0;
        count = 0;
    }
}
