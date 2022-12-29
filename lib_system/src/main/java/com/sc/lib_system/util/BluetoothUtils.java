package com.sc.lib_system.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;

/**
 * @ClassName BluetoothUtils
 * @Description TODO
 * @Author shufeng.jiang
 * @Date 2022/4/24 11:02
 */
public class BluetoothUtils {
    final String TAG = getClass().getName();
    Context context;
    private static BluetoothUtils bluetoothInstance;
    private BluetoothAdapter bluetoothAdapter ;
    private BluetoothInterface bluetoothInterface;
    private BluetoothUtils (){}
    private String dev_mac_adress = "";
    ArrayList<DeviceBean> deviceBeans = new ArrayList<>();

    public static BluetoothUtils getInstance() {
        if (bluetoothInstance == null) {
            bluetoothInstance = new BluetoothUtils();
        }
        return bluetoothInstance;
    }

    public void initBluetooth(Context context){
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerBroadcas(context);
    }
    public  void setBluetoothListener(BluetoothInterface bluetoothInterface){
        this.bluetoothInterface = bluetoothInterface;
    }
    private void registerBroadcas(Context context){
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
//        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
//        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        context.registerReceiver(bluetoothBroadcast, intent);
        Log.i(TAG,"registerReceiver");

    }

    BroadcastReceiver bluetoothBroadcast = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                /* 搜索结果 */
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //dev_mac_adress.contains(device.getAddress())避免重复添加
                if( device.getName() != null && !dev_mac_adress.contains(device.getAddress())){
                    deviceBeans.add(new DeviceBean(device.getName(),device.getAddress()));
                    dev_mac_adress += device.getAddress();
                    Log.i(TAG,device.getName()+"："+device.getAddress());
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())){
                //正在搜索
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                // 搜索完成
                dev_mac_adress = "";
                bluetoothInterface.getBluetoothList(deviceBeans);
                deviceBeans.clear();
            }
        }
    };


    /** 开启蓝牙 */
    public void enable(){
        if (bluetoothAdapter !=null && !bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }
    }
    /** 关闭蓝牙 */
    public void disable(){
        if (bluetoothAdapter !=null && bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
        }
    }

    /** 取消搜索 */
    public void cancelDiscovery(){
        if(isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
    }

    /** 开始搜索 */
    public void startDiscovery(){
        if (bluetoothAdapter !=null && bluetoothAdapter.isEnabled()){
            bluetoothAdapter.startDiscovery();
        }
    }

    /** 判断蓝牙是否打开 */
    public boolean isEnabled(){
        if (bluetoothAdapter !=null){
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }
    /** 判断当前是否正在查找设备，是返回true */
    public boolean isDiscovering(){
        if (bluetoothAdapter !=null){
            return bluetoothAdapter.isDiscovering();
        }
        return false;
    }

    public void onDestroy(){
        context.unregisterReceiver(bluetoothBroadcast);
    }
    public interface BluetoothInterface{
        /* 获取蓝牙列表 */
        void getBluetoothList(ArrayList<DeviceBean> deviceBeans);

    }

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
}
