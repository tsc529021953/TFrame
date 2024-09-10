package com.nbhope.lib_frame.utils

import android.content.Context
import android.text.TextUtils
import com.nbhope.lib_frame.bean.FileBean
import java.io.*

/**
 *Created by ywr on 2021/11/12 9:38
 */
object FileUtil {

    val VIDEO_EXTENSIONS = arrayListOf("mp4", "mkv", "avi", "mov", "flv", "wmv", "3gp")
    val PICTURES_EXTENSIONS = arrayListOf(".jpg", ".png")

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

    fun getDicVideoImageByExs(path: String): List<File> {
        val exs = VIDEO_EXTENSIONS
        exs.addAll(PICTURES_EXTENSIONS)
        System.out.println("getDicVideoImageByExs exs ${exs.size}")
        return getDicFilesByExs(exs, path)
    }

    fun getDicFilesByExs(exs: List<String>, path: String): List<File> {
        val files = mutableListOf<File>()
        val directory = File(path)
        if (directory.exists() && directory.isDirectory) {
            val fileList = directory.listFiles()
            System.out.println("getDicFilesByExs exs ${directory.absolutePath} ${fileList?.size}")
            if (fileList!= null) {
                for (file in fileList) {
                    if (file.isDirectory) {
                        // 如果是目录，则递归调用该方法
//                        files.addAll(getVideoFiles(file.absolutePath))
                    } else {
                        // 如果是文件，则检查扩展名是否为视频文件
                        val fileName = file.name.lowercase()
                        System.out.println("fileName $fileName")
                        if (exs.any { fileName.endsWith(it) }) {
                            files.add(file)
                        }
                    }
                }
            }
        }
        return files
    }

}
