package com.sc.lib_system.sp;

//import android.serialport.SerialPort;
//import android.serialport.SerialPortFinder;
//import android_serialport_api.SerialPortFinder;
import android_serialport_api.SerialPortFinder;
import timber.log.Timber;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * author: sc
 * date: 2022/1/16
 * 1. 检测权限
 * 判断此串口是否存在
 * 初始化 打开串口
 * 接收函数
 * 发送函数
 * 2. 释放
 */
public class SerialPortUtil {

    private static final String SP_NSME = "/dev/ttyS3";

    private static final int IBAUD = 19200;

    SerialPortFinder serialPortFinder;

    SerialHelper serialHelper;
//    SerialPort serialHelper;

    String sp;
    int iBaud;

    public String[] getSpList() {
        if (serialPortFinder == null)
            serialPortFinder = new SerialPortFinder();
        return serialPortFinder.getAllDevicesPath();
    }

    public SerialPortUtil() {
        this(SP_NSME, IBAUD);
    }

    public SerialPortUtil(String sp, int iBaud) {
        this.sp = sp;
        this.iBaud = iBaud;
        init();
    }

    void init() {
//        String[] devices = getSpList();
//        TLog.d("串口设备数量：" + devices.length);
//        for (int i = 0; i< devices.length; i++) {
//            TLog.d("device: " + devices[i]);
//        }
    }

    public boolean open() {
        List<String> deviceList = Arrays.asList(getSpList());
        if (!deviceList.contains(sp)) {
            Timber.d("此串口不存在");
            return false;
        }
        if (serialHelper == null)
            serialHelper = new SerialHelper(sp, iBaud) {
                @Override
                protected void onDataReceived(ComBean paramComBean) {
                    Timber.d("接收到消息");
                    onDataReceivedFun(paramComBean);
                }
            };
        if (serialHelper.isOpen()) {
            return false;
        }
        Timber.d("存在串口 " + sp);
        try {
            serialHelper.open();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isOpen(){
        if (serialHelper == null || !serialHelper.isOpen())return false;
        return true;
    }

//    public boolean open(){
//        try {
//            serialHelper = SerialPort.newBuilder(sp, iBaud).build();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public void setSuPath(){
        // su默认路径为 "/system/bin/su"
// The default path of su is "/system/bin/su"
// 可通过此方法修改
// If the path is different then change it using this
        SerialPort.setSuPath("/system/xbin/su");
    }


    void onDataReceivedFun(ComBean paramComBean){
        String msg = null;
        try {
            msg = new String(paramComBean.bRec, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void writeHex(String msg) {
        if (serialHelper != null && serialHelper.isOpen()){
            serialHelper.sendHex(msg);
        }
    }
    public void writeStr(String msg) {
        if (serialHelper != null && serialHelper.isOpen()){
            serialHelper.sendTxt(msg);
        }
    }
    public void write(byte[] msg) {
        if (serialHelper != null && serialHelper.isOpen()){
            serialHelper.send(msg);
        }
    }


    void close() {
        if (serialHelper != null && serialHelper.isOpen())
            serialHelper.close();
//        if (serialHelper != null)
//            serialHelper.tryClose();
    }

    public void send(String msg) {

    }

    public void dispose() {
        close();
    }

}
