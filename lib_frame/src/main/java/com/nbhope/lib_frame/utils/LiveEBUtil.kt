package com.nbhope.lib_frame.utils

import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.nbhope.lib_frame.bus.event.BaseEvent

/**
 *Created by ywr on 2021/11/10 15:39
 */
class LiveEBUtil {
    companion object {
        /**
         * 普通注册
         */
        @JvmStatic
        fun regist(
            @NonNull event: Class<*>,
            @NonNull owner: LifecycleOwner,
            @NonNull observer: Observer<Any>
        ) {
            LiveEventBus.get().with(event.simpleName).observe(owner, observer)
        }

        /**
         * 粘性注册
         */
        @JvmStatic
        fun registSticky(
            @NonNull event: Class<*>,
            @NonNull owner: LifecycleOwner,
            @NonNull observer: Observer<Any>
        ) {
            LiveEventBus.get().with(event.simpleName).observeSticky(owner, observer)
        }

        @JvmStatic
        fun registForever(
            @NonNull event: Class<*>,
            @NonNull liveObserver: Observer<Any>
        ) {
            LiveEventBus.get().with(event.simpleName).observeForever(liveObserver)
        }

        /**
         * 手动remove上面的永久注册，liveOvserver必须一致
         */
        @JvmStatic
        fun unRegist(@NonNull event: Class<*>, @NonNull liveObserver: Observer<Any>) {
            LiveEventBus.get().with(event.simpleName).removeObserver(liveObserver)
        }

        @JvmStatic
        fun post(event: BaseEvent) {
            LiveEventBus.get().with(event::class.java.simpleName).post(event)
        }
        /**
         * 延迟发送
         */
        @JvmStatic
        fun postDelay(event: BaseEvent, time:Long){
            LiveEventBus.get().with(event::class.java.simpleName).postDelay(event,time)
        }

        /**
         * 顺序发送，顺序接收
         */
        @JvmStatic
        fun postOrderly(event: BaseEvent){
            LiveEventBus.get().with(event::class.java.simpleName).postOrderly(event)
        }

    }
}