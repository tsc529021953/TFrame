package com.sc.tmp_translate.utils

interface IPcmRecord {

    fun onPcmData(card: Int, data: ByteArray?)

}