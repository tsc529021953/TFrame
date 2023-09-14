package com.sc.nft.bean

/**
 * author: sc
 * date: 2023/9/10
 */
class FileImgBean(var name: String? = null, var image: String? = null, var filename: String? = null, var file: String? = null
) {

    override fun toString(): String {
        return "FileImgBean(name=$name, image=$image, filename=$filename, file=$file)"
    }
}
