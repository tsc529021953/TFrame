package com.nbhope.phmina.bean.data


/**
 *Created by ywr on 2021/7/26 15:18
 */
data class MinaSong(
    var id: Int?,
    //歌曲唯一id，本地歌曲为数据库歌曲id，云端歌曲为云端返回唯一id
    val thirdId: String,
    //歌曲类别 以下4种类别
    val thirdType: String,
    //歌词 云端歌曲返回
    var lrcUrl: String?,
    //歌曲标题
    val title: String?,
    //歌曲标题拼音排序
    val titlePinyin: String?,
    //作者
    val artist: String?,
    //作者拼音排序
    val artistPinyin: String?,
    //歌曲专辑名
    val album: String?,
    //歌曲专辑名拼音排序
    val albumPinyin: String?,
    //歌曲专辑id 用于蜻蜓请求url
    val albumId: String?,
    //歌曲地址 本地为本地歌曲地址，云端为网络地址
    var path: String?,
    //歌曲图片
    var img: String?,
    var md5info: String? = null,
    var url: String? = null
) {
    companion object {
        val TYPE_LOCAL = "local"
        val TYPE_MIGU = "migu"


    }
}