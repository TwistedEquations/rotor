/**
 * Copyright (C) 05/01/2015 Patrick
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


import com.twistedequations.rotor.MediaMetadataCompat;
import com.twistedequations.rotor.MetaDataClient;
import com.twistedequations.rotor.Position;

public class DefaultMetaDataClient extends MetaDataClient {

    @Override
    public MediaMetadataCompat onRequestMetaData(Position position) {
        return null;
    }

}
