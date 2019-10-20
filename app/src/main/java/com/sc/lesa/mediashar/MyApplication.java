package com.sc.lesa.mediashar;

import android.app.Application;
import android.media.projection.MediaProjection;

public class MyApplication extends Application {
    static MediaProjection mediaProjection;

    public static void setMediaProjection(MediaProjection mediaProjection){
        MyApplication.mediaProjection=mediaProjection;
    }

    public static MediaProjection getMediaProjection() throws Exception{
        if (mediaProjection==null){
            throw new Exception("MediaProjection Not Set!");
        }
        return mediaProjection;
    }

}
