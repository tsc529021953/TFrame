package com.sc.tmp_translate.vm

import android.view.View
import androidx.databinding.ObservableBoolean
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.sc.tmp_translate.constant.MessageConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class TranslatingViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()
}
