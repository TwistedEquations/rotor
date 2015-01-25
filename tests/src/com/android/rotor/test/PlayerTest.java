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

import android.test.suitebuilder.annotation.SmallTest;

import com.android.rotor.test.mock.StubPlayer;
import com.twistedequations.rotor.Action;
import com.twistedequations.rotor.Rotor;
import com.twistedequations.rotor.StateListener;

import junit.framework.TestCase;

public class PlayerTest extends TestCase {

    int currentState;
    int count;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        currentState = 0;
        count = 0;
    }

    @SmallTest
    public void testPlayerState() {

        StateListener stateListener = new StateListener() {
            @Override
            public void onStateChange(int state) {
                count ++;
                assertEquals(currentState, state);
            }
        };

        StubPlayer player = new StubPlayer() {
            @Override
            public void preformAction(Action action) {

            }
        };
        player.addListener(stateListener);

        currentState = Rotor.STATE_PLAYING;
        player.pushSate(currentState);
        assertEquals(count, 1);

        currentState = Rotor.STATE_PLAYING;
        player.pushSate(currentState);
        assertEquals(count, 2);

        currentState = Rotor.STATE_PAUSED;
        player.pushSate(currentState);
        assertEquals(count, 3);

        currentState = Rotor.STATE_BUFFERING;
        player.pushSate(currentState);
        assertEquals(count, 4);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        currentState = 0;
        count = 0;
    }
}
