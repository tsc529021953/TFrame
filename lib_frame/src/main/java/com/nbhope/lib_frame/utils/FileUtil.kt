package com.nbhope.lib_frame.utils

import android.text.TextUtils
import java.io.File

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
    }
}