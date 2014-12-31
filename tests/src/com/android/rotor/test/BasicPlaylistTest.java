/**
 * Copyright (C) 31/12/2014 Patrick
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

import com.android.rotor.Playlist;
import com.android.rotor.toolbox.BasicPlaylist;

import junit.framework.TestCase;

public class BasicPlaylistTest extends TestCase {

    private Playlist playlist = new BasicPlaylist();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        playlist.add("source1");
        playlist.add("source2");
        playlist.add("source3");
    }

    public void testGetCurrent() {
        String current = playlist.getCurrent();
        assertEquals("source1", current);
    }

    public void testGetPosition() {
        assertEquals(0, playlist.getPosition());
    }

    public void testNewPosition() {
        assertTrue(playlist.moveTo(2));
        assertEquals(2, playlist.getPosition());
        assertFalse(playlist.moveTo(-1));
        assertEquals(2, playlist.getPosition());
    }

    public void testNextPosition() {
        playlist.next();
        playlist.next();
        assertEquals(2, playlist.getPosition());
    }

    public void testPrevPosition() {
        assertTrue(playlist.moveTo(2));
        playlist.prev();
        playlist.prev();
        assertEquals(0, playlist.getPosition());
    }
}
