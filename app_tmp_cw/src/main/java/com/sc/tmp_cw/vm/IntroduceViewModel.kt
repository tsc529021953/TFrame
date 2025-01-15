package com.sc.tmp_cw.vm

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.R
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
class IntroduceViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {

        var fileBean: FileBean? = null
        var bigList: List<FileBean>? = null
        var videoList: List<FileBean>? = null
        var textList: List<FileBean>? = null

    }

    val textObs = ObservableField<String>("")

    val typeObs = ObservableBoolean(true) // image video

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var player: SimpleExoPlayer? = null

    fun initData(context: Context) {
        // 首先判断大图
    }

    override fun onCleared() {
        super.onCleared()
        fileBean = null
        bigList = null
        videoList = null
        textList = null
    }


}
