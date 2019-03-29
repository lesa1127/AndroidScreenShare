package com.sc.lesa.mediashar.jlib.util;

public class CombinValue {
    public static byte[] shortToByte(short value){
        byte[] src = new byte[2];
        src[1] =  (byte) ((value>>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    public static byte[] intToByte(int value){
        byte[] src = new byte[4];
        for (int i = 0; i < src.length; i++) {
            src[i] = (byte) ((value >>> 8 * i) & 0xFF);
        }
        return src;
    }

    public static byte[] longToBytes(long value){
        byte[] src = new byte[8];
        for (int i = 0; i < src.length; i++) {
            src[i] = (byte) ((value >>> 8 * i) & 0xFF);
        }
        return src;
    }

    public static byte[] doubleToBytes(double val){
        long value = Double.doubleToRawLongBits(val);
        return longToBytes(value);
    }
    public static byte[] floatToBytes(float val){
        int value = Float.floatToIntBits(val);
        return intToByte(value);
    }
    public static short bytesToShort(byte[] bytes){
        short src = 0;
        for (int i = 0; i < bytes.length; i++) {
            long tmp = ((long) bytes[i] & 0xffl);
            src |= (tmp<<i*8);
        }
        return src;
    }
    public static int bytesToInt(byte[] bytes){
        int src = 0;
        for (int i = 0; i < bytes.length; i++) {
            long tmp = ((long) bytes[i] & 0xffl);
            src |= (tmp<<i*8);
        }
        return src;
    }
    public static long bytesToLong(byte[] bytes){
        long src = 0;
        for (int i = 0; i < bytes.length; i++) {
            long tmp = ((long) bytes[i] & 0xffl);
            src |= (tmp<<i*8);
        }
        return src;
    }

    public static float bytesToFloat(byte[] bytes){
        return Float.intBitsToFloat(bytesToInt(bytes));
    }

    public static double bytesToDouble(byte[] bytes){
        return Double.longBitsToDouble(bytesToLong(bytes));
    }
}
