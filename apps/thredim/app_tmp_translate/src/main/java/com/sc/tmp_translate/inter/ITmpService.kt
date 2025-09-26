package com.sc.tmp_translate.inter

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import com.sc.tmp_translate.bean.TransRecordBean
import com.sc.tmp_translate.utils.record.PcmAudioPlayer

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

    // 是否到翻译界面
    fun getTranslatingObs(): ObservableBoolean?
    fun setTranslating(play: Boolean)
    fun notifyTransPage(trans: Boolean, reset: Boolean)
    fun changeTranslatingState(open: Boolean)

    // 是否开启翻译
    fun getTransStateObs(index: Int): ObservableBoolean?
    fun setTransState(play: Boolean, index: Int)
    fun setTransState(index: Int)

    // 是否处于识别中
    fun getTransRecognition(index: Int): ObservableBoolean?

    // 音频播放
    fun getTransPlayObs(): ObservableField<String>?
    fun getPlayStatusObs(): ObservableField<PcmAudioPlayer.State>?
    fun setTransPlay(bean: TransRecordBean)

    fun getTransRecordObs(): ObservableBoolean?
    fun setTransRecord(play: Boolean)

    fun showVolume()
}
