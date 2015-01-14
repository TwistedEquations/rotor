/**
 * Copyright (C) 30/12/2014 Patrick
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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Wrapper around the metadata to be passed by to the notifications etc, etc
 */
public class MetaData implements Parcelable {

    public String title;

    public String artist;

    public Bitmap artwork;

    public String header;

    public boolean hasArtWork() {
        return artwork != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(header);
        parcel.writeParcelable(artwork, i);
    }

    public static final Creator<MetaData> CREATOR = new Creator<MetaData>() {
        @Override
        public MetaData createFromParcel(Parcel parcel) {
            MetaData metaData = new MetaData();
            metaData.title = parcel.readString();
            metaData.artist = parcel.readString();
            metaData.header = parcel.readString();
            metaData.artwork = parcel.readParcelable(Bitmap.class.getClassLoader());
            return metaData;
        }

        @Override
        public MetaData[] newArray(int size) {
            return new MetaData[size];
        }
    };


}
