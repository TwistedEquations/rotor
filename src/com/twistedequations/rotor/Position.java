/**
 * Copyright (C) 20/01/2015 Patrick
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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Stack;

/**
 * Wrapper around the players position, buffer and duration of the media. When you are finished with the position you must call
 * recycle to return the object to an internal pool. This is prevent heap churning as the get position may be called a lot of times.
 */
public class Position implements Parcelable {

    public long position;
    public long duration;
    public long buffer;

    private static Stack<Position> recycledPositions = new Stack<>();

    public synchronized static Position get(long position, long duration, long buffer) {
        if(!recycledPositions.isEmpty()) {
            Position pos = recycledPositions.pop();
            pos.position = position;
            pos.duration = duration;
            pos.buffer = buffer;
            return pos;
        }
        else {
            return new Position(position, duration, buffer);
        }
    }

    private Position(long position, long duration, long buffer) {
        this.position = position;
        this.duration = duration;
        this.buffer = buffer;
    }

    private Position(Parcel parcel) {
        this.position = parcel.readLong();
        this.duration = parcel.readLong();
        this.buffer = parcel.readLong();
    }

    /**
     * This called by the recycle method and is used to reset all the
     * fields to defaults.
     */
    protected void clear() {
        position = 0;
        duration = 0;
        buffer = 0;
    }

    /**
     * Call this to return the Position to the internal pool to prevent heap churn.
     */
    public synchronized void recycle() {
        clear();
        recycledPositions.add(this);
    }

    @Override
    public String toString() {
        return "Position, [position = "+position+"] [duration = "+duration+"] [buffer = "+buffer+"]";
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash *= 17 + position;
        hash *= 31 + duration;
        hash *= 3 + buffer;
        return hash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(position);
        parcel.writeLong(duration);
        parcel.writeLong(buffer);
    }

    public static Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel parcel) {
            return new Position(parcel);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };
}
