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

package com.twistedequations.rotor.toolbox;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.twistedequations.rotor.Action;
import com.twistedequations.rotor.Player;
import com.twistedequations.rotor.Playlist;
import com.twistedequations.rotor.Position;
import com.twistedequations.rotor.Rotor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class BasicAudioPlayer extends Player implements Runnable {
    //Dependencies
    private Playlist playlist;

    private MediaExtractor extractor;
    private AudioTrack audioTrack;
    private ChunkPool byteArrayPool;
    private boolean stop = false;
    private String mime = null;
    private int sampleRate = 0;
    private int channels = 0;
    private int bitrate = 0;
    private AtomicLong presentationTime = new AtomicLong();
    private AtomicLong cachedDuration = new AtomicLong();
    private long duration = 0;
    private boolean isRunning;

    private CountDownLatch resetLatch;
    private CountDownLatch pauseLatch;
    private boolean isPreformingAction;

    public BasicAudioPlayer(Playlist playlist) {
        this.playlist = playlist;
        byteArrayPool = new ChunkPool(1024 * 512);
    }

    @Override
    public void preformAction(Action action) {

        if(isPreformingAction) {
            throw new IllegalStateException("Player is already preforming an action");
        }

        int actionInt = action.getAction();

        isPreformingAction = true;

        switch (actionInt) {
            case Rotor.ACTION_PLAY:
                play();
                break;

            case Rotor.ACTION_PAUSE:
                pause();
                break;

            case Rotor.ACTION_NEXT:
                if(playlist.next()) {
                    reset();
                }
                break;

            case Rotor.ACTION_PREV:
                if(playlist.prev()) {
                    reset();
                }
                break;

            case Rotor.ACTION_STOP:
                reset();
                break;
        }

        isPreformingAction = false;
    }

    @Override
    public Position getPosition() {
        return Position.get(presentationTime.get(), duration, cachedDuration.get());
    }

    private void play() {
        stop = false;
        if(isRunning && getState() == Rotor.STATE_PLAYING) {
            return;
        }
        if(getState() != Rotor.STATE_PAUSED) {
            Thread thread = new Thread(this);
            thread.setName("Rotor Basic Audio Player Thread");
            thread.start();
        }
        else {
            resume();
        }
    }

    private void reset() {
        if(getState() == Rotor.STATE_PAUSED) {
            resume();
        }
        if(isRunning) {
            resetLatch = new CountDownLatch(1);
            stop = true;
            try {
                resetLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setState(Rotor.STATE_WAITING);
    }

    private void pause() {
        pauseLatch = new CountDownLatch(1);
        setState(Rotor.STATE_PAUSED);
        try {
            pauseLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void resume() {
        setState(Rotor.STATE_PLAYING);
        this.notifyAll();
    }

    @Override
    public void run() {
        isRunning = true;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        //Main loop for the playerlist
        while(!playlist.empty() && !stop) {

            // Set up a new audio player based on the stream data

            // extractor gets information about the stream
            extractor = new MediaExtractor();
            setState(Rotor.STATE_BUFFERING);
            try {
                String source = playlist.getCurrent();
                if(source == null) {
                    stop = true;
                }
                else {
                    extractor.setDataSource(playlist.getCurrent());
                }
            } catch (IOException e) {
                setState(Rotor.STATE_ERROR);
                return;
            }

            // Read track header
            MediaFormat format = null;
            try {
                int tracks = extractor.getTrackCount();
                if(tracks < 1) {
                    throw new IllegalArgumentException("No tracks in extractor");
                }
                format = extractor.getTrackFormat(0);
                mime = format.getString(MediaFormat.KEY_MIME);
                sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                duration = format.getLong(MediaFormat.KEY_DURATION);
                bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("Rotor","Media info: mime:" + mime + " sampleRate:" + sampleRate + " channels:" + channels + " bitrate:" + bitrate + " duration:" + duration);

            // check we have audio content we know
            if (format == null || !mime.startsWith("audio/")) {
                setState(Rotor.STATE_ERROR);
                return;
            }

            // create the actual decoder, using the mime to select
            MediaCodec codec = null;
            try {
                codec = MediaCodec.createDecoderByType(mime);
            } catch (IOException e) {
                e.printStackTrace();
                setState(Rotor.STATE_ERROR);
                return;
            }

            // check we have a valid codec instance
            if (codec == null) {
                setState(Rotor.STATE_ERROR);
                return;
            }

            codec.configure(format, null, null, 0);
            codec.start();
            ByteBuffer[] codecInputBuffers  = codec.getInputBuffers();
            ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();

            // configure AudioTrack
            int channelConfiguration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
            int minSize = AudioTrack.getMinBufferSize(sampleRate, channelConfiguration, AudioFormat.ENCODING_PCM_16BIT);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfiguration, AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);
            // start playing, we will feed the AudioTrack later
            audioTrack.play();
            extractor.selectTrack(0);

            // start decoding
            final long kTimeOutUs = 5000000;
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            boolean sawInputEOS = false;
            boolean sawOutputEOS = false;

            long presentationTimeUs = 0;
            while (!sawOutputEOS && !stop) {
                // pause implementation
                waitPlay();

                // read a buffer before feeding it to the decoder
                if (!sawInputEOS) {
                    int inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);

                    if (inputBufIndex >= 0) {

                        ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];

                        //Post delayed to set buffering
                        //handler.postDelayed(bufferingRunnable, 1000);
                        int sampleSize = extractor.readSampleData(dstBuf, 0);

                        if (sampleSize < 0) {
                            Log.v("Rotor","saw input EOS. Checking for next url");
                            sawInputEOS = true;
                            sampleSize = 0;
                        }
                        else {
                            presentationTimeUs = extractor.getSampleTime();
                            presentationTime.set(presentationTimeUs / 1000);
                            long cachedDuration = extractor.getCachedDuration();
                            this.cachedDuration.set(cachedDuration);
                        }

                        codec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                        if (!sawInputEOS) {
                            extractor.advance();
                        }
                    }
                    else {
                        Log.v("Rotor","inputBufIndex " +inputBufIndex);
                    }
                }

                // decode to PCM and push it to the AudioTrack player
                int outputBufIndex = codec.dequeueOutputBuffer(info, kTimeOutUs);

                if (outputBufIndex >= 0) {

                    ByteBuffer buf = codecOutputBuffers[outputBufIndex];

                    //if the player is playing smoothly the buffering state will not be set
                    //handler.removeCallbacks(bufferingRunnable);

                    //Using a byte array pool from volley to prevent heap churn
                    final byte[] chunk = byteArrayPool.getBuffer(info.size);
                    buf.get(chunk);
                    buf.clear();
                    if(chunk.length > 0 && audioTrack != null){
                        audioTrack.write(chunk, 0, chunk.length);

                        waitPlay();
                        setState(Rotor.STATE_PLAYING);
                    }

                    codec.releaseOutputBuffer(outputBufIndex, false);

                    byteArrayPool.returnBuffer(chunk);

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.v("Rotor","saw output EOS.");
                        sawOutputEOS = true;
                    }
                }
                else if (outputBufIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    codecOutputBuffers = codec.getOutputBuffers();
                    Log.v("Rotor", "output buffers have changed");
                }
                else if (outputBufIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat oformat = codec.getOutputFormat();
                    Log.v("Rotor", "output format has changed to " + oformat);

                    int sampleRate = oformat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int channels = oformat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

                    int newChannelConfiguration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
                    int newMinSize = AudioTrack.getMinBufferSize(sampleRate, newChannelConfiguration, AudioFormat.ENCODING_PCM_16BIT);
                    Log.v("Rotor", "newMinSize: " + newMinSize);

                    if(audioTrack != null) {
                        audioTrack.flush();
                        audioTrack.release();
                        audioTrack = null;
                    }

                    // Karl - have seen here 'java.lang.IllegalArgumentException: Invalid audio buffer size' by using 'newMinSize'
                    // So adding a fallback to hard code the buffer size to something we know that works - 8466 bytes
                    try {
                        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, newChannelConfiguration, AudioFormat.ENCODING_PCM_16BIT, newMinSize, AudioTrack.MODE_STREAM);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, newChannelConfiguration, AudioFormat.ENCODING_PCM_16BIT, 8466, AudioTrack.MODE_STREAM);
                    }

                    audioTrack.play();
                }
                else {
                    Log.v("Rotor", "dequeueOutputBuffer returned " + outputBufIndex);
                }
            }

            codec.stop();
            codec.release();
            codec = null;

            if(audioTrack != null) {
                audioTrack.flush();
                audioTrack.release();
                audioTrack = null;
            }

            // clear url and the other globals
            duration = 0;
            mime = null;
            sampleRate = 0;
            channels = 0;
            bitrate = 0;
            presentationTime.set(0);
            duration = 0;
            presentationTimeUs = 0;

            setState(Rotor.STATE_WAITING);
        }
        if(stop && resetLatch != null) {
            resetLatch.countDown();
        }
        isRunning = false;
    }

    private Runnable bufferingRunnable = new Runnable() {
        @Override
        public void run() {
            setState(Rotor.STATE_BUFFERING);
        }
    };

    // A pause mechanism that would block getCurrent thread when pause flag is set (READY_TO_PLAY)
    public synchronized void waitPlay(){
        while(getState() == Rotor.STATE_PAUSED) {
            pauseLatch.countDown();
            try {
                ((Object)this).wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
