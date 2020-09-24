package com.sc.lesa.mediashar.jlib.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class MyAudioRecord extends Thread {


    private static String TAG = "MyAudioRecord";

    protected AudioRecord mAudioRecord;
    private int mEncodeFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mChannelMode = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int mSampleRate = 44100;
    private int mAudioSource=AudioFormat.ENCODING_PCM_16BIT;
    private final int mFrameSize = 2048;//帧buffer 大小

    private boolean mExit = false;
    private OnDataInput mOnDataInput;

    /**
     *
     * @param audioSource 音频源
     * 详细音频源类型请查看{@link MediaRecorder.AudioSource}
     *
     * @param sampleRateInHz 采样频率 默认44100
     * {@link AacFormat#SampleRate44100} {@link AacFormat#SampleRate48000}
     *
     * @param channelConfig 声道采集配置
     * See {@link AudioFormat#CHANNEL_IN_MONO} and{@link AudioFormat#CHANNEL_IN_STEREO}.
     * {@link AudioFormat#CHANNEL_IN_MONO} is guaranteed to work on all devices.
     *
     * @param audioFormat 采集格式
     * See {@link AudioFormat#ENCODING_PCM_8BIT}, {@link AudioFormat#ENCODING_PCM_16BIT},
     * and {@link AudioFormat#ENCODING_PCM_FLOAT}.
     */
    public MyAudioRecord(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat){
        super(TAG);
        this.mAudioSource=audioSource;
        this.mSampleRate=sampleRateInHz;
        this.mChannelMode=channelConfig;
        this.mEncodeFormat=audioFormat;

    }

    public void init(){
        int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannelMode,
                mEncodeFormat);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                mSampleRate, mChannelMode, mEncodeFormat, minBufferSize * 2);
    }

    public void setOnDataInput(OnDataInput onDataInput){
        this.mOnDataInput = onDataInput;
    }

    @Override
    public void run() {
        mAudioRecord.startRecording();
        byte[] buffer = new byte[mFrameSize];
        int num = 0;
        while (!mExit){
            num = mAudioRecord.read(buffer, 0, mFrameSize);
            if(mOnDataInput!= null)mOnDataInput.inputData(buffer,0,num);
            Log.d(TAG, "buffer len " + ", num = " + num);
        }
        Log.d(TAG, "exit loop");

        distory();
        Log.d(TAG, "clean up");
    }


    private void distory(){
        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord=null;
        mOnDataInput.release();
        mOnDataInput=null;

    }

    public void release(){
        this.mExit=true;
    }

    public interface OnDataInput{
        public void inputData(byte[] bytes,int offset,int len);
        public void release();
    }
}
