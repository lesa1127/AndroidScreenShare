package com.sc.lesa.mediashar;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import android.util.Log;


import com.sc.lesa.mediashar.jlib.io.ByteObjectInputStream;
import com.sc.lesa.mediashar.jlib.io.VoicePack;
import com.sc.lesa.mediashar.jlib.server.SocketServerThread;
import com.sc.lesa.mediashar.jlib.util.CombinValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;



import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TestSocketServerThread {
    SocketServerThread socketServerThread;
    @Before
    public void berfor(){
         socketServerThread =new SocketServerThread(6666);
        socketServerThread.start();
    }

    @Test
    public void testSocket(){

        VoicePack voicePack =new VoicePack(1,2,
                3,4,5,0,new byte[]{31,32,31});
        socketServerThread.putVoicePack(voicePack);
        socketServerThread.putVoicePack(voicePack);

        try {
            Socket socket = new Socket("127.0.0.1",6666);

            InputStream inputStream = socket.getInputStream();
            ByteObjectInputStream byteObjectInputStream =new ByteObjectInputStream(inputStream);
            byte [] tmp=byteObjectInputStream.readObject();

            inputStream.close();
            byteObjectInputStream.close();
            socket.close();

            byte[] obj = new byte[tmp.length-4];
            System.arraycopy(tmp,4,obj,0,tmp.length-4);
            VoicePack voicePack1 = new VoicePack(obj);
            assertArrayEquals(voicePack1.datas,voicePack.datas);

            int hread = CombinValue.bytesToInt(new byte[]{tmp[0], tmp[1],tmp[2], tmp[3]
            });
            assertTrue(hread == VoicePack.TYPE_VOICE);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @After
    public void after(){
        socketServerThread.exit();
    }
}
