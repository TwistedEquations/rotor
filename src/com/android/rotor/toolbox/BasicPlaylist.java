/**
 * Copyright (C) 29/12/2014 Patrick
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

package com.android.rotor.toolbox;

import com.android.rotor.Playlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class BasicPlaylist implements Playlist {

    private int position = 0;
    private List<String> list = new ArrayList<>();

    @Override
    public String getCurrent() {
        if(position >= 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public void add(String source) {
        list.add(source);
    }

    @Override
    public void addAll(Collection<String> sources) {
        list.addAll(sources);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean next() {
        if(position < list.size()) {
            position ++;
            return true;
        }
        else {

            return false;
        }
    }

    @Override
    public boolean prev() {
        if(position <= 0) {
            return false;
        }
        else {
            position --;
            return true;
        }
    }

    @Override
    public boolean moveTo(int pos) {
        if(pos >= 0 && pos < list.size()) {
            position = pos;
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean empty() {
        return list.isEmpty();
    }

    @Override
    public int getPosition() {
        return position;
    }
}
