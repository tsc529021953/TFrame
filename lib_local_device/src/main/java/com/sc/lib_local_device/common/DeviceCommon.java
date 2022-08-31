package com.sc.lib_local_device.common;

import com.sc.lib_frame.utils.SharedPreferencesManager;

/**
 * @author tsc
 * @version 0.0.0-1
 * @date 2022/8/30 16:02
 * @description
 */
public class DeviceCommon {

    public static final String DEVICE_TYPE = "DEVICE_TYPE";

    public static DeviceType deviceType = DeviceType.View;

    public static void initDeviceType(){

//        String deviceType = SharedPreferencesManager.Companion.getInstance()
//                .getTray().getString(DeviceCommon.DEVICE_TYPE, DeviceCommon.DeviceType.View.toString());
//        setDeviceType(deviceType);
    }

    public static void setDeviceType(String type) {
        try{
            deviceType = DeviceType.valueOf(type);
        }catch (Exception e) {
            deviceType = DeviceType.UN_KNOW;
        }
    }

    public enum DeviceType {
        View,
        Ctrl,
        UN_KNOW
    }

}
