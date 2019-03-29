package com.sc.lesa.mediashar.jlib.server;

import com.sc.lesa.mediashar.jlib.io.VideoPack;
import com.sc.lesa.mediashar.jlib.io.VoicePack;
import com.sc.lesa.mediashar.jlib.io.Writable;
import com.sc.lesa.mediashar.jlib.util.BufferList;
import com.sc.lesa.mediashar.jlib.util.CombinValue;
import com.sc.lesa.mediashar.jlib.util.TempBufferList;

import java.io.IOException;

public class Classifier {
    BufferList<Writable> bufferListVideo = new TempBufferList<>(120);
    BufferList<Writable> bufferListVoice= new TempBufferList<>(120);

    public void putDataPack(byte[] objecy){
        int type = CombinValue.bytesToInt(new byte[]{objecy[0],objecy[1],
                objecy[2],objecy[3]});

        if (type == VideoPack.TYPE_VIDEO){
            byte[] bytes = new byte[objecy.length-4];
            System.arraycopy(objecy,4,bytes,0,objecy.length-4);
            try {
                bufferListVideo.push(new VideoPack(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(type == VoicePack.TYPE_VOICE) {
            byte[] bytes = new byte[objecy.length-4];
            System.arraycopy(objecy,4,bytes,0,objecy.length-4);
            try {
                bufferListVoice.push(new VoicePack(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Writable getVideoPack(){
        return bufferListVideo.lastValue();
    }
    public Writable getVoicePack(){
        return bufferListVoice.lastValue();
    }

    public static byte[] buildVideoData(byte[] bytes){
        byte[] tmp = new byte[bytes.length+4];
        System.arraycopy(CombinValue.intToByte(VideoPack.TYPE_VIDEO),0,tmp,0,4);
        System.arraycopy(bytes,0,tmp,4,bytes.length);
        return tmp;
    }

    public static byte[] buildVioceData(byte[] bytes){
        byte[] tmp = new byte[bytes.length+4];
        System.arraycopy(CombinValue.intToByte(VoicePack.TYPE_VOICE),0,tmp,0,4);
        System.arraycopy(bytes,0,tmp,4,bytes.length);
        return tmp;
    }

}
