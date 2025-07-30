package com.sc.audio;

public class DualRecorderJNI {

    static {
        System.loadLibrary("dualrecorder");
    }
    // 初始化录音器
    public static native long initRecorder(String devicePath, int sampleRate, int channels);
//    // 开始录音
//    public static native void startRecording(long recorderPtr, String filePath);
//    // 停止录音
//    public static native void stopRecording(long recorderPtr);
//    // 释放资源
//    public static native void releaseRecorder(long recorderPtr);

}
