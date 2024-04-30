package com.nbhope.lib_frame.utils

import timber.log.Timber
import java.io.DataOutputStream
import java.io.File
import java.io.IOException

/**
 * @author  tsc
 * @date  2024/4/7 20:04
 * @version 0.0.0-1
 * @description
 */
object ShellUtil {

    /**
     * 屏幕截图
     * 适用于lanucher版
     */
    fun shotScreen(path: String? = null, linuxPath: String? = null, callback: ((res: Int) -> Unit)?) {
        //adb截图方法
        Thread {
            Timber.i("开始屏幕截图... $path $linuxPath")
            if (path != null) {
                val file = File(path)
                if (!file.exists()) file.mkdirs()
            }
            // ${System.currentTimeMillis()}
            val filepath = "/sdcard/${linuxPath ?: ""}/paint_${System.currentTimeMillis()}.png"
            try {
                val res = execRootCmdSilent("screencap -p $filepath")
                callback?.invoke(res)
            } catch (e: java.lang.Exception) {
                Timber.e("屏幕截图出现异常：$e")
            }
        }.start()
    }

    fun execRootCmdSilent(cmd: String): Int {
        var result = -1
        var dos: DataOutputStream? = null
        try {
            val p = Runtime.getRuntime().exec("sh")
            dos = DataOutputStream(p.outputStream)
            dos.writeBytes(
                """
                $cmd
                
                """.trimIndent()
            )
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            p.waitFor()
            result = p.exitValue()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

}
