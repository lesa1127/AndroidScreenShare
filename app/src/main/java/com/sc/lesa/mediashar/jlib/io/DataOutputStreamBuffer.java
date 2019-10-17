package com.sc.lesa.mediashar.jlib.io;


import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataOutputStreamBuffer implements DataOutput {
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private DataOutputStream dataOutputStream ;

    public DataOutputStreamBuffer() {
        super();
        dataOutputStream=new DataOutputStream(byteArrayOutputStream);
    }


    @Override
    public void write(int i) throws IOException {
        dataOutputStream.write(i);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        dataOutputStream.write(bytes);
    }

    @Override
    public void write(byte[] bytes, int i, int i1) throws IOException {
        dataOutputStream.write(bytes,i,i1);
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        dataOutputStream.writeBoolean(b);
    }

    @Override
    public void writeByte(int i) throws IOException {
        dataOutputStream.writeByte(i);
    }

    @Override
    public void writeShort(int i) throws IOException {
        dataOutputStream.writeShort(i);
    }

    @Override
    public void writeChar(int i) throws IOException {
        dataOutputStream.writeChar(i);
    }

    @Override
    public void writeInt(int i) throws IOException {
        dataOutputStream.writeInt(i);
    }

    @Override
    public void writeLong(long l) throws IOException {
        dataOutputStream.writeLong(l);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        dataOutputStream.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        dataOutputStream.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        dataOutputStream.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        dataOutputStream.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        dataOutputStream.writeUTF(s);
    }

    public void close() throws IOException {
        dataOutputStream.close();
        byteArrayOutputStream.close();
    }

    public void flush() throws IOException {
        dataOutputStream.flush();
        byteArrayOutputStream.flush();
    }

    public byte[] toByteArray(){
        return byteArrayOutputStream.toByteArray();
    }

}
