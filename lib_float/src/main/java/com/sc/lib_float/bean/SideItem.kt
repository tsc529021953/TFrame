package com.sc.lib_float.bean

import android.view.View

/**
 * @author  tsc
 * @date  2024/5/28 16:43
 * @version 0.0.0-1
 * @description
 */
class SideItem(var content: String, var id: Int, var callback: ((view: View) -> Unit)? = null) {

}
