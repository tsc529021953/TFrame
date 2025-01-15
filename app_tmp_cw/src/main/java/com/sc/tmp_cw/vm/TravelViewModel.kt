package com.sc.tmp_cw.vm

import android.os.Environment
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.TimerHandler
import com.sc.tmp_cw.constant.MessageConstant
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
class TravelViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {


    }

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var list: List<FileBean>? = null
    var smallListObs = MutableLiveData<ArrayList<FileBean>>()
    var bigList: List<FileBean>? = null
    var videoList: List<FileBean>? = null
    var textList: List<FileBean>? = null


    fun initData() {
        var path = Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_TRAVEL
        list = FileUtil.getDicFileBeansByExs(FileUtil.PICTURES_EXTENSIONS, path) ?: return
        val list2 = list!!.filter { !it.name.endsWith("_big") }
        val list3 = ArrayList<FileBean>()
        list3.addAll(list2)
        smallListObs.postValue(list3)

        bigList = list!!.filter { it.name.endsWith("_big") }
        textList = FileUtil.getDicFileBeansByExs(FileUtil.TEXT_EXTENSIONS, path)
        videoList = FileUtil.getDicFileBeansByExs(FileUtil.VIDEO_EXTENSIONS, path)
    }


}
