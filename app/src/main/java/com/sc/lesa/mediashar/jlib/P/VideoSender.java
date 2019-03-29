package com.sc.lesa.mediashar.jlib.P;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.projection.MediaProjection;
import android.util.Log;

import com.sc.lesa.mediashar.jlib.io.VideoPack;
import com.sc.lesa.mediashar.jlib.media.Encoder;
import com.sc.lesa.mediashar.jlib.media.MediaReader;
import com.sc.lesa.mediashar.jlib.server.SocketServerThread;


public class VideoSender implements Encoder.EncoderListener {
    final String TAG =VideoSender.class.getName();

    SocketServerThread socketServerThread;
    Context context;
    int width;
    int height;
    int videoBitrate;
    int videoFrameRate;

    MediaReader mediaReader;
    public VideoSender(SocketServerThread st, Context context, MediaProjection mp){
        socketServerThread=st;
        this.context=context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "videoparm",Context.MODE_PRIVATE);

        this.width=sharedPreferences.getInt("width",1920);
        this.height=sharedPreferences.getInt("height",1080);
        this.videoBitrate=sharedPreferences.getInt("videoBitrate",16777216);
        this.videoFrameRate=sharedPreferences.getInt("videoFrameRate",24);

        this.mediaReader =new MediaReader(width,height,videoBitrate,
                videoFrameRate,this,mp);
        mediaReader.startEncode();
    }

    public static void setParam(Context context,int width,int height,int videoBitrate,int videoFrameRate){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "videoparm",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("width",width);
        editor.putInt("height",height);
        editor.putInt("videoBitrate",videoBitrate);
        editor.putInt("videoFrameRate",videoFrameRate);
        editor.commit();

    }

    @Override
    public void onH264(byte[] buffer, int type, long ts) {

        byte[] datas=new byte[buffer.length];
        System.arraycopy(buffer,0,datas,0,buffer.length);
        VideoPack pack = new VideoPack(datas,width,height,videoBitrate,
                videoFrameRate,type,ts);
        if (socketServerThread!=null)socketServerThread.putVideoPack(pack);


    }
    public void close(){
        Log.d(TAG,"正在退出");
        mediaReader.releaseMediaCodec();
    }

    @Override
    public void onCloseH264() {
        mediaReader.quit();
        mediaReader=null;
        context=null;
        socketServerThread=null;
        Log.d(TAG,"退出完成");
    }
}
