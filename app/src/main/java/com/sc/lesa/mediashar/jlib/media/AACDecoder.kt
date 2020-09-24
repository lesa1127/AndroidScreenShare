package com.sc.lesa.mediashar.jlib.media

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *
 * @param ChannelCount 声道数
 * [AacFormat.ChannleOutOne] 或 [AacFormat.ChannleOutTwo]
 *
 * @param ByteRate 比特率 如 384000 256000 128000
 * 支持的范围 从[AacFormat.ByteRate64Kbs] 到 [AacFormat.ByteRate384Kbs]
 *
 * @param SampleRate 采样频率
 * [AacFormat.SampleRate44100] [AacFormat.SampleRate48000]
 */
class AACDecoder(val ChannelCount: Int, val ByteRate: Int,val SampleRate: Int) {

    //解码器
    private lateinit var mDecoder: MediaCodec
    private val info = MediaCodec.BufferInfo()

    //返回解码失败的次数
    //用来记录解码失败的帧数
    var count = 0
        private set

    private var onDecodeDone: OnDecodeDone? = null

    /**
     * 初始化所有变量
     * @throws Exception 初始化编码器失败
     */
    fun init() {
        prepare()
    }

    fun setOnDecodeDone(onDecodeDone: OnDecodeDone) {
        this.onDecodeDone = onDecodeDone
    }

    /**
     * 初始化解码器
     *
     * @return 初始化失败返回false，成功返回true
     */
    fun prepare(): Boolean {
        //需要解码数据的类型
        val mine = "audio/mp4a-latm"
        //初始化解码器
        mDecoder = MediaCodec.createDecoderByType(mine)
        //MediaFormat用于描述音视频数据的相关参数
        val mediaFormat = MediaFormat()
        //数据类型
        mediaFormat.setString(MediaFormat.KEY_MIME, mine)
        //声道个数
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, ChannelCount)
        //采样率
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SampleRate)
        //比特率
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, ByteRate)
        //用来标记AAC是否有adts头，1->有
        mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 1)
        //用来标记aac的类型
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        //ByteBuffer key（暂时不了解该参数的含义，但必须设置）
        val data = byteArrayOf(0x11.toByte(), 0x90.toByte())
        val csd_0 = ByteBuffer.wrap(data)
        mediaFormat.setByteBuffer("csd-0", csd_0)
        //解码器配置
        mDecoder.configure(mediaFormat, null, null, 0)

        mDecoder.start()
        return true
    }

    /**
     * aac解码
     */
    fun decode(buf: ByteArray, offset: Int, length: Int, ts: Long) {
        //等待时间，0->不等待，-1->一直等待
        val kTimeOutUs: Long = 300
        //返回一个包含有效数据的input buffer的index,-1->不存在
        val inputBufIndex = mDecoder.dequeueInputBuffer(kTimeOutUs)
        if (inputBufIndex >= 0) {
            //获取当前的ByteBuffer
            val dstBuf = mDecoder.getInputBuffer(inputBufIndex)!!
            //清空ByteBuffer
            dstBuf.clear()
            //填充数据
            dstBuf.put(buf, offset, length)
            //将指定index的input buffer提交给解码器
            mDecoder.queueInputBuffer(inputBufIndex, 0, length, ts, 0)
        }
        //编解码器缓冲区
        //返回一个output buffer的index，-1->不存在
        var outputBufferIndex = mDecoder.dequeueOutputBuffer(info, kTimeOutUs)
        if (outputBufferIndex < 0) {
            //记录解码失败的次数
            count++
        }
        var outputBuffer: ByteBuffer
        while (outputBufferIndex >= 0) {
            //获取解码后的ByteBuffer
            outputBuffer = mDecoder.getOutputBuffer(outputBufferIndex)!!
            //用来保存解码后的数据
            val outData = ByteArray(info.size)
            outputBuffer[outData]
            //清空缓存
            outputBuffer.clear()
            //播放解码后的数据
            if (onDecodeDone != null) onDecodeDone!!.onDecodeData(outData, 0, info.size)
            //释放已经解码的buffer
            mDecoder.releaseOutputBuffer(outputBufferIndex, false)
            //解码未解完的数据
            outputBufferIndex = mDecoder.dequeueOutputBuffer(info, kTimeOutUs)
        }
    }

    /**
     * 释放资源
     */
    fun stop() {
        mDecoder.stop()
        mDecoder.release()
        onDecodeDone?.onClose()
    }

    interface OnDecodeDone {
        fun onDecodeData(bytes: ByteArray?, offset: Int, len: Int)
        fun onClose()
    }


}