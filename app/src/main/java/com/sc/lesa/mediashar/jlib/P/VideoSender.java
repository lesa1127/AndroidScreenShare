package com.sc.lesa.mediashar.jlib.P;


import android.media.projection.MediaProjection;
import android.util.Log;

import com.sc.lesa.mediashar.jlib.io.VideoPack;
import com.sc.lesa.mediashar.jlib.media.Encoder;
import com.sc.lesa.mediashar.jlib.media.MediaReader;
import com.sc.lesa.mediashar.jlib.server.SocketServerThread;


public class VideoSender implements Encoder.EncoderListener {
    final String TAG =VideoSender.class.getName();

    SocketServerThread socketServerThread;

    int width;
    int height;
    int videoBitrate;
    int videoFrameRate;

    MediaReader mediaReader;

    /**
     *
     * @param st 发送线程
     * @param mp  MediaProjection
     * @param width 视频宽度 1080
     * @param height 视频高度 1920
     * @param videoBitrate 视频 比特率  16777216
     * @param videoFrameRate 视频 帧率 24
     */
    public VideoSender(SocketServerThread st, MediaProjection mp,
                       int width,int height,
                       int videoBitrate,int videoFrameRate
    ){

        socketServerThread=st;
        this.width=width;
        this.height=height;
        this.videoBitrate=videoBitrate;
        this.videoFrameRate=videoFrameRate;

        this.mediaReader =new MediaReader(width,height,videoBitrate,
                videoFrameRate,this,mp);
        mediaReader.startEncode();
    }

    @Override
    public void onH264(byte[] buffer, int type, long ts) {
        //Log.d(TAG,"h264 encode :"+buffer.length);
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
        socketServerThread=null;
        Log.d(TAG,"退出完成");
    }
}
