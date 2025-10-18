package com.sc.common.vm

import android.content.Context
import android.os.Environment
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.common.bean.AppInfoBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/26 14:43
 * @version 0.0.0-1
 * @description
 */
class MainViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {

        const val BASE_FILE = "/THREDIM_MEDIA/"
        const val CONFIG_FILE = "AppInfo.json"
    }

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var playStatusObs = ObservableBoolean(false)

    var filesObs = MutableLiveData<List<File>>()

    var isImage: Boolean? = null

    var playIndex = 0

    lateinit var timerHandler: TimerHandler

    var url = ""

    var type = 0

    fun initData() {

    }

    fun initAppData(context: Context, copy: Boolean = true, callback: () -> Unit) {
        mScope.launch {
            var path = Environment.getExternalStorageDirectory().absolutePath + BASE_FILE
            var file = File(path)
            val copyFun = {
                val res = FileUtil.copyAssetFile(context, CONFIG_FILE, path)
                Timber.i("KTAG copyFun $res")
                if (copy)
                    initAppData(context, false, callback)
            }
            path += CONFIG_FILE
            if (!file.exists()) {
                file.mkdirs()
                Timber.i("KTAG 路径不存在 $path")
                // copy
                copyFun.invoke()
            } else {
                file = File(path)
                if (file.exists()) {
                    val json = FileUtil.readFile(path)
                    val netBean = gson.fromJson(json, AppInfoBean::class.java)
                    url = netBean.url ?: url
                    type = netBean.type
                    callback.invoke()
                } else {
                    copyFun.invoke()
                }
            }
        }
    }


}
