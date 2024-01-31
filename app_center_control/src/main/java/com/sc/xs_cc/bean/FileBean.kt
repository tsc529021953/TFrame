package com.sc.xs_cc.bean

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

/**
 * author: sc
 * date: 2023/9/14
 */
class FileBean {

    var name = ObservableField<String>("")

    var fileName: String = ""

    var file: String = ""

    var text = ObservableField<String>("")

    var image: String? = null

    var files = ArrayList<FileImgBean>()

    var type = ObservableInt(-1)

//    var images =

//    var texts =

    var index = 0

    var loaded = false

    fun postValue(it: FileBean) {
        name.set(it.name.get())
        fileName = it.fileName
        file = it.file
        text.set(it.text.get())
        image = it.image
        files = it.files
        type.set(it.type.get())
        index = it.index
    }

    override fun toString(): String {
        return "FileBean(name=$name, fileName='$fileName', file='$file', text=$text, image=$image, files=$files, type=$type, index=$index, loaded=$loaded)"
    }


}
