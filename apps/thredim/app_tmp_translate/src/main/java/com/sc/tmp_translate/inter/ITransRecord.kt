package com.sc.tmp_translate.inter

interface ITransRecord {

    fun onRecordEnd(isMaster: Boolean, path: String?)

    fun onReceiveRes(isMaster: Boolean, path: String?, resList: List<String>)

    fun onReceiveToView(isMaster: Boolean, pcm: ByteArray?, pcmList: List<ByteArray>?, )

    fun onTransStateChange(isMaster: Boolean, isTrans: Boolean)
}