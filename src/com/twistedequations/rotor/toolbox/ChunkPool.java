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

package com.twistedequations.rotor.toolbox;

import java.util.Stack;

public class ChunkPool {

    private Stack<byte[]> bufferStack = new Stack<>();

    private int maxSize;
    private int totalSize;

    public ChunkPool(int size) {
        this.maxSize = size;
    }

    public byte[] getBuffer(int size) {
        if(bufferStack.empty()) {
            return new byte[size];
        }

        for (int i = 0; i < bufferStack.size(); i++) {
            byte[] buff = bufferStack.peek();
            if (buff.length == size) {
                return bufferStack.pop();
            }
        }

        return new byte[size];
    }

    public void returnBuffer(byte[] buffer) {
        int newSize = totalSize + buffer.length;

        if(newSize > maxSize) {
            //trim the stack until its under the size
            for (int i = bufferStack.size() - 1; i >= 0; i--) {
                byte[] buff = bufferStack.get(i);
                bufferStack.remove(i);
                totalSize -= buff.length;

                if(totalSize + buffer.length < maxSize) {
                    //trimmed enough buffers
                    break;
                }
            }
        }
        bufferStack.push(buffer);
        totalSize += buffer.length;
    }
}
