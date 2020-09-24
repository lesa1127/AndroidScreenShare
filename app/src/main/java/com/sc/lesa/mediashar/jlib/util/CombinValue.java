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
            long tmp = ((long) bytes[i] & 0xffL);
            src |= (tmp<<i*8);
        }
        return src;
    }
    public static int bytesToInt(byte[] bytes){
        int src = 0;
        for (int i = 0; i < bytes.length; i++) {
            long tmp = ((long) bytes[i] & 0xffL);
            src |= (tmp<<i*8);
        }
        return src;
    }
    public static long bytesToLong(byte[] bytes){
        long src = 0;
        for (int i = 0; i < bytes.length; i++) {
            long tmp = ((long) bytes[i] & 0xffL);
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


    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes,String splie) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            if (i!=bytes.length-1) {
                sb.append(hex+splie);
            }else {
                sb.append(hex);
            }
        }
        return sb.toString();
    }

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte
     */
    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

}
