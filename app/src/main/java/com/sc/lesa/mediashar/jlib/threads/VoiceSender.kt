package com.sc.lesa.mediashar.jlib.threads

import android.media.MediaRecorder
import android.util.Log
import com.sc.lesa.mediashar.jlib.io.VoicePack
import com.sc.lesa.mediashar.jlib.media.AACEncoder
import com.sc.lesa.mediashar.jlib.media.AACEncoder.OnEncodeDone
import com.sc.lesa.mediashar.jlib.media.MyAudioRecord
import com.sc.lesa.mediashar.jlib.media.MyAudioRecord.OnDataInput
import com.sc.lesa.mediashar.jlib.server.SocketServerThread

/**
 *
 * @param s 发送线程
 * @param ChannelMode [AudioFormat.CHANNEL_IN_MONO] 或 [AudioFormat.CHANNEL_IN_STEREO]
 * @param EncodeFormat [AudioFormat.ENCODING_PCM_16BIT]
 * @param ChannelCount [AacFormat.ChannleOutOne]
 * @param ByteRate [AacFormat.ByteRate256Kbs]
 * @param SampleRate [AacFormat.SampleRate44100]
 */
class VoiceSender(var socketServer: SocketServerThread,
                  var ChannelMode: Int, var EncodeFormat: Int,
                  var ChannelCount: Int, var ByteRate: Int,
                  var SampleRate: Int
) : OnEncodeDone, OnDataInput {

    val TAG = VoiceSender::class.java.name
    private val aacEncoder: AACEncoder = AACEncoder(ChannelCount, ByteRate, SampleRate)
    private val  myAudioRecord = MyAudioRecord(MediaRecorder.AudioSource.MIC,
            SampleRate, ChannelMode, EncodeFormat)

    override fun onEncodeData(bytes: ByteArray, offset: Int, len: Int, ts: Long) {
        val voicePack = VoicePack(ChannelMode, EncodeFormat, ChannelCount,
                ByteRate, SampleRate, ts, bytes)
        socketServer.putVoicePack(voicePack)
        //LogUtil.appendToFile("/sdcard/tsstaac.aac",voicePack.datas);
    }

    fun exit() {
        Log.d(TAG, "退出中")
        myAudioRecord.release()
    }

    override fun onClose() {
        Log.d(TAG, "退出完成")
    }

    override fun inputData(bytes: ByteArray, offset: Int, len: Int) {
        val bytes1 = ByteArray(len)
        System.arraycopy(bytes, offset, bytes1, 0, len)
        aacEncoder.encode(bytes1)
    }

    override fun release() {
        aacEncoder.release()
    }


    init {
        aacEncoder.setOnEncodeDone(this)
        aacEncoder.init()
        myAudioRecord.setOnDataInput(this)
        myAudioRecord.init()
        myAudioRecord.start()
    }
}