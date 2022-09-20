package com.sc.app_xsg.vm

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nbhope.lib_frame.app.HopeBaseApp
import com.nbhope.lib_frame.utils.SharedPreferencesManager
import com.nbhope.phmina.base.MinaConstants
import com.nbhope.phmina.bean.data.ClientInfo
import com.nbhope.phmina.bean.data.Cmd
import com.sc.lib_local_device.common.DeviceCommon
import com.sc.lib_local_device.dao.CmdItem
import com.sc.lib_local_device.service.MainService
import com.sc.lib_local_device.vm.BaseLDViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject


/**
 * @author  tsc
 * @date  2022/8/30 16:31
 * @version 0.0.0-1
 * @description
 */
class HomeViewModel @Inject constructor(val spManager: SharedPreferencesManager) : BaseLDViewModel() {

    //    val deviceInfo = Observable<>()
    companion object {
        const val IMAGES_PATH = "images/"
    }

    private var images = ArrayList<Bitmap>()

    var image = ObservableField<Drawable>()

    var picIndex = 0

    var picGroup = 0

    var callback: Handler.Callback? = null

    var service: MainService? = null

    override fun initData() {
        super.initData()
        //
    }

    fun onSignClick(group: Int) {
        Timber.i("XTAG onSignClick $group ${image.get()}")
        if (DeviceCommon.deviceType == DeviceCommon.DeviceType.Ctrl) {
            // 发送控制信息给服务端
            sendGroup()
        }
        // 进入到图组
        loadAllImages(group)
    }

    fun next() {
        picIndex--
        refreshImage()
    }

    fun last() {
        picIndex++
        refreshImage()
    }

    fun refreshImage() {
        if (images.size <= 0) return
        if (picIndex < 0) picIndex = images.size - 1
        else if (picIndex >= images.size) picIndex = 0
        sendIndex()
        image.set(BitmapDrawable(images[picIndex]))
    }

    fun loadAllImages(group: Int, index: Int = -1) {
        picGroup = group
        images.clear()
        val am: AssetManager = HopeBaseApp.getContext().assets
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val files = am.list(IMAGES_PATH + group.toString())
                Timber.i("XTAG group ${files != null} ${files?.isNotEmpty()}")
                if (files != null && files.isNotEmpty()) {
                    callback?.handleMessage(Message().also { it.what = files.size })
                    if (index > 0 && index < files.size -1) picIndex = index
                    picIndex = files.size / 2
                    for (i in files.indices) {
                        val inputStream: InputStream = am.open(IMAGES_PATH + group.toString() + "/" + files!![i])
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        images.add(bitmap)
                        inputStream.close()
                        if (i == picIndex) {
                            // 先拿去显示
                            Timber.i("XTAG index $picIndex")
                            image.set(BitmapDrawable(bitmap))
                        }
                    }
                    if (DeviceCommon.deviceType == DeviceCommon.DeviceType.Ctrl) {
                        // 发送控制信息给服务端
                        sendIndex()
                    }
                } else {
                    callback?.handleMessage(Message().also { it.what = 0 })
                }
            }
        } catch (e: Exception) {

        }
    }

    /**
     * 请求设备列表
     */
    fun loadDeviceList() {
        val cmd = Cmd(MinaConstants.CMD_DISCOVER, ClientInfo())
        service?.sendMulMessage(Gson().toJson(cmd))
    }

    fun sendGroup(){
        if (DeviceCommon.deviceType != DeviceCommon.DeviceType.Ctrl) return
        var da = CmdItem()
        da.group = picGroup
        da.index = picIndex
        val cmd = Cmd(MinaConstants.CMD_CHANGE_GROUP, da)
        service?.sendClientMessage(Gson().toJson(cmd))
    }

    fun sendIndex(){
        if (DeviceCommon.deviceType != DeviceCommon.DeviceType.Ctrl) return
        var da = CmdItem()
        da.group = picGroup
        da.index = picIndex
        val cmd = Cmd(MinaConstants.CMD_CHANGE_INDEX, da)
        service?.sendClientMessage(Gson().toJson(cmd))
    }

    fun sendSign(){
        if (DeviceCommon.deviceType != DeviceCommon.DeviceType.Ctrl) return
        var da = CmdItem()
        da.group = picGroup
        da.index = picIndex
        val cmd = Cmd(MinaConstants.CMD_CHANGE_SIGN, da)
        service?.sendClientMessage(Gson().toJson(cmd))
    }
}