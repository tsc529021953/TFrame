#include <jni.h>
#include <string>
#include <alsa/asoundlib.h>
#include <alsa/pcm.h>
#include <pthread.h>
#include <unistd.h>
#include <fcntl.h>

// 录音器结构体
struct Recorder {
    snd_pcm_t *pcm_handle;
    int sample_rate;
    int channels;
    pthread_t thread_id;
    volatile bool is_recording;
    int fd; // 文件描述符
};

// 录音线程函数
void* record_thread(void* arg) {
    Recorder* recorder = static_cast<Recorder*>(arg);
    const int buffer_size = 1024;
    char buffer[buffer_size];

    while (recorder->is_recording) {
        int frames = snd_pcm_readi(recorder->pcm_handle, buffer, buffer_size / (2 * recorder->channels));

        if (frames < 0) {
            frames = snd_pcm_recover(recorder->pcm_handle, frames, 0);
            if (frames < 0) {
                break; // 无法恢复的错误
            }
        }

        if (frames > 0) {
            size_t bytes_to_write = frames * recorder->channels * 2; // 16位采样
            ssize_t written = write(recorder->fd, buffer, bytes_to_write);
            if (written != bytes_to_write) {
                // 写入错误
                break;
            }
        }
    }

    return nullptr;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_dualusbrecorder_DualRecorderJNI_initRecorder(
        JNIEnv* env,
        jclass clazz,
        jstring devicePath,
        jint sampleRate,
        jint channels) {

    const char *path = env->GetStringUTFChars(devicePath, 0);

    Recorder* recorder = new Recorder();
    recorder->sample_rate = sampleRate;
    recorder->channels = channels;
    recorder->is_recording = false;

    // 打开PCM设备
    int err = snd_pcm_open(&recorder->pcm_handle, path, SND_PCM_STREAM_CAPTURE, 0);
    if (err < 0) {
        delete recorder;
        env->ReleaseStringUTFChars(devicePath, path);
        return 0;
    }

    // 配置硬件参数
    snd_pcm_hw_params_t *hw_params;
    snd_pcm_hw_params_alloca(&hw_params);

    snd_pcm_hw_params_any(recorder->pcm_handle, hw_params);
    snd_pcm_hw_params_set_access(recorder->pcm_handle, hw_params, SND_PCM_ACCESS_RW_INTERLEAVED);
    snd_pcm_hw_params_set_format(recorder->pcm_handle, hw_params, SND_PCM_FORMAT_S16_LE);
    snd_pcm_hw_params_set_channels(recorder->pcm_handle, hw_params, channels);
    snd_pcm_hw_params_set_rate_near(recorder->pcm_handle, hw_params, (unsigned int*)&sampleRate, 0);

    // 应用参数
    err = snd_pcm_hw_params(recorder->pcm_handle, hw_params);
    if (err < 0) {
        snd_pcm_close(recorder->pcm_handle);
        delete recorder;
        env->ReleaseStringUTFChars(devicePath, path);
        return 0;
    }

    env->ReleaseStringUTFChars(devicePath, path);
    return reinterpret_cast<jlong>(recorder);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_dualusbrecorder_DualRecorderJNI_startRecording(
        JNIEnv* env,
        jclass clazz,
        jlong recorderPtr,
        jstring filePath) {

    Recorder* recorder = reinterpret_cast<Recorder*>(recorderPtr);
    if (recorder == nullptr) return;

    const char *path = env->GetStringUTFChars(filePath, 0);

    // 创建录音文件
    recorder->fd = open(path, O_WRONLY | O_CREAT | O_TRUNC, 0644);
    if (recorder->fd < 0) {
        env->ReleaseStringUTFChars(filePath, path);
        return;
    }

    env->ReleaseStringUTFChars(filePath, path);

    // 开始录音
    recorder->is_recording = true;
    pthread_create(&recorder->thread_id, nullptr, record_thread, recorder);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_dualusbrecorder_DualRecorderJNI_stopRecording(
        JNIEnv* env,
        jclass clazz,
        jlong recorderPtr) {

    Recorder* recorder = reinterpret_cast<Recorder*>(recorderPtr);
    if (recorder == nullptr) return;

    if (recorder->is_recording) {
        recorder->is_recording = false;
        pthread_join(recorder->thread_id, nullptr);

        snd_pcm_drop(recorder->pcm_handle);

        if (recorder->fd >= 0) {
            close(recorder->fd);
            recorder->fd = -1;
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_dualusbrecorder_DualRecorderJNI_releaseRecorder(
        JNIEnv* env,
        jclass clazz,
        jlong recorderPtr) {

    Recorder* recorder = reinterpret_cast<Recorder*>(recorderPtr);
    if (recorder == nullptr) return;

    if (recorder->is_recording) {
        Java_com_example_dualusbrecorder_DualRecorderJNI_stopRecording(env, clazz, recorderPtr);
    }

    if (recorder->pcm_handle != nullptr) {
        snd_pcm_close(recorder->pcm_handle);
    }

    delete recorder;
}