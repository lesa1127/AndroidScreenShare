package com.sc.lesa.mediashar.jlib.server;

import android.util.Log;

import com.sc.lesa.mediashar.jlib.io.ByteObjectOutputStream;

import com.sc.lesa.mediashar.jlib.io.DataOutputStreamBuffer;

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
    ByteObjectOutputStream dataOutput;
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (serverSocket == null )return;
        while (!exit){
            try {
                socket =serverSocket.accept();
                Log.d(TAG,"client connected");
                socketout = socket.getOutputStream();
                dataOutput = new ByteObjectOutputStream(socketout);
                while (!exit){
                    Writable video = bufferListVideo.lastValue();
                    if (video!=null){
                        byte[] tmp = DataPackList.Companion.buildVideoPack(video);
                        dataOutput.writeObject(tmp);

                        Log.d(TAG, "has send video pack");


                    }

                    Writable voice = bufferListVoice.lastValue();
                    if (voice!=null){
                        byte[] tmp = DataPackList.Companion.buildVoicePack(voice);
                        dataOutput.writeObject(tmp);

                        Log.d(TAG, "has send voice pack");

                    }

                }
                dataOutput.close();

            } catch (Throwable e) {
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
        if (bufferListVoice.size()<100)
        this.bufferListVoice.push(writable);
    }

    public void putVideoPack(Writable writable){
        if (bufferListVideo.size()<100)
            this.bufferListVideo.push(writable);
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
        Log.d(TAG,"退出中");
        this.exit = true;
        super.interrupt();
    }
}
