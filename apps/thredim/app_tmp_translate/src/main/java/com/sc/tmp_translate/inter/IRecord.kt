package com.sc.tmp_translate.inter

interface IRecord {

    var card: Int

    fun init()

    fun open(card: Int)

    fun close()

    fun onPcmData(data: ByteArray?)

}