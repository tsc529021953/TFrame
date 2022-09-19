package com.nbhope.phmina.bean.data;

import android.os.Build;

import com.nbhope.phmina.base.Utils;
import kotlin.jvm.Throws;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by ywr on 2021/7/12 13:31
 */
public class ClientInfo {
    private String hopeSn = Utils.INSTANCE.getSn();
    private String name = getDefName();
    private int type = 1; //正常 2 忙碌中 3 勿扰
    private int speechType = 0;
    private GroupInfo group = new GroupInfo();
    private String localIp = Utils.INSTANCE.getLANIP();
    private Boolean isSelect = false;
    private Boolean linkState = false;
    private String linkedSn = null;
    private String linkedName = null;

    private int deviceType = 0;

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }

    public String getHopeSn() {
        return hopeSn;
    }

    public void setHopeSn(String hopeSn) {
        this.hopeSn = hopeSn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSpeechType() {
        return speechType;
    }

    public void setSpeechType(int speechType) {
        this.speechType = speechType;
    }

    public GroupInfo getGroup() {
        return group;
    }

    public void setGroup(GroupInfo group) {
        this.group = group;
    }

    private String getDefName() {
        String sn = Utils.INSTANCE.getSn();
        if (sn == null) {
            return "unKnow";
        } else {
            int length = sn.length();
            int start = length > 6 ? length - 7 : 0;
            return sn.substring(start);
        }
    }

    public String macAddress() throws SocketException {
        String address = "unKnow";
        // 把当前机器上访问网络的接口存入 Enumeration集合中
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netWork = interfaces.nextElement();
            // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
            byte[] by = netWork.getHardwareAddress();
            if (by == null || by.length == 0) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            for (byte b : by) {
                builder.append(String.format("%02X:", b));
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            String mac = builder.toString();
            // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
            if (netWork.getName().equals("wlan0")) {
                address = mac;
            }
        }
        return address;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "hopeSn='" + hopeSn + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", speechType=" + speechType +
                ", group=" + group +
                '}';
    }

    public String getLinkedSn() {
        return linkedSn;
    }

    public void setLinkedSn(String linkedSn) {
        this.linkedSn = linkedSn;
    }

    public Boolean getLinkState() {
        return linkState;
    }

    public void setLinkState(Boolean linkState) {
        this.linkState = linkState;
    }

    public String getLinkedName() {
        return linkedName;
    }

    public void setLinkedName(String linkedName) {
        this.linkedName = linkedName;
    }

}
