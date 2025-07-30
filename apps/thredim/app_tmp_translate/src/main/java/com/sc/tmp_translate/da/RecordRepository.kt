package com.sc.tmp_translate.da

import android.os.Handler
import android.os.Looper
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.bean.TransTextBean

object RecordRepository {
    private val data = mutableListOf<TransRecordBean>()
    private val observers = mutableListOf<(List<TransRecordBean>) -> Unit>()
    private val lock = Any()

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

    fun addItem(item: TransRecordBean) {
        synchronized(lock) {
            data += item
            System.out.println("data?? ${data[data.size-1].text}")
            notifyObservers()
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
