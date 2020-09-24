package com.sc.lesa.mediashar.jlib.threads

import android.media.AudioFormat
import android.media.AudioManager
import android.util.Log
import com.sc.lesa.mediashar.jlib.io.VoicePack
import com.sc.lesa.mediashar.jlib.media.AACDecoder
import com.sc.lesa.mediashar.jlib.media.AACDecoder.OnDecodeDone
import com.sc.lesa.mediashar.jlib.media.MyAudioTrack
import com.sc.lesa.mediashar.jlib.server.DataPackList

class VoicePlayThread(var inputdata: DataPackList) : Thread(TAG), OnDecodeDone {

    var exit = false
    lateinit var aacDecoder: AACDecoder
    lateinit var myAudioTrack: MyAudioTrack
    var hasInitVoice = false

    companion object {
        val TAG = VoicePlayThread::class.java.name
    }

    private fun initVoiceDecoder(ChannelMode: Int, EncodeFormat: Int, ChannelCount: Int,
                                 ByteRate: Int, SampleRate: Int) {
        val mChannelMode = if (ChannelMode == AudioFormat.CHANNEL_IN_MONO) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO
        myAudioTrack = MyAudioTrack(SampleRate, mChannelMode,
                EncodeFormat, AudioManager.STREAM_MUSIC)
        myAudioTrack.init()
        aacDecoder = AACDecoder(ChannelCount, ByteRate, SampleRate)
        aacDecoder.setOnDecodeDone(this)
        aacDecoder.init()
    }

    override fun run() {
        while (!exit) {
            val voicePack = inputdata.getVoicePack() as VoicePack?
            if (voicePack != null) {
                if (!hasInitVoice) {
                    initVoiceDecoder(voicePack.ChannelMode, voicePack.EncodeFormat,
                            voicePack.ChannelCount, voicePack.ByteRate, voicePack.SampleRate)
                    hasInitVoice = true

                    sleep(1000)
                    voicePack.presentationTimeUs = 0
                }
                try {
                    aacDecoder.decode(voicePack.datas, 0, voicePack.datas.size, voicePack.presentationTimeUs)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                //LogUtil.appendToFile("/sdcard/tsstaac.aac",voicePack.datas);
            }
        }
        dirtory()
    }

    private fun dirtory() {
        aacDecoder.stop()
        myAudioTrack.release()
        Log.i(TAG, "退出成功")
    }

    fun exit() {
        Log.i(TAG, "开始退出")
        exit = true
    }

    override fun onDecodeData(bytes: ByteArray?, offset: Int, len: Int) {
        myAudioTrack.playAudioTrack(bytes, offset, len)
    }

    override fun onClose() {}

}