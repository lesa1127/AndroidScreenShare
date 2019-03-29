package com.sc.lesa.mediashar.jlib.media;

public class AacAdstUtil {
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
     *
     * @param ID MPEG 标示符。0表示MPEG-4，1表示MPEG-2
     * 如 {@link AacAdstUtil#TYPE_MEPG_4} 和 {@link AacAdstUtil#TYPE_MEPG_2}
     *
     * @param protection_absent 标识是否进行误码校验。0表示有CRC校验，1表示没有CRC校验
     * {@link AacAdstUtil#UNUSE_CRC} 和 {@link AacAdstUtil#USE_CRC}
     *
     * @param profile 标识使用哪个级别的AAC。1: AAC Main 2:AAC LC (Low Complexity) 3:AAC SSR (Scalable Sample Rate) 4:AAC LTP (Long Term Prediction)
     * {@link AacAdstUtil#AAC_LC}
     *
     * @param sampling_frequency_index 标识使用的采样率的下标
     * {@link AacAdstUtil#SAMPLING_RATE_44_1KHZ}
     *
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
}
