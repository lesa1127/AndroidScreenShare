package com.sc.lesa.mediashar.jlib.io;


import com.sc.lesa.mediashar.jlib.util.CRC;
import com.sc.lesa.mediashar.jlib.util.CombinValue;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteObjectInputStream {
    byte breaking;
    InputStream inputStream;
    BufferedInputStream bufferedInputStream;

    public ByteObjectInputStream(InputStream i){
        this(i,(byte) 0x8f);
    }

    public ByteObjectInputStream(InputStream i,byte breaking){
        this.inputStream=i;
        this.breaking=breaking;
        bufferedInputStream = new BufferedInputStream(inputStream);
    }

    public byte[] readObject() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (true) {
            byte one = (byte) bufferedInputStream.read();
            if (one == breaking) {
                byte two = (byte) bufferedInputStream.read();
                if (two == breaking) {
                    byteArrayOutputStream.write(breaking);
                } else if (two == 0) {
                    return checkCrc(byteArrayOutputStream.toByteArray());
                }else {
                    System.out.println("Other Data"+two);
                }

            } else {
                byteArrayOutputStream.write(one);
            }
        }


    }

    private byte[] checkCrc(byte[] bytes) throws IOException {
        int crcCode = CombinValue.bytesToInt(new byte[]{bytes[0],bytes[1],bytes[2],bytes[3]});
        byte[] tmp =new byte[bytes.length-4];
        System.arraycopy(bytes,4,tmp,0,bytes.length-4);
        if (CRC.getIntCRC(tmp)==crcCode){
            return tmp;
        }else {
            throw new IOException("Crc Code Error");
        }
    }

    public void close() throws IOException {
        bufferedInputStream.close();
    }

}
