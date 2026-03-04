package com.sc.tmp_translate.vm

import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import com.google.gson.Gson
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.tmp_translate.constant.MessageConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject

class TranslatingViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    fun calculateAmplitude(pcm: ByteArray): Float {
        var max = 0
        var i = 0
        while (i < pcm.size) {
            val value = ((pcm[i + 1].toInt() shl 8) or (pcm[i].toInt() and 0xFF)).toShort().toInt()
            max = kotlin.math.max(max, kotlin.math.abs(value))
            i += 2
        }
        return max / 32768f  // 归一化到 [0, 1]
    }

    fun exportLog() {
        try {
            // 保存路径
            val logDir = "/sdcard/logs"
            val dir = File(logDir)
            if (!dir.exists()) dir.mkdirs()

            val logFile = File(dir, "logcat_${System.currentTimeMillis()}.txt")

            // 清空旧日志 (可选)
//            Runtime.getRuntime().exec("logcat -c")

            // 导出命令
            val cmd = arrayOf("logcat", "-v", "time", "-d")
            val process = Runtime.getRuntime().exec(cmd)

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val writer = BufferedWriter(FileWriter(logFile))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                writer.write(line)
                writer.newLine()
            }

            writer.flush()
            writer.close()
            reader.close()

            Toast.makeText(HopeBaseApp.getContext(), "日志已导出到: ${logFile.absolutePath}", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(HopeBaseApp.getContext(), "导出失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}
