package com.sc.lesa.mediashar.jlib.io;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

public class DataInputStreamBuffer implements DataInput {
    private ByteArrayInputStream byteArrayInputStream;
    private DataInputStream dataInputStream;

    public DataInputStreamBuffer(byte[] data){
        byteArrayInputStream = new ByteArrayInputStream(data);
        dataInputStream = new DataInputStream(byteArrayInputStream);
    }

    @Override
    public void readFully(byte[] bytes) throws IOException {
        dataInputStream.readFully(bytes);
    }

    @Override
    public void readFully(byte[] bytes, int i, int i1) throws IOException {
        dataInputStream.readFully(bytes,i,i1);
    }

    @Override
    public int skipBytes(int i) throws IOException {
        return dataInputStream.skipBytes(i);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return dataInputStream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return dataInputStream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return dataInputStream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return dataInputStream.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return dataInputStream.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return dataInputStream.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return dataInputStream.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return dataInputStream.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return dataInputStream.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return dataInputStream.readDouble();
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return dataInputStream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return dataInputStream.readUTF();
    }

    public void close()throws IOException{
        dataInputStream.close();
        byteArrayInputStream.close();
    }
}
