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

import android.test.suitebuilder.annotation.SmallTest;

import com.twistedequations.rotor.MediaMetadataCompat;
import com.twistedequations.rotor.MetaDataClient;
import com.twistedequations.rotor.Position;
import com.twistedequations.rotor.RatingCompat;

import junit.framework.TestCase;

public class MetaDataClientTest extends TestCase {

    @SmallTest
    public void testPending() {
        MetaDataClient metaDataClient = new MetaDataClient() {
            @Override
            protected MediaMetadataCompat onRequestMetaData(Position position) {
                return getControlData();
            }
        };

        metaDataClient.requestMetaData(getControlPosition());
        assertTrue(metaDataClient.isPending());
        assertFalse(metaDataClient.isAync());
        assertEquals(getControlData(), metaDataClient.getMetaData());
    }

    @SmallTest
    public void testAync() {
        MetaDataClient metaDataClient = new MetaDataClient() {
            @Override
            protected MediaMetadataCompat onRequestMetaData(Position position) {
                unlatch();
                return null;
            }
        };

        metaDataClient.requestMetaData(getControlPosition());
        assertTrue(metaDataClient.isAync());
        assertFalse(metaDataClient.isPending());
        assertFalse(getControlData().equals(metaDataClient.getMetaData()));

        metaDataClient.updateMetaData(getControlData());
        assertTrue(metaDataClient.isPending());
        assertFalse(metaDataClient.isAync());
        assertTrue(getControlData().equals(metaDataClient.getMetaData()));
    }

    private static Position getControlPosition() {
        return Position.get(0, 0, 0);
    }

    private static MediaMetadataCompat getControlData() {
        RatingCompat ratingCompat = RatingCompat.newThumbRating(true);
        RatingCompat userRatingCompat = RatingCompat.newPercentageRating(50.9f);

        return new MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, "Lunatic")
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Lunatic")
                .putText(MediaMetadataCompat.METADATA_KEY_TITLE, "Lunatic")
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "The Kongos")
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "Come With Me Now")
                .putText(MediaMetadataCompat.METADATA_KEY_AUTHOR, "The Kongos")
                .putText(MediaMetadataCompat.METADATA_KEY_GENRE, "Rock")
                .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "mediaid89265")
                .putRating(MediaMetadataCompat.METADATA_KEY_RATING, ratingCompat)
                .putRating(MediaMetadataCompat.METADATA_KEY_USER_RATING, userRatingCompat)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "http://www.imgur.com/thekongos")
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 10)
                .build();
    }
}
