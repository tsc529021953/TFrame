package com.sc.tmp_translate.utils.record

import android.os.Environment
import com.sc.tmp_translate.inter.IRecord
import com.sc.tmp_translate.utils.PcmRecord
import com.signway.aec.AEC
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PcmAudioRecord(): IRecord {

    companion object {
        const val TAG = "PcmAudioRecordTAG"

        const val BYTE_ARRAY_SIZE = 1200
        const val RATE = 44100
    }

    var pcmRecord: PcmRecord? = null

    var aec: AEC? = null

    var tempLength = 0
    var tempArray = ByteArray(BYTE_ARRAY_SIZE)
    private val queue: Queue<ByteArray> = LinkedList()
    private var isEnd = false

    var fos: FileOutputStream? = null
    var index = 0

    constructor(index: Int) : this() {
        this.index = index
        init()
    }

    override var card: Int = -1

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
    }

    override fun open(card: Int) {
        Thread {
            Timber.i("线程开始")

            while (!isEnd) {
                if (queue.size > 0) {
                    val datas = queue.poll() // BLEUtils.mergeQueueBytes(queue) //
                    if (datas != null) {
                        fos?.write(datas)
////                        ADPCMDecoder2.reset()
////                        val d3 = ADPCMDecoder2.decodeToByteArray(datas)
//
////                        fos2?.write(datas)
//                        val d3 = adpcmCodec.decode2(datas) // ADPcm2PcmUtil.decodeADPcmStream(state, datas)
////                        fos2?.write(d3)
//////                        apm.ProcessCaptureStream()
//////                        val d4 = BLEUtils.applyGainToPcmByteArray(d3, 4f)
//                        val d4 = processWebRTC(d3) //
////                        val hasVoice = apm?.VADHasVoice() ?: true
//////                        log("VAD $hasVoice")
////                        if (hasVoice) {
//                        iHandset?.onReceiveVoice(d4) // BLEUtils.monoToStereoFast(d4)
                    }
                }
            }
            Timber.i("线程结束")
        }.start()
        createOutputFile(index)
        this.card = card
        pcmRecord?.open(card, 0, RATE, 2)
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
        println("card " + card + "onPcmData: " + data!!.size + " bytes")
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


}