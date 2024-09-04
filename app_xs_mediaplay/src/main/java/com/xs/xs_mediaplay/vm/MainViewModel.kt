package com.xs.xs_mediaplay.vm

import android.animation.ObjectAnimator
import android.os.Environment
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    private var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var playStatusObs = ObservableBoolean(false)

    var filesObs = MutableLiveData<List<File>>()

    fun initData() {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/XS/"
        val files = FileUtil.getDicVideoImageByExs(path)
        filesObs.postValue(files)
    }
}
