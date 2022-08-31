package com.lib.network.vo;

import java.io.Serializable;
import java.util.Map;

public class OssEntity implements Serializable {

    private String category;
    private String name;
    private String devID;
    private String pid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevID() {
        return devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    private Map deviceDpMap;
    private Map dps;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Map getDeviceDpMap() {
        return deviceDpMap;
    }

    public void setDeviceDpMap(Map deviceDpMap) {
        this.deviceDpMap = deviceDpMap;
    }

    public Map getDps() {
        return dps;
    }

    public void setDps(Map dps) {
        this.dps = dps;
    }

    public OssEntity(String category, String pid, Map deviceDpMap, Map dps) {
        this.category = category;
        this.pid = pid;
        this.deviceDpMap = deviceDpMap;
        this.dps = dps;
    }

    public OssEntity(String category, String name, String devID, String pid, Map deviceDpMap, Map dps) {
        this.category = category;
        this.name = name;
        this.devID = devID;
        this.pid = pid;
        this.deviceDpMap = deviceDpMap;
        this.dps = dps;
    }
}
