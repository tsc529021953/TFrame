package com.sc.test.utils

import android.content.Context
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AssetsFileManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AssetsFileManager"
    }

    private fun log(msg: Any?) {
        Timber.d("${msg ?: "null"}")
    }
    
    /**
     * 判断assets下file文件夹是否存在文件
     * @return true: 存在文件, false: 不存在或为空
     */
    fun hasFilesInAssetsFolder(folderPath: String = "file"): Boolean {
        return try {
            // 列出assets中指定路径下的所有文件
            val files = context.assets.list(folderPath)
            
            // 判断是否存在文件（排除空目录和只有子目录的情况）
            val hasFiles = files?.any { fileName ->
                !isAssetDirectory("$folderPath/$fileName")
            } ?: false
            
            log("Assets文件夹 [$folderPath] ${if (hasFiles) "存在" else "不存在"} 文件")
            hasFiles
            
        } catch (e: IOException) {
            log("读取Assets文件夹失败: ${e.message}")
            false
        }
    }
    
    /**
     * 判断Assets路径是否为目录
     */
    private fun isAssetDirectory(path: String): Boolean {
        return try {
            // 如果能列出子文件，说明是目录
            context.assets.list(path)?.isNotEmpty() == true
        } catch (e: IOException) {
            false
        }
    }
    
    /**
     * 获取assets下file文件夹中的所有文件列表
     */
    fun getAssetFilesList(folderPath: String = "file"): List<String> {
        return try {
            val items = context.assets.list(folderPath) ?: emptyArray()
            
            // 过滤掉目录，只保留文件
            val files = items.filter { fileName ->
                !isAssetDirectory("$folderPath/$fileName")
            }
            
            log("Assets文件夹 [$folderPath] 包含以下文件:")
            files.forEach { log("  - $it") }
            
            files
        } catch (e: IOException) {
            log("获取文件列表失败: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * 将assets下file文件夹的所有文件拷贝到内部存储
     * @return 拷贝成功的文件列表
     */
    fun copyAssetsFolderToInternal(
        assetFolderPath: String = "file",
        targetFolderName: String = "copied_files"
    ): List<File> {
        val copiedFiles = mutableListOf<File>()
        
        try {
            // 1. 检查源文件夹是否存在文件
            if (!hasFilesInAssetsFolder(assetFolderPath)) {
                log("Assets文件夹 [$assetFolderPath] 中没有文件，跳过拷贝")
                return emptyList()
            }
            
            // 2. 创建目标目录（内部存储） filesDir
            val targetDir = File(context.getExternalFilesDir(null), targetFolderName)
            if (targetDir.exists()) {
                targetDir.deleteRecursively()
            }
            targetDir.mkdirs()
            log("创建目标目录: ${targetDir.absolutePath}")
            
            // 3. 获取所有文件并拷贝
            val files = getAssetFilesList(assetFolderPath)
            
            files.forEach { fileName ->
                val assetPath = "$assetFolderPath/$fileName"
                val targetFile = File(targetDir, fileName)
                
                try {
                    copyAssetToFile(assetPath, targetFile)
                    copiedFiles.add(targetFile)
                    log("拷贝成功: $fileName -> ${targetFile.absolutePath}")
                } catch (e: IOException) {
                    log("拷贝失败: $fileName - ${e.message}")
                }
            }
            
            // 4. 打印统计信息
            log("\n拷贝完成统计:")
            log("  总文件数: ${copiedFiles.size}")
            log("  目标路径: ${targetDir.absolutePath}")
            log("  总大小: ${formatFileSize(copiedFiles.sumOf { it.length() })}")
            
        } catch (e: Exception) {
            log("拷贝过程出错: ${e.message}")
            e.printStackTrace()
        }
        
        return copiedFiles
    }
    
    /**
     * 拷贝单个asset文件到目标文件
     */
    private fun copyAssetToFile(assetPath: String, targetFile: File) {
        context.assets.open(assetPath).use { inputStream ->
            FileOutputStream(targetFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
    
    /**
     * 带进度的拷贝（适用于大文件）
     */
    fun copyAssetsFolderWithProgress(
        assetFolderPath: String = "file",
        targetFolderName: String = "copied_files",
        onProgress: (fileName: String, progress: Int) -> Unit = { _, _ -> }
    ): List<File> {
        val copiedFiles = mutableListOf<File>()
        
        try {
            val files = getAssetFilesList(assetFolderPath)
            if (files.isEmpty()) return emptyList()
            
            val targetDir = File(context.filesDir, targetFolderName)
            targetDir.mkdirs()
            
            var completedCount = 0
            val totalFiles = files.size
            
            files.forEach { fileName ->
                val assetPath = "$assetFolderPath/$fileName"
                val targetFile = File(targetDir, fileName)
                
                context.assets.open(assetPath).use { inputStream ->
                    val fileSize = inputStream.available()
                    FileOutputStream(targetFile).use { outputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead = 0
                        
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            
                            // 计算进度
                            val progress = (totalBytesRead * 100 / fileSize)
                            onProgress(fileName, progress)
                        }
                    }
                }
                
                copiedFiles.add(targetFile)
                completedCount++
                
                log("[$completedCount/$totalFiles] 已拷贝: $fileName")
            }
            
        } catch (e: Exception) {
            log("拷贝过程出错: ${e.message}")
        }
        
        return copiedFiles
    }
    
    /**
     * 递归拷贝整个文件夹结构（包含子目录）
     */
    fun copyAssetsFolderRecursively(
        assetFolderPath: String = "file",
        targetFolderName: String = "copied_files_recursive"
    ): List<File> {
        val copiedFiles = mutableListOf<File>()
        val targetRootDir = File(context.filesDir, targetFolderName)
        
        fun copyRecursive(assetPath: String, targetDir: File) {
            try {
                val items = context.assets.list(assetPath) ?: return
                
                items.forEach { itemName ->
                    val fullAssetPath = if (assetPath.isEmpty()) itemName else "$assetPath/$itemName"
                    val targetFile = File(targetDir, itemName)
                    
                    if (isAssetDirectory(fullAssetPath)) {
                        // 是目录，递归处理
                        targetFile.mkdirs()
                        copyRecursive(fullAssetPath, targetFile)
                    } else {
                        // 是文件，直接拷贝
                        copyAssetToFile(fullAssetPath, targetFile)
                        copiedFiles.add(targetFile)
                        log("拷贝: $fullAssetPath")
                    }
                }
            } catch (e: IOException) {
                log("递归拷贝失败: ${e.message}")
            }
        }
        
        log("开始递归拷贝 Assets/$assetFolderPath -> ${targetRootDir.absolutePath}")
        targetRootDir.mkdirs()
        copyRecursive(assetFolderPath, targetRootDir)
        log("递归拷贝完成，共 ${copiedFiles.size} 个文件")
        
        return copiedFiles
    }
    
    private fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }
}