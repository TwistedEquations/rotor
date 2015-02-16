/**
 * Copyright (C) 16/02/2015 Patrick
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

package com.twistedequations.rotor.toolbox;

import com.twistedequations.rotor.Action;
import com.twistedequations.rotor.Player;
import com.twistedequations.rotor.Position;
import com.twistedequations.rotor.Rotor;
import com.twistedequations.rotor.StateListener;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  This abstract class manages the set of listeners for the player state and also notifies the listenrs
 */
public abstract class StatePlayer implements Player {

    private Set<StateListener> listeners = new HashSet<>();
    private AtomicInteger currentState = new AtomicInteger(Rotor.STATE_WAITING);

    @Override
    public void addListener(StateListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(StateListener listener) {
        this.listeners.remove(listener);
    }

    protected void notifyListeners() {
        for(StateListener listener : listeners) {
            listener.onStateChange(getState(), this);
        }
    }

    protected void setState(int state) {
        currentState.set(state);
        notifyListeners();
    }

    @Override
    public int getState() {
        return currentState.get();
    }
}
