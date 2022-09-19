package com.nbhope.phmina.bean.data;

/**
 * Created by ywr on 2021/6/23 17:30
 */
public class Cmd<T> {
    public static final int CODE_SUCCESS = 10000;
    public static final int CODE_FAILED = 20000;
    /**
     * //协议类型
     * 由于Halo协议和智能家居产业联盟协议协议内容基本相同，
     * 可以进行复用，故对不同部分进行相关判断
     * 1.Hope Halo协议，对应plugin.newhopehalo
     * 2.智能家居产业联盟协议 对应plugin.alliance
     */
    private static int PROTOCOL_TYPE = 1;

    public static int getProtocolType() {
        return PROTOCOL_TYPE;
    }

    public static void setProtocolType(int protocolType) {
        PROTOCOL_TYPE = protocolType;
    }

    public Cmd(String cmd, T params) {
        this.cmd = cmd;
        this.params = params;
    }

    protected String cmd;
    protected String type;

    protected T params;
    protected T data;

    protected Integer code;
    protected String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCmd() {
        if (cmd != null) {
            return cmd;
        }
        return type;
    }

    public void setCmd(String cmd) {
        if (PROTOCOL_TYPE == 1) {
            this.cmd = cmd;
            this.type = null;
        } else {
            this.type = cmd;
            this.cmd = null;
        }
    }

    public T getParams() {
        if (params != null) {
            return params;
        }
        return data;
    }

    public void setParams(T params) {
        if (PROTOCOL_TYPE == 1) {
            this.params = params;
            this.data = null;
        } else {
            this.data = params;
            this.params = null;
        }
    }

    public String getType() {
        return getCmd();
    }

    public void setType(String type) {
        setCmd(type);
    }

    public T getData() {
        return getParams();
    }

    public void setData(T data) {
        setParams(data);
    }

}