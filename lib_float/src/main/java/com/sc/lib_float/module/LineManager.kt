package com.sc.lib_float.module

import android.R.attr.bitmap
import android.app.Application
import android.os.Environment
import android.view.View
import android.widget.ImageView
import com.nbhope.lib_frame.dialog.TipNFDialog
import com.nbhope.lib_frame.utils.AppUtils
import com.nbhope.lib_frame.utils.ShellUtil
import com.nbhope.lib_frame.utils.ValueHolder
import com.nbhope.lib_frame.widget.IconFontView
import com.sc.lib_float.R
import com.sc.lib_float.inter.IPaint
import com.sc.lib_float.widget.DrawView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.OutputStream
import java.util.HashMap


/**
 * @author  tsc
 * @date  2024/4/7 19:00
 * @version 0.0.0-1
 * @description
 */
class LineManager constructor(var mScope: CoroutineScope, var application: Application, var isFloat: Boolean = true): IPaint {

    /*view 版块*/
    var rootView: View? = null
    var iconIv: ImageView? = null
    var line1Ly: View? = null
    var line2Ly: View? = null

    var penIFV: IconFontView? = null
    var eraserIFV: IconFontView? = null
    var colorIFV: IconFontView? = null
    var saveIFV: IconFontView? = null

    /*paint view*/
    var paintView: View? = null
    var drawView: DrawView? = null

    var selectedList = ArrayList<IconFontView>()

    fun initView(root: View?) {
        if (root != null) {
            rootView = root
            if (rootView != null) {
                iconIv = rootView!!.findViewById(R.id.icon_iv)
                Timber.i("KTAG iconIv ${iconIv == null}")
                iconIv?.setOnClickListener {
                    Timber.i("PTAG showLine")
                    System.out.println("click")
                    showLine()
                }
                Timber.i("KTAG hasOnClickListeners ${iconIv?.hasOnClickListeners()}")
                iconIv?.performClick()
                line1Ly = rootView!!.findViewById(R.id.line1_ly)
                penIFV = rootView!!.findViewById(R.id.pen_tv)
                penIFV?.setOnClickListener {
                    showDraw()
                    // 设置选中状态
                    setSelect(penIFV, true)
                }
                eraserIFV = rootView!!.findViewById(R.id.eraser_tv)
                eraserIFV?.setOnClickListener {
                    showDraw()
                    setSelect(eraserIFV, true)
                    drawView?.eraser()
                }
                setIFVClick(R.id.clear_tv) {
                    drawView?.clear()
                }
                setIFVClick(R.id.close_tv) {
                    Timber.i("KTAG close")
                   close()
                }
                setIFVClick(R.id.save_line_tv) {
                    save()
                }
                saveIFV = rootView!!.findViewById(R.id.save_line_tv)
            }
            Timber.i("FTAG rootView $rootView")
        }
    }

    fun setIFVClick(id: Int, cb: View.OnClickListener) {
        rootView?.findViewById<IconFontView>(id)?.setOnClickListener(cb)
    }

    override fun showLine() {
        if (line1Ly != null) {
            iconIv?.visibility = View.GONE
            line1Ly?.visibility = View.VISIBLE
        }
    }

    override fun hideLine() {
        if (line1Ly != null) {
            iconIv?.visibility = View.VISIBLE
            line1Ly?.visibility = View.GONE
        }
    }

    override fun showDraw() {
        paintView?.visibility = View.VISIBLE
    }

    override fun hideDraw() {
        paintView?.visibility = View.GONE
        for (iconFontView in selectedList) {
            setSelect(iconFontView, false)
        }
    }

    fun save() {
        if (!isFloat) {

        }
    }

    /**
     * 关闭按钮点击
     *
     */
    fun close() {
        if (isFloat) {
            // 是否关闭绘制界面
            if (paintView?.visibility == View.VISIBLE) {
                TipNFDialog.showInfoTip(application,
                    message = application.resources.getString(R.string.text_close_draw),
                    sureStr = application.resources.getString(R.string.text_yes),
                    cancelStr = application.resources.getString(R.string.text_no),
                    callBack = {
                        hideDraw()
                        drawView?.clear()
                        return@showInfoTip true
                    },
                    cancelCallBack = {
                        hideLine()
                        return@showInfoTip true
                    })
            } else {
                hideLine()
            }
        } else {
            hideLine()
        }
    }

    fun setSelect(iconFontView: IconFontView?, select: Boolean = true) {
        if (iconFontView == null) return
        if (select && !selectedList.contains(iconFontView)) {
            iconFontView.setSelect(true)
            selectedList.add(iconFontView)
        } else if (!select && selectedList.contains(iconFontView)) {
            iconFontView.setSelect(false)
            selectedList.remove(iconFontView)
        }
    }

}
