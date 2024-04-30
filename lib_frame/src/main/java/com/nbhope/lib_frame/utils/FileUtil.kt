package com.nbhope.lib_frame.utils

import android.content.Context
import android.text.TextUtils
import java.io.*

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

        @JvmStatic
        fun copyAssetFile(context: Context, fileName: String, targetFilePath: String): String? {
            var `is`: InputStream? = null
            var os: FileOutputStream? = null
            try {
                `is` = context.getAssets().open(fileName)
                if (!File(targetFilePath).parentFile.exists()) {
                    File(targetFilePath).parentFile.mkdirs()
                }
                os = FileOutputStream(targetFilePath)
                val buffer = ByteArray(1024)
                var len: Int
                while (`is`.read(buffer).also { len = it } != -1) {
                    os.write(buffer, 0, len)
                }
                os.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                    os?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return targetFilePath
        }
    }



}
