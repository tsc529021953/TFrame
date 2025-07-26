package com.sc.tmp_translate.base

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.databinding.Observable
import androidx.databinding.ViewDataBinding
import com.nbhope.lib_frame.base.BaseBindingActivity
import com.nbhope.lib_frame.base.BaseBindingFragment
import com.nbhope.lib_frame.base.BaseViewModel
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import com.sc.tmp_translate.R
import com.sc.tmp_translate.constant.MessageConstant
import com.sc.tmp_translate.service.TmpServiceDelegate

abstract class BaseTransActivity<T : ViewDataBinding, VM : BaseViewModel> : BaseBindingActivity<T, VM>() {

    private var fontSize = 1f

    var firstView = false

    private var fontSizeObsListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            runOnUiThread {
                fontSize = TmpServiceDelegate.getInstance().getFontSizeObs()?.get() ?: 1f
                onFontSizeChanged(fontSize)
            }
        }
    }

    private var listener = androidx.lifecycle.Observer<Any> {
        it as RemoteMessageEvent
        when (it.cmd) {
            MessageConstant.CMD_BIND_SUCCESS -> {
                if (firstView) {
                    fontSize = TmpServiceDelegate.getInstance().getFontSizeObs()?.get() ?: 1f
                    onFontSizeChanged(fontSize)
                }
                TmpServiceDelegate.getInstance().getFontSizeObs()?.addOnPropertyChangedCallback(fontSizeObsListener)
            }
        }
        onLiveEB(it.cmd, it.data)
    }

    abstract fun onFontSizeChanged(fontSize: Float)
    abstract fun onLiveEB(cmd: String, data: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LiveEBUtil.regist(RemoteMessageEvent::class.java, this, listener)
        if (TmpServiceDelegate.getInstance().getFontSizeObs() != null)
            TmpServiceDelegate.getInstance().getFontSizeObs()?.addOnPropertyChangedCallback(fontSizeObsListener)
    }

    override fun onResume() {
        super.onResume()
        if (TmpServiceDelegate.getInstance().getFontSizeObs() != null && TmpServiceDelegate.getInstance().getFontSizeObs()!!.get() != fontSize) {
            fontSize = TmpServiceDelegate.getInstance().getFontSizeObs()?.get() ?: 1f
            onFontSizeChanged(fontSize)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveEBUtil.unRegist(RemoteMessageEvent::class.java, listener)
        TmpServiceDelegate.getInstance().getFontSizeObs()?.removeOnPropertyChangedCallback(fontSizeObsListener)
    }

    fun setFontSize(view: View, id: Int) {
        setFontSize(view, resources.getDimension(id))
    }

    fun setFontSize(view: View,size: Float) {
        if (view is TextView) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * fontSize)
        }
    }

    fun getFontSize(): Float {
        return fontSize
    }

}
