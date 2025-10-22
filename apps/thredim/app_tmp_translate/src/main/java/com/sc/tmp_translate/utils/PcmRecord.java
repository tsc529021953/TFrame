package com.sc.tmp_translate.utils;

public class PcmRecord {

    static {
        System.loadLibrary("PcmRecord");
    }

    public native int init();

    public native int open(int card, int device, int sampleRate, int channels);

    public native int close();

}
