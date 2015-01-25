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

import android.os.Parcel;
import android.os.Parcelable;
import android.test.suitebuilder.annotation.SmallTest;

import com.twistedequations.rotor.Position;

import junit.framework.TestCase;

public class PositionTest extends TestCase {

    @SmallTest
    public void testPositionConstruct() {
        Position position = Position.get(6593, 3261,32453);
        assertEquals(32453, position.buffer);
        assertEquals(3261, position.duration);
        assertEquals(6593, position.position);
    }

    @SmallTest
    public void testPositionParcelable() {
        Position control = Position.get(6593, 3261,32453);

        Parcel parcel = Parcel.obtain();
        control.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Position position = Position.CREATOR.createFromParcel(parcel);
        assertEquals(32453, position.buffer);
        assertEquals(3261, position.duration);
        assertEquals(6593, position.position);
    }

    @SmallTest
    public void testPositionReuse() {
        Position control = Position.get(6593, 3261,32453);
        control.recycle();

        assertEquals(0, control.buffer);
        assertEquals(0, control.duration);
        assertEquals(0, control.position);
    }
}
