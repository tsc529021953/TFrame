package com.nbhope.lib_frame.viewmodel

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.FileUtil
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/15 9:26
 * @version 0.0.0-1
 * @description
 */
class LogViewModel @Inject constructor() : BaseViewModel() {

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var textList = MutableLiveData<ArrayList<FileBean>>()

    fun initData() {
        var path = Environment.getExternalStorageDirectory().absolutePath + "/Android/data/" + HopeBaseApp.getContext().packageName + "/cache/"
        val list3 = ArrayList<FileBean>()
        list3.addAll(FileUtil.getDicFileBeansByExs(FileUtil.TEXT_EXTENSIONS, path))
        textList.postValue(list3)
        System.out.println("日志数量 ${list3?.size} $path")
    }

    fun loadLog(bean: FileBean, cb : (msg: String) -> Unit) {
//        mScope.cancel()
        mScope.launch {
            FileUtil.readFile(bean.path)?.let { cb.invoke(it) }
        }
    }

}
