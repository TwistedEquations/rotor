/**
 * Copyright (C) 01/01/2015 Patrick
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
import android.support.annotation.DrawableRes;

import com.twistedequations.rotor.toolbox.Rotor;

import java.util.HashMap;
import java.util.Map;

public class Action implements Parcelable {

    public static final Action PAUSE;
    public static final Action PLAY;
    public static final Action NEXT;
    public static final Action PREV;
    public static final Action STOP;

    private int action;

    private int icon;

    private String title;

    private Map<String, String> args = new HashMap<>();

    static {
        PAUSE = new Builder(Rotor.ACTION_PAUSE).icon(R.drawable.rotor_action_pause).title("Pause").build();
        PLAY = new Builder(Rotor.ACTION_PLAY).icon(R.drawable.rotor_action_play).title("Play").build();
        NEXT = new Builder(Rotor.ACTION_NEXT).icon(R.drawable.rotor_action_next).title("Next").build();
        PREV = new Builder(Rotor.ACTION_PREV).icon(R.drawable.rotor_action_previous).title("Previous").build();
        STOP = new Builder(Rotor.ACTION_STOP).icon(R.drawable.rotor_action_cancel).title("Stop").build();
    }

    private Action(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public String getTitle() {
        return title;
    }

    public long getLong(String key) {
        return Long.parseLong(args.get(key));
    }

    public String getString(String key) {
        return args.get(key);
    }

    public double getDouble(String key) {
        return Double.parseDouble(args.get(key));
    }

    public int getIcon() {
        return icon;
    }

    public boolean hasIcon() {
        return icon != 0;
    }

    @Override
    public int hashCode() {
        return args.hashCode() + action + icon + title.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Action && hashCode() == o.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(action);
        parcel.writeInt(icon);
        parcel.writeString(title);
        parcel.writeInt(args.size());
        for(Map.Entry<String, String> entry : args.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
    }

    public static Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel parcel) {
            int actionInt = parcel.readInt();
            int icon = parcel.readInt();
            String title = parcel.readString();
            int argsSize = parcel.readInt();
            Map<String, String> args = new HashMap<>(argsSize);
            for (int i = 0; i < argsSize; i++) {
                String key = parcel.readString();
                String value = parcel.readString();
                args.put(key, value);
            }

            Action action = new Action(actionInt);
            action.args = args;
            action.icon = icon;
            action.title = title;
            return action;
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[0];
        }
    };

    public static class Builder {

        private Action action;

        public Builder (int action) {
            this.action = new Action(action);
        }

        public Builder addLong(String key, long integer) {
            action.args.put(key, Long.toString(integer));
            return this;
        }

        public Builder icon(@DrawableRes int icon) {
            action.icon = icon;
            return this;
        }

        public Builder addString(String key, String integer) {
            action.args.put(key ,integer);
            return this;
        }

        public Builder title(String title) {
            action.title = title;
            return this;
        }

        public Builder addDouble(String key, double integer) {
            action.args.put(key, Double.toString(integer));
            return this;
        }

        public Action build() {
            return action;
        }
    }
}
