package com.sc.lib_local_device.service

import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.lib.network.vo.DuiWordData
import com.nbhope.lib_frame.event.RemoteMessageEvent
import timber.log.Timber
import java.lang.Exception

/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
object LiveRemoteObserver {
    var mainServiceImpl: MainServiceImpl? = null

    val liveRemoteMusicObserver = Observer<Any> {
        Timber.d("VOICE_OPER:data:${it.toString()}")
        it as RemoteMessageEvent
        when (it.cmd) {
//            MessageConsts.UHOME_LOCAL_DEVICE_CTRL -> {
////                Timber.d("VOICE_OPER:data:${it.data}")
//                val data = JsonParser().parse(it.data).asJsonObject
////                speechService?.agentText(if (data.has("voice")) data.get("voice").asString else "文字不能为空")
//                uHomeLocalServiceImpl?.deviceControl(data)
//            }
//            MessageConsts.UHOME_LOCAL_SCENE_CTRL -> {
//                val data = JsonParser().parse(it.data).asJsonObject
////                speechService?.agentText(if (data.has("voice")) data.get("voice").asString else "文字不能为空")
//                uHomeLocalServiceImpl?.sceneControl(data)
//            }
//            MessageConsts.UPLOAD_UHOME_DATA -> {
//                // 监听到语控更新
//                try {
//                    var items = Gson().fromJson<DuiWordData>(it.data, DuiWordData::class.java)
//                    uHomeLocalServiceImpl?.onHomeDataLoaded(items)
//                } catch (e: Exception) {}
//

//            }
        }
    }
}