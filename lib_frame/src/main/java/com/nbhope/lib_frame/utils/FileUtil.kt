package com.nbhope.lib_frame.utils

import android.text.TextUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.StringBuilder

/**
 *Created by ywr on 2021/11/12 9:38
 */
class FileUtil {
    companion object {
        @JvmStatic
        fun isFileExist(filePath: String?): Boolean {
            if (TextUtils.isEmpty(filePath)) {
                return false
            }

            val file: File = File(filePath)
            return file.exists() && file.isFile
        }

        @JvmStatic
        fun deleteFile(path: String?): Boolean {

            if (TextUtils.isEmpty(path)) {
                return true
            }

            val file: File = File(path)
            if (!file.exists()) {
                return true
            }
            if (file.isFile) {
                return file.delete()
            }
            if (!file.isDirectory) {
                return false
            }
            for (f in file.listFiles()) {
                if (f.isFile) {
                    f.delete()
                } else if (f.isDirectory) {
                   deleteFile(f.absolutePath)
                }
            }
            return file.delete()
        }

        @JvmStatic
        suspend fun readFile(filePath: String?): String? {
            val file = File(filePath)
            if (!file.exists()) return null
            var sb = StringBuilder()
            try {
                val fr = FileReader(filePath)
                val br = BufferedReader(fr)
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    println(line)
                    sb.append(line)
                }
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return sb.toString()
        }
    }
}
