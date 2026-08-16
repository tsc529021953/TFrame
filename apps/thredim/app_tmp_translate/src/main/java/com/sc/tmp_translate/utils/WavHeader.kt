package com.sc.tmp_translate.utils

import android.content.Context
import timber.log.Timber
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.min

data class WavHeader(
    val chunkID: String,          // "RIFF"
    val chunkSize: Int,           // 文件大小-8
    val format: String,          // "WAVE"
    val subchunk1ID: String,     // "fmt "
    val subchunk1Size: Int,      // 16 for PCM
    val audioFormat: Int,        // 1 for PCM
    val numChannels: Int,        // 2 for stereo
    val sampleRate: Int,         // 采样率
    val byteRate: Int,           // 字节率
    val blockAlign: Int,         // 块对齐
    val bitsPerSample: Int,      // 位深度
    val subchunk2ID: String,     // "data"
    val subchunk2Size: Int       // 数据大小
)

class WavFileProcessor(private val context: Context) {
    
    companion object {
        private const val TAG = "WavFileProcessor"
    }
    
    private fun log(msg: Any?) {
        Timber.d("${msg ?: "null"}")
    }
    
    /**
     * 拆解双通道WAV文件
     * @param assetFileName assets中的WAV文件名
     * @return 左右声道文件路径
     */
    fun splitStereoWav(assetFileName: String): Pair<String, String> {
        log("splitStereoWav $assetFileName")
        // 1. 从assets读取WAV文件
        val wavData = readAssetFile(assetFileName)
        log("wavData ${wavData.size}")
        // 2. 解析WAV头
        val header = parseWavHeader(wavData)
        require(header.numChannels == 2) { "不是双通道WAV文件" }
        
        log("  WAV文件信息:")
        log("  采样率: ${header.sampleRate} Hz")
        log("  位深度: ${header.bitsPerSample} bit")
        log("  通道数: ${header.numChannels}")
        log("  数据大小: ${header.subchunk2Size} 字节")
        
        // 3. 分离左右声道数据
        val audioData = wavData.copyOfRange(44, wavData.size) // PCM数据从44字节开始
        val (leftData, rightData) = splitChannels(audioData, header.bitsPerSample)
        
        // 4. 创建新的WAV文件（单声道）
        val leftFilePath = createMonoWavFile("left_channel_${assetFileName}", leftData, header)
        val rightFilePath = createMonoWavFile("right_channel_${assetFileName}", rightData, header)
        
        // 5. 打印路径
        log("  文件保存路径:")
        log("  左声道: $leftFilePath")
        log("  右声道: $rightFilePath")
        
        return Pair(leftFilePath, rightFilePath)
    }
    
    /**
     * 从assets读取文件
     */
    private fun readAssetFile(fileName: String): ByteArray {
        return context.assets.open(fileName).use { inputStream ->
            inputStream.readBytes()
        }
    }
    
    /**
     * 解析WAV文件头
     */
    private fun parseWavHeader(data: ByteArray): WavHeader {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        
        return WavHeader(
            chunkID = String(data, 0, 4),
            chunkSize = buffer.getInt(4),
            format = String(data, 8, 4),
            subchunk1ID = String(data, 12, 4),
            subchunk1Size = buffer.getInt(16),
            audioFormat = buffer.getShort(20).toInt(),
            numChannels = buffer.getShort(22).toInt(),
            sampleRate = buffer.getInt(24),
            byteRate = buffer.getInt(28),
            blockAlign = buffer.getShort(32).toInt(),
            bitsPerSample = buffer.getShort(34).toInt(),
            subchunk2ID = String(data, 36, 4),
            subchunk2Size = buffer.getInt(40)
        )
    }
    
    /**
     * 分离左右声道数据
     */
    private fun splitChannels(audioData: ByteArray, bitsPerSample: Int): Pair<ByteArray, ByteArray> {
        val bytesPerSample = bitsPerSample / 8
        val frameSize = bytesPerSample * 2 // 立体声每帧字节数
        val frameCount = audioData.size / frameSize
        
        val leftData = ByteArray(frameCount * bytesPerSample)
        val rightData = ByteArray(frameCount * bytesPerSample)
        
        for (i in 0 until frameCount) {
            val srcPos = i * frameSize
            
            // 复制左声道数据
            System.arraycopy(audioData, srcPos, leftData, i * bytesPerSample, bytesPerSample)
            // 复制右声道数据
            System.arraycopy(audioData, srcPos + bytesPerSample, rightData, i * bytesPerSample, bytesPerSample)
        }
        
        return Pair(leftData, rightData)
    }
    
    /**
     * 创建单声道WAV文件
     */
    private fun createMonoWavFile(fileName: String, audioData: ByteArray, originalHeader: WavHeader): String {
        // 创建内部存储目录
        val wavDir = File(context.filesDir, "split_wav_files")
        if (!wavDir.exists()) {
            wavDir.mkdirs()
        }
        
        val wavFile = File(wavDir, fileName)
        
        FileOutputStream(wavFile).use { fos ->
            // 写入WAV头
            writeWavHeader(fos, audioData.size, originalHeader.sampleRate, originalHeader.bitsPerSample)
            // 写入音频数据
            fos.write(audioData)
        }
        
        return wavFile.absolutePath
    }
    
    /**
     * 写入WAV文件头（单声道）
     */
    private fun writeWavHeader(outputStream: FileOutputStream, dataSize: Int, sampleRate: Int, bitsPerSample: Int) {
        val byteRate = sampleRate * bitsPerSample / 8
        val blockAlign = bitsPerSample / 8
        
        val buffer = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        
        // RIFF chunk descriptor
        buffer.put("RIFF".toByteArray())
        buffer.putInt(36 + dataSize)  // ChunkSize
        buffer.put("WAVE".toByteArray())
        
        // fmt subchunk
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16)  // Subchunk1Size (16 for PCM)
        buffer.putShort(1) // AudioFormat (1 for PCM)
        buffer.putShort(1) // NumChannels (1 for mono)
        buffer.putInt(sampleRate)
        buffer.putInt(byteRate)
        buffer.putShort(blockAlign.toShort())
        buffer.putShort(bitsPerSample.toShort())
        
        // data subchunk
        buffer.put("data".toByteArray())
        buffer.putInt(dataSize)
        
        outputStream.write(buffer.array())
    }
    
    /**
     * 打印文件信息
     */
    fun printFileInfo(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            log("📊 文件详细信息:")
            log("  路径: ${file.absolutePath}")
            log("  大小: ${formatFileSize(file.length())}")
            log("  修改时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(file.lastModified()))}")
            
            // 验证文件是否为有效的WAV
            try {
                FileInputStream(file).use { fis ->
                    val header = ByteArray(44)
                    fis.read(header)
                    val isValid = String(header, 0, 4) == "RIFF" && String(header, 8, 4) == "WAVE"
                    log("  WAV格式: ${if (isValid) "✅ 有效" else "❌ 无效"}")
                }
            } catch (e: Exception) {
                log("  WAV格式: ❌ 读取失败")
            }
        } else {
            log("❌ 文件不存在: $filePath")
        }
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