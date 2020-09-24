package com.sc.lesa.mediashar.jlib.server

import com.sc.lesa.mediashar.jlib.io.*
import java.util.concurrent.LinkedBlockingQueue


class DataPackList {

    private val bufferListVideo: LinkedBlockingQueue<Writable> = LinkedBlockingQueue(120)
    private val bufferListVoice: LinkedBlockingQueue<Writable> = LinkedBlockingQueue(120)

    fun getVideoPack(): Writable? {
        val tmp = bufferListVideo.peek()
        tmp?.also {
            bufferListVideo.remove(it)
        }
        return tmp
    }

    fun getVoicePack(): Writable? {
        val tmp = bufferListVoice.peek()
        tmp?.also {
            bufferListVoice.remove(it)
        }
        return tmp
    }


    fun putDataPack(_object: ByteArray) {
        val tmp = DataInputStreamBuffer(_object)
        val dataPack = DataPack(tmp)
        tmp.close()
        if (dataPack.isVideoPack()){
            bufferListVideo.offer(VideoPack(dataPack.byteArray))
        }else if (dataPack.isVoicePack()){
            bufferListVoice.offer(VoicePack(dataPack.byteArray))
        }
    }


}