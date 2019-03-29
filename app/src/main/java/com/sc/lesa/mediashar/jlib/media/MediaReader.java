package com.sc.lesa.mediashar.jlib.media;


import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;



/**
 * Created by Lesa on 2018/12/03.
 */
public class MediaReader  extends Encoder {
    private static final String TAG = "MediaReader";

    protected int mWidth;//方向宽度
    protected int mHeight;//方向高度
    protected int videoBitrate;
    protected int videoFrameRate;

    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private int mDpi = 1;
    private Surface mSurface;
    private VirtualDisplay mVirtualDisplay;


    public MediaReader(int width, int height,int videoBitrate, int videoFrameRate, EncoderListener encoderListener, MediaProjection mp) {
        super(width, height, videoBitrate, videoFrameRate, encoderListener);
        mMediaProjection = mp;

        this.mWidth=width;
        this.mHeight=height;

        this.videoBitrate=videoBitrate;
        this.videoFrameRate=videoFrameRate;

        initVirtualDisplay();

    }

    /**
     * stop task
     */
    public final void quit() {
        release();
    }

    public void startEncode() {
        super.start();
    }

    private void initVirtualDisplay() {
        mSurface = super.getmSurface();
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mSurface, null, null);
        Log.d(TAG, "created virtual display: " + mVirtualDisplay);
    }

    private void release() {
        if(mMediaProjection!= null){
            mMediaProjection.stop();
            mMediaProjection=null;
        }
        if(mVirtualDisplay!=null){
            mVirtualDisplay.release();
            mVirtualDisplay=null;
        }
        if (mSurface!= null){
            mSurface.release();
            mSurface=null;
        }
    }

}
