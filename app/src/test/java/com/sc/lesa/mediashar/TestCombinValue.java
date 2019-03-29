package com.sc.lesa.mediashar;
import com.sc.lesa.mediashar.jlib.util.CombinValue;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestCombinValue {
    @Test
    public void combin(){
        short s = 0xf5f;
        byte[] bytes = CombinValue.shortToByte(s);

        short s1 = CombinValue.bytesToShort(bytes);

        assertTrue(s==s1);

        long lsd = 5454455874l;

        bytes=CombinValue.longToBytes(lsd);
        long s2 = CombinValue.bytesToLong(bytes);

        assertTrue(lsd==s2);

        double pi = 3.1415926d;

        bytes = CombinValue.doubleToBytes(pi);

        double pi2 = CombinValue.bytesToDouble(bytes);

        assertTrue(pi==pi2);


    }
}
