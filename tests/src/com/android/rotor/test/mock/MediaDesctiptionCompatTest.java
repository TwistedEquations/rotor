/**
 * Copyright (C) 12/02/2015 Patrick
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

package com.android.rotor.test.mock;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.twistedequations.rotor.MediaDescriptionCompat;
import com.twistedequations.rotor.MediaMetadataCompat;

import junit.framework.TestCase;

public class MediaDesctiptionCompatTest extends TestCase {

    @SmallTest
    private void testParcelable() {
        MediaDescriptionCompat descriptionCompat = new MediaDescriptionCompat();
        descriptionCompat.mediaID = "432";
        descriptionCompat.title = "Hey I dont know";
        descriptionCompat.description = "The kongos";
        descriptionCompat.subtitle = "The kongos";
        descriptionCompat.iconUri = Uri.parse("http://www.exaple.com");

        Parcel parcel = Parcel.obtain();
        descriptionCompat.writeToParcel(parcel, 0);

        MediaDescriptionCompat restored = MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
        assertEquals("432",  restored.mediaID);
        assertEquals("Hey I dont know",  restored.title);
        assertEquals("The kongos",  restored.description);
        assertEquals("The kongos",  restored.subtitle);
        assertEquals(Uri.parse("http://www.exaple.com"),  restored.iconUri);
    }
}
