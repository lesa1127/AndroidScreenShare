package com.sc.lesa.mediashar.jlib.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;


public class VoiceEncoder extends Thread{

    public static final int AudioChannelModeOne = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    public static final int AudioChannelModeTwo = AudioFormat.CHANNEL_IN_STEREO;

    public static final int ChannelCountOne = 1;
    public static final int ChannelCountTwo = 2;

    public static final int ENCODING_PCM_16BIT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int ENCODING_PCM_8BIT = AudioFormat.ENCODING_PCM_8BIT;

    public static final int ByteRate64Kbps = 64000;
    public static final int ByteRate128Kbps = 128000;
    public static final int ByteRate256Kbps = 256000;
    public static final int ByteRate384Kbps = 384000;

    public static final int SampleRate_48000 = 48000;
    public static final int SampleRate_44100 = 44100;

    String MIME_TYPE="audio/mp4a-latm";
    private static String TAG = "VEncoder";

    AudioRecord mRecord = null;
    boolean mReqStop = false;


    private int kChannelMode = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int kEncodeFormat = AudioFormat.ENCODING_PCM_16BIT;

    private final int kFrameSize = 2048;//帧buffer 大小

    OnDataOutPut onDataOutPut;

    private MediaCodec mEncoder;
    MediaCodec.BufferInfo mBufferInfo;
    private int KEY_CHANNEL_COUNT=1; //声道数
    private int KEY_SAMPLE_RATE=44100;//采样频率
    private int KEY_BIT_RATE=384000;//比特率
    private final int KEY_AAC_PROFILE= MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    private byte[] mFrameByte;


    public VoiceEncoder(OnDataOutPut o){
        super(TAG);
        this.onDataOutPut=o;
        init();
    }

    public VoiceEncoder(OnDataOutPut o,final int ChannelMode,final int EncodeFormat,int ChannelCount,
                        int ByteRate,int SampleRate){
        super(TAG);
        this.onDataOutPut=o;
        this.kChannelMode=ChannelMode;
        this.kEncodeFormat=EncodeFormat;
        this.KEY_CHANNEL_COUNT=ChannelCount;
        this.KEY_BIT_RATE=ByteRate;
        this.KEY_SAMPLE_RATE=SampleRate;
        init();
    }

