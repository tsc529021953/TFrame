package com.lib.network.vo

/**
 *  Create: enjie
 *  Date: 2021/4/13
 *  Describe:
 */
data class EduResource(
    val cataId: Long,
    val cataNo: Int,
    val createTime: Long,
    val createUser: Long,
    val delState: Boolean,
    val hasSave: Boolean,
    val itemAudioUrl: String,
    val itemComm: Boolean,
    val itemHit: Int,
    val itemLrc: String,
    val itemLrcUrl: String,
    val itemMent: Boolean,
    val itemOrder: Int,
    val itemPath: String,
    val itemState: Boolean,
    val itemTitle: String,
    val itemTop: Boolean,
    val refrenceId: Long,
    val updateTime: Long,
    val updateUser: Long
)