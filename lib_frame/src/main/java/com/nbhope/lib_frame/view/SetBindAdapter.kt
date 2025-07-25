package com.nbhope.lib_frame.view

import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.*
import com.nbhope.lib_frame.R
import timber.log.Timber


/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
object SetBindAdapter {
    @JvmStatic
    @BindingAdapter(value = ["onItemSelect"], requireAll = true)
    fun onItemSelected(view: View, state: ObservableBoolean?){
        view.isSelected = state?.get()!!
    }

    @JvmStatic
    @BindingAdapter(value = ["tunnelDef", "tunnelData"], requireAll = true)
    fun tunnelViewSel(view: View, tunnel: Int, data: ObservableInt?) {
        view.isEnabled = tunnel != data?.get()
    }


    @JvmStatic
    @BindingAdapter(value = ["selectDefault", "selectState"], requireAll = true)
    fun selectViewSel(view: TextView, state: Int, data: ObservableInt?) {
        view.isSelected = state == data?.get()
    }

    @JvmStatic
    @BindingAdapter("viewIsEnabled")
    fun baseTunnelSwitch(view: View, isSwitch: Boolean?) {
        view.isEnabled = isSwitch!!
    }

    @JvmStatic
    @BindingAdapter("viewIsEnabled","seekVolume")
    fun seekBarVolumeControl(view: SeekBar, isSwitch: Boolean?,volume:Int?) {
        view.isEnabled = isSwitch!!
        if (isSwitch){
            volume?.apply {
                view.progress = this
            }
        }else{
            view.progress = 0
        }
    }

//    @JvmStatic
//    @BindingAdapter("wifiLevel")
//    fun bindWifiLevel(view: ImageView, level: Int?) {
//        when(level){
//            0->{
//                view.setImageResource(R.drawable.setting_ic_wifi3)
//            }
//            1->{
//                view.setImageResource(R.drawable.setting_ic_wifi2)
//            }
//            2->{
//                view.setImageResource(R.drawable.setting_ic_wifi1)
//            }
//        }
//    }

    @JvmStatic
    @BindingAdapter(value = ["itemName", "appNewCheck","framNewCheck","otaCheck"], requireAll = true)
    fun bindVersionRed(view: ImageView, name: String, appNew: Boolean, framNew: Boolean, otaNew: Boolean) {
        if (name == "检查更新"&&(appNew||framNew||otaNew)){
            view.visibility = View.VISIBLE
        }else{
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["speakDef", "speakData"], requireAll = true)
    fun speakerViewSel(view: View, tunnel: String, data: ObservableField<String>?) {
        view.isEnabled = tunnel != data?.get()
    }

    @JvmStatic
    @BindingAdapter(value = ["allStateShow", "highStateShow"], requireAll = true)
    fun highSetShow(view: View, allState: Boolean, highState: Boolean) {
        if (allState&&highState){
            view.visibility = View.VISIBLE
        }else{
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter(value = [ "tunnelValueData", "tunnelValueDef"], requireAll = true)
    fun tunnelSet(view: View, tunnel: String, data: ObservableField<String>?) {
        Timber.tag("hhy").d("tunnel = $tunnel data = ${data?.get()}")
        val dataValue = data?.get() ?: "1"
        view.isEnabled = dataValue != tunnel
//        view.isEnabled =  data?.get() != tunnel
    }

//    @JvmStatic
//    @BindingAdapter(value = ["tunnelValueDef"], requireAll = false)
//    fun bindTunnelValueDef(view: TextView, tunnelValue: ObservableField<String>?) {
//        view.isEnabled = tunnelValue?.get() != "1" // 根据需要设置逻辑
//    }

    @JvmStatic
    @BindingAdapter(value = [ "songDataValueData", "songDataValueDef"], requireAll = true)
    fun songSet(view: View, tunnel: String, data: ObservableField<String>?) {
        Timber.tag("hhy").d("songSet  tunnel = $tunnel")
        view.isEnabled =  data?.get() != tunnel
    }

//    @JvmStatic
//    @BindingAdapter(value = ["selected"])
//    fun setSelected(imageView: ImageView, selected: ObservableBoolean) {
//        Timber.tag("hhy").d("setSelected  selected = $selected")
//        val drawable =
//            imageView.context.resources.getDrawable(if (selected.get()) R.drawable.setting_checkbox_selected else R.drawable.setting_checkbox_unselected)
//        imageView.setImageDrawable(drawable)
//    }

    @JvmStatic
    @BindingAdapter(value = ["floatDef", "floatData"], requireAll = true)
    fun floatViewSel(view: View, value: Float, data: ObservableFloat?) {
        view.isEnabled = value != data?.get()
    }

    @JvmStatic
    @BindingAdapter(value = ["boolDef", "boolData"], requireAll = true)
    fun boolViewSel(view: View, value: Boolean, data: ObservableBoolean?) {
        view.isEnabled = value != data?.get()
    }
}
