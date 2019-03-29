package com.sc.lesa.mediashar;


import com.sc.lesa.mediashar.jlib.io.BytesObjectWritable;
import com.sc.lesa.mediashar.jlib.io.StreamData;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
public class TestBytesObjectWritable {
    @Test
    public void testBytesObjectWritable(){
        BytesObjectWritable bytesObjectWritable = new BytesObjectWritable(new byte[]{1,2,3,4});
        StreamData streamData = new StreamData();

        try {
            bytesObjectWritable.write(streamData.getDataOutput());
        } catch (IOException e) {
            e.printStackTrace();
        }

        BytesObjectWritable bytesObjectWritable1 = new BytesObjectWritable();

        try {
            bytesObjectWritable1.readFields(StreamData.buildDataInput(streamData.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertArrayEquals(bytesObjectWritable.bytes,bytesObjectWritable1.bytes);

    }
}
