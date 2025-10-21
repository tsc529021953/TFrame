package com.signway.aec;

/**
 * 2 * <pre>
 * 3 * author : Administrator
 * 4 * e-mail : xxx@xx
 * 5 * time : 2025/07/22
 * 6 * desc :
 * 7 * version: 1.0
 * 8 * </pre>
 * 9
 */
public class AEC {

    static {
        System.loadLibrary("pcm_jni");
    }

    public synchronized native int open(int card, int device, int sampleRate, int channels);

    public synchronized native int close();

    public synchronized native int read(byte[] buf, int len);
}
