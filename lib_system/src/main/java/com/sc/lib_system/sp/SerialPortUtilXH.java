//package com.sc.lib_system.sp;
//
////import android.serialport.SerialPort;
////import android.serialport.SerialPortFinder;
////import android_serialport_api.SerialPortFinder;
////import android_serialport_api.SerialPortFinder;
////import com.thehelper.th_shell_android.common.TLog;
////import top.maybesix.xhlibrary.serialport.SerialPortHelper;
////import tp.xmaihh.serialport.SerialHelper;
////import tp.xmaihh.serialport.bean.ComBean;
//
////import top.maybesix.xhlibrary.serialport.ComPortData;
////import top.maybesix.xhlibrary.serialport.SerialPortHelper;
////import top.maybesix.xhlibrary.util.HexStringUtils;
//
//
///**
// * author: sc
// * date: 2022/1/16
// * 1. 检测权限
// * 判断此串口是否存在
// * 初始化 打开串口
// * 接收函数
// * 发送函数
// * 2. 释放
// */
//public class SerialPortUtilXH {
//
////    private static final String SP_NSME = "/dev/ttyS3";
////
////    private static final int IBAUD = 19200;
////
////    SerialPortFinder serialPortFinder;
////
////    SerialPortHelper serialHelper;
//////    SerialPort serialHelper;
////
////    String sp;
////    int iBaud;
////    private SerialPortHelper.OnSerialPortReceivedListener OnSerialPortReceived
////            = new SerialPortHelper.OnSerialPortReceivedListener() {
////        @Override
////        public void onSerialPortDataReceived(ComPortData comPortData) {
////            TLog.d("接收到数据！");
////        }
////    };
////
////    public String[] getSpList() {
////        if (serialPortFinder == null)
////            serialPortFinder = new SerialPortFinder();
////        return serialPortFinder.getAllDevicesPath();
////    }
////
////    public SerialPortUtilXH() {
////        this(SP_NSME, IBAUD);
////    }
////
////    public SerialPortUtilXH(String sp, int iBaud) {
////        this.sp = sp;
////        this.iBaud = iBaud;
////        init();
////    }
////
////    void init() {
//////        String[] devices = getSpList();
//////        TLog.d("串口设备数量：" + devices.length);
//////        for (int i = 0; i< devices.length; i++) {
//////            TLog.d("device: " + devices[i]);
//////        }
////    }
////
////    public boolean open() {
////        List<String> deviceList = Arrays.asList(getSpList());
////        if (!deviceList.contains(sp)) {
////            TLog.d("此串口不存在");
////            return false;
////        }
////        if (serialHelper == null)
////            serialHelper = new SerialPortHelper(sp, iBaud);
////        if (serialHelper.isOpen()) {
////            return false;
////        }
////        TLog.d("存在串口 " + sp);
////        serialHelper.setSerialPortReceivedListener(OnSerialPortReceived);
////        serialHelper.open();
////
////        return true;
////    }
////
//////    public boolean open(){
//////        try {
//////            serialHelper = SerialPort.newBuilder(sp, iBaud).build();
//////            return true;
//////        } catch (IOException e) {
//////            e.printStackTrace();
//////        }
//////        return false;
//////    }
////
////    public void setSuPath(){
////        // su默认路径为 "/system/bin/su"
////// The default path of su is "/system/bin/su"
////// 可通过此方法修改
////// If the path is different then change it using this
////        SerialPort.setSuPath("/system/xbin/su");
////    }
////
////    public void writeHex(String msg) {
////        TLog.d(msg);
////        if (serialHelper != null && serialHelper.isOpen()){
////            TLog.d("发送");
////            serialHelper.sendHex(msg);
////        }
////    }
////    public void writeStr(String msg) {
////        TLog.d(msg);
////        if (serialHelper != null && serialHelper.isOpen()){
////            TLog.d("发送");
////            serialHelper.sendTxtString(msg);
////        }
////    }
////    public void write(byte[] msg) {
////        TLog.d(msg);
////        if (serialHelper != null && serialHelper.isOpen()){
////            TLog.d("发送");
////            serialHelper.sendHex(HexStringUtils.byteArray2HexString(msg));
////        }
////    }
////
////
////    void close() {
////        if (serialHelper != null && serialHelper.isOpen())
////            serialHelper.close();
//////        if (serialHelper != null)
//////            serialHelper.tryClose();
////    }
////
////    public void send(String msg) {
////
////    }
////
////    public void dispose() {
////        close();
////    }
//
//}
