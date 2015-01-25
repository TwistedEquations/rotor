/**
 * Copyright (C) 07/01/2015 Patrick
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

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.twistedequations.rotor.Action;
import com.twistedequations.rotor.Rotor;

import junit.framework.TestCase;

public class ActionTest extends TestCase {

    @SmallTest
    public void testActionParcelable() {
        Action action = new Action.Builder(Rotor.ACTION_PAUSE)
                .addDouble("key1", 3.5)
                .addLong("key2", 89l)
                .addString("key3", "value3")
                .build();

        Parcel parcel = Parcel.obtain();
        action.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Action restored = Action.CREATOR.createFromParcel(parcel);

        assertEquals(Rotor.ACTION_PAUSE, restored.getAction());
        assertEquals(3.5, restored.getDouble("key1"));
        assertEquals(89l, restored.getLong("key2"));
        assertEquals("value3", restored.getString("key3"));
    }
}
