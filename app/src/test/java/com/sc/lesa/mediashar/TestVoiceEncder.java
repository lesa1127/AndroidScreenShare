package com.sc.lesa.mediashar;

import com.sc.lesa.mediashar.jlib.media.VoiceEncoder;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestVoiceEncder {
    @Test
    public void testAddADTS(){
        byte[] bytes1 = new byte[50];
        VoiceEncoder.addADTStoPacket(bytes1,bytes1.length);
        byte[] bytes2 = new byte[50];
        VoiceEncoder.addADTStoPacketType(bytes2,VoiceEncoder.TYPE_MEPG_2,VoiceEncoder.UNUSE_CRC, VoiceEncoder.AAC_LC, VoiceEncoder.SAMPLING_RATE_44_1KHZ, 2,bytes2.length);
        assertArrayEquals(bytes1,bytes2);
    }
}
