package com.sc.lesa.mediashar.jlib.media;


import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;


import java.io.IOException;
import java.nio.ByteBuffer;

public class AACEncoder {
    private static String TAG = AACEncoder.class.getName();

    long NOW_TIME = System.currentTimeMillis();
    private MediaCodec mEncoder;
    private MediaCodec.BufferInfo mBufferInfo;
    String MIME_TYPE="audio/mp4a-latm";

    private int channelCount=1; //声道数
    private int sampleRate=44100;//采样频率
    private int bitRate=384000;//比特率
    private final int KEY_AAC_PROFILE= MediaCodecInfo.CodecProfileLevel.AACObjectLC;

    private byte[] mFrameByte;
    private OnEncodeDone onEncodeDone;

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
    public AACEncoder(int ChannelCount, int ByteRate,int SampleRate){
        Log.d(TAG,"ChannelCount:"+ChannelCount+" ByteRate:"+ByteRate+" SampleRate:"+SampleRate);
        this.channelCount=ChannelCount;
        this.bitRate=ByteRate;
        this.sampleRate=SampleRate;
    }

    public void start(){
        init();
    }

    private void init() {
        mBufferInfo = new MediaCodec.BufferInfo();
        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE,
                    sampleRate, channelCount);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
                    KEY_AAC_PROFILE);
            mEncoder.configure(mediaFormat, null, null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE);
            mEncoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnEncodeDone(OnEncodeDone onEncodeDone){
        this.onEncodeDone=onEncodeDone;
    }

    public void encode(byte[] data) {
        int inputBufferIndex = mEncoder.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mEncoder.getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            inputBuffer.put(data);
            inputBuffer.limit(data.length);
            mEncoder.queueInputBuffer(inputBufferIndex, 0, data.length,
                    System.nanoTime(), 0);
        }

        int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex);
            //给adts头字段空出7的字节
            int length=mBufferInfo.size+7;
            if(mFrameByte==null||mFrameByte.length<length){
                mFrameByte=new byte[length];
            }

            AacAdstUtil.addADTStoPacketType(mFrameByte,AacAdstUtil.TYPE_MEPG_2,AacAdstUtil.UNUSE_CRC,
                    AacAdstUtil.AAC_LC,
                    (this.sampleRate==AacFormat.SampleRate44100)?AacAdstUtil.SAMPLING_RATE_44_1KHZ:AacAdstUtil.SAMPLING_RATE_48KHZ,
                    this.channelCount,length);
            outputBuffer.get(mFrameByte,7,mBufferInfo.size);

            if (onEncodeDone!=null)onEncodeDone.onEncodeData(mFrameByte,0,length,
                    System.currentTimeMillis()-NOW_TIME);

            mEncoder.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
        }
    }

    public interface OnEncodeDone{
        public void onEncodeData(byte[] bytes,int offset,int len,long ts);
        public void onClose();
    }




    public void release(){
        this.mEncoder.stop();
        this.mEncoder.release();
        mEncoder=null;
        this.mBufferInfo=null;
        if (onEncodeDone!=null){
            onEncodeDone.onClose();
            onEncodeDone=null;
        }
    }

}
