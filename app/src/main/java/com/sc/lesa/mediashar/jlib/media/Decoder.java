package com.sc.lesa.mediashar.jlib.media;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Decoder {
    private MediaCodec mCodec;
    private Surface mSurface;
    // Video Constants
    private final static String MIME_TYPE = "video/avc"; // H.264 Advanced Video
    private int VIDEO_WIDTH = 1440;
    private int VIDEO_HEIGHT = 2560;
    private int TIME_INTERNAL = 24; //视频帧率

    public Decoder(int width,int height,int fps,Surface surface){
        this.VIDEO_WIDTH=width;
        this.VIDEO_HEIGHT=height;
        this.TIME_INTERNAL=fps;
        this.mSurface = surface;
        initDecoder();
    }

    public void initDecoder() {

        try {
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE,
                VIDEO_WIDTH, VIDEO_HEIGHT);
        mCodec.configure(mediaFormat, mSurface,
                null, 0);
        mCodec.start();
    }

    int mCount = 0;
    public boolean onFrame(byte[] buf, int offset, int length,long ts) {
        Log.e("Media", "onFrame start");
        Log.e("Media", "onFrame Thread:" + Thread.currentThread().getId());
        // Get input buffer index
        try {
            int inputBufferIndex = mCodec.dequeueInputBuffer(10000);

            Log.e("Media", "onFrame index:" + inputBufferIndex);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mCodec.getInputBuffer(inputBufferIndex);
                inputBuffer.clear();
                inputBuffer.put(buf, offset, length);
                mCodec.queueInputBuffer(inputBufferIndex, 0, length, ts, 0);
                mCount++;
            } else {
                return false;
            }

            // Get output buffer index
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 100);
            while (outputBufferIndex >= 0) {
                mCodec.releaseOutputBuffer(outputBufferIndex, true);
                outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
            Log.e("Media", "onFrame end");
            return true;
        }catch (Exception e){
            Log.e("Media", "onFrame faile");
            e.printStackTrace();
            return false;
        }
    }

    public void release(){
        mSurface=null;
        if (mCodec !=null){
            mCodec.stop();
            mCodec.release();
            mCodec=null;
        }
    }
}
