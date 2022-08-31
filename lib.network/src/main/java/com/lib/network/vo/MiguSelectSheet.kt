package com.lib.network.vo

import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author qiukeling
 * @Date 2020-02-19-10:20
 * @Email qiukeling@nbhope.cn
 */
class MiguSelectSheet : MultiItemEntity {
    var name: String? = null
    val type: String? = null
    var columnId: String? = null
    val refrenceId: Long = 0
    var albumIds: ArrayList<String>?=null
    var infos: ArrayList<MiguSelectInfo>? = null
    var albumType = 0
    override val itemType: Int = 1
}