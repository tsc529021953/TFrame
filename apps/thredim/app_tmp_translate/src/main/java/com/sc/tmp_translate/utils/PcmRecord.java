package com.sc.tmp_translate.utils;

public class PcmRecord {

    static {
        System.loadLibrary("PcmRecord");
    }

    public native int init();

    public native int open(int card, int device, int sampleRate, int channels, int index);

    public native int close();

    public static IPcmRecord iPcmRecord;

    // 被 native 回调（实时PCM）
    void onPcmData(int card, byte[] data) {
        // TODO: 这里可以推送给 AudioTrack、WebSocket、文件写入等
        if (iPcmRecord != null)
            iPcmRecord.onPcmData(card, data);
    }

}
