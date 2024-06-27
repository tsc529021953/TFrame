package com.xs.xs_by.bean

import androidx.databinding.ObservableBoolean

class OneCtrlBean constructor(
    var name: String = "",
    var id: String? = "",
    var img: String? = null,
    var imgType: Int = 0,
    var showReload: Boolean = false,
    var theme: String = "亲子互动",
    var switchObs: ObservableBoolean = ObservableBoolean(false)
) {


    constructor(bean: SceneBean) : this(bean.name, bean.id, bean.img, bean.imgType, bean.showReload, bean.theme , ObservableBoolean(bean.switch)) {

    }

    fun equal(bean: OneCtrlBean): Boolean {
        return id == bean.id && name == bean.name && img == bean.img && imgType == bean.imgType && switchObs.get() == bean.switchObs.get()
    }

    inner class SceneBean(
        var name: String = "",
        var id: String? = "",
        var img: String? = null,
        var imgType: Int = 0,
        var switch: Boolean = false,
        var showReload: Boolean = false,
        var theme: String = "亲子互动",
    ) {
        override fun toString(): String {
            return "SceneBean(name='$name', id=$id, img=$img, imgType=$imgType, switch=$switch)"
        }
    }
}
