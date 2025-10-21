package com.sc.tmp_translate.utils;

public class PcmRecord {

    static {
        System.loadLibrary("PcmRecord");
    }

    public native int init();

}
