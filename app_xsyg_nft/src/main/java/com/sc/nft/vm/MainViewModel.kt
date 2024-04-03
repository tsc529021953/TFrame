package com.sc.nft.vm

import android.os.Environment
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.FileUtil
import com.sc.nft.bean.FileBean
import com.sc.nft.bean.FileImgBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifDrawable
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

        const val FILENAME = "filename"
        const val FILE = "file"
        const val NAME = "name"
        const val INDEX = "index"
        const val TYPE = "type"

        const val TYPE_UN_INIT = -1
        const val TYPE_FIRST = 1
        const val TYPE_FILE = 2
        const val TYPE_IMAGE = 3
    }

    var fileImgType = ObservableInt(0)
    var fileImgTitle = ObservableField<String>("")
    var fileImgItem: FileImgBean? = null

    var fileBean: FileBean? = null
    var fileImgList = ArrayList<FileImgBean>()

    fun load(fileBean: FileBean?, callBack: ((fileBean: FileBean) -> Unit)) {
        if (fileBean == null) return
        if (fileBean.loaded) {
            callBack.invoke(fileBean)
            return
        }
        fileBean.files.clear()
        viewModelScope.launch(Dispatchers.IO) {
            val f = File(fileBean.file)
            if (!f.exists()) {
                Timber.e("NTAG 路径不存在 ${fileBean.file}")
                return@launch
            }
            val images = FileUtils.listFilesInDirWithFilter(fileBean.file, mFilter3, false)
            val texts = FileUtils.listFilesInDirWithFilter(fileBean.file, mFilter2, false)
            if (texts.size > 0) {
                FileUtil.readFile(texts[0].absolutePath)?.also {
                    fileBean.text.set(it)
                }
            }
            if (fileBean.type.get() == 1 || images.size <= 0) {
                // 目录类
                if (fileBean.type.get() != 1) fileBean.type.set(TYPE_FILE)
                var files: Array<out File> = f.listFiles()
                files.reverse()
                if (!files.isNullOrEmpty()) {
//                    files.reverse()
                    for (_file in files) {
                        Timber.i("NTAG _file ${_file.absolutePath} ${_file.name}")
                        if (_file.isDirectory) {
                            var file = FileImgBean(_file.name.substring(1), filename = _file.name, file = _file.absolutePath)
                            fileBean.files.add(file)
                        }
                    }
                    fileBean.files.reverse()
                }
            } else {
                // 按图片来
                fileBean.type.set(TYPE_IMAGE)
                fileBean.image = images[0].absolutePath
            }
            fileBean.loaded = true
            callBack.invoke(fileBean)
        }
    }

    fun clickFileImg(file: FileImgBean?, index: Int = 0) {
        if (file == null) return
        Timber.i("NTAG clickFileImg $index ${file.filename} ${file.name}")
        fileIndex = index
        fileImg3 = file!!
        fileImgTitle.set(file.name)
        val path =
            Environment.getExternalStorageDirectory().absolutePath + BASE_PATH + "/" + fileImg2.filename + "/" + fileImg3.filename
        val f = File(path)
        if (!f.exists()) { //判断路径是否存在
            Timber.i("NTAG getDirs !exists")
        }
        imgFiles = FileUtils.listFilesInDirWithFilter(path, mFilter3, false)
        fileImg3Img.set("")
        Timber.i("NTAG fs ${imgFiles.size}")
        if (imgFiles.size > 0) {
            fileImg3Img.set(imgFiles[0].absolutePath)
        }

        textFiles = FileUtils.listFilesInDirWithFilter(path, mFilter2, false)
        fileImg3Content.set("")
        Timber.i("NTAG fs ${textFiles.size}")
        if (textFiles.size > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                FileUtil.readFile(textFiles[0].absolutePath)?.also {
                    fileImg3Content.set(it)
                }
            }
        }
    }

    var fileImg1List = ArrayList<FileImgBean>()
    var fileImg2List = ArrayList<FileImgBean>()
//    var fileImg3List = ArrayList<FileImgBean>()

    var fileImg2 = FileImgBean()
    var fileImg2Content = ObservableField<String>("")
    var fileImg3 = FileImgBean()
    var fileImg3Title = ObservableField<String>("")
    var fileImg3Content = ObservableField<String>("")
    var fileImg3Img = ObservableField<String>("")

    var imgFiles: List<File> = ArrayList<File>()
    var textFiles: List<File> = ArrayList<File>()
    var fileIndex = 0;

    private val mFilter2 = FileFilter { pathname ->
        val ex = pathname.name.lowercase()
        ex.endsWith(".txt")
    }
    private val mFilter3 = FileFilter { pathname ->
        Timber.i("NTAG pathname $pathname")
        val ex = pathname.name.lowercase()
        ex.endsWith(".jpg") || ex.endsWith(".png") || ex.endsWith(".gif")
    }

    fun clickFileImg3(file: FileImgBean?, index: Int = 0) {
        if (file == null) return
        Timber.i("NTAG clickFileImg3 $index ${file.filename} ${file.name}")
        fileIndex = index
        fileImg3 = file!!
        fileImg3Title.set(fileImg3.name)
        val path =
            Environment.getExternalStorageDirectory().absolutePath + BASE_PATH + "/" + fileImg2.filename + "/" + fileImg3.filename
        val f = File(path)
        if (!f.exists()) { //判断路径是否存在
            Timber.i("NTAG getDirs !exists")
        }
        imgFiles = FileUtils.listFilesInDirWithFilter(path, mFilter3, false)
        fileImg3Img.set("")
        Timber.i("NTAG fs ${imgFiles.size}")
        if (imgFiles.size > 0) {
            fileImg3Img.set(imgFiles[0].absolutePath)
        }

        textFiles = FileUtils.listFilesInDirWithFilter(path, mFilter2, false)
        fileImg3Content.set("")
        Timber.i("NTAG fs ${textFiles.size}")
        if (textFiles.size > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                FileUtil.readFile(textFiles[0].absolutePath)?.also {
                    fileImg3Content.set(it)
                }
            }
        }
    }

    fun goHome() {

    }

    fun next(view: View) {
        val index = fileIndex + 1
        val max = MainViewModel.getInstance().fileImg2List.size
        if (index >= max) {
            ToastUtils.showShort("已到最后一个！")
            return
        }
        clickFileImg3(fileImg2List[index], index)
    }

    fun prev(view: View) {
        val index = fileIndex - 1
        if (index < 0) {
            ToastUtils.showShort("已到最开始位置！")
            return
        }
        clickFileImg3(fileImg2List[index], index)
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
        val fs = FileUtils.listFilesInDirWithFilter(path, mFilter2, false)
        Timber.i("NTAG fs ${fs.size}")
        fileImg2Content.set("")
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
        files.reverse()
        for (_file in files) {
            Timber.i("NTAG _file ${_file.absolutePath} ${_file.name}")
            if (_file.isDirectory) {
                var imgs1 = FileImgBean(_file.name.substring(1), filename = _file.name, file = _file.absolutePath)
                fileImg1List.add(imgs1)
            }
        }
    }

}
