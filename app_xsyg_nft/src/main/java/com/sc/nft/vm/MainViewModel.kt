package com.sc.nft.vm

import android.os.Environment
import com.nbhope.lib_frame.base.BaseViewModel
import com.sc.nft.bean.FileImgBean
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File

/**
 * author: sc
 * date: 2023/9/10
 */
class MainViewModel : BaseViewModel() {

    companion object {
        private var instance: MainViewModel? = null

        fun getInstance(): MainViewModel {
            if (instance == null)
                instance = MainViewModel()
            return instance!!
        }

        const val BASE_PATH = "/TImages/"
    }

    var fileImg1List = ArrayList<FileImgBean>()
    var fileImg2List = ArrayList<FileImgBean>()
    var fileImg3List = ArrayList<FileImgBean>()

    var fileImg2 = ""
    var fileImg2Content = ""
    var fileImg3 = ""

    fun clickFileImg2(file: String?) {
        if (file == null) return
        fileImg2 = file!!
        fileImg2List.clear()
        val path = Environment.getExternalStorageDirectory().absolutePath + BASE_PATH + "/" + fileImg2
        val f = File(path)
        Timber.i("NTAG file ${Environment.getExternalStorageDirectory().absolutePath}")
        if (!f.exists()) { //判断路径是否存在
            Timber.i("NTAG getDirs !exists")
        }
        Timber.i("NTAG getDirs exists ${f.absolutePath} ${f.list()} ${f.listFiles()}")
        val files: Array<out File> = f.listFiles() ?: return
        for (_file in files) {
            Timber.i("NTAG _file ${_file.absolutePath} ${_file.name}")
            if (_file.isDirectory) {
                var imgs2 = FileImgBean(_file.name.substring(1))
                fileImg2List.add(imgs2)
            }
        }
    }

    fun getDirs(callBack: ((type: Int) -> Unit)) {
        fileImg1List.clear()
        val path = Environment.getExternalStorageDirectory().absolutePath + BASE_PATH
        val f = File(path)
        Timber.i("NTAG file ${Environment.getExternalStorageDirectory().absolutePath}")
        if (!f.exists()) { //判断路径是否存在
            Timber.i("NTAG getDirs !exists")
        }
        Timber.i("NTAG getDirs exists ${f.absolutePath} ${f.list()} ${f.listFiles()}")
        val files: Array<out File> = f.listFiles() ?: return
        for (_file in files) {
            Timber.i("NTAG _file ${_file.absolutePath} ${_file.name}")
            if (_file.isDirectory) {
                var imgs1 = FileImgBean(_file.name.substring(1))
                fileImg1List.add(imgs1)
            }
        }
    }

}