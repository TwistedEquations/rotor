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

package com.android.rotor;

import java.util.HashMap;
import java.util.Map;

public class Action {

    private int action;

    private Map<String, String> args = new HashMap<>();

    private Action(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
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

    public static class Builder {

        private Action action;

        public Builder (int action) {
            this.action = new Action(action);
        }

        public Builder addLong(String key, long integer) {
            action.args.put(key, Long.toString(integer));
            return this;
        }

        public Builder addString(String key, String integer) {
            action.args.put(key ,integer);
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
