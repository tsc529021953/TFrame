package com.sc.tmp_translate.inter

/**
 * @author  tsc
 * @date  2025/7/31 10:06
 * @version 0.0.0-1
 * @description
 */
interface ITmpTTS {

    fun init()
    fun release()
    fun speak(msg: String)

}
