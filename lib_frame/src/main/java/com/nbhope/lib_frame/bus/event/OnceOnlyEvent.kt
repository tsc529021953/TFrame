package com.nbhope.lib_frame.bus.event

/**
 * Created by zhouwentao on 2019-09-02.
 * 一次性事件 用于LiveEvent
 * ⚠️⚠️慎用
 * 已经将此类事件使用场景封装于baseViewModel中
 *
 * 详见 https://juejin.im/post/5b2b1b2cf265da5952314b63
 *
 * 使用场景举例：
 * 定义一个liveEvent，用于点击跳转新页面，当liveEvent的值为true，跳转。
 *
 * 问题：
 * 1.用户点击按钮 Details Activity 启动。(liveEvent值设为true,当前主activity监听跳转)
 * 2.用户按下返回，回到主 Activity。
 * 3.观察者在 Activity 处于回退栈时从非监听状态再次变成监听状态。
 * 4.但是该值仍然为 “真”，因此 Detail Activity 启动出错。
 *
 * 解决方案：
 * 需要一种一次性事件，被执行过后就自动报废，直到下次liveEvent的值再次主动改变
 */


/**
 * 使用事件包装器，防止一个事件被执行了仍然返回
 */

open class OnceOnlyEvent<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // 只读属性，不可写
    /**
     * 只有该事件没有被执行才返回
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * 即使该事件已被执行，仍然返回。
     */
    fun peekContent(): T = content
}

