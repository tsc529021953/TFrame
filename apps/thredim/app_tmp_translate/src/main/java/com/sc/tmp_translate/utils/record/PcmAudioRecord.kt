package com.sc.tmp_translate.utils.record

import android.os.Environment
import com.bk.webrtc.Apm
import com.sc.tmp_translate.inter.IRecord
import com.sc.tmp_translate.inter.ITransRecord
import com.sc.tmp_translate.service.TmpServiceImpl
import com.sc.tmp_translate.utils.PcmRecord
import com.sc.tmp_translate.utils.hs.Client
import com.sc.tmp_translate.utils.hs.HSTranslateUtil
import com.signway.aec.AEC
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PcmAudioRecord(): IRecord {

    companion object {
        const val TAG = "PcmAudioRecordTAG"

        const val BYTE_ARRAY_SIZE = 1200
        const val RATE = 44100
        const val THRESHOLD = 500
    }

    var pcmRecord: PcmRecord? = null

    var aec: AEC? = null

    var tempLength = 0
    var tempArray = ByteArray(BYTE_ARRAY_SIZE)
    private val queue: Queue<ByteArray> = LinkedList()
    private val voiceList: ArrayList<ByteArray> = arrayListOf()
    private var isEnd = false

    var fos: FileOutputStream? = null
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
    override fun init() {
        pcmRecord = PcmRecord()
        val res = pcmRecord?.init()
        Timber.tag(TAG).i("init res $res")

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
//                        fos?.write(datas)
                        val d4 = processWebRTC(datas) //
                        val hasVoice = apm?.VADHasVoice() ?: true
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
                                    Timber.i("触发翻译 $voiceCount ${voiceList.size}")
                                    val list = voiceList.toList()
                                    val ex = tmpServiceImpl!!.getExStr()
                                    val isMaster = index == 1
                                    val source = if (isMaster) "zh" else ex
                                    val target = if (!isMaster) "zh" else ex
                                    tmpServiceImpl?.hsTranslateUtil?.translate(list, source, target) { resList ->
                                        iTransRecord?.onReceiveRes(isMaster, "", resList)
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
        val file = File(dir, fileName)
        fos = FileOutputStream(file)
        return file
    }

    private fun initWebRTC() {
//        apm.AECMSetSuppressionLevel(Apm.AECM_RoutingMode.LoudSpeakerphone);
//        apm.AECM(true);
        apm = Apm(false, true, true, false, false, false, false)
        apm?.AEC(false)
        apm?.AECM(false)
        apm?.NSSetLevel(Apm.NS_Level.VeryHigh);
        apm?.NS(true);
        apm?.AGC(true)
        apm?.AGCSetMode(Apm.AGC_Mode.FixedDigital)
        apm?.HighPassFilter(true)
        apm?.VAD(true)
        apm?.VADSetLikeHood(Apm.VAD_Likelihood.VeryLowLikelihood)
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