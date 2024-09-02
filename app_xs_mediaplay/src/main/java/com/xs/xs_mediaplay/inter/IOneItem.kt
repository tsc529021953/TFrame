package com.xs.xs_mediaplay.inter

import com.xs.xs_mediaplay.bean.OneCtrlBean

interface IOneItem {

    fun onCheckedChanged(var1: OneCtrlBean?, var2: Boolean)

    fun onItemBtnClick(var1: OneCtrlBean?)

}
