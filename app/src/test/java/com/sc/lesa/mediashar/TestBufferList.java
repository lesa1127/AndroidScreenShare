package com.sc.lesa.mediashar;
import com.sc.lesa.mediashar.jlib.util.TempBufferList;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestBufferList {

    @Test
    public void testPush(){
        TempBufferList<String> tempBufferList = new TempBufferList<>(10);
        tempBufferList.push("ok");
        tempBufferList.push("2");
        assertTrue(tempBufferList.getAll(new String[2])[1].equals("ok"));
    }

    @Test
    public void testPop(){
        TempBufferList<String> tempBufferList = new TempBufferList<>(10);
        tempBufferList.push("ok");
        tempBufferList.push("2");

        assertTrue(tempBufferList.pop().equals("2"));
        assertTrue(tempBufferList.pop().equals("ok"));
    }

    @Test
    public void testLastValue(){
        TempBufferList<String> tempBufferList = new TempBufferList<>(10);
        tempBufferList.push("ok");
        tempBufferList.push("2");
        assertTrue(tempBufferList.lastValue().equals("ok"));
        assertTrue(tempBufferList.lastValue().equals("2"));
    }

    @Test
    public void testSize(){
        TempBufferList<String> tempBufferList = new TempBufferList<>(10);
        tempBufferList.push("ok");
        tempBufferList.push("2");
        assertTrue(tempBufferList.size()==2);
    }

    @Test
    public void testOverData(){
        TempBufferList<String> tempBufferList = new TempBufferList<>(10);
        tempBufferList.push("ok");
        tempBufferList.push("2");
        tempBufferList.push("ok");
        tempBufferList.push("2");
        tempBufferList.push("ok");
        tempBufferList.push("2");
        tempBufferList.push("ok");
        tempBufferList.push("2");
        tempBufferList.push("ok");
        tempBufferList.push("2");
        tempBufferList.push("ok");
        tempBufferList.push("2");
        tempBufferList.push("ok");
        tempBufferList.push("2");
        tempBufferList.push("ok");
        tempBufferList.push("2");
        assertTrue(tempBufferList.size()==10);

    }

}
