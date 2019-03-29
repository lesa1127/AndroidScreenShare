package com.sc.lesa.mediashar.jlib.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AACDecoder{
    private static final String TAG = AACDecoder.class.getName();


    //解码器
    private MediaCodec mDecoder;
    //用来记录解码失败的帧数
    private int count = 0;
    private OnDecodeDone onDecodeDone;


    private int KEY_CHANNEL_COUNT=1; //声道数
    private int KEY_SAMPLE_RATE=44100;//采样频率
    private int KEY_BIT_RATE=384000;//比特率

    /**
     *
     * @param ChannelCount 声道数
     * {@link AacFormat#ChannleOutOne} 或 {@link AacFormat#ChannleOutTwo}
     *
     * @param ByteRate 比特率 如 384000 256000 128000
     * 支持的范围 从{@link AacFormat#ByteRate64Kbs} 到 {@link AacFormat#ByteRate384Kbs}
     *
     * @param SampleRate 采样频率
     * {@link AacFormat#SampleRate44100} {@link AacFormat#SampleRate48000}
     */
    public AACDecoder(int ChannelCount, int ByteRate,int SampleRate){
        this.KEY_CHANNEL_COUNT=ChannelCount;
        this.KEY_BIT_RATE=ByteRate;
        this.KEY_SAMPLE_RATE=SampleRate;
    }

    /**
     * 初始化所有变量
     */
    public void start() {
        prepare();
    }

    public void setOnDecodeDone(OnDecodeDone onDecodeDone) {
        this.onDecodeDone = onDecodeDone;
    }

    /**
     * 初始化解码器
     *
     * @return 初始化失败返回false，成功返回true
     */
    public boolean prepare() {

        try {
            //需要解码数据的类型
            String mine = "audio/mp4a-latm";
            //初始化解码器
            mDecoder = MediaCodec.createDecoderByType(mine);
            //MediaFormat用于描述音视频数据的相关参数
            MediaFormat mediaFormat = new MediaFormat();
            //数据类型
            mediaFormat.setString(MediaFormat.KEY_MIME, mine);
            //声道个数
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, KEY_CHANNEL_COUNT);
            //采样率
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, KEY_SAMPLE_RATE);
            //比特率
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
            //用来标记AAC是否有adts头，1->有
            mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 1);
            //用来标记aac的类型
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            //ByteBuffer key（暂时不了解该参数的含义，但必须设置）
            byte[] data = new byte[]{(byte) 0x11, (byte) 0x90};
            ByteBuffer csd_0 = ByteBuffer.wrap(data);
            mediaFormat.setByteBuffer("csd-0", csd_0);
            //解码器配置
            mDecoder.configure(mediaFormat, null, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (mDecoder == null) {
            return false;
        }
        mDecoder.start();
        return true;
    }

    /**
     * aac解码
     */
    public void decode(byte[] buf, int offset, int length,long ts) {
        //等待时间，0->不等待，-1->一直等待
        long kTimeOutUs = 0;
        try {
            //返回一个包含有效数据的input buffer的index,-1->不存在
            int inputBufIndex = mDecoder.dequeueInputBuffer(kTimeOutUs);
            if (inputBufIndex >= 0) {
                //获取当前的ByteBuffer
                ByteBuffer dstBuf = mDecoder.getInputBuffer(inputBufIndex);
                //清空ByteBuffer
                dstBuf.clear();
                //填充数据
                dstBuf.put(buf, offset, length);
                //将指定index的input buffer提交给解码器
                mDecoder.queueInputBuffer(inputBufIndex, 0, length, ts, 0);
            }
            //编解码器缓冲区
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            //返回一个output buffer的index，-1->不存在
            int outputBufferIndex = mDecoder.dequeueOutputBuffer(info, kTimeOutUs);

            if (outputBufferIndex < 0) {
                //记录解码失败的次数
                count++;
            }
            ByteBuffer outputBuffer;
            while (outputBufferIndex >= 0) {
                //获取解码后的ByteBuffer
                outputBuffer = mDecoder.getOutputBuffer(outputBufferIndex);
                //用来保存解码后的数据
                byte[] outData = new byte[info.size];
                outputBuffer.get(outData);
                //清空缓存
                outputBuffer.clear();
                //播放解码后的数据
                if(this.onDecodeDone!=null)this.onDecodeDone.onDecodeData(outData, 0, info.size);
                //释放已经解码的buffer
                mDecoder.releaseOutputBuffer(outputBufferIndex, false);
                //解码未解完的数据
                outputBufferIndex = mDecoder.dequeueOutputBuffer(info, kTimeOutUs);
            }
        } catch (Throwable e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    //返回解码失败的次数
    public int getCount() {
        return count;
    }

    /**
     * 释放资源
     */
    public void stop() {
        try {
            if (mDecoder != null) {
                mDecoder.stop();
                mDecoder.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.onDecodeDone!=null){
            this.onDecodeDone.close();
            this.onDecodeDone=null;
        }
    }
    public interface OnDecodeDone{
        public void onDecodeData(byte[] bytes,int offset,int len);
        public void close();
    }

}
