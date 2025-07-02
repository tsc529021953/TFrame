package com.sc.tframe.utils;

import android.content.Context;
import android.media.*;
import android.os.Build;
import timber.log.Timber;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class PCMPlayer extends Thread {
    protected AudioTrack mAudioTrack;
    protected int mMiniBufferSize;
    protected byte[] mBuffer;

    private boolean isStop = false;

    private Context context;

    File file;
    public FileInputStream in;
    RandomAccessFile raf;
    FileChannel channel;

    private boolean isPaused = false;
    private final Object pauseLock = new Object();

    private IPlayer iPlayer = null;

    private int total = 0;
    private int percent = 0;
    private int sum = 0;
    private long duration = 0;

    public FileInputStream getInS() {
        return in;
    }

    /**
     * @param filePath 文件路径
     * @param fileName 文件名称
     */
    public void init(String filePath, String fileName, Context context, IPlayer iPlayer) {
        this.context = context;
        this.iPlayer = iPlayer;
        isStop = false;
        try {
//            setDevice();
            file = new File(filePath, fileName);
            in = new FileInputStream(file);
            raf = new RandomAccessFile(file, "r");
            channel = raf.getChannel();
            System.out.println("PCMPlayer " + file.exists());

            /**
             * 参数1：采样率    根据录音的采样率自定义
             * 参数2：声道     {@link AudioFormat.CHANNEL_OUT_MONO}单声道  {@link AudioFormat.CHANNEL_OUT_STEREO} 双声道
             * 参数3：比特率  {@link AudioFormat.ENCODING_PCM_16BIT} {@link AudioFormat.ENCODING_PCM_8BIT}
             * */
            mMiniBufferSize = AudioTrack.getMinBufferSize(DTSPlayer.RATE_IN_HZ,
                    DTSPlayer.CHANNEL, // CHANNEL_CONFIGURATION_MONO,
                    DTSPlayer.ENCODING);
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(DTSPlayer.CONTENT_TYPE) // CONTENT_TYPE_MOVIE
                    .build();
            AudioFormat format = new AudioFormat.Builder()
                    .setSampleRate(DTSPlayer.RATE_IN_HZ)
                    .setChannelMask(DTSPlayer.CHANNEL)
                    .setEncoding(DTSPlayer.ENCODING)
                    .build();
            mAudioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(attr)
                    .setAudioFormat(format)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(mMiniBufferSize)
                    .build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                System.out.println("PCMPlayer isDirectPlaybackSupported " + AudioTrack.isDirectPlaybackSupported(format, attr) + " " + mMiniBufferSize);
            }
            mBuffer = new byte[mMiniBufferSize]; // mMiniBufferSize

        } catch (Exception e) {
            e.printStackTrace();
        }
        initAudioFocus();
    }

    void initAudioFocus() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                        @Override
                        public void onAudioFocusChange(int focusChange) {
                            // 处理音频焦点变化
                        }
                    })
                    .build();
            audioManager.requestAudioFocus(audioFocusRequest);
        }
    }

    public void pause() {
        if (isPaused) return;
        isPaused = true;
//        mAudioTrack.pause();
        iPlayer.onPause();
    }

    public void play() {
        if (!isPaused) return;
        synchronized (pauseLock) {
            isPaused = false;
            pauseLock.notifyAll();
        }
        iPlayer.onPlay();
//        mAudioTrack.play();
    }

    public void playPause() {
        if (isPaused) {
            play();
        } else {
            pause();
        }
    }

    public void release() {
        play();
        isStop = true;
        iPlayer.onRelease();
    }

    public void run() {
        System.out.println("PCMPlayer state " + mAudioTrack.getState() + " " + (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED));
        if (mAudioTrack != null && mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            mAudioTrack.play();
//            try {
////                in.skip(5000);
////                in.getChannel().
////                raf.seek(60000);
//            } catch (IOException e) {
//                System.out.println("PCMPlayer skip err " + e.getMessage());
//                e.printStackTrace();
//            }
            try {
                total = in.available();
                System.out.println("PCMPlayer run start " + total);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int read = -1;
            try {
                iPlayer.onPlay();
                while ((read = in.read(mBuffer)) != -1 && !isStop) {
                    Object pauseLock = new Object();
                    synchronized (pauseLock) {
                        while (isPaused) {
                            pauseLock.wait();
                        }
                    }
                    try {
//                    System.out.println("PCMPlayer read size " + read + " " +  isStop);
                        sum += read;
                        if (sum > percent)
                            mAudioTrack.write(mBuffer, 0, read);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
        try {
//            channel.close();
            in.close();
            iPlayer.onRelease();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("PCMPlayer End");
    }

    public void seekTo(long positionBytes, long duration) throws IOException {
//        mAudioTrack.pause();
//        mAudioTrack.setPlaybackHeadPosition(0);
        mAudioTrack.flush();
        in.skip(0);
        in.getChannel().position(0);
        sum = 0;
        percent = (int) (total * positionBytes / duration);
//        mAudioTrack.play();
    }

    public long getPlaybackPositionMs() {
        if (mAudioTrack == null) return 0;
        int frames = mAudioTrack.getPlaybackHeadPosition();
        return frames * 1000L / mAudioTrack.getSampleRate();
    }

    void setDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : devices) {
                System.out.println("PCMPlayer device " + device.getType() + " " + AudioDeviceInfo.TYPE_HDMI);
                if (device.getType() == AudioDeviceInfo.TYPE_HDMI) {
                    if (mAudioTrack instanceof AudioRouting) {
                        ((AudioRouting) mAudioTrack).setPreferredDevice(device);
                    }
                }
            }
        }
    }

    public interface IPlayer {

        void onPlay();

        void onPause();

        void onRelease();

        void onPosition(long position);
    }

}
