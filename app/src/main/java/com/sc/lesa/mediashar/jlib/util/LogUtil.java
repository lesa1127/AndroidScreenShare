package com.sc.lesa.mediashar.jlib.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogUtil {
    public static void writeLine(String path,String context){
        try {
            FileOutputStream outputStream = new FileOutputStream(path,true);
            outputStream.write((context+"\n").getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeString(String path,String context){
        try {
            FileOutputStream outputStream = new FileOutputStream(path,true);
            outputStream.write((context).getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendToFile(String path,byte[] bytes){
        try {
            FileOutputStream outputStream = new FileOutputStream(path,true);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
