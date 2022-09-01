package com.sc.lib_local_device.common;

import com.google.gson.Gson;
import com.sc.lib_frame.utils.SharedPreferencesManager;
import com.sc.lib_local_device.dao.DeviceInfo;

/**
 * @author tsc
 * @version 0.0.0-1
 * @date 2022/8/30 16:02
 * @description
 */
public class DeviceCommon {

    public static final String DEVICE_TYPE = "DEVICE_TYPE";
    public static final String DEVICE_INFO = "DEVICE_INFO";

    public static DeviceType deviceType = DeviceType.View;

    public static DeviceInfo recordDeviceInfo = null;

    public static void readDeviceType(SharedPreferencesManager sp) {
        if (sp == null) return;
        readDeviceType(sp.getString(DEVICE_TYPE, sp.getString(DeviceCommon.DEVICE_TYPE, DeviceCommon.DeviceType.UN_KNOW.toString())));
    }

    public static void readDeviceType(String type) {
        try {
            deviceType = DeviceType.valueOf(type);
        } catch (Exception e) {
            deviceType = DeviceType.UN_KNOW;
        }
    }

    public static void saveDeviceType(SharedPreferencesManager sp, DeviceType type) {
        deviceType = type;
        sp.setString(DEVICE_TYPE, deviceType.toString());
    }

    public static void readRecordDeviceInfo(String info) {
        if (info == null) return;
        // 转成类
        try {
            recordDeviceInfo = new Gson().fromJson(info, DeviceInfo.class);
        } catch (Exception e) {
        }
    }

    public static void readRecordDeviceInfo(SharedPreferencesManager sp) {
        if (sp == null) return;
        readRecordDeviceInfo(sp.getString(DEVICE_INFO, null));
    }

    public static void saveRecordDeviceInfo(SharedPreferencesManager sp, DeviceInfo info) {
        recordDeviceInfo = info;
        sp.setString(DEVICE_INFO, new Gson().toJson(info));
    }

    public enum DeviceType {
        View,
        Ctrl,
        UN_KNOW
    }

}
