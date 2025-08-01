package com.sc.tmp_translate.da

import android.os.Handler
import android.os.Looper
import com.nbhope.lib_frame.utils.FileSpManager
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.bean.TransTextBean
import com.sc.tmp_translate.service.TmpServiceImpl
import java.io.File

object TransRepository {
    private val data = mutableListOf<TransTextBean>()
    private val observers = mutableListOf<(List<TransTextBean>) -> Unit>()
    private val lock = Any()

    fun getData(): List<TransTextBean> = synchronized(lock) { data.toList() }

    fun addObserver(observer: (List<TransTextBean>) -> Unit) {
        synchronized(lock) {
            observers += observer
            observer(data) // 初始推送一次
        }
    }

    fun removeObserver(observer: (List<TransTextBean>) -> Unit) {
        synchronized(lock) { observers -= observer }
    }

    fun addItem(item: TransTextBean) {
        synchronized(lock) {
            data += item
            notifyObservers()
        }
    }

    fun addItem(item: TransTextBean, lang: String, path: String, service: TmpServiceImpl) {
        synchronized(lock) {
            data += item
            notifyObservers()
            try {
                val file = File(path)
                if (file.exists() && file.isFile) {
                    file.delete()
                }
            } catch (e: Exception) {  e.printStackTrace() }
            // 添加到记录中
            service.transRecordBean?.beans?.add(item)
//            val bean = TransRecordBean()
//            bean.lang = lang
//            bean.path = path
//            bean.bean = item
//            RecordRepository.addItem(bean, spManager)
        }
    }

    fun updateItems(newData: List<TransTextBean>) {
        synchronized(lock) {
            data.clear()
            data.addAll(newData)
            notifyObservers()
        }
    }

    fun clear() {
        synchronized(lock) {
            data.clear()
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
