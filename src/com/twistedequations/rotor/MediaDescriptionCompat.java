/**
 * Copyright (C) 24/01/2015 Patrick
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

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaDescription;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Short description of the {@link com.twistedequations.rotor.MediaMetadataCompat}.
 */
public class MediaDescriptionCompat implements Parcelable {

    public String mediaID;

    public CharSequence title;

    public CharSequence subtitle;

    public CharSequence description;

    public Bitmap icon;

    public Uri iconUri;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MediaDescription getDescription() {
        MediaDescription description = new MediaDescription.Builder()
                .setDescription(this.description)
                .setIconBitmap(icon)
                .setIconUri(iconUri)
                .setMediaId(mediaID)
                .setTitle(title)
                .setSubtitle(subtitle)
                .build();
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mediaID);
        parcel.writeString(title.toString());
        parcel.writeString(subtitle.toString());
        parcel.writeString(description.toString());
        parcel.writeParcelable(icon, i);
        parcel.writeString(iconUri.toString());
    }

    public static final Creator<MediaDescriptionCompat> CREATOR = new Creator<MediaDescriptionCompat>() {
        @Override
        public MediaDescriptionCompat createFromParcel(Parcel parcel) {
            MediaDescriptionCompat description = new MediaDescriptionCompat();
            description.mediaID = parcel.readString();
            description.title = parcel.readString();
            description.subtitle = parcel.readString();
            description.icon = parcel.readParcelable(Bitmap.class.getClassLoader());
            description.iconUri = Uri.parse(parcel.readString());
            return description;
        }

        @Override
        public MediaDescriptionCompat[] newArray(int size) {
            return new MediaDescriptionCompat[size];
        }
    };
}
