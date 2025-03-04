package com.sc.tmp_cw.vm

import android.os.Environment
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
class PlaylistViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {


    }

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var videoListObs = MutableLiveData<ArrayList<FileBean>>()

    fun initData() {
        var path = Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_VIDEO
        val list = FileUtil.getDicFileBeansByExs(FileUtil.VIDEO_EXTENSIONS, path) ?: return
        val list3 = ArrayList<FileBean>()
        val recordList = ArrayList<FileBean>()
        val unRecordList = ArrayList<FileBean>()

        val local = spManager.getString(MessageConstant.SP_PLAYLIST, "")
        if (local.isNullOrEmpty()) {
            list!!.map {
                it.status = 1
            }
            recordList.addAll(list)
        } else {
            val record = gson.fromJson<ArrayList<FileBean>>(local, object : TypeToken<List<FileBean?>?>() {}.type)
            record.forEach { it2 ->
                val item = list!!.find { it3 -> it3.path == it2.path }
                if (item != null) {
                    item.status = 1
                    recordList.add(item)
                }
            }
            list!!.map { it ->
                val item = record.find { it2 -> it.path == it2.path }
                if (item != null) {
//                    recordList.add(it)
                } else {
                    unRecordList.add(it)
                }
            }
        }
        list3.addAll(recordList!!)
        list3.addAll(unRecordList!!)
        videoListObs.postValue(list3)
    }


}
