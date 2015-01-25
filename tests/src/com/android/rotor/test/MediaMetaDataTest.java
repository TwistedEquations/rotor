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

import android.media.MediaMetadataEditor;
import android.media.MediaMetadataRetriever;
import android.media.Rating;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Build;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.twistedequations.rotor.MediaDescriptionCompat;
import com.twistedequations.rotor.MediaMetadataCompat;
import com.twistedequations.rotor.RatingCompat;

import junit.framework.TestCase;

public class MediaMetaDataTest extends TestCase {

    @SmallTest
    public void testMetaDataCompatEquals() {
        RatingCompat ratingControlCompat = RatingCompat.newThumbRating(true);
        RatingCompat userRatingControlCompat = RatingCompat.newPercentageRating(50.9f);

        MediaMetadataCompat mediaMetadataControlCompat = new MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, "Lunatic")
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Lunatic")
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "The Kongos")
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "Come With Me Now")
                .putText(MediaMetadataCompat.METADATA_KEY_AUTHOR, "The Kongos")
                .putText(MediaMetadataCompat.METADATA_KEY_GENRE, "Rock")
                .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "mediaid89265")
                .putRating(MediaMetadataCompat.METADATA_KEY_RATING, ratingControlCompat)
                .putRating(MediaMetadataCompat.METADATA_KEY_USER_RATING, userRatingControlCompat)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "http://www.imgur.com/thekongos")
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 10)
                .build();

        RatingCompat ratingCompat = RatingCompat.newThumbRating(true);
        RatingCompat userRatingCompat = RatingCompat.newPercentageRating(50.9f);

        MediaMetadataCompat mediaMetadataCompat = new MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, "Lunatic")
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Lunatic")
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

        assertEquals(mediaMetadataControlCompat, mediaMetadataCompat);
    }

    @SmallTest
    public void testMetaDataCompat() {
        RatingCompat ratingCompat = RatingCompat.newThumbRating(true);
        RatingCompat userRatingCompat = RatingCompat.newPercentageRating(50.9f);

        MediaMetadataCompat mediaMetadataCompat = new MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, "Lunatic")
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Lunatic")
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

        MediaDescriptionCompat description = mediaMetadataCompat.getDescription();
        assertEquals("Lunatic", description.title);
        assertEquals("The Kongos", description.subtitle);
        assertEquals("Come With Me Now", description.description);
        assertEquals("mediaid89265", description.mediaID);
        assertEquals(Uri.parse("http://www.imgur.com/thekongos"), description.iconUri);
    }

    @SmallTest
    public void testMetaDataKitKat() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.w("Rotor", "Skipping test [testMetaDataKitKat] due to old device");
            return;
        }
        RatingCompat ratingCompat = RatingCompat.newThumbRating(true);
        RatingCompat userRatingCompat = RatingCompat.newPercentageRating(50.9f);

        MediaMetadataCompat mediaMetadataCompat = new MediaMetadataCompat.Builder()
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

        RemoteControlClient remoteControlClient = new RemoteControlClient(null);
        mediaMetadataCompat.populateRemoteControlClient(remoteControlClient);
        RemoteControlClient.MetadataEditor editor = remoteControlClient.editMetadata(false);
        Rating returnedUser = (Rating) editor.getObject(MediaMetadataEditor.RATING_KEY_BY_USER, ratingCompat);
        Rating returned = (Rating) editor.getObject(MediaMetadataEditor.RATING_KEY_BY_OTHERS, userRatingCompat);

        assertEquals(userRatingCompat.getPercentRating(), returnedUser.getPercentRating());
        assertEquals(returned.isThumbUp(), ratingCompat.isThumbUp());
        assertEquals("Lunatic", editor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUM, "default"));
        assertEquals("Lunatic", editor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, "default"));
        assertEquals("The Kongos", editor.getString(MediaMetadataRetriever.METADATA_KEY_AUTHOR, "default"));
        assertEquals("Rock", editor.getString(MediaMetadataRetriever.METADATA_KEY_GENRE, "default"));
    }

    @SmallTest
    public void testMetaDataLollipop() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            RatingCompat ratingCompat = RatingCompat.newThumbRating(true);
            RatingCompat userRatingCompat = RatingCompat.newPercentageRating(50.9f);

            MediaMetadataCompat mediaMetadataCompat = new MediaMetadataCompat.Builder()
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

            android.media.MediaMetadata returned = mediaMetadataCompat.getMediaSessionData();

            assertEquals("Lunatic", returned.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM));
            assertEquals("Lunatic", returned.getString(android.media.MediaMetadata.METADATA_KEY_TITLE));
            assertEquals("The Kongos", returned.getString(android.media.MediaMetadata.METADATA_KEY_AUTHOR));
            assertEquals("Rock", returned.getString(android.media.MediaMetadata.METADATA_KEY_GENRE));
            assertEquals("Lunatic", returned.getString(android.media.MediaMetadata.METADATA_KEY_DISPLAY_TITLE));
            assertEquals("The Kongos", returned.getString(android.media.MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE));
            assertEquals("Come With Me Now", returned.getString(android.media.MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION));
            assertEquals(10, returned.getLong(android.media.MediaMetadata.METADATA_KEY_NUM_TRACKS));

            Rating ratingOther = returned.getRating(android.media.MediaMetadata.METADATA_KEY_RATING);
            Rating ratingUser = returned.getRating(android.media.MediaMetadata.METADATA_KEY_USER_RATING);
            assertEquals(ratingCompat.getRating().isThumbUp(), ratingOther.isThumbUp());
            assertEquals(userRatingCompat.getRating().getPercentRating(), ratingUser.getPercentRating());
        }
        else {
            Log.w("Rotor", "Skipping test [testMetaDataLollipop] due to old device");
        }
    }
}
