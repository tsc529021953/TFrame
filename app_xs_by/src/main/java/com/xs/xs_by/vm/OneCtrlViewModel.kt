package com.xs.xs_by.vm

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.xs.xs_by.R
import com.xs.xs_by.bean.OneCtrlBean
import com.xs.xs_by.bean.OneCtrlPage
import com.xs.xs_by.constant.BYConstants
import com.xs.xs_by.service.TmpServiceDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2024/4/26 14:43
 * @version 0.0.0-1
 * @description
 */
class OneCtrlViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseViewModel() {

    companion object {

        var PAGE_LIST = arrayListOf(
            OneCtrlPage(
                arrayListOf(
                    OneCtrlBean("背景", "0", "整体背景", 1),
                    OneCtrlBean("百子互动", "1", R.mipmap.ic_one_item2.toString()),
                    OneCtrlBean("龙凤动画", "2", R.mipmap.ic_one_item3.toString()),
                    OneCtrlBean("中间大盘", "3", R.mipmap.ic_one_item4.toString())
                )
            ),
            OneCtrlPage(
                arrayListOf(
                    OneCtrlBean("餐盘-内", "4", R.mipmap.ic_one_item5.toString()),
                    OneCtrlBean("餐盘-外", "5", R.mipmap.ic_one_item6.toString()),
                    OneCtrlBean("边缘动画", "6", R.mipmap.ic_one_item7.toString()),
                    OneCtrlBean("互动特效", "7", "互动特效", 1),
                )
            )
        )
    }

    private var mScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var gson = Gson()

    fun initData() {

    }

}
