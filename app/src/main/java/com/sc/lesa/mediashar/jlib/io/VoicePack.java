package com.sc.lesa.mediashar.jlib.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VoicePack implements Writable {
    public static final int TYPE_VOICE=45445;

    public byte[] datas;
    public int ChannelMode,EncodeFormat,ChannelCount,ByteRate, SampleRate;
    public long presentationTimeUs;

    public VoicePack(){}

    public VoicePack(byte[] bytes) throws IOException {
        DataInputStreamBuffer input = new DataInputStreamBuffer(bytes);
        readFields(input);
        input.close();
    }

    public VoicePack(int ChannelMode,int EncodeFormat,int ChannelCount,
                     int ByteRate,int SampleRate,long presentationTimeUs,byte[] bytes){
        this.ChannelMode=ChannelMode;
        this.EncodeFormat=EncodeFormat;
        this.ChannelCount=ChannelCount;
        this.ByteRate=ByteRate;
        this.SampleRate=SampleRate;
        this.datas=bytes;
        this.presentationTimeUs=presentationTimeUs;
    }


    @Override
    public void write(DataOutput var1) throws IOException {
        var1.writeInt(ChannelMode);
        var1.writeInt(EncodeFormat);
        var1.writeInt(ChannelCount);
        var1.writeInt(ByteRate);
        var1.writeInt(SampleRate);
        var1.writeLong(presentationTimeUs);
        var1.writeInt(datas.length);
        var1.write(datas);


    }

    @Override
    public void readFields(DataInput var1) throws IOException {
        this.ChannelMode=var1.readInt();
        this.EncodeFormat=var1.readInt();
        this.ChannelCount=var1.readInt();
        this.ByteRate=var1.readInt();
        this.SampleRate=var1.readInt();
        presentationTimeUs=var1.readLong();
        int len =var1.readInt();
        this.datas=new byte[len];
        for (int i =0;i<len;i++){
            datas[i]=var1.readByte();
        }
    }
}
