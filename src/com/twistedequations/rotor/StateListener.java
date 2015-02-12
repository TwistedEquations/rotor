package com.twistedequations.rotor;

/*
 * Copyright (C) 02/01/2015 Patrick
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * Used by the player to notify state changes. If this a added to the player all the player events and sent here even if the state di not change.
 * For example the {@link com.twistedequations.rotor.toolbox.BasicAudioPlayer BasicAudioPlayer} will noitfy for the playing state evey time a chunk of audio is rendered.
 * This is useful for progress bars
 * <br/>
 * <br/>
 * If this is attached to the {@link Rotor Rotor} instance it will only be notified when the state changes.
 * This is useful for changing the state of play/pause buttons
 */
public interface StateListener {

    public void onStateChange(int state, Player player);
}
