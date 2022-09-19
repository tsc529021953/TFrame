package com.nbhope.lib_frame.utils

import android.content.Context
import android.os.Environment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.*

/**
 *Created by ywr on 2021/5/14 9:21
 */
class FileSpManager() {
    // 数据存储位置
    private val dataFile = Environment.getRootDirectory().toString() + "/hopelauncher/data"
    var fileData: JsonObject

    init {
        if (readFormFile().isEmpty()) {
            fileData = JsonObject()
        } else {
            fileData = JsonParser().parse(readFormFile()).asJsonObject
        }

    }

    fun putData(key: String, value: String) {
        if (fileData.has(key)) {
            fileData.remove(key)
        }
        fileData.addProperty(key, value)
        GlobalScope.launch(Dispatchers.IO) {
            writeToFile(fileData.toString())
        }
    }

    fun getData(key: String): String {
        return fileData.get(key).toString()
    }

    private fun writeToFile(json: String) {

        var file = File(dataFile)
        var fos: BufferedOutputStream? = null
        try {
            fos = BufferedOutputStream(FileOutputStream(file))
            fos.write(json.toByteArray())
            fos.close()
        } catch (e: Exception) {
            Timber.i("writeToFile${e.message} ")
        } finally {
            fos?.apply {
                this.close()
            }
        }
    }

    private fun readFormFile(): String {
        var file = File(dataFile)
        val sb = StringBuffer()
        var br: BufferedReader? = null
        try {
            br = BufferedReader(InputStreamReader(FileInputStream(file)))
            var line = br.readLine()
            while (line != null) {
                sb.append(line)
                line = br.readLine()
            }
            br.close()
        } catch (e: Exception) {
            Timber.i("writeToFile${e.message} ")
            br?.apply {
                this.close()
            }
        } finally {
            br?.apply {
                this.close()
            }
            return sb.toString()
        }
    }

}