    private void init() {
        mBufferInfo = new MediaCodec.BufferInfo();
        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE,
                    KEY_SAMPLE_RATE, KEY_CHANNEL_COUNT);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
                    KEY_AAC_PROFILE);
            mEncoder.configure(mediaFormat, null, null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE);
            mEncoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int minBufferSize = AudioRecord.getMinBufferSize(KEY_SAMPLE_RATE, kChannelMode,
                kEncodeFormat);
        mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                KEY_SAMPLE_RATE, kChannelMode, kEncodeFormat, minBufferSize * 2);
    }



    @Override
    public void run() {

        mRecord.startRecording();

        byte[] buffer = new byte[kFrameSize];
        int num = 0;
        while (!mReqStop) {
            num = mRecord.read(buffer, 0, kFrameSize);
            encode(buffer);
            Log.d(TAG, "buffer len " + ", num = " + num);
        }

        Log.d(TAG, "exit loop");

        distory();
        Log.d(TAG, "clean up");
    }
    private void distory(){
        if (mRecord!=null){
            mRecord.stop();
            mRecord.release();
            mRecord = null;
        }
        if (mEncoder!=null){
            mEncoder.stop();
            mEncoder.release();
            mEncoder=null;
        }
    }
    public void close() {
        mReqStop = true;
    }

    private void encode(byte[] data) {
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
            //addADTStoPacket(mFrameByte,length);
            addADTStoPacketType(mFrameByte,TYPE_MEPG_2,UNUSE_CRC,AAC_LC,
                    (this.KEY_SAMPLE_RATE==SampleRate_44100)?SAMPLING_RATE_44_1KHZ:SAMPLING_RATE_48KHZ,
                    this.KEY_CHANNEL_COUNT,length);
            outputBuffer.get(mFrameByte,7,mBufferInfo.size);
            if (onDataOutPut!=null)onDataOutPut.onDataOutPut(mFrameByte);

            mEncoder.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
        }
    }

    /**
     * 给编码出的aac裸流添加adts头字段
     * @param packet 要空出前7个字节，否则会搞乱数据
     * @param packetLen
     */
    public static void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 2;  //CPE
        packet[0] = (byte)0xFF;
        packet[1] = (byte)0xF9;
        packet[2] = (byte)(((profile-1)<<6) + (freqIdx<<2) +(chanCfg>>2));
        packet[3] = (byte)(((chanCfg&3)<<6) + (packetLen>>11));
        packet[4] = (byte)((packetLen&0x7FF) >> 3);
        packet[5] = (byte)(((packetLen&7)<<5) + 0x1F);
        packet[6] = (byte)0xFC;
    }

    public static final boolean TYPE_MEPG_4 = false;
    public static final boolean TYPE_MEPG_2 = true;
    public static final boolean USE_CRC = false;
    public static final boolean UNUSE_CRC = true;
    public static final int AAC_MAIN = 1;
    public static final int AAC_LC = 2;
    public static final int AAC_SSR = 3;
    public static final int AAC_LTP = 4;

    public static final int SAMPLING_RATE_44_1KHZ = 0x4;
    public static final int SAMPLING_RATE_48KHZ = 0x3;

    /**
     * 给编码出的aac裸流添加adts头字段
     * @param packet 原始数据流
     * @param ID MPEG 标示符。0表示MPEG-4，1表示MPEG-2
     * @param protection_absent 标识是否进行误码校验。0表示有CRC校验，1表示没有CRC校验
     * @param profile 标识使用哪个级别的AAC。1: AAC Main 2:AAC LC (Low Complexity) 3:AAC SSR (Scalable Sample Rate) 4:AAC LTP (Long Term Prediction)
     * @param sampling_frequency_index 标识使用的采样率的下标
     * @param channel_configuration 标识声道数
     * @param packetLen ADTS帧长度包括ADTS长度和AAC声音数据长度的和
     */
    public static void addADTStoPacketType(byte[] packet,boolean ID,boolean protection_absent,
                                     int profile,int sampling_frequency_index,
                                     int channel_configuration,int packetLen
                                     ) {

        byte b1 = (byte) 0xFF;//同步头 总是0xFFF, all bits must be 1，代表着一个ADTS帧的开始

        byte b2 = (byte) 0b11110000;
        if (ID){//MPEG标识符，0标识MPEG-4，1标识MPEG-2
            b2=(byte)(b2|(byte) 0b00001000);
        }else {
            b2=(byte)(b2|(byte) 0b00000000);
        }
        b2 |= (byte)0b00000000;//Layer always: '00'

        if (protection_absent){//表示是否误码校验。Warning, set to 1 if there is no CRC and 0 if there is CRC
            b2 |= (byte)0b00000001;
        }else {
            b2 |= (byte)0b00000000;
        }

        byte b3 = 0;
        b3 |= ((profile-1)<<6);
        b3 |= (sampling_frequency_index<<2);
        b3 |= (channel_configuration>>2);

        byte b4 = 0;
        b4 |= ((channel_configuration&3)<<6);
        b4 |= (packetLen>>11);

        byte b5 = (byte)((packetLen&0x7FF) >> 3);
        byte b6 = (byte)(((packetLen&7)<<5) + 0x1F);
        byte b7 = (byte)0xFC;

        packet[0] = b1;
        packet[1] = b2;
        packet[2] = b3;
        packet[3] = b4;
        packet[4] = b5;
        packet[5] = b6;
        packet[6] = b7;
    }


    public interface OnDataOutPut{
        public void onDataOutPut(byte[] framByte);
    }

}
