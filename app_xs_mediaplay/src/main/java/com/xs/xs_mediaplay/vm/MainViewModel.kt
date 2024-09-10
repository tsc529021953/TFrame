package com.xs.xs_mediaplay.vm

import android.animation.ObjectAnimator
import android.os.Environment
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
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

    var playStatusObs = ObservableBoolean(false)

    var filesObs = MutableLiveData<List<File>>()

    lateinit var player: SimpleExoPlayer

    var isImage: Boolean? = null

    var playIndex = 0

    lateinit var timerHandler: TimerHandler

    fun initData() {
        Environment.getExternalStorageDirectory().absolutePath
        val path = Environment.getExternalStorageDirectory().absolutePath + "/XS/"
        val files = FileUtil.getDicVideoImageByExs(path)
        Timber.i("initData path $path ${files.size}")
        filesObs.postValue(files)
    }

    fun playOrPause() {
        ValueHolder.setValue(300) {
            if (playStatusObs.get()) {
                playStatusObs.set(false)
                if (isImage == true) {
                    timerHandler.stop()
                    return@setValue
                }
                player.pause()
            } else {
                playStatusObs.set(true)
                if (isImage == true) {
                    timerHandler.start()
                    return@setValue
                }
                player.play()
            }
        }
    }

    fun pre(onIndexChanged: () -> Unit) {
        playIndex--
        if (playIndex < 0) {
            playIndex = filesObs.value!!.size - 1
        }
        onIndexChanged.invoke()
    }

    fun next(onIndexChanged: () -> Unit) {
        playIndex++
        if (playIndex >= filesObs.value!!.size) {
            playIndex = 0
        }
        onIndexChanged.invoke()
    }

}
