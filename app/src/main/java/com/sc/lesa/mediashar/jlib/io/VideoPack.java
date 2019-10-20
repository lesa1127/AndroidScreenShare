package com.sc.lesa.mediashar.jlib.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VideoPack implements Writable{
    public static final int TYPE_VIDEO=6546;

    public byte [] frames;
    public int width,height,videoBitrate,videoFrameRate,framestype;
    public long presentationTimeUs;

    public VideoPack(){}

    public VideoPack(byte[] bytes) throws IOException {
        DataInputStreamBuffer input = new DataInputStreamBuffer(bytes);
        readFields(input);
        input.close();
    }

    public VideoPack(byte[] frames , int width, int height,int videoBitrate, int videoFrameRate,
                     int framestype,long presentationTimeUs){
        this.frames=frames;
        this.width=width;
        this.height=height;
        this.videoBitrate=videoBitrate;
        this.videoFrameRate=videoFrameRate;
        this.framestype=framestype;
        this.presentationTimeUs=presentationTimeUs;
    }


    @Override
    public void write(DataOutput var1) throws IOException {

        var1.writeInt(width);
        var1.writeInt(height);
        var1.writeInt(videoBitrate);
        var1.writeInt(videoFrameRate);
        var1.writeInt(framestype);
        var1.writeLong(presentationTimeUs);
        var1.writeInt(frames.length);
        var1.write(frames);
    }

    @Override
    public void readFields(DataInput var1) throws IOException {

        width=var1.readInt();
        height=var1.readInt();
        videoBitrate=var1.readInt();
        videoFrameRate=var1.readInt();
        framestype=var1.readInt();
        presentationTimeUs=var1.readLong();
        int len = var1.readInt();
        frames =new byte[len];
        for (int i =0;i<len;i++){
            frames[i]=var1.readByte();
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("width:");
        buffer.append(width);
        buffer.append(" height:");
        buffer.append(height);
        buffer.append(" presentationTimeUs:");
        buffer.append(presentationTimeUs);
        return buffer.toString();
    }
}
