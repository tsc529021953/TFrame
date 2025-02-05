package com.sc.tmp_cw.vm

import android.net.Uri
import android.os.Environment
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
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
import timber.log.Timber
import java.io.File
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/26 14:43
 * @version 0.0.0-1
 * @description
 */
class LocalViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {


    }

    var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    var player: SimpleExoPlayer? = null

    var videoListObs = MutableLiveData<ArrayList<FileBean>>()

    var list: List<FileBean>? = null

    var playStatusObs = ObservableBoolean(false)
    var playIndex = 0

    var lastStr = ""

    fun initData() {
        var path = Environment.getExternalStorageDirectory().absolutePath + MessageConstant.PATH_VIDEO
        list = FileUtil.getDicFileBeansByExs(FileUtil.VIDEO_EXTENSIONS, path) ?: return
        lastStr = spManager.getString(MessageConstant.SP_PLAYLIST_CHECK, "")
        checkVideo(false)
    }

    fun checkVideo(check: Boolean = true) {
        if (check) {
            val record = spManager.getString(MessageConstant.SP_PLAYLIST_CHECK, "")
            if (record == lastStr) {
                System.out.println("相同数据，无需校验！")
                return
            } else lastStr = record
        }

        val local = spManager.getString(MessageConstant.SP_PLAYLIST, "")
        val list3 = ArrayList<FileBean>()
        if (local.isNullOrEmpty()) {

        } else {
            val record = gson.fromJson<ArrayList<FileBean>>(local, object : TypeToken<List<FileBean?>?>() {}.type)
            list!!.map { it ->
                val item = record.find { it2 -> it.path == it2.path }
                if (item != null) {
                    list3.add(it)
                    it.status = 1
                } else it.status = 0
            }
        }
        videoListObs.postValue(list3)
    }

    fun next() {
        if (videoListObs.value == null) return
        if (playIndex + 1 >= videoListObs.value!!.size) setIndex(0)
        else setIndex(playIndex + 1)
    }

    fun pre() {
        if (videoListObs.value == null) return
        if (playIndex - 1 < 0) setIndex(videoListObs.value!!.size - 1)
        else setIndex(playIndex - 1)
    }

    private fun setIndex(index: Int) {
        if (videoListObs.value == null) return
        if (index < 0 || index >= videoListObs.value!!.size) return
        val fileBean = videoListObs.value!!.get(index)
        play(fileBean)
    }

    fun play(item: FileBean) {
        if (videoListObs.value == null) return
        playIndex = videoListObs.value!!.indexOf(item)
        val uri = Uri.fromFile(File(item.path))
        var mediaItem = MediaItem.fromUri(uri)
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.play()
    }

    fun stop() {
        if (videoListObs.value == null) return
        try {
            player?.stop()
            player?.next()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
