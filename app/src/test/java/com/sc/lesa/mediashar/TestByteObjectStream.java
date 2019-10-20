package com.sc.lesa.mediashar;
import com.sc.lesa.mediashar.jlib.io.ByteObjectInputStream;
import com.sc.lesa.mediashar.jlib.io.ByteObjectOutputStream;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class TestByteObjectStream {
    @Test
    public void testOutPutStream(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteObjectOutputStream objectOutPutStream = new ByteObjectOutputStream(outputStream);

        byte[] object = new byte[]{1,2,3,4,(byte) 0x8f,5,6,7,8};

        try {
            objectOutPutStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] ruse = outputStream.toByteArray();
        byte[]ruse1 = new byte[ruse.length-4];

        System.arraycopy(ruse,4,ruse1,0,ruse.length-4);

        assertArrayEquals(ruse1, new byte[]{1,2,3,4,(byte) 0x8f,(byte) 0x8f,5,6,7,8,(byte) 0x8f,0});
    }

    @Test
    public void testInputStream(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteObjectOutputStream objectOutPutStream = new ByteObjectOutputStream(outputStream);

        byte[] object = new byte[]{1,2,3,4,(byte) 0x8f,5,6,7,8};

        try {
            objectOutPutStream.writeObject(object);
            objectOutPutStream.writeObject(object);
            objectOutPutStream.writeObject(object);
            objectOutPutStream.writeObject(object);
            objectOutPutStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] ruse = outputStream.toByteArray();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ruse);
        ByteObjectInputStream objectInputStream = new ByteObjectInputStream(byteArrayInputStream);

        byte[] object2 = null;
        byte[] object3 = null;
        try {
            object2 = objectInputStream.readObject();
            object3 = objectInputStream.readObject();
            object3 = objectInputStream.readObject();
            object3 = objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertArrayEquals(object,object2);
        assertArrayEquals(object,object3);

    }

    @Test
    public void testInputStream2(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteObjectOutputStream objectOutPutStream = new ByteObjectOutputStream(outputStream);



        byte[] object = new byte[9000];
        for (int i=0;i<9000;i++){
            object[i]= (byte) i;
        }

        try {
            objectOutPutStream.writeObject(object);
            objectOutPutStream.writeObject(object);
            objectOutPutStream.writeObject(object);
            objectOutPutStream.writeObject(object);
            objectOutPutStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] ruse = outputStream.toByteArray();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ruse);
        ByteObjectInputStream objectInputStream = new ByteObjectInputStream(byteArrayInputStream);

        byte[] object2 = null;
        byte[] object3 = null;
        try {
            object2 = objectInputStream.readObject();
            object3 = objectInputStream.readObject();
            object3 = objectInputStream.readObject();
            object3 = objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertArrayEquals(object,object2);
        assertArrayEquals(object,object3);

    }

}
