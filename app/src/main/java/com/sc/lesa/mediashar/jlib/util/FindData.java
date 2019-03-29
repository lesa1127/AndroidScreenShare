package com.sc.lesa.mediashar.jlib.util;

public class FindData {
    public static int findBytesData(byte[] bytes,int offset,int len,byte[] tag){
        int count = 0;

        for (int i = 0; i<=len-tag.length;i++){
            for (int e = 0;e<tag.length;e++){
                if (bytes[offset+i+e]==tag[e]){
                    count++;
                    if (count==tag.length)return i;
                }
                else {
                    count=0;
                    break;
                }
            }
        }

        return -1;
    }
}
