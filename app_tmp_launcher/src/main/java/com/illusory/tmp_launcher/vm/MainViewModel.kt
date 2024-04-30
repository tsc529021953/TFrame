package com.illusory.tmp_launcher.vm

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.illusory.tmp_launcher.bean.AppInfo
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.AppUtils
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2024/4/26 14:43
 * @version 0.0.0-1
 * @description
 */
class MainViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {
        val AppList = arrayListOf<AppInfo>(
            AppInfo("无线投屏2", "com.android.settings"),
            AppInfo("白板书写2", "com.illusory.isdb"),
            AppInfo("文件管理2", "com.android.documentsui"),
            AppInfo("更多应用2", "com.android.settings/com.android.settings.SubSettings")
        )

        const val BASE_FILE = "/THREDIM_MEDIA/"
        const val CONFIG_FILE = "AppInfo.json"
    }

    private var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    fun initData() {

    }

    fun initAppData(context: Context, callback: () -> Unit) {
        mScope.launch {
            var path = Environment.getExternalStorageDirectory().absolutePath + BASE_FILE
            var file = File(path)
            val copyFun = {
                val res = FileUtil.copyAssetFile(context, CONFIG_FILE, path)
                Timber.i("KTAG copyFun $res")
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
                    val applist = gson.fromJson<ArrayList<AppInfo>>(json, object : TypeToken<List<AppInfo?>?>() {}.type)
                    Timber.i("KTAG json $json ${applist.size}")
                    for (i in 0 until applist.size) {
                        val app = applist[i]
                        if (i < AppList.size) {
                            AppList[i].name = app.name
                        }
                        Timber.i("KTAG app $app")
                    }
                    callback.invoke()
                } else {
                    copyFun.invoke()
                }
            }
        }
    }

    fun openMainAppItem(context: Context, index: Int) {
        val map = HashMap<String, String>()
//        map.put("selectType", "ktv")
        val pacakage = AppList[index]
        if (!pacakage.packageName.contains("/")) {
            AppUtils.startExtraApp(context, pacakage.packageName)
        } else {
            val arr = pacakage.packageName.split("/")
            AppUtils.startExtraApp(
                context, arr[0], arr[1], map
            )
        }
    }

}
