package com.sc.lesa.mediashar;
import com.sc.lesa.mediashar.jlib.util.CRC;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestCRC {
    @Test
    public void tetscrc(){
        System.out.println(CRC.getIntCRC(new byte[]{1,2,5,6,7}));
    }
}
