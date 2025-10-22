#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <malloc.h>

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "asoundlib.h"
//#include <asoundlib.h>

#define LOG_TAG "PcmRecord"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

static struct pcm *pcm_handle = NULL;

struct pcm_config config;
jint config_card;
jint config_device;

extern "C" {

    void* threadFunc(void* arg) {
        const unsigned int periodSize = 1024;
        const unsigned int channels = 1;
        const unsigned int rate = 16000;

        int id = *(int*)arg;
        LOGD("线程 %d 开始 card: %d device: %d rate: %d channels: %d", id, config_card, config_device, config.rate, config.channels);
        if (pcm_handle) {
            return nullptr;
        }
        memset(&config, 0, sizeof(config));

        config.channels = channels;
        config.rate = rate;
        config.period_size = periodSize;
        config.period_count = 4;
        config.format = PCM_FORMAT_S16_LE;
        config.start_threshold = 0;
        config.stop_threshold = 0;
        config.silence_threshold = 0;

        pcm_handle = pcm_open(config_card, config_device, PCM_IN, &config);
        if (!pcm_is_ready(pcm_handle)) {
            int fd = 0;// pcm_handle.fd; // pcm_get_poll_fd(pcm_handle);
            LOGD("pcm device is not ready! %d", fd);
            return nullptr;
        }



        pcm_close(pcm_handle);
        LOGD("线程 %d 结束", id);
        return nullptr;
    }

    JNIEXPORT jint JNICALL Java_com_sc_tmp_1translate_utils_PcmRecord_init(JNIEnv*, jobject) {
        LOGD("init jni");
        return -1;
    }

    JNIEXPORT jint JNICALL Java_com_sc_tmp_1translate_utils_PcmRecord_open(JNIEnv*, jobject, jint card, jint device, jint sampleRate, jint channels) {
        LOGD("open jni %d", card);
        config_card = card;
        config.rate = sampleRate;
        config_device = device;
        config.channels = channels;
        pthread_t tid;
        int id = card + 10;
        pthread_create(&tid, nullptr, threadFunc, &id);
        pthread_detach(tid);  // 分离线程，不阻塞主线程
//        pthread_create(&thread_id1, nullptr, recordThread1, NULL);
        return 0;
    }

    JNIEXPORT jint JNICALL Java_com_sc_tmp_1translate_utils_PcmRecord_close(JNIEnv*, jobject) {
        LOGD("close jni");

        return 0;
    }

}