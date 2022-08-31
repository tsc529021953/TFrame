package com.lib.network.vo;

import java.io.Serializable;
import java.util.List;

public class OssUpdateEntity implements Serializable {

    private String sn;
    private String updateTime;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    private String appVersion;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    private List<OssEntity> list;
    private List<String> devicesIgnore;

    public List<String> getDevicesIgnore() {
        return devicesIgnore;
    }

    public void setDevicesIgnore(List<String> devicesIgnore) {
        this.devicesIgnore = devicesIgnore;
    }

    public List<OssEntity> getList() {
        return list;
    }

    public void setList(List<OssEntity> list) {
        this.list = list;
    }
}
