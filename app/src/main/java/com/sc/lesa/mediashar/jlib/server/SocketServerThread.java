package com.sc.lesa.mediashar.jlib.server;

import android.util.Log;

import com.sc.lesa.mediashar.jlib.io.ByteObjectOutPutStream;

import com.sc.lesa.mediashar.jlib.io.StreamData;
import com.sc.lesa.mediashar.jlib.io.Writable;
import com.sc.lesa.mediashar.jlib.util.BufferList;
import com.sc.lesa.mediashar.jlib.util.TempBufferList;

import java.io.IOException;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;


public class SocketServerThread extends Thread {
    final String TAG =SocketServerThread.class.getName();
    int port;
    ServerSocket serverSocket;
    boolean exit;

    BufferList<Writable> bufferListVideo = new TempBufferList<>(100);
    BufferList<Writable> bufferListVoice= new TempBufferList<>(100);

    public SocketServerThread(int port){
        super();
        this.port=port;
    }

    Socket socket;
    OutputStream socketout;
    ByteObjectOutPutStream dataOutput;
    @Override
    public void run() {
        init();
        if (serverSocket == null )return;
        while (!exit){
            try {
                if (exit)break;
                socket =serverSocket.accept();
                if (socket==null)break;
                Log.d(TAG,"client connected");
                socketout = socket.getOutputStream();
                dataOutput = new ByteObjectOutPutStream(socketout);
                while (!exit){
                    Writable video = bufferListVideo.lastValue();
                    if (video!=null){
                        StreamData streamData =new StreamData();
                        video.write(streamData.getDataOutput());
                        byte[] tmp = Classifier.buildVideoData(streamData.getBytes());
                        dataOutput.writeObject(tmp);
                        Log.d(TAG, "has send video pack");


                    }

                    Writable voice = bufferListVoice.lastValue();
                    if (voice!=null){
                        StreamData streamData =new StreamData();
                        voice.write(streamData.getDataOutput());
                        byte[] tmp = Classifier.buildVioceData(streamData.getBytes());
                        dataOutput.writeObject(tmp);
                        Log.d(TAG, "has send voice pack");

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (socket!=null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG ,"client has disconnect");
            }
        }
        close();

    }

    public void putVoicePack(Writable writable){
        this.bufferListVoice.push(writable);
    }

    public void putVideoPack(Writable writable){
        this.bufferListVideo.push(writable);
    }

    private void init(){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void close(){
        if (serverSocket!=null){
            try {
                serverSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG,"退出完成");
    }

    public void exit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"退出中");
        this.exit = true;
    }
}
