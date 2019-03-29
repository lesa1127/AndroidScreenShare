package com.sc.lesa.mediashar.jlib.io;

import com.sc.lesa.mediashar.jlib.util.CRC;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BytesObjectWritable implements Writable{
    public byte[] bytes;
    public int crcCode;

    public BytesObjectWritable(){}
    public BytesObjectWritable(byte[] bytes){
        this.bytes=bytes;
        this.crcCode=CRC.getIntCRC(this.bytes);
    }

    public static BytesObjectWritable buildBytesObjectWritable(byte[] bytes) throws IOException {
        BytesObjectWritable bytesObjectWritable = new BytesObjectWritable();
        bytesObjectWritable.readFields(StreamData.buildDataInput(bytes));
        return bytesObjectWritable;
    }

    @Override
    public void write(DataOutput var1) throws IOException {
        var1.writeInt(crcCode);
        var1.writeInt(bytes.length);
        var1.write(bytes);

    }

    @Override
    public void readFields(DataInput var1) throws IOException {
        this.crcCode=var1.readInt();
        int len = var1.readInt();
        this.bytes = new byte[len];
        for (int i = 0;i<len;i++){
            this.bytes[i]=var1.readByte();
        }
        if(crcCode!=CRC.getIntCRC(bytes)){
            throw new IOException("CRC检验出错");
        }

    }
}
