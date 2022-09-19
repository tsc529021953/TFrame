package com.sc.lib_local_device.service

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.lib.network.vo.DuiWordData
import com.nbhope.lib_frame.event.RemoteMessageEvent
import com.lib.tcp.enums.MinaEventEnum
import com.lib.tcp.event.MinaHandlerEvent
import com.nbhope.lib_frame.utils.LiveEBUtil
import timber.log.Timber

object MainReceiverHelper {
    private val TAG = MainReceiverHelper::class.java.simpleName

//    var rooms: ArrayList<String> = ArrayList()
//    var roomList: ArrayList<UHLRoom> = ArrayList()
//    var devices: ArrayList<DuiWordData.DeviceBean> = ArrayList()
//
//    // 用于传递给语控
//    var scenes: ArrayList<DuiWordData.SceneBean> = ArrayList()
//    var deviceList = ArrayList<UHLItem>()
//    var sceneList = ArrayList<UHLItem>()

    fun invokeTcpMessage(receiver: MinaHandlerEvent) {
        when (receiver.enum) {
            MinaEventEnum.SESSION_CLOSE -> {
                // 连接断开
//                LiveEBUtil.post(UHomeLocalEvent(MessageConsts.UHOME_LOCAL_DISCONNECT, receiver))
            }
            MinaEventEnum.SESSION_OPEN -> {
//                LiveEBUtil.post(UHomeLocalEvent(MessageConsts.UHOME_LOCAL_CONNECT, receiver))
            }
            else -> {
//                receiver.message?.apply {
//                    val jsonBody = this.getMessageBody()?.let {
//                        JsonParser().parse(it).asJsonObject
//                    } ?: return
//                    if (jsonBody!!.has(InSonaBean.METHOD)) {
//                        Timber.i("LTAG ${jsonBody.get(InSonaBean.METHOD).asString}")
//                        when (jsonBody.get(InSonaBean.METHOD).asString) {
//                            InSonaBean.METHOD_S_QUERY -> {
//                                // 所有信息
//                                var infos =
//                                    Gson().fromJson<InSonaDevicesBean>(
//                                        this.getMessageBody(),
//                                        InSonaDevicesBean::class.java
//                                    )
//                                if (infos != null) {
//                                    Timber.i("LTAG 设备nu ${infos.devices.size} ${infos.toString()}")
//                                    // 数据正确，通知出去
//                                    deviceList.clear()
//                                    devices.clear()
//                                    rooms.clear()
//                                    roomList.clear()
//                                    for (i in 0 until infos.rooms.size) {
//                                        rooms.add(infos.rooms[i].name)
//                                        roomList.add(UHLRoom(infos.rooms[i].name, infos.rooms[i].roomId.toString()))
//                                    }
//
//                                    for (i in 0 until infos.devices.size) {
//                                        var deviceBean = infos.devices.get(i)
//                                        var uBean = getInSonaDeviceInfos(deviceBean, infos)
//                                        Timber.i("LTAG ${uBean.status.toString()}")
//                                        deviceList.add(uBean)
//
//                                        var dev = DuiWordData.DeviceBean()
//                                        dev.deviceName = uBean.name
//                                        dev.roomName = uBean.roomName
//                                        devices.add(dev)
//                                    }
//                                    var duiWordData = DuiWordData(rooms, devices, scenes)
//                                    // 通知
//                                    LiveEBUtil.post(UHomeLocalEvent(MessageConsts.UHOME_LOCAL_ROOMS, roomList))
//
//                                    LiveEBUtil.post(UHomeLocalEvent(MessageConsts.UHOME_LOCAL_DEVICES, deviceList))
//
//                                    // 通知语控
//                                    LiveEBUtil.post(
//                                        RemoteMessageEvent(
//                                            MessageConsts.UPLOAD_UHOME_DATA,
//                                            Gson().toJson(duiWordData)
//                                        )
//                                    )
//                                }
//                            }
//                            InSonaBean.METHOD_S_EVENT -> {
//                                var info = Gson().fromJson<InSonaSEventBean>(
//                                    this.getMessageBody(),
//                                    InSonaSEventBean::class.java
//                                )
//                                if (info?.did != null) {
//                                    var item = getInSonaDeviceInfo(info) ?: return
//                                    Timber.i("LTAG METHOD_S_EVENT ${item!!.status}")
//                                    LiveEBUtil.post(UHomeLocalEvent(MessageConsts.UHOME_LOCAL_DEVICE_UPDATE, item!!))
//                                } else if (info.evt == MESH_CHANGE && info.func == STATE_MESH_CHANGE) {
//                                    LiveEBUtil.post(UHomeLocalEvent(MessageConsts.UHOME_LOCAL_REFRESH, info))
//                                }
//                            }
//                            InSonaBean.METHOD_S_QUERY_SCENE -> {
//                                var infos =
//                                    Gson().fromJson<InSonaScenesBean>(
//                                        this.getMessageBody(),
//                                        InSonaScenesBean::class.java
//                                    )
//                                if (infos != null) {
//                                    scenes.clear()
//                                    sceneList.clear()
//                                    for (i in 0 until infos.scenes.size) {
//                                        var sce = DuiWordData.SceneBean()
//                                        sce.sceneName = infos.scenes[i].name
//                                        scenes.add(sce)
//                                        var item = UHLItem()
//                                        item.type = UHLItemViewModel.SCENE
//                                        item.name = infos.scenes[i].name
//                                        item.id = infos.scenes[i].sceneId.toString()
//                                        sceneList.add(item)
//                                    }
//                                    var duiWordData = DuiWordData(rooms, devices, scenes)
//                                    // 通知
//                                    LiveEBUtil.post(UHomeLocalEvent(MessageConsts.UHOME_LOCAL_SCENES, sceneList))
//
//                                    // 通知语控
//                                    LiveEBUtil.post(
//                                        RemoteMessageEvent(
//                                            MessageConsts.UPLOAD_UHOME_DATA,
//                                            Gson().toJson(duiWordData)
//                                        )
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }
    }

}