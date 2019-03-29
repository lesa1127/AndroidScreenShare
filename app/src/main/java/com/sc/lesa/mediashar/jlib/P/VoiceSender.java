package com.sc.lesa.mediashar.jlib.P;

import android.content.Context;
import android.content.SharedPreferences;
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
    Context context;

    int ChannelMode,EncodeFormat,ChannelCount,ByteRate, SampleRate;

    public VoiceSender(SocketServerThread s, Context context){
        this.socketServer=s;
        this.context=context;

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "voiceparam",Context.MODE_PRIVATE);

        this.ChannelMode=sharedPreferences.getInt("ChannelMode",AudioFormat.CHANNEL_IN_MONO);
        this.EncodeFormat=sharedPreferences.getInt("EncodeFormat",AudioFormat.ENCODING_PCM_16BIT);
        this.ChannelCount=sharedPreferences.getInt("ChannelCount",1);
        this.ByteRate=sharedPreferences.getInt("ByteRate",AacFormat.ByteRate384Kbs);
        this.SampleRate=sharedPreferences.getInt("SampleRate",AacFormat.SampleRate44100);

        aacEncoder = new AACEncoder(ChannelCount,ByteRate,SampleRate);
        aacEncoder.setOnEncodeDone(this);
        aacEncoder.start();
        myAudioRecord = new MyAudioRecord(MediaRecorder.AudioSource.MIC,
                SampleRate,ChannelMode,EncodeFormat);
        myAudioRecord.setOnDataInput(this);
        myAudioRecord.start();
    }

    /**
     *
     * @param context 上下文
     * @param ChannelMode {@link AudioFormat#CHANNEL_IN_MONO} 或 {@link AudioFormat#CHANNEL_IN_STEREO}
     * @param EncodeFormat {@link AudioFormat#ENCODING_PCM_16BIT}
     * @param ChannelCount {@link AacFormat#ChannleOutOne}
     * @param ByteRate {@link AacFormat#ByteRate256Kbs}
     * @param SampleRate {@link AacFormat#SampleRate44100}
     */
    public static void setParam(Context context,int ChannelMode,int EncodeFormat,
                                int ChannelCount,int ByteRate,int SampleRate){
        SharedPreferences sharedPreferences = context.getSharedPreferences("voiceparam",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("ChannelMode",ChannelMode);
        editor.putInt("EncodeFormat",EncodeFormat);
        editor.putInt("ChannelCount",ChannelCount);
        editor.putInt("ByteRate",ByteRate);
        editor.putInt("SampleRate",SampleRate);
        editor.commit();
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
    public void close() {
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
