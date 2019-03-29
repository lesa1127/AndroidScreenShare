package com.sc.lesa.mediashar;

import com.sc.lesa.mediashar.jlib.util.FindData;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestFindData {
    @Test
    public void testFind(){

        assertTrue(FindData.findBytesData(new byte[]{0,1,2,3,4,5,6},1,7,new byte[]{5,6})==4);

        assertTrue(FindData.findBytesData(new byte[]{0,1,2,3,4,5,6},0,7,new byte[]{0,1})==0);
    }
}
