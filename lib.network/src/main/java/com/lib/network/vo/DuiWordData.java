package com.lib.network.vo;

import java.util.List;

/**
 * created by Caesar on 2019/2/19
 * email : 15757855271@163.com
 */
public class DuiWordData {

    private List<String> rooms;
    private List<DeviceBean> device;
    private List<SceneBean> scene;

    public DuiWordData(List<String> rooms, List<DeviceBean> device, List<SceneBean> scene) {
        this.rooms = rooms;
        this.device = device;
        this.scene = scene;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }

    public List<DeviceBean> getDevice() {
        return device;
    }

    public void setDevice(List<DeviceBean> device) {
        this.device = device;
    }

    public List<SceneBean> getScene() {
        return scene;
    }

    public void setScene(List<SceneBean> scene) {
        this.scene = scene;
    }

    public static class DeviceBean {
        /**
         * deviceName : HOPE-A7
         * hopeCata : 100057
         * roomName : 默认房间
         */

        private String deviceName;
        private int hopeCata;
        private String roomName;

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public int getHopeCata() {
            return hopeCata;
        }

        public void setHopeCata(int hopeCata) {
            this.hopeCata = hopeCata;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
    }

    public static class SceneBean {
        /**
         * sceneName : 一个情景
         */

        private String sceneName;
        private String voiceMean;

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }

        public String getVoiceMean() {
            return voiceMean;
        }

        public void setVoiceMean(String voiceMean) {
            this.voiceMean = voiceMean;
        }

        public String[] getVoiceMeans() {
            if (voiceMean != null) {
                String regex = ",|，";
                return voiceMean.split(regex);
            } else
                return null;
        }

        public String getLastSceneName() {
            StringBuffer buffer = new StringBuffer();
            buffer.append(sceneName);
            if (getVoiceMeans() != null) {
                int size = getVoiceMeans().length;
                if (size == 1) {
                    buffer.append(":").append(getVoiceMeans()[0]);
                } else {
                    StringBuffer itemsb = new StringBuffer();
                    for (int i = 0; i < getVoiceMeans().length - 1; i++) {
                        itemsb.append(getVoiceMeans()[i]).append(",");
                    }
                    itemsb.append(getVoiceMeans()[size - 1]);
                    buffer.append(":").append(itemsb);
                }

            }
            return buffer.toString();
        }
    }
}
