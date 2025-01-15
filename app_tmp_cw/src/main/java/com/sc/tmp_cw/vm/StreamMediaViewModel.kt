package com.sc.tmp_cw.vm

import com.google.android.exoplayer2.SimpleExoPlayer
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import timber.log.Timber
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2025/1/8 15:44
 * @version 0.0.0-1
 * @description
 */
class StreamMediaViewModel@Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    var rtspURL = ""

    lateinit var player: SimpleExoPlayer

    var initListener = false

    init {
        Timber.i("StreamMediaViewModel init")
    }

}
