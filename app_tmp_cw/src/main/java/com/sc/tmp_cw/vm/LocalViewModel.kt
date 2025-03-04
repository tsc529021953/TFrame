package com.sc.tmp_cw.vm

import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.view.Surface
import android.view.SurfaceHolder
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
//import androidx.media3.common.MediaItem
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.exoplayer.SimpleExoPlayer
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.bean.FileBean
import com.nbhope.lib_frame.utils.FileUtil
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.lib_frame.utils.TimerHandler
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.sc.tmp_cw.constant.MessageConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.EOFException
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

    var player: IjkMediaPlayer? = null
//    var player: ExoPlayer? = null
//    var player: MediaPlayer? = null
    var surfaceHolder: SurfaceHolder? = null
    var surface: Surface? = null

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

    fun checkVideo(check: Boolean = true, checkPlay: Boolean = false) {
        if (check) {
            val record = spManager.getString(MessageConstant.SP_PLAYLIST_CHECK, "")
            if (record == lastStr) {
                System.out.println("相同数据，无需校验！ $checkPlay")
                if (checkPlay) {
                    try {
                        player?.start()
//                        player?.play()
//                        setIndex(0)
                    } catch (e: Exception) {
                        Timber.i("checkVideo replay ${e.message}")
                        e.printStackTrace()
                    }
                }
                return
            } else lastStr = record
        }

        val local = spManager.getString(MessageConstant.SP_PLAYLIST, "")
        val list3 = ArrayList<FileBean>()
        if (local.isNullOrEmpty()) {
            list?.let { list3.addAll(it) }
        } else {
            val record = gson.fromJson<ArrayList<FileBean>>(local, object : TypeToken<List<FileBean?>?>() {}.type)
            record.forEach { it2 ->
                val item = list!!.find { it3 -> it3.path == it2.path }
                if (item != null) {
                    it2.status = 1
                    list3.add(it2)
                }
            }
//            list!!.map { it ->
//                val item = record.find { it2 -> it.path == it2.path }
//                if (item != null) {
//                    list3.add(it)
//                    it.status = 1
//                } else it.status = 0
//            }
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
        if (!File(item.path).exists()) {
            mScope.launch(Dispatchers.Main) {
                ToastUtil.showS("未检测到视频 ${item.name},将重新加载")
            }
            initData()
            return
        }
        val uri = Uri.fromFile(File(item.path))
//        var mediaItem = MediaItem.fromUri(uri)
//        player!!.setMediaItem(mediaItem)
//        player!!.prepare()
//        player!!.play()


        player?.dataSource = item.path
        player?.prepareAsync()
        player?.start()
        Timber.i("播放 ${item.name}")
//        player?.reset()
//        player?.setDataSource(item.path)
//        player?.prepareAsync()

    }

    fun stop() {
        if (videoListObs.value == null) return
        try {
            player?.stop()
//            player?.next()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        player?.release()
    }

    fun mute(isMute: Boolean) {
//        if (isMute)
//            player?.volume = 0f
//        else player?.volume = 1.0f
        if (isMute)
            player?.setVolume(0f, 0f)
        else player?.setVolume(1f, 1f)
    }

}
