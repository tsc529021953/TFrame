package com.sc.tmp_translate.utils.record;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public class TinyCapManager {

    private static final String TAG = "TinyCapManager";

    public String outputPath;
    public String newPath;
    private int sampleRate = 44100;
    private int channels = 2;
    private int bits = 16;
    public int card = 4;
    private int device = 0;

    private Process recordProcess;

    boolean recordRunning = false;

    public TinyCapManager() {

    }

    public void setParams(int sampleRate, int channels, int bits, int card, int device) {
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bits = bits;
        this.card = card;
        this.device = device;
    }

    public boolean startRecording(String outputPath) {
        this.outputPath = outputPath;
        if (recordProcess != null) {
            Log.w(TAG, "Already recording");
            return false;
        }

        String cmd = String.format("tinycap %s -r %d -c %d -b %d -D %d -d %d",
                outputPath, sampleRate, channels, bits, card, device);
        try {
            Log.d(TAG, "Executing: " + cmd);
            recordProcess = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
            recordRunning = true;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            recordProcess = null;
            return false;
        }
    }

    public void stopRecording() {
        if (recordProcess != null) {
            recordProcess.destroy();  // 终止录音进程
            recordProcess = null;
        } else {
            Log.w(TAG, "Recording process is null");
        }
    }

    public boolean isRecording() {
        return recordProcess != null;
    }

    public static boolean isTinyCapAvailable() {
        File tinycap = new File("/system/bin/tinycap");
        return tinycap.exists() && tinycap.canExecute();
    }
}
