package com.sc.nft.vm

import android.os.Environment
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.sc.nft.bean.FileImgBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileFilter

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

    var fileImg2 = FileImgBean()
    var fileImg2Content = ObservableField<String>("")
    var fileImg3 = FileImgBean()

    private val mFilter = FileFilter { pathname ->
        Timber.i("NTAG pathname $pathname")
        pathname.name.endsWith(".txt")
    }

    fun clickFileImg3(file: FileImgBean?) {

    }

    fun clickFileImg2(file: FileImgBean?) {
        if (file == null) return
        fileImg2 = file!!
        fileImg2List.clear()
        val path = Environment.getExternalStorageDirectory().absolutePath + BASE_PATH + "/" + fileImg2.filename
        val f = File(path)
        Timber.i("NTAG file $path")
        if (!f.exists()) { //判断路径是否存在
            Timber.i("NTAG getDirs !exists")
        }
        Timber.i("NTAG getDirs exists ${f.absolutePath} ${f.list().size} ${f.listFiles().size}")
        var files: Array<out File> = f.listFiles() ?: return
        for (_file in files) {
            Timber.i("NTAG _file ${_file.absolutePath} ${_file.name}")
            if (_file.isDirectory) {
                var imgs2 = FileImgBean(_file.name.substring(1), filename = _file.name)
                fileImg2List.add(imgs2)
            }
        }
        val fs = FileUtils.listFilesInDirWithFilter(path, mFilter, false)
        Timber.i("NTAG fs ${fs.size}")
        if (fs.size > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                FileUtil.readFile(fs[0].absolutePath)?.also {
                    fileImg2Content.set(it)
                }
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
                var imgs1 = FileImgBean(_file.name.substring(1), filename = _file.name)
                fileImg1List.add(imgs1)
            }
        }
    }

}
