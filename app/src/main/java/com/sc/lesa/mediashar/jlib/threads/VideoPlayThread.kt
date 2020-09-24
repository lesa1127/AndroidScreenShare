package com.sc.lesa.mediashar.jlib.threads

import android.util.Log
import android.view.Surface
import com.sc.lesa.mediashar.jlib.io.VideoPack
import com.sc.lesa.mediashar.jlib.media.Decoder
import com.sc.lesa.mediashar.jlib.server.DataPackList

class VideoPlayThread(var surface: Surface, var inputdata: DataPackList) : Thread(TAG) {
    var exit = false
    var hasInitVideo = false
    lateinit var videodecoder: Decoder

    companion object {
        val TAG = VideoPlayThread::class.java.name
    }


    private fun initVideoDecoder(width: Int, height: Int, videoBitrate: Int, videoFrameRate: Int) {
        videodecoder = Decoder(width, height, videoFrameRate, surface)
        videodecoder.init()

    }

    override fun run() {
        while (!exit) {
            val videoPack:VideoPack? = inputdata.getVideoPack() as VideoPack?
            if (videoPack != null) {
                if (!hasInitVideo) {
                    Log.d(TAG, "video pack init $videoPack")
                    initVideoDecoder(videoPack.width, videoPack.height,
                            videoPack.videoBitrate, videoPack.videoFrameRate)
                    hasInitVideo = true
                    try {
                        sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                videodecoder.onFrame(videoPack.frames, 0, videoPack.frames.size, videoPack.presentationTimeUs)
            }
        }
        dirtory()
    }

    private fun dirtory() {
        videodecoder.release()
        Log.i(TAG, "退出成功")
    }

    fun exit() {
        Log.i(TAG, "开始退出")
        exit = true
    }
}