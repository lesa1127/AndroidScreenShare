package com.sc.lesa.mediashar.jlib.server

import com.sc.lesa.mediashar.jlib.io.*
import com.sc.lesa.mediashar.jlib.util.BufferList
import com.sc.lesa.mediashar.jlib.util.TempBufferList

class DataPackList {

    private val bufferListVideo: BufferList<Writable> = TempBufferList(120)
    private val bufferListVoice: BufferList<Writable> = TempBufferList(120)

    fun getVideoPack(): Writable? {
        return bufferListVideo.lastValue()
    }

    fun getVoicePack(): Writable? {
        return bufferListVoice.lastValue()
    }


    fun putDataPack(_object: ByteArray) {
        val tmp = DataInputStreamBuffer(_object)
        val dataPack = DataPack(tmp)
        tmp.close()
        if (dataPack.isVideoPack()){
            bufferListVideo.push(VideoPack(dataPack.byteArray))
        }else if (dataPack.isVoicePack()){
            bufferListVoice.push(VoicePack(dataPack.byteArray))
        }
    }

    companion object{
        fun buildVideoPack(writable:Writable):ByteArray{
            var out = DataOutputStreamBuffer()
            writable.write(out)
            val pack = DataPack(DataPack.TYPE_VIDEO,out.toByteArray())
            out.close()

            out= DataOutputStreamBuffer()
            pack.write(out)

            val tmp = out.toByteArray()
            out.close()
            return tmp
        }

        fun buildVoicePack(writable:Writable):ByteArray{
            var out = DataOutputStreamBuffer()
            writable.write(out)
            val pack = DataPack(DataPack.TYPE_VOICE,out.toByteArray())
            out.close()

            out= DataOutputStreamBuffer()
            pack.write(out)

            val tmp = out.toByteArray()
            out.close()
            return tmp
        }
    }
}