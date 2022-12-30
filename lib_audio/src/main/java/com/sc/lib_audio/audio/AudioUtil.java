package com.sc.lib_audio.audio;

import android.annotation.SuppressLint;
import android.media.*;
import android.os.Handler;
import android.os.Message;
import timber.log.Timber;

import java.util.LinkedList;

/**
 * @author tsc
 * @version 0.0.0-1
 * @date 2022/3/22 16:21
 * @description
 */
public class AudioUtil {

    String TAG = "AudioRecordTAG";



    private int mInBuffSize;
    private AudioRecord mInAudioRecord;
    private byte[] mInBytes;
    private LinkedList<byte[]> mInLinkedList;

    private int mOutBuffSize;
    private AudioTrack mOutAudioTrack;
    private byte[] mOutBytes;

    private Thread mRecordThread;
    private Thread mPlayThread;
    private boolean mFlag = true;

    private Handler.Callback onMicVolumeCallBack;

    /**
     * 更新话筒状态
     *
     */
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    public void setOnMicVolumeCallBack(Handler.Callback onMicVolumeCallBack) {
        this.onMicVolumeCallBack = onMicVolumeCallBack;
    }

    public void stop(){
        mFlag = false;
        if (mInAudioRecord != null) {
            mInAudioRecord.stop();
            mInAudioRecord = null;
            return;
        }
        if (mOutAudioTrack != null) {
            mOutAudioTrack.stop();
            mOutAudioTrack = null;
        }
        if (mPlayThread != null) {
            mPlayThread.interrupt();
            mPlayThread = null;
        }
        if (mRecordThread != null) {
            mRecordThread.interrupt();
            mRecordThread = null;
        }
    }

    public void start(){
        startRecord();
    }

    private void startRecord() {
        init();
        mRecordThread = new Thread(new RecordSoundRunnable());
        mPlayThread = new Thread(new PlayRecordRunnable());
        mRecordThread.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayThread.start();
            }
        }, 1000);
    }

    @SuppressLint("MissingPermission")
    private void init() {
        mInBuffSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mInAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                mInBuffSize);

        mInBytes = new byte[mInBuffSize];
        mInLinkedList = new LinkedList<>();

        mOutBuffSize = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mOutAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                mOutBuffSize,
                AudioTrack.MODE_STREAM);
        mOutBytes = new byte[mOutBuffSize];

    }

    // 录音
    private class RecordSoundRunnable implements Runnable {

        @Override
        public void run() {
            Timber.i(TAG + " RecordSoundRunnable running: ");
            byte[] bytes;
            mInAudioRecord.startRecording();
            while (mFlag) {
                int read = mInAudioRecord.read(mInBytes, 0, mInBuffSize);
                if (onMicVolumeCallBack != null) {
                    long v = 0;
                    for (short tmp : mInBytes)
                        v += tmp * tmp;
                    double vol = 10 * Math.log10(v / (double) read);
                    Message message = new Message();
//                    message.what = VOICE_VOLUME;
                    message.obj = vol;
//                            (int) (vol * 50 + 3000);
                    onMicVolumeCallBack.handleMessage(message);
                }
                bytes = mInBytes.clone();
                if (mInLinkedList.size() >= 2) {
                    mInLinkedList.removeFirst();
                } else {
                    mInLinkedList.add(bytes);
                }
            }
        }
    }

//    private void updateMicStatus() {
//        if (mInAudioRecord != null) {
//            double ratio = (double)mInAudioRecord.getMaxAmplitude() /BASE;
//            double db = 0;// 分贝
//            if (ratio > 1)
//                db = 20 * Math.log10(ratio);
//            Log.d(TAG,"分贝值："+db);
//            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
//        }
//    }


    //放音
    private class PlayRecordRunnable implements Runnable {

        @Override
        public void run() {
            Timber.i(TAG + " PlayRecordRunnable running: ");
            mOutAudioTrack.play();
            byte[] bytes;

            try {
                while (mFlag) {
                    mOutBytes = mInLinkedList.getFirst();
                    bytes = mOutBytes.clone();
//                    mOutAudioTrack.write(bytes, 0, bytes.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



//    private AudioRecord recorder = null;
//    private AudioTrack tracker = null;
//
//    // 采样率
//    private int frequency = 44100;
//    // 采样通道
//    private int channelInConfig = AudioFormat.CHANNEL_IN_MONO;
//    // 播放通道
//    private int channelOutConfig = AudioFormat.CHANNEL_OUT_MONO;
//    // 16位音频编码
//    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//    // 录音缓存区
//    private byte[] bufferIn = null;
//    // 记录是否正在录音
//    private boolean isRecording = false;
//    // 记录是否正在播放
//    private boolean isPlaying = false;
//
//    // 存放录音缓存的队列
//    private Queue<byte[]> bufferQueue = null;
//
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
////                   Timber.i(TAG + " 正在播放");
//                    break;
//                case 2:
////                    Timber.i(TAG + " 停止播放");
//                    break;
//            }
//        }
//    };
//
//    public void start() {
//        if (!isRecording) {
//            new AudioRecordTask().execute();
//            new Thread(new RecordPlayingRunnable()).start();
//        }
//    }
//
//    public void stop(){
//        if (recorder != null) {
//            if (isRecording) {
//                isRecording = false;
//                recorder.stop();
//                recorder.release();
//                recorder = null;
//            }
//        }
//        if (tracker != null) {
//            if (isPlaying) {
//                isPlaying = false;
//                tracker.stop();
//                tracker.release();
//                tracker = null;
//            }
//        }
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    public class AudioRecordTask extends AsyncTask<Void, Integer, Void> {
//        @SuppressLint("MissingPermission")
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelInConfig, audioEncoding);
//            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelInConfig, audioEncoding, bufferSize);
//            bufferQueue = new LinkedList<>();
//            bufferIn = new byte[bufferSize];
//            if (recorder != null) {
//                recorder.startRecording();
//                isRecording = true;
//            }
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            while (isRecording) {
//                recorder.read(bufferIn, 0, bufferIn.length);
//                /*if(bufferQueue.size() >= 20) {
//                    bufferQueue.remove();
//                }*/
//                bufferQueue.add(bufferIn);
//                //Log.d(TAG, String.valueOf(bufferQueue.size()));
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//    }
//
//    private class RecordPlayingRunnable implements Runnable {
//        @Override
//        public void run() {
//            int bufferSize = AudioTrack.getMinBufferSize(frequency, channelOutConfig, audioEncoding);
//            tracker = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelOutConfig, audioEncoding, bufferSize, AudioTrack.MODE_STREAM);
//            tracker.play();
//            isPlaying = true;
//            while (true) {
//                byte[] bufferOut = bufferQueue.poll();
//                if (bufferOut != null) {
//                    Timber.i(TAG + " ?? " + bufferOut.length);
//                    tracker.write(bufferOut, 0, bufferOut.length);
////                    Log.d(TAG, String.valueOf(bufferOut.length));
//                }
//                handler.sendEmptyMessage(1);
//                if (!isPlaying) {
//                    handler.sendEmptyMessage(2);
//                    return;
//                }
//            }
//        }
//    }

}
