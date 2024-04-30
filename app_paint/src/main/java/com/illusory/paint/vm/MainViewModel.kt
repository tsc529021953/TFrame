package com.illusory.paint.vm

import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

/**
 * @author  tsc
 * @date  2024/4/30 15:11
 * @version 0.0.0-1
 * @description
 */
class MainViewModel @Inject constructor(val spManager: SharedPreferencesManager): BaseViewModel() {

    var mScope =  CoroutineScope(Dispatchers.IO + SupervisorJob())

}
