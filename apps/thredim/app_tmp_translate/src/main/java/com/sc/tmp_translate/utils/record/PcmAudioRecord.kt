package com.sc.tmp_translate.utils.record

import com.sc.tmp_translate.inter.IRecord
import com.sc.tmp_translate.utils.PcmRecord
import com.signway.aec.AEC
import timber.log.Timber


class PcmAudioRecord: IRecord {

    companion object {
       const val TAG = "PcmAudioRecordTAG"
    }

    var pcmRecord: PcmRecord? = null

    var aec: AEC? = null

    constructor() {
        init()
    }

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
        pcmRecord?.open(card, 0, 44100, 2)
//        aec?.open(card,0,16000,1);
    }

    override fun close() {
        pcmRecord?.close()
//        aec?.close()
    }

}