#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <malloc.h>

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "asoundlib.h"
//#include <asoundlib.h>
//#include <speex/speex_resampler.h>

#define LOG_TAG "PcmRecord"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))



extern "C" {

struct pcm *pcm_handle1 = NULL;
struct pcm *pcm_handle2 = NULL;

struct pcm_config config;
jint config_card;
jint config_device;
jint config_channels;
jint config_rate;

 int capturing = 0;
//static std::atomic<bool> gRunning(false);
 JavaVM* gJvm = nullptr;
 jobject gAudioProcessorObj = nullptr;

    struct RecordArgs {
        unsigned int card;
        unsigned int device;
        jobject javaObj;
        unsigned int index;
    };

    jint JNI_OnLoad(JavaVM* vm, void* reserved) {
        gJvm = vm;  // 保存全局引用
        return JNI_VERSION_1_6;
    }

    /**
     * 线性插值降采样
     * 输入: 44100Hz 单声道
     * 输出: 16000Hz 单声道
     */
    /**
     * 双声道 + 降采样转换
     * 输入: 44100Hz, 双声道, PCM 16-bit
     * 输出: 16000Hz, 单声道, PCM 16-bit
     *
     * 参数:
     *   in              - 输入PCM数据 (unsigned char*, 44100Hz stereo)
     *   in_bytes        - 输入字节数
     *   out             - 输出缓冲区 (unsigned char*)
     *   out_capacity    - 输出缓冲区容量（字节数）
     * 返回:
     *   实际输出的字节数
     */
    size_t convert_stereo44100_to_mono16000(const unsigned char* in, size_t in_bytes,
                                            unsigned char* out, size_t out_capacity)
    {
        const int16_t* in_samples = reinterpret_cast<const int16_t*>(in);
        int16_t* out_samples = reinterpret_cast<int16_t*>(out);

        size_t in_frames = in_bytes / (2 * sizeof(int16_t)); // 双声道 → 每帧左右各一个采样
        double ratio = 44100.0 / 16000.0;
        size_t out_frames = (size_t)(in_frames / ratio);
        if (out_frames * sizeof(int16_t) > out_capacity)
            out_frames = out_capacity / sizeof(int16_t);

        // 临时缓冲: 先混合成单声道
        int16_t* mono = (int16_t*)malloc(in_frames * sizeof(int16_t));
        if (!mono) return 0;

        for (size_t i = 0; i < in_frames; ++i) {
            int32_t left = in_samples[2 * i];
            int32_t right = in_samples[2 * i + 1];
            mono[i] = (int16_t)((left + right) / 2);
        }

        // 线性插值降采样 44100 → 16000
        for (size_t i = 0; i < out_frames; ++i) {
            double src_pos = i * ratio;
            size_t idx = (size_t)src_pos;
            double frac = src_pos - idx;

            if (idx + 1 >= in_frames) {
                out_samples[i] = mono[in_frames - 1];
            } else {
                double s1 = mono[idx];
                double s2 = mono[idx + 1];
                out_samples[i] = (int16_t)((1.0 - frac) * s1 + frac * s2);
            }
        }

        free(mono);
        return out_frames * sizeof(int16_t);
    }


    /**
     * 双声道 → 单声道
     */
    void stereo_to_mono(const unsigned char* input, unsigned char* output, size_t frame_count) {
        const int16_t* in = reinterpret_cast<const int16_t*>(input);
        int16_t* out = reinterpret_cast<int16_t*>(output);

        for (size_t i = 0; i < frame_count; ++i) {
            int16_t left = in[2 * i];
            int16_t right = in[2 * i + 1];
            out[i] = (int16_t)(((int32_t)left + (int32_t)right) / 2);
        }
    }

    void* threadFunc(void* arg) {
        const unsigned int periodSize = 1024;
        RecordArgs* args = (RecordArgs*)arg;
        unsigned int card = args->card;
        unsigned int index = args->index;
        unsigned int device = args->device;

        JNIEnv* env = nullptr;
        bool needDetach = false;

        // 附加当前线程到 JVM
        if (gJvm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
            gJvm->AttachCurrentThread(&env, nullptr);
            needDetach = true;
        }

        int id = *(int*)arg;
        LOGD("线程 %d 开始", id);
//        if (pcm_handle1) {
//            return nullptr;
//        }
        memset(&config, 0, sizeof(config));

        config.channels = config_channels;
        config.rate = config_rate;
        config.period_size = periodSize;
        config.period_count = 4;
        config.format = PCM_FORMAT_S16_LE;
        config.start_threshold = 0;
        config.stop_threshold = 0;
        config.silence_threshold = 0;
        LOGD("pcm_open card: %d device: %d rate: %d channels: %d", id, card, device, config.rate, config.channels);
        int res = 0;
        if (index == 1) {
            pcm_handle1 = pcm_open(card, device, PCM_IN, &config);
            res = pcm_is_ready(pcm_handle1);
        } else {
            pcm_handle2 = pcm_open(card, device, PCM_IN, &config);
            res = pcm_is_ready(pcm_handle2);
        }
        if (!res) {
            if (index == 1) LOGD("pcm device is not ready %d %s", res, pcm_get_error(pcm_handle1));
            else LOGD("pcm device is not ready %d %s", res, pcm_get_error(pcm_handle2));
        } else {
            LOGD("pcm device is ready!");
            int len = 0;
            if (index == 1) len = pcm_frames_to_bytes(pcm_handle1, periodSize);
            else len = pcm_frames_to_bytes(pcm_handle2, periodSize);

            unsigned char* buffer = new unsigned char[len];
            unsigned char* mono_buffer = new unsigned char[len / 2];

            // 预估输出缓冲区大小（16000Hz采样率约为44100的36%）
            size_t out_bytes_capacity = (size_t)(len * (16000.0 / config_rate));
            unsigned char* out_buf = (unsigned char*)malloc(out_bytes_capacity);

            jclass cls = env->GetObjectClass(gAudioProcessorObj);
            jmethodID onPcmData = env->GetMethodID(cls, "onPcmData", "(I[B)V");

            capturing = true;
            while (capturing) {
                int ret = 0;
                if (index == 1)
                    ret = pcm_read(pcm_handle1, buffer, pcm_frames_to_bytes(pcm_handle1, periodSize));
                else ret = pcm_read(pcm_handle2, buffer, pcm_frames_to_bytes(pcm_handle2, periodSize));
                if (ret == 0) {
//                    LOGD("read ok %d %d", buffer[0], pcm_frames_to_bytes(pcm_handle1, periodSize));
//                    fwrite(buffer, 1, pcm_frames_to_bytes(pcm_handle1, periodSize), output_file);

                    // step1: 双声道混合成单声道
//                    stereo_to_mono(buffer, mono_buffer, periodSize);
                    size_t out_bytes = convert_stereo44100_to_mono16000(buffer, len, out_buf, out_bytes_capacity);
//                    LOGD("out ok %d %d", out_bytes, out_buf[0]);


                    // 创建 Java ByteArray
                    jbyteArray byteArray = env->NewByteArray(out_bytes);
                    env->SetByteArrayRegion(byteArray, 0, out_bytes, (jbyte*)out_buf);

                    // 调用 Java 回调
                    env->CallVoidMethod(args->javaObj, onPcmData, args->card, byteArray);

                    // 释放局部引用，防止内存增长
                    env->DeleteLocalRef(byteArray);
                } else {
                    LOGD("read fail");
                }
            }
            delete[] buffer;
            free(out_buf);
        }
        if (index == 1)
            pcm_close(pcm_handle1);
        else pcm_close(pcm_handle2);
        if (gAudioProcessorObj) {
            env->DeleteGlobalRef(gAudioProcessorObj);
            gAudioProcessorObj = nullptr;
        }
        LOGD("线程 %d 结束", id);

    EXIT:
        if (needDetach)
            gJvm->DetachCurrentThread();
        pthread_exit(nullptr);
        LOGD("线程 %d EXIT", id);
        return nullptr;
    }

    JNIEXPORT jint JNICALL Java_com_sc_tmp_1translate_utils_PcmRecord_init(JNIEnv*, jobject) {
        LOGD("init jni");
        return -1;
    }

    JNIEXPORT jint JNICALL Java_com_sc_tmp_1translate_utils_PcmRecord_open(JNIEnv* env, jobject thiz, jint card, jint device, jint sampleRate, jint channels, jint index) {
        LOGD("open jni %d", card);
        config_card = card;
        config_rate = sampleRate;
        config_device = device;
        config_channels = channels;

        gAudioProcessorObj = env->NewGlobalRef(thiz);
        RecordArgs* args = new RecordArgs();
        args->card = card;
        args->device = device;
        args->javaObj = env->NewGlobalRef(thiz);
        args->index = index;

        pthread_t gThread;
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
        pthread_create(&gThread, &attr, threadFunc, args);
        pthread_attr_destroy(&attr);

//        pthread_t tid;
//        int id = card + 10;
//        pthread_create(&tid, nullptr, threadFunc, &id);
//        pthread_detach(tid);  // 分离线程，不阻塞主线程

        return 0;
    }

    JNIEXPORT jint JNICALL Java_com_sc_tmp_1translate_utils_PcmRecord_close(JNIEnv* env, jobject thiz) {
        LOGD("close jni");
        capturing = 0;
        return 0;
    }

}