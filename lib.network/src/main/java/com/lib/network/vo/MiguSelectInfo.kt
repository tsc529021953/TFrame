package com.lib.network.vo

/**
 * @Author qiukeling
 * @Date 2020-02-19-10:20
 * @Email qiukeling@nbhope.cn
 */
class MiguSelectInfo : MultiItemEntity{
    var name: String? = null
    var albumId: String? = null
    var dir: String? = null
    val refrenceId: Long = 0
    val singer: String? = null
    val id: String? = null
    var musicType: String? = null
    val slideImg = 0
    var albumType: Int  = 0
    var musicIds :ArrayList<String>? =null
    override val itemType: Int = 2

    override fun toString(): String {
        return "name:$name id:$albumId dir:$dir"
    }
}