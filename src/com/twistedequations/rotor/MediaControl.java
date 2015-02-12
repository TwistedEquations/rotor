package com.twistedequations.rotor;

import android.support.annotation.Nullable;

/**
 * Copyright (C) 07/01/2015 Patrick
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
 */
public interface MediaControl {

    /**
     * Create the media control, eg post the initial notification
     */
    public void create();

    /**
     * Create the media control, eg cancel the notification
     */
    public void destroy();

    /**
     * Called when the player state changes
     * @param state the players new state
     */
    public void updateState(int state);

    /**
     * Called when the metadata has changed
     * @param metaData the new meta data to display
     */
    public void onUpdateMetaData(MediaMetadataCompat metaData);

    /**
     * Called after create, used to pass actions back to rotor. This will also be called just before destroy
     */
    public void setRotor(@Nullable Rotor rotor);
}
