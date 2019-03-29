package com.sc.lesa.mediashar.jlib.P;

import android.util.Log;
import android.view.Surface;

import com.sc.lesa.mediashar.jlib.io.VideoPack;

import com.sc.lesa.mediashar.jlib.media.Decoder;
import com.sc.lesa.mediashar.jlib.server.Classifier;


public class VideoPlayThread extends Thread {
    public final static String TAG = VideoPlayThread.class.getName();
    boolean exit;
    Surface surface;
    boolean hasInitVideo;

    Classifier inputdata ;
    Decoder videodecoder;


    public VideoPlayThread(Surface surface,Classifier classifier){
        this.surface=surface;
        this.inputdata=classifier;
    }

    private void initVideoDecoder(int width,int height,int videoBitrate,int videoFrameRate){
        videodecoder=new Decoder(width,height,videoFrameRate,this.surface);
    }

    @Override
    public void run() {
        while (!exit){
            VideoPack videoPack = (VideoPack) inputdata.getVideoPack();
            if (videoPack!=null){
                if (hasInitVideo==false){
                    Log.d(TAG,"video pack init "+videoPack.toString());
                    initVideoDecoder(videoPack.width,videoPack.height,
                            videoPack.videoBitrate,videoPack.videoFrameRate);
                    hasInitVideo=true;
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    videoPack.presentationTimeUs=0;
                }
                videodecoder.onFrame(videoPack.frames, 0, videoPack.frames.length,videoPack.presentationTimeUs);

            }

        }
        dirtory();
    }
    private void dirtory(){
        videodecoder.release();
        surface=null;
        inputdata=null;
        videodecoder=null;
        Log.i(TAG,"退出成功");
    }

    public void exit(){
        Log.i(TAG,"开始退出");
        this.exit=true;
    }
}
