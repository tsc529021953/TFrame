package com.sc.lib_audio.utils

import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 功能
 * 一
 * 有两个 PCM 文件（比如两个录音）
 * 自动对齐（补偿开头静音/偏移），比较它们内容
 * 判断是否存在内容相同但音量不同的部分
 */
object PcmDiffProcessor {

    fun alignAndSplit(
        pcmPath1: String,
        pcmPath2: String,
        output1: String,
        output2: String,
        sampleRate: Int,
        channels: Int,
        blockSizeMs: Int = 20,
        similarityThreshold: Double = 0.85
    ) {
        val pcm1 = readPcm16le(pcmPath1)
        val pcm2 = readPcm16le(pcmPath2)

        // 自动对齐，找到最佳偏移
        val offset = findBestOffset(pcm1, pcm2)
        log("最佳对齐偏移: $offset 样本")

        // 应用对齐
        val (aligned1, aligned2) = applyOffset(pcm1, pcm2, offset)

        // 分块大小（按毫秒转样本数）
        val blockSize = (sampleRate * channels * blockSizeMs) / 1000

        val out1 = mutableListOf<Short>()
        val out2 = mutableListOf<Short>()

        var i = 0
        while (i < min(aligned1.size, aligned2.size)) {
            val block1 = aligned1.copyOfRange(i, min(i + blockSize, aligned1.size))
            val block2 = aligned2.copyOfRange(i, min(i + blockSize, aligned2.size))

            val sim = similarity(block1, block2)
            log("sim $sim")
            if (sim >= similarityThreshold) {
                // 波形相似，比较音量
                val vol1 = rms(block1)
                val vol2 = rms(block2)

                if (vol1 >= vol2) {
                    out1.addAll(block1.toList())
                } else {
//                    out1.addAll(ShortArray(block1.size) { 0 }.toList())
                }

                if (vol2 > vol1) {
                    out2.addAll(block2.toList())
                } else {
//                    out2.addAll(ShortArray(block2.size) { 0 }.toList())
                }
            } else {
                // 波形不相似，各自保留
                out1.addAll(block1.toList())
                out2.addAll(block2.toList())
            }
            i += blockSize
        }

        writePcm16le(output1, out1.toShortArray())
        writePcm16le(output2, out2.toShortArray())
    }

    private fun readPcm16le(path: String): ShortArray {
        FileInputStream(File(path)).use { fis ->
            val bytes = fis.readBytes()
            val shorts = ShortArray(bytes.size / 2)
            for (i in shorts.indices) {
                shorts[i] = ((bytes[i * 2].toInt() and 0xFF) or
                        (bytes[i * 2 + 1].toInt() shl 8)).toShort()
            }
            return shorts
        }
    }

    private fun writePcm16le(path: String, data: ShortArray) {
        FileOutputStream(File(path)).use { fos ->
            val bytes = ByteArray(data.size * 2)
            for (i in data.indices) {
                bytes[i * 2] = (data[i].toInt() and 0xFF).toByte()
                bytes[i * 2 + 1] = ((data[i].toInt() shr 8) and 0xFF).toByte()
            }
            fos.write(bytes)
        }
    }

    private fun rms(data: ShortArray): Double {
        var sumSq = 0.0
        for (s in data) sumSq += s * s
        return sqrt(sumSq / data.size)
    }

    private fun similarity(a: ShortArray, b: ShortArray): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        val minLen = min(a.size, b.size)
        var dot = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in 0 until minLen) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        return if (normA == 0.0 || normB == 0.0) 0.0 else dot / sqrt(normA * normB)
    }

    private fun findBestOffset(a: ShortArray, b: ShortArray, searchRange: Int = 2000): Int {
        // searchRange 是最大搜索的样本偏移
        var bestOffset = 0
        var bestScore = Double.NEGATIVE_INFINITY
        for (offset in -searchRange..searchRange) {
            val score = correlationAtOffset(a, b, offset)
            if (score > bestScore) {
                bestScore = score
                bestOffset = offset
            }
        }
        return bestOffset
    }

    private fun correlationAtOffset(a: ShortArray, b: ShortArray, offset: Int): Double {
        var dot = 0.0
        var normA = 0.0
        var normB = 0.0
        val len = min(a.size, b.size)
        for (i in 0 until len) {
            val ai = if (i + offset in a.indices) a[i + offset] else 0
            val bi = if (i in b.indices) b[i] else 0
            dot += ai * bi
            normA += ai * ai
            normB += bi * bi
        }
        return if (normA == 0.0 || normB == 0.0) Double.NEGATIVE_INFINITY else dot / sqrt(normA * normB)
    }

    private fun applyOffset(a: ShortArray, b: ShortArray, offset: Int): Pair<ShortArray, ShortArray> {
        return if (offset >= 0) {
            Pair(a.copyOfRange(offset, a.size), b.copyOfRange(0, b.size - offset))
        } else {
            val pos = abs(offset)
            Pair(a.copyOfRange(0, a.size - pos), b.copyOfRange(pos, b.size))
        }
    }

    fun log(msg: Any?) {
        Timber.i("Diff ${msg ?: "null"}")
    }

}