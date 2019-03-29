package com.sc.lesa.mediashar.jlib.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * Created by Lesa on 2018/12/03.
 */

public class Encoder extends Thread {

    private MediaCodec codec;
    private EncoderListener encoderListener;

    private int videoW;
    private int videoH;
    private int videoBitrate;
    private int videoFrameRate;
    private Surface mSurface;

    private final int TIMEOUT_USEC = 12000;
    private byte[] configbyte;

    private boolean exit = false;

    private static final String TAG = "Encoder";
    private static final String MIME = "Video/AVC";

    public Encoder(int videoW, int videoH, int videoBitrate, int videoFrameRate, EncoderListener encoderListener) {
        super(TAG);
        this.videoW = videoW;
        this.videoH = videoH;
        this.videoBitrate = videoBitrate;
        this.videoFrameRate = videoFrameRate;
        this.encoderListener = encoderListener;

        initMediaCodec();
    }

    public Surface getmSurface() {
        return mSurface;
    }

    private void initMediaCodec() {
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MIME, videoW, videoH);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//颜色格式
            format.setInteger(MediaFormat.KEY_BIT_RATE, videoBitrate);//码流
            format.setInteger(MediaFormat.KEY_FRAME_RATE, videoFrameRate);//帧数
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, -1); // 关键帧 5秒
            
            codec = MediaCodec.createEncoderByType(MIME);
            codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mSurface = codec.createInputSurface();
            codec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取h264数据
     * **/
    @Override
    public void run(){
        try
        {
            while(!exit){
                if(codec == null) return;

                MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIndex  = codec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                while (outputBufferIndex >= 0){
                    ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferIndex);
                    byte[] outData = new byte[mBufferInfo.size];
                    outputBuffer.get(outData);
                    if(mBufferInfo.flags == 2){
                        configbyte = new byte[mBufferInfo.size];
                        configbyte = outData;
                    }

                    else if(mBufferInfo.flags == 1){
                        byte[] keyframe = new byte[mBufferInfo.size + configbyte.length];
                        System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                        System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
                        encoderListener.onH264(keyframe,1,mBufferInfo.presentationTimeUs);

                    }else{
                        //其他帧末
                        encoderListener.onH264(outData,2,mBufferInfo.presentationTimeUs);

                    }
                    codec.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = codec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        close();

    }

    private void close(){
        codec.stop();
        codec.release();
        codec = null;
        encoderListener.onCloseH264();
        encoderListener=null;
    }

    public void releaseMediaCodec() {
        exit = true;
    }

    public interface EncoderListener {
        void onH264(byte[] buffer, int type,long ts);
        void onCloseH264();
    }
}
