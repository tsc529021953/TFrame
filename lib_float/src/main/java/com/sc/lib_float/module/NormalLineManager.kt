package com.sc.lib_float.module

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.widget.IconFontView
import com.sc.lib_float.R
import com.sc.lib_float.bean.SideItem
import com.sc.lib_float.databinding.SideItemBinding
import com.sc.lib_float.enum.PaintState
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

/**
 * @author  tsc
 * @date  2024/5/28 16:20
 * @version 0.0.0-1
 * @description
 */
class NormalLineManager constructor(mScope: CoroutineScope, application: Application, isFloat: Boolean = true): LineManager(mScope, application, isFloat) {



    private val DRAW_LINE_LIST = arrayListOf<SideItem>(
        SideItem(getIcon(R.string.icon_pen), R.string.icon_pen) { drawView?.pen() },
        SideItem(getIcon(R.string.icon_eraser), R.string.icon_eraser) { drawView?.eraser() },
        SideItem(getIcon(R.string.icon_back), R.string.icon_back) { drawView?.backStep() },
        SideItem(getIcon(R.string.icon_next), R.string.icon_next) { drawView?.nextStep() },
        SideItem(getIcon(R.string.icon_clear), R.string.icon_clear) { drawView?.clear() },
        SideItem(getIcon(R.string.icon_close), R.string.icon_close) {
            hideLine2()
            hideLine3()},
    )

    private val SETTING_LINE_LIST = arrayListOf<SideItem>(
        SideItem(getIcon(R.string.icon_close), R.string.icon_close) {
            hideLine2()
            hideLine3()},
    )

    private val FIRST_LINE_LIST = arrayListOf<SideItem>(
        SideItem(getIcon(R.string.icon_draw), R.string.icon_draw) { showLine2Base(DRAW_LINE_LIST) { it, binding ->
            showDrawLine(it, binding)
        } },
        SideItem(getIcon(R.string.icon_ctrl), R.string.icon_ctrl) { drawView?.ctrl() },
        SideItem(getIcon(R.string.icon_save), R.string.icon_save),
        SideItem(getIcon(R.string.icon_setting), R.string.icon_setting){ showLine2Base(SETTING_LINE_LIST) { it, binding ->
//            showDrawLine(it, binding)
        } },
        SideItem(getIcon(R.string.icon_close), R.string.icon_close) { close() },
    )

    override fun initView(root: View?) {
        super.initView(root)
        if (rootView != null) {
            if (line1Ly != null) {
                line1Ly?.removeAllViews()
                // 根据列表新增
                FIRST_LINE_LIST.forEach {
                    showLineBase(line1Ly, FIRST_LINE_LIST) { it, binding ->
                        showFirstLine(it, binding)
                    }
                }
                initData()
            }
        }
    }

    fun initData() {
        drawView?.paintState?.removeOnPropertyChangedCallback(onPaintStateChanged)
        drawView?.paintState?.addOnPropertyChangedCallback(onPaintStateChanged)
        onPaintStateChanged()
    }

    private val onPaintStateChanged = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            onPaintStateChanged()
        }
    }

    fun onPaintStateChanged() {
        when (drawView?.paintState?.get()) {
            PaintState.CTRL -> {
                ctrlIFV?.isSelected = true
                drawIFV?.isSelected = false
                penIFV?.isSelected = false
                eraserIFV?.isSelected = false
            }
            PaintState.PAINTING -> {
                ctrlIFV?.isSelected = false
                drawIFV?.isSelected = true
                penIFV?.isSelected = true
                eraserIFV?.isSelected = false
            }
            PaintState.ERASER -> {
                ctrlIFV?.isSelected = false
                drawIFV?.isSelected = true
                penIFV?.isSelected = false
                eraserIFV?.isSelected = true
            }
        }
    }

    private fun showLine2Base(siList: ArrayList<SideItem>, callback: (sideItem: SideItem, binding: SideItemBinding) -> Unit) {
        showLineBase(line2Ly, siList, callback)
        showLine2()
    }

    private fun showLineBase(lineLy: LinearLayout?, siList: ArrayList<SideItem>, callback: (sideItem: SideItem, binding: SideItemBinding) -> Unit) {
        if (lineLy == null) return
        lineLy?.removeAllViews()
        for (sideItem in siList) {
            var binding = DataBindingUtil.inflate<SideItemBinding>(LayoutInflater.from(application), R.layout.side_item, lineLy!!, true)
            callback.invoke(sideItem, binding)
            binding.root.setOnClickListener { view ->
                sideItem.callback?.invoke(view)
            }
            binding.vm = sideItem
        }
    }

    private fun showFirstLine(sideItem: SideItem, binding: SideItemBinding) {
        Timber.i("KTAG sideItem ${sideItem.id} ${R.string.icon_draw} ${R.string.icon_ctrl}")
        when (sideItem.id) {
            R.string.icon_draw -> drawIFV = binding.ifv
            R.string.icon_ctrl -> ctrlIFV = binding.ifv
            R.string.icon_save -> saveIFV = binding.ifv
            R.string.icon_setting -> settingIFV = binding.ifv
        }
    }

    private fun showDrawLine(sideItem: SideItem, binding: SideItemBinding) {
        when (sideItem.id) {
            R.string.icon_pen -> penIFV = binding.ifv
            R.string.icon_eraser -> eraserIFV = binding.ifv
        }
    }

}
