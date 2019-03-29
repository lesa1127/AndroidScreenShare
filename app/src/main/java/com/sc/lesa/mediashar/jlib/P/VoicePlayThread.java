package com.sc.lesa.mediashar.jlib.P;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.util.Log;

import com.sc.lesa.mediashar.jlib.io.VoicePack;
import com.sc.lesa.mediashar.jlib.media.AACDecoder;
import com.sc.lesa.mediashar.jlib.media.MyAudioTrack;
import com.sc.lesa.mediashar.jlib.server.Classifier;

public class VoicePlayThread extends Thread implements AACDecoder.OnDecodeDone {
    public final static String TAG = VoicePlayThread.class.getName();

    boolean exit;
    Classifier inputdata ;
    AACDecoder aacDecoder;
    MyAudioTrack myAudioTrack;
    boolean hasInitVoice;

    public VoicePlayThread(Classifier classifier){
        this.inputdata=classifier;
    }

    private void initVoiceDecoder(int ChannelMode,int EncodeFormat,int ChannelCount,
                                  int ByteRate,int SampleRate){
        int mChannelMode = ChannelMode==AudioFormat.CHANNEL_IN_MONO?
                AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO;

        myAudioTrack = new MyAudioTrack(SampleRate,mChannelMode,
                EncodeFormat,AudioManager.STREAM_MUSIC);
        myAudioTrack.init();

        aacDecoder=new AACDecoder(ChannelCount,ByteRate,SampleRate);
        aacDecoder.setOnDecodeDone(this);
        aacDecoder.start();
    }

    @Override
    public void run() {
        while (!exit){

            VoicePack voicePack = (VoicePack) inputdata.getVoicePack();
            if (voicePack!=null){
                if (hasInitVoice==false){
                    initVoiceDecoder(voicePack.ChannelMode,voicePack.EncodeFormat,
                            voicePack.ChannelCount,voicePack.ByteRate,voicePack.SampleRate);
                    hasInitVoice=true;
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    voicePack.presentationTimeUs=0;
                }
                try {
                    this.aacDecoder.decode(voicePack.datas, 0, voicePack.datas.length, voicePack.presentationTimeUs);
                }catch (Throwable e){
                    e.printStackTrace();
                }
                //LogUtil.appendToFile("/sdcard/tsstaac.aac",voicePack.datas);

            }
        }
        dirtory();
    }

    private void dirtory(){
        aacDecoder.stop();
        myAudioTrack.release();

        inputdata=null;
        aacDecoder=null;
        myAudioTrack=null;

        Log.i(TAG,"退出成功");
    }

    public void exit(){
        Log.i(TAG,"开始退出");
        this.exit=true;
    }

    @Override
    public void onDecodeData(byte[] bytes, int offset, int len) {
        this.myAudioTrack.playAudioTrack(bytes,offset,len);
    }

    @Override
    public void close() {

    }

}
