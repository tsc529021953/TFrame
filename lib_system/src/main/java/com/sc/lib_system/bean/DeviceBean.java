package com.sc.lib_system.bean;

public class DeviceBean{
        public String name;

        public String address;

        public DeviceBean(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }
    }