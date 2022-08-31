package com.lib.network.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.ObservableBoolean
import java.util.*
import kotlin.collections.ArrayList

/**
 *Created by ywr on 2021/4/6 17:01
 */
class AutoBean {

    /**
     *{
    "autoName": "联动一",		//联动信息名称
    "autoStatus": true,			//当前状态（0：关闭，1：开启）
    "autoId": 817956676673064960	//当前联动信息唯一编号
    }
     */

    var autoName: String? = null
    var autoStatus: Boolean = false
    var autoId: Long? = null

}