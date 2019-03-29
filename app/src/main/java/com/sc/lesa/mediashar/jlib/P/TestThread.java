package com.sc.lesa.mediashar.jlib.P;

import android.media.AudioFormat;

import com.sc.lesa.mediashar.jlib.media.VoiceEncoder;
import com.sc.lesa.mediashar.jlib.util.LogUtil;

public class TestThread implements VoiceEncoder.OnDataOutPut {

    VoiceEncoder voiceEncoder;

    public TestThread(){
        voiceEncoder=new VoiceEncoder(this,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,1,384000,44100);
    }

    public void start(){
        voiceEncoder.start();
    }

    @Override
    public void onDataOutPut(byte[] framByte) {
        LogUtil.appendToFile("/sdcard/tsstaac1.aac",framByte);
    }
}
