package com.lib.network.vo.response;

import java.io.Serializable;

public class SignBeanRespone implements Serializable {


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
