package com.sc.tmp_translate.da

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.nbhope.lib_frame.utils.FileSpManager
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.constant.MessageConstant
import java.io.File

object RecordRepository {
    private val data = mutableListOf<TransRecordBean>()
    private val observers = mutableListOf<(List<TransRecordBean>) -> Unit>()
    private val lock = Any()
    private val gson = Gson()

    fun getData(): List<TransRecordBean> = synchronized(lock) { data.toList() }

    fun addObserver(observer: (List<TransRecordBean>) -> Unit) {
        synchronized(lock) {
            observers += observer
            observer(data) // 初始推送一次
        }
    }

    fun removeObserver(observer: (List<TransRecordBean>) -> Unit) {
        synchronized(lock) { observers -= observer }
    }

    fun addItem(item: TransRecordBean, spManager: SharedPreferencesManager) {
        synchronized(lock) {
            data += item
            notifyObservers()
            spManager.setString(MessageConstant.SP_TRANS_RECORD, gson.toJson(data))
        }
    }

    fun removeItem(item: TransRecordBean, spManager: SharedPreferencesManager) {
        synchronized(lock) {
            data -= item
            notifyObservers()
            spManager.setString(MessageConstant.SP_TRANS_RECORD, gson.toJson(data))
            val file = File(item.path)
            if (file.exists() && file.isFile) {
                file.delete()
            }
        }
    }

    fun updateItems(newData: List<TransRecordBean>) {
        synchronized(lock) {
            data.clear()
            data.addAll(newData)
            notifyObservers()
        }
    }

    private fun notifyObservers() {
        val snapshot = data.toList()
        val mainHandler = Handler(Looper.getMainLooper())
        observers.forEach { observer ->
            mainHandler.post { observer(snapshot) }
        }
    }
}
