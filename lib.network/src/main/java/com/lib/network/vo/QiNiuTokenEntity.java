package com.lib.network.vo;

import java.io.Serializable;

public class QiNiuTokenEntity implements Serializable {


    /**
     * code : 100000
     * desc : success!
     * message : 成功！
     * object : {"accessid":"LTAI5tGUTXPLosXoonEikBJd","policy":"eyJleHBpcmF0aW9uIjoiMjAyMi0wNy0yMlQwMTowMDowMi4yNjFaIiwiY29uZGl0aW9ucyI6W1siY29udGVudC1sZW5ndGgtcmFuZ2UiLDAsMTA0ODU3NjAwMF0sWyJzdGFydHMtd2l0aCIsIiRrZXkiLCJwbHVnaW4vdHV5YS8iXV19","signature":"pjv6pBC5NTye3T2myvZvg4u92JI=","dir":"plugin/tuya/","host":"http://hope-oss-ota.oss-cn-hangzhou.aliyuncs.com","expire":"1658451602"}
     */

    private int code;
    private String desc;
    private String message;
    private ObjectBean object;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ObjectBean getObject() {
        return object;
    }

    public void setObject(ObjectBean object) {
        this.object = object;
    }

    public static class ObjectBean implements Serializable {
        /**
         * accessid : LTAI5tGUTXPLosXoonEikBJd
         * policy : eyJleHBpcmF0aW9uIjoiMjAyMi0wNy0yMlQwMTowMDowMi4yNjFaIiwiY29uZGl0aW9ucyI6W1siY29udGVudC1sZW5ndGgtcmFuZ2UiLDAsMTA0ODU3NjAwMF0sWyJzdGFydHMtd2l0aCIsIiRrZXkiLCJwbHVnaW4vdHV5YS8iXV19
         * signature : pjv6pBC5NTye3T2myvZvg4u92JI=
         * dir : plugin/tuya/
         * host : http://hope-oss-ota.oss-cn-hangzhou.aliyuncs.com
         * expire : 1658451602
         */

        private String accessid;
        private String policy;
        private String signature;
        private String dir;
        private String host;
        private String expire;

        public String getAccessid() {
            return accessid;
        }

        public void setAccessid(String accessid) {
            this.accessid = accessid;
        }

        public String getPolicy() {
            return policy;
        }

        public void setPolicy(String policy) {
            this.policy = policy;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getExpire() {
            return expire;
        }

        public void setExpire(String expire) {
            this.expire = expire;
        }
    }
}
