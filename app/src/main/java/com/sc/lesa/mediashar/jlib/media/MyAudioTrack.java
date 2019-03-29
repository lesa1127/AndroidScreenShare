package com.sc.lesa.mediashar.jlib.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class MyAudioTrack {


    private int mFrequency;// 采样率
    private int mChannel;// 声道
    private int mSampBit;// 采样精度
    private AudioTrack mAudioTrack;
    private int mStreamType;

    /**
     *
     * @param frequency 采样频率
     * {@link AacFormat#SampleRate44100} {@link AacFormat#SampleRate48000}
     *
     * @param channel 声道
     * See {@link AudioFormat#CHANNEL_OUT_MONO} and {@link AudioFormat#CHANNEL_OUT_STEREO}
     *
     * @param sampbit 采样精度
     * See {@link AudioFormat#ENCODING_PCM_16BIT},{@link AudioFormat#ENCODING_PCM_8BIT}
     *
     * @param streamType 系统音频类型
     *{@link AudioManager#STREAM_VOICE_CALL}, {@link AudioManager#STREAM_SYSTEM},
     *{@link AudioManager#STREAM_RING}, {@link AudioManager#STREAM_MUSIC},
     *{@link AudioManager#STREAM_ALARM}, and {@link AudioManager#STREAM_NOTIFICATION}.
     */
    public MyAudioTrack(int frequency, int channel, int sampbit,int streamType) {
        this.mFrequency = frequency;
        this.mChannel = channel;
        this.mSampBit = sampbit;
        this.mStreamType=streamType;
    }

    /**
     * 初始化
     */
    public void init() {
        if (mAudioTrack != null) {
            release();
        }
        // 获得构建对象的最小缓冲区大小
        int minBufSize = getMinBufferSize();
        mAudioTrack = new AudioTrack(mStreamType,
                mFrequency, mChannel, mSampBit, minBufSize, AudioTrack.MODE_STREAM);
        mAudioTrack.play();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }

    /**
     * 将解码后的pcm数据写入audioTrack播放
     *
     * @param data   数据
     * @param offset 偏移
     * @param length 需要播放的长度
     */
    public void playAudioTrack(byte[] data, int offset, int length) {
        if (data == null || data.length == 0) {
            return;
        }
        try {
            mAudioTrack.write(data, offset, length);
        } catch (Exception e) {
            Log.e("MyAudioTrack", "AudioTrack Exception : " + e.toString());
        }
    }

    public int getMinBufferSize() {
        return AudioTrack.getMinBufferSize(mFrequency,
                mChannel, mSampBit);
    }

}
