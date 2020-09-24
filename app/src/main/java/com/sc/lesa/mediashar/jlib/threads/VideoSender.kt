package com.sc.lesa.mediashar.jlib.threads

import android.media.projection.MediaProjection
import android.util.Log
import com.sc.lesa.mediashar.jlib.io.VideoPack
import com.sc.lesa.mediashar.jlib.media.Encoder.EncoderListener
import com.sc.lesa.mediashar.jlib.media.MediaReader
import com.sc.lesa.mediashar.jlib.server.SocketServerThread

/**
 *
 * @param st 发送线程
 * @param mp  MediaProjection
 * @param width 视频宽度 1080
 * @param height 视频高度 1920
 * @param videoBitrate 视频 比特率  16777216
 * @param videoFrameRate 视频 帧率 24
 */
class VideoSender(var socketServerThread: SocketServerThread, mp: MediaProjection,
                  var width: Int, var height: Int,
                  var videoBitrate: Int, var videoFrameRate: Int
) : EncoderListener {
    val TAG = VideoSender::class.java.name
    var mediaReader: MediaReader = MediaReader(width, height, videoBitrate,
            videoFrameRate, this, mp)

    override fun onH264(buffer: ByteArray, type: Int, ts: Long) {
        val datas = ByteArray(buffer.size)
        System.arraycopy(buffer, 0, datas, 0, buffer.size)
        val pack = VideoPack(datas, width, height, videoBitrate,
                videoFrameRate, type, ts)
        socketServerThread.putVideoPack(pack)
    }

    override fun onError(t: Throwable) {

    }

    fun exit() {
        Log.d(TAG, "正在退出")
        mediaReader.exit()
    }

    override fun onCloseH264() {
        Log.d(TAG, "退出完成")
    }


    init {
        mediaReader.init()
        mediaReader.start()
    }
}