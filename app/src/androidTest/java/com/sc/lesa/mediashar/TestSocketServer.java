package com.sc.lesa.mediashar;


import com.sc.lesa.mediashar.jlib.server.Classifier;
import com.sc.lesa.mediashar.jlib.io.VoicePack;
import com.sc.lesa.mediashar.jlib.server.SocketClientThread;
import com.sc.lesa.mediashar.jlib.server.SocketServerThread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.io.IOException;

public class TestSocketServer {
    SocketServerThread socketServerThread;
    VoicePack voicePack1;
    MyThread myThread;
    private class MyThread extends Thread{
        boolean exit;
        @Override
        public void run() {
            while (!exit){

            }
        }
        public void close(){
            exit=true;
        }
    }

    @Before
    public void before(){
        socketServerThread = new SocketServerThread(6666);
        socketServerThread.start();

        VoicePack voicePack =new VoicePack(1,2,
                3,4,5,0,new byte[]{31,32,31});
        socketServerThread.putVoicePack(voicePack);
        socketServerThread.putVoicePack(voicePack);
        voicePack1=voicePack;

        myThread = new MyThread(){
            @Override
            public void run() {
                while (!exit) {
                    VoicePack voicePack = new VoicePack(1, 2,
                            3, 4, 5,0, new byte[]{31, 32, 31});
                    socketServerThread.putVoicePack(voicePack);
                }
            }
        };
        myThread.start();
    }

    @Test
    public void testClient(){
        SocketClientThread socketClientThread =new SocketClientThread("127.0.0.1",6666);
        try {
            socketClientThread.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketClientThread.start();
        Classifier classifier = socketClientThread.getClassifier();
        VoicePack voicePack = (VoicePack) classifier.getVoicePack();
        while (voicePack==null){
            voicePack = (VoicePack) classifier.getVoicePack();
        }
        voicePack = (VoicePack) classifier.getVoicePack();
        while (voicePack==null){
            voicePack = (VoicePack) classifier.getVoicePack();
        }
        assertArrayEquals(voicePack1.datas,voicePack.datas);

    }

    @After
    public void aftert(){
        myThread.close();
        socketServerThread.exit();
    }
}
