package com.nbhope.phmina.base;

/**
 * 常量
 *
 * @author EthanCo
 * @since 2017/1/10
 */

public class MinaConstants {
    //虚拟客户端
    public static final String VIRTUAL_CLIENT = "VIRTUAL_CLIENT";

    //设备发现
    public static final String CMD_DISCOVER = "CMD_DISCOVER";
    public static final String CMD_DISCOVER_RS = "CMD_DISCOVER_RS";
    //邀请连接
    public static final String CMD_INVITE_LINK_M = "CMD_INVITE_LINK_M";  //广播邀请指定的客户连接
    public static final String CMD_INVITE_LINK_M_RS = "CMD_INVITE_LINK_M_RS"; //指定客户端连接服务端成功



    //服务端记录的客户端连接状态
    public static final String CMD_SOMEONE_LINK_STATE = "CMD_SOMEONE_LINK_STATE";  //服务端记录的客户端连接状态

    //服务端状态
    public static final String CMD_SERVICE_STATE = "CMD_SERVICE_STATE";  //服务端状态

    //客户端状态
    public static final String CMD_CLIENT_STATE = "CMD_CLIENT_STATE";  //客户端状态

    //邀请连接结果
    public static final String CMD_LINK_RES = "CMD_LINK_RES";  //邀请连接结果

    //TCP通信命令
    public static final String HEART_BEAT = "HEART_BEAT";   // 心跳
    public static final String CMD_S_REGISTER = "CMD_S_REGISTER";   //  设备端注册
    public static final String CMD_S_UPDATA = "CMD_S_UPDATA";   //  设备端数据更新
    public static final String CMD_S_QURLIST = "CMD_S_QURLIST";   // 请求设备列表
    public static final String CMD_S_QURLIST_CB = "CMD_S_QURLIST_CB";   // 请求设备列表回复
    public static final String CMD_INVITE_COMM = "CMD_INVITE_COMM";   // 连接通话
    public static final String CMD_ICECANDIDATE_COMM = "CMD_ICECANDIDATE_COMM";   // 通话呼叫
    public static final String CMD_INVITE_COMM_RS = "CMD_INVITE_COMM_RS";   // 通话请求回复
    public static final String CMD_T_MUSIC_CTR_S = "CMD_T_MUSIC_CTR_S";   // 透传同步歌单列表发送端
    public static final String CMD_T_MUSIC_CTR_R = "CMD_T_MUSIC_CTR_R";   // 透传同步歌单列表接收端
    public static final String CMD_T_MUSIC_ERRPR = "CMD_T_MUSIC_ERRPR";   // 从机播放失败
    public static final String CMD_S_QURUNLINK = "CMD_S_QURUNLINK";   // 主机断开从机的连接
    public static final String CMD_S_NTP_S = "CMD_S_NTP_S";   // 客户端请求校时
    public static final String CMD_S_NTP_R = "CMD_S_NTP_R";   // 服务端校时回复

    public static final String CMD_T_TEST = "CMD_T_TEST";   // 消息透传

    public static final String CMD_NETWORK_LOST = "CMD_NETWORK_LOST";
}
