package com.sc.lesa.mediashar.jlib.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

public class StreamData {
    public static DataInput buildDataInput(byte[] bytes){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        DataInput input =new DataInputStream(inputStream);
        return input;
    }
    ByteArrayOutputStream outputStream ;
    DataOutput output;
    public StreamData(){
        outputStream=new ByteArrayOutputStream();
        output= new DataOutputStream(outputStream);
    }
    public  DataOutput getDataOutput(){
        return output;
    }
    public byte[] getBytes(){
        return outputStream.toByteArray();
    }

}
