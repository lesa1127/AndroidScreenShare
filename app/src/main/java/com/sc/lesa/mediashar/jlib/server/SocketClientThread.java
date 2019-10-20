package com.sc.lesa.mediashar.jlib.server;


import android.util.Log;

import com.sc.lesa.mediashar.jlib.io.ByteObjectInputStream;



import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketClientThread extends Thread {
    final String TAG = SocketClientThread.class.getName();

    DataPackList dataPackList = new DataPackList();

    private Socket client;
    private boolean exit;
    SocketAddress socketAddress;
    InputStream inputStream;
    ByteObjectInputStream dataInput ;

    public SocketClientThread(String ip,int port){
        client=new Socket();
        socketAddress=new InetSocketAddress(ip,port) ;

    }

    public void connect() throws IOException {
        client.connect(socketAddress);
        Log.d(TAG,"连接成功");
    }

    public DataPackList getDataPackList() {
        return dataPackList;
    }

    @Override
    public void run() {
        while (!exit){
            try {
                inputStream=client.getInputStream();
                dataInput=new ByteObjectInputStream(inputStream);
                while (!exit){
                    byte[] bytes = null;
                    try {
                        bytes = dataInput.readObject();
                        dataPackList.putDataPack(bytes);
                    }catch (Throwable e){
                        e.printStackTrace();
                        continue;
                    }
                }
                dataInput.close();

            } catch (Throwable e) {
                e.printStackTrace();
            }finally {
                try {
                    if (inputStream!=null) inputStream.close();
                    client.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                exit=true;
                break;
            }
        }
        Log.d(TAG,"退出成功");
    }

    public void exit(){
        exit=true;
        super.interrupt();
    }
}
