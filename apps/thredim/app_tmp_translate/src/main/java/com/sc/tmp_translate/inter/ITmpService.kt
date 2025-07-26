package com.sc.tmp_translate.inter

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.lifecycle.MutableLiveData
import com.sc.tmp_translate.bean.TransTextBean
import java.util.*

/**
 * @author  tsc
 * @date  2024/4/12 13:28
 * @version 0.0.0-1
 * @description
 */
interface ITmpService {

    fun init(context: Context)
    fun showFloat()
    fun hideFloat(delayMillis: Long)
    fun write(msg: String)
    fun reBuild()

    fun getFontSizeObs(): ObservableFloat?
    fun setFontSize(size: Float)

    /*语言相关*/
    fun getTransLangObs(): ObservableField<String>?
    fun setTransLang(lang: String)

    fun getMoreDisplayObs(): ObservableBoolean?
    fun setMoreDisplay(more: Boolean)

    fun getTextPlayObs(): ObservableBoolean?
    fun setTextPlay(play: Boolean)

    fun getTranslatingObs(): ObservableBoolean?
    fun setTranslating(play: Boolean)
    fun notifyTransPage(trans: Boolean)

    fun getTranslatingList() : MutableLiveData<ArrayList<TransTextBean>>?
}
