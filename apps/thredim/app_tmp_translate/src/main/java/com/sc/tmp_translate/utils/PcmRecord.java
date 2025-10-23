package com.sc.tmp_translate.utils;

public class PcmRecord {

    static {
        System.loadLibrary("PcmRecord");
    }

    public native int init();

    public native int open(int card, int device, int sampleRate, int channels);

    public native int close();

    // 被 native 回调（实时PCM）
    void onPcmData(byte[] data) {
        // TODO: 这里可以推送给 AudioTrack、WebSocket、文件写入等
        System.out.println("Received PCM chunk: " + data.length +  " bytes");
    }

}
