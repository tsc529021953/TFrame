package com.nbhope.lib_frame.utils.view

import android.graphics.Color
import android.view.Gravity
import androidx.drawerlayout.widget.DrawerLayout

object DrawLayoutUtils {

    fun initDL(dl: DrawerLayout) {
        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        dl.setScrimColor(Color.TRANSPARENT)
    }

    fun openDL(dl: DrawerLayout, gravity: Int = Gravity.RIGHT) {
        dl.openDrawer(gravity)
    }

    fun closeDL(dl: DrawerLayout, gravity: Int = Gravity.RIGHT) {
        dl.closeDrawer(gravity)
    }

}