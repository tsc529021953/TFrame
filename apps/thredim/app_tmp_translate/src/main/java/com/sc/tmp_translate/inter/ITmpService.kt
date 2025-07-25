package com.sc.tmp_translate.inter

import android.content.Context
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
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
    fun getTransLangKHObs(): ObservableField<String>?
    fun setTransLang(lang: String)
}
