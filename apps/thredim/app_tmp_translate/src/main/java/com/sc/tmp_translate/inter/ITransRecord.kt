package com.sc.tmp_translate.inter

interface ITransRecord {

    fun onRecordEnd(isMaster: Boolean, path: String?)

    fun onReceiveRes(isMaster: Boolean, path: String?, resList: List<String>)

}