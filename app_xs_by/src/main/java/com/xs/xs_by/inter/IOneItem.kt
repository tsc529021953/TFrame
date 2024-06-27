package com.xs.xs_by.inter

import android.widget.CompoundButton
import com.xs.xs_by.bean.OneCtrlBean

interface IOneItem {

    fun onCheckedChanged(var1: OneCtrlBean?, var2: Boolean)

    fun onItemBtnClick(var1: OneCtrlBean?)

}
