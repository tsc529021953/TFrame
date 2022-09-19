package com.sc.lib_local_device.dao;

import androidx.databinding.ObservableField;

/**
 * @author tsc
 * @version 0.0.0-1
 * @date 2022/8/30 16:52
 * @description
 */
public class DeviceInfo {

    public DeviceInfo(){}

    public DeviceInfo(String code, String ip){
        this.code = code;
        this.ip = ip;
        this.oName.set(code);
        this.info.set(ip);
    }

    public String name;

    public String code;

    public String ip;

    public ObservableField<String> oName = new ObservableField<>("");

    public ObservableField<String> info = new ObservableField<>("");

}
