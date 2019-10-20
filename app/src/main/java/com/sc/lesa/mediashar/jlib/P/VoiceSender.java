package com.sc.lesa.mediashar.jlib.P;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.sc.lesa.mediashar.jlib.io.VoicePack;
import com.sc.lesa.mediashar.jlib.media.AACEncoder;
import com.sc.lesa.mediashar.jlib.media.AacFormat;
import com.sc.lesa.mediashar.jlib.media.MyAudioRecord;
import com.sc.lesa.mediashar.jlib.server.SocketServerThread;
import com.sc.lesa.mediashar.jlib.util.LogUtil;

public class VoiceSender implements AACEncoder.OnEncodeDone , MyAudioRecord.OnDataInput {
    final String TAG = VoiceSender.class.getName();

    AACEncoder aacEncoder;
    MyAudioRecord myAudioRecord;

    SocketServerThread socketServer;

    int ChannelMode,EncodeFormat,ChannelCount,ByteRate, SampleRate;

    /**
     *
     * @param s 发送线程
     * @param ChannelMode {@link AudioFormat#CHANNEL_IN_MONO} 或 {@link AudioFormat#CHANNEL_IN_STEREO}
     * @param EncodeFormat {@link AudioFormat#ENCODING_PCM_16BIT}
     * @param ChannelCount {@link AacFormat#ChannleOutOne}
     * @param ByteRate {@link AacFormat#ByteRate256Kbs}
     * @param SampleRate {@link AacFormat#SampleRate44100}
     */
    public VoiceSender(SocketServerThread s,
                       int ChannelMode, int EncodeFormat,
                       int ChannelCount, int ByteRate,
                       int SampleRate
    ){
        this.socketServer=s;

        this.ChannelMode=ChannelMode;
        this.EncodeFormat=EncodeFormat;
        this.ChannelCount=ChannelCount;
        this.ByteRate=ByteRate;
        this.SampleRate=SampleRate;

        aacEncoder = new AACEncoder(ChannelCount,ByteRate,SampleRate);
        aacEncoder.setOnEncodeDone(this);
        aacEncoder.start();
        myAudioRecord = new MyAudioRecord(MediaRecorder.AudioSource.MIC,
                SampleRate,ChannelMode,EncodeFormat);
        myAudioRecord.setOnDataInput(this);
        myAudioRecord.start();
    }

    @Override
    public void onEncodeData(byte[] bytes, int offset, int len,long ts) {
        VoicePack voicePack = new VoicePack(ChannelMode,EncodeFormat,ChannelCount,
                ByteRate, SampleRate,ts,bytes);
        this.socketServer.putVoicePack(voicePack);
        //LogUtil.appendToFile("/sdcard/tsstaac.aac",voicePack.datas);
    }

    public void exit(){
        Log.d(TAG,"退出中");
        myAudioRecord.release();
        myAudioRecord=null;

    }

    @Override
    public void onClose() {
        socketServer=null;
        Log.d(TAG,"退出完成");
    }

    @Override
    public void inputData(byte[] bytes, int offset, int len) {
        byte[] bytes1 =new byte[len];
        System.arraycopy(bytes,offset,bytes1,0,len);
        this.aacEncoder.encode(bytes1);
    }

    @Override
    public void release() {
        aacEncoder.release();
        aacEncoder=null;
    }
}
