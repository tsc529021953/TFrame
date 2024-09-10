package com.xs.xs_ctrl.vm

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.TimerHandler
import com.nbhope.lib_frame.utils.ValueHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    lateinit var timerHandler: TimerHandler

    var isLightViewObs = ObservableBoolean(true)

    var playStatus01Obs = ObservableBoolean(false)
    var playVolume01Obs = ObservableInt(0)
    var playStatus02Obs = ObservableBoolean(false)
    var playVolume02Obs = ObservableInt(0)
    var playStatus03Obs = ObservableBoolean(false)
    var playVolume03Obs = ObservableInt(0)
    var playStatus04Obs = ObservableBoolean(false)
    var playVolume04Obs = ObservableInt(0)
    var playStatus05Obs = ObservableBoolean(false)
    var playVolume05Obs = ObservableInt(0)
    var playStatus06Obs = ObservableBoolean(false)
    var playVolume06Obs = ObservableInt(0)
    var playStatus07Obs = ObservableBoolean(false)
    var playVolume07Obs = ObservableInt(0)

    fun initData() {

    }



}
