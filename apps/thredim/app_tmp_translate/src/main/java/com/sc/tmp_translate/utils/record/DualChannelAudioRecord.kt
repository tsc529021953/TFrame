package com.sc.tmp_translate.utils.record

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import com.bk.webrtc.Apm
import com.sc.tmp_translate.bean.TransThreadBean
import com.sc.tmp_translate.inter.IRecord
import com.sc.tmp_translate.inter.ITransRecord
import com.sc.tmp_translate.service.TmpServiceImpl
import com.sc.tmp_translate.utils.PcmRecord
import com.sc.tmp_translate.utils.hs.Client
import com.sc.tmp_translate.utils.hs.HSTranslateUtil
import com.sc.tmp_translate.utils.record.TransAudioRecord.Companion
import com.sc.tmp_translate.utils.record.TransAudioRecord.Companion.RECORD_CHANNEL_CONFIG
import com.sc.tmp_translate.utils.record.TransAudioRecord.Companion.SAMPLE_RATE_IN_HZ
import com.signway.aec.AEC
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DualChannelAudioRecord(): IRecord {

    companion object {
        const val TAG = "DualChannelAudioRecordTAG"

        const val BYTE_ARRAY_SIZE = 1200
        const val RATE = 16000
        const val THRESHOLD = 500
        const val RECORD_CHANNEL_CONFIG_STEREO: Int = AudioFormat.CHANNEL_IN_STEREO
        const val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var minSize = 0


    var pcmRecord: PcmRecord? = null

    var aec: AEC? = null

    var tempLength = 0
    var tempArray = ByteArray(BYTE_ARRAY_SIZE)
    private val queue: Queue<ByteArray> = LinkedList()
    private val voiceList: ArrayList<ByteArray> = arrayListOf()
    private var isEnd = false

    var fos: FileOutputStream? = null
    var fosOrigin: FileOutputStream? = null
    var index = 0

    var apm: Apm? = null

    var iTransRecord: ITransRecord? = null

    var tmpServiceImpl: TmpServiceImpl? = null

    constructor(index: Int, iTransRecord: ITransRecord, tmpServiceImpl: TmpServiceImpl?) : this() {
        this.index = index
        this.iTransRecord = iTransRecord
        this.tmpServiceImpl = tmpServiceImpl
        init()
    }

    override var card: Int = -1

    var noVoiceCount = 0
    var voiceCount = 0

    //    private var pcm: AEC = AEC()
//
//    fun open(card: Int) {
//        pcm.open(card, 0, 16000, 1)
//    }
//
//    fun close() {
//        pcm.close()
//    }
    @SuppressLint("MissingPermission")
    override fun init() {
//        aec = AEC()
        initWebRTC()
    }

    override fun open(card: Int) {
        isEnd = false
        Thread {
            Timber.i("线程开始")

            while (!isEnd) {
                if (queue.size > 0) {
                    val datas = queue.poll() // BLEUtils.mergeQueueBytes(queue) //
                    if (datas != null) {
                        val isMaster = index == 1
                        fosOrigin?.write(datas)
                        val d4 = processWebRTC(datas) //
                        val hasVoice = apm?.VADHasVoice() ?: true

//                        val d4 = datas
//                        val hasVoice = isValidVoice(d4)

//                        System.out.println("VAD $card $hasVoice")
                        if (hasVoice) {
                            System.out.println("VAD $card 有声音")
                            noVoiceCount = 0
                            voiceCount++
                            voiceList.add(d4)
                        } else {
                            noVoiceCount++
                            if (noVoiceCount > 10) {
                                if (voiceCount > 10) {
                                    // 触发翻译
//                                    Timber.i("触发翻译 $isMaster $voiceCount ${voiceList.size}")
                                    val list = voiceList.toList()
                                    val ex = tmpServiceImpl!!.getExStr()

                                    val source = if (isMaster) "zh" else ex
                                    val target = if (!isMaster) "zh" else ex
                                    iTransRecord?.onTransStateChange(isMaster, true)

                                    val bean = TransThreadBean()
                                    bean.source = source
                                    bean.target = target
                                    bean.isMaster = isMaster
                                    bean.voiceList = list
//                                    iTransRecord?.onTransThreadGet(bean)
//                                    /*直接翻译*/
                                    var isValidVoice = isValidVoiceList(list)

                                    Timber.i("触发翻译 $isMaster $voiceCount ${voiceList.size} $isValidVoice")
                                    if (isValidVoice) {
                                        if (isMaster) {
                                            tmpServiceImpl?.hsTranslateUtil1?.translate(list, source, target) { resList ->
                                                iTransRecord?.onReceiveRes(isMaster, "", resList)
                                                iTransRecord?.onTransStateChange(isMaster, false)
                                            }
                                        } else {
                                            tmpServiceImpl?.hsTranslateUtil2?.translate(list, source, target) { resList ->
                                                iTransRecord?.onReceiveRes(isMaster, "", resList)
                                                iTransRecord?.onTransStateChange(isMaster, false)
                                            }
                                        }
//                                    iTransRecord?.onReceiveToView(isMaster, list)
                                    }
                                    for (b in list) {
                                        fos?.write(b)
                                    }
                                } else {
                                    // 声音太短，无效数据
                                }
                                voiceCount = 0
                                voiceList.clear()
                            } else {

                            }
                        }
                        iTransRecord?.onReceiveToView(isMaster, datas, null)
//                        iHandset?.onReceiveVoice(d4) // BLEUtils.monoToStereoFast(d4)
                    }
                }
            }
            Timber.i("线程结束")
        }.start()
        createOutputFile(index)
        this.card = card
        pcmRecord?.open(card, 0, RATE, 2, index)
//        aec?.open(card,0,16000,1);
        queue.clear()
        tempArray = ByteArray(BYTE_ARRAY_SIZE)
        tempLength = 0
    }

    override fun close() {
        isEnd = true
        pcmRecord?.close()
        fos?.close()
        fos = null
        fosOrigin?.close()
        fosOrigin = null
    }

    override fun onPcmData(data: ByteArray?) {
//        println("card " + card + "onPcmData: " + data!!.size + " bytes")
        if (data == null) return
        if (tempLength + data.size >= tempArray.size) {
            // 超出了数组界限，补齐数组，添加到队列中
            val len = tempArray.size - tempLength
            System.arraycopy(data, 0, tempArray, tempLength, len)
            queue.add(tempArray.clone())
            // 剩余的添加到新的数组中
            tempArray = ByteArray(BYTE_ARRAY_SIZE)
            tempLength = data.size - len
            if (tempLength != 0)
                System.arraycopy(data, 0, tempArray, 0, tempLength)
        } else {
            // 没超出往数组里面加
            System.arraycopy(data, 0, tempArray, tempLength, data.size)
            tempLength += data.size
        }
    }

    private fun createOutputFile(index: Int): File {
        val dir = File(Environment.getExternalStorageDirectory(), "AudioRecordings")
        if (!dir.exists()) dir.mkdirs()
        val fileName = "${index}_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".pcm"
        val fileName2 = "${index}_or_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".pcm"
        val file = File(dir, fileName)
        val file2 = File(dir, fileName2)
        fos = FileOutputStream(file)
        fosOrigin = FileOutputStream(file2)
        return file
    }

    private fun initWebRTC() {
//        apm.AECMSetSuppressionLevel(Apm.AECM_RoutingMode.LoudSpeakerphone);
//        apm.AECM(true);
        apm = Apm(false, true, true, false, false, false, false)
        apm?.AEC(false)
        apm?.AECM(false)
        apm?.NSSetLevel(Apm.NS_Level.VeryHigh); // Moderate
        apm?.NS(true);
        apm?.AGC(true)
        apm?.AGCSetMode(Apm.AGC_Mode.AdaptiveAnalog)
        apm?.HighPassFilter(false)
        apm?.VAD(true)
        apm?.VADSetLikeHood(Apm.VAD_Likelihood.HighLikelihood)
        Timber.i("apm $apm")

    }

    private fun processWebRTC(data: ByteArray): ByteArray {
//        log("datas ${data.size}")
//        return data
        // 转short
        val end = data.size % 320
        val endCount = if (end == 0) 320 else end
        val count = if (data.size % 320 == 0) data.size / 320 else (data.size / 320) + 1
        var data2 = ByteArray(count * 320)
        val tempData = ByteArray(320)
        val inputData = ShortArray(160)
        val outNsData = ShortArray(160)
        val outAgcData = ShortArray(160)
        val outData = ShortArray(160)
        val bufferCvt = ByteBuffer.allocate(2)
        bufferCvt.order(ByteOrder.LITTLE_ENDIAN);
        for (i in 0 until count) {
            val index = i * 320
            System.arraycopy(data, index, tempData, 0, if (i == count -1) endCount else 320);
            ByteBuffer.wrap(tempData).order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
                .get(inputData)

            /*val res =*/ apm?.ProcessRenderStream(inputData, 0)
            /*val res2 = */apm?.ProcessCaptureStream(inputData, 0)
//            val res3 = apm?.ProcessCaptureStream()
//            nsUtils!!.nsxProcess(nsxId, inputData, 1, outNsData)
//            val res = agcUtils!!.agcProcess(
//                agcId, inputData, 1, 160, outData,
//                0, 0, 0, false
//            )
//            nsUtils!!.nsxProcess(nsxId, outAgcData, 1, outData)
//            log("APM process $res $res2")
            for (j in 0 until inputData.size) {
                bufferCvt.clear()
                bufferCvt.putShort(inputData[j])
                data2[index + j * 2] = bufferCvt[0]
                data2[index + j * 2 + 1] = bufferCvt[1]
            }
        }
        // agc
//        log("datas ${data2.size}")
        return data2
    }

    private fun isValidVoiceList(audioDatas: List<ByteArray>): Boolean {
        audioDatas.forEach {
            if (isValidVoice(it)) { // 只要有一个有效语音
                return true
            }
        }
        return false
    }

    /**
     * 判断音频数据是否为有效语音（有声且非杂音）
     * @param audioData PCM 音频数据 (16bit)
     * @param energyThreshold 能量阈值（根据实际采样率调整，建议 1000-5000） 根据麦克风灵敏度和环境噪音调整（安静环境 1000-2000，嘈杂环境 3000-5000）
     * @param zcrThreshold 过零率阈值（一般 0.1-0.4，越高越可能是噪声）
     * @return true=有效语音，false=无声或噪声
     */
    private fun isValidVoice(
        audioData: ByteArray,
        energyThreshold: Double = 5000.0,
        zcrThreshold: Double = 0.35
    ): Boolean {
        // 1. 检查能量是否达到有声标准
        val energy = calculateEnergy(audioData)
        if (energy < energyThreshold) {
            return false // 能量太低，判定为无声
        }

        // 2. 检查过零率，过滤高频噪声
        val zcr = calculateZeroCrossingRate(audioData)
        if (zcr > zcrThreshold) {
            return false // 过零率太高，可能是噪声
        }

        // 3. 可选：检查振幅范围（排除削波失真）
        val amplitudeRange = checkAmplitudeRange(audioData)
        if (!amplitudeRange) {
            return false // 振幅异常，可能是爆音或削波
        }

        return true // 通过所有检测，判定为有效语音
    }

    /**
     * 检查音频振幅范围是否正常
     * @param audioData PCM 音频数据
     * @return true=正常，false=可能削波或失真
     */
    private fun checkAmplitudeRange(audioData: ByteArray): Boolean {
        val sampleCount = audioData.size / 2
        var maxSample = 0
        var clipCount = 0 // 削波点数

        for (i in 0 until sampleCount) {
            val sample = Math.abs((audioData[i * 2 + 1].toInt() shl 8) or (audioData[i * 2].toInt() and 0xFF))
            if (sample > maxSample) maxSample = sample

            // 检测是否接近最大值（32767 是 16bit 最大值）
            if (sample > 32000) {
                clipCount++
            }
        }

        // 如果超过 5% 的点都削波，认为失真
        return clipCount.toDouble() / sampleCount < 0.05
    }

    /**
     * 计算音频的短时能量
     * @param audioData PCM 音频数据 (16bit)
     * @return 能量值
     */
    private fun calculateEnergy(audioData: ByteArray): Double {
        val sampleCount = audioData.size / 2
        var energy = 0.0

        for (i in 0 until sampleCount) {
            // 将 byte 转为 short (16bit 有符号)
            val sample = ((audioData[i * 2 + 1].toInt() shl 8) or (audioData[i * 2].toInt() and 0xFF))
            energy += sample * sample.toDouble()
        }

        return energy / sampleCount
    }

    /**
     * 计算过零率
     * @param audioData PCM 音频数据 (16bit)
     * @return 过零率 (0.0 - 1.0)
     */
    private fun calculateZeroCrossingRate(audioData: ByteArray): Double {
        val sampleCount = audioData.size / 2
        var zeroCrossings = 0

        for (i in 1 until sampleCount) {
            val current = ((audioData[i * 2 + 1].toInt() shl 8) or (audioData[i * 2].toInt() and 0xFF))
            val previous = ((audioData[(i - 1) * 2 + 1].toInt() shl 8) or (audioData[(i - 1) * 2].toInt() and 0xFF))

            if ((current >= 0 && previous < 0) || (current < 0 && previous >= 0)) {
                zeroCrossings++
            }
        }

        return zeroCrossings.toDouble() / sampleCount
    }


    /**
     * 判断 PCM 数据是否为静音
     * 仅适用于 16bit PCM, 单声道
     */
    private fun isSilent(pcmData: ByteArray, threshold: Int): Boolean {
        if (pcmData.size < 2) return true
        var sum = 0L
        var count = 0

        // 每两个字节为一个采样点（小端序）
        var i = 0
        while (i < pcmData.size) {
            val lo = pcmData[i].toInt() and 0xFF
            val hi = pcmData[i + 1].toInt()
            val sample = (hi shl 8) or lo
            sum += kotlin.math.abs(sample)
            count++
            i += 2
        }

        val avg = sum / count
        return avg < threshold
    }

}