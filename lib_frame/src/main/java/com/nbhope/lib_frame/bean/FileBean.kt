package com.nbhope.lib_frame.bean

/**
 * @author  tsc
 * @date  2024/9/4 16:22
 * @version 0.0.0-1
 * @description
 */
class FileBean {

    companion object {
        const val UNKNOW = -1
        const val IMAGE = 0
        const val VIDEO = 1
    }

    var name: String = ""
    var path: String = ""

    /**
     * 后缀
     */
    var ex: String = ""
    var type: Int = UNKNOW
    var status: Int = 0

}
