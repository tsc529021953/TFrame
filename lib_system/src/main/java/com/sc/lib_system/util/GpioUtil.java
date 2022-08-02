package com.sc.lib_system.util;

import android.os.Build;
import android.os.Handler;
import android.webkit.ValueCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sc
 * @date 2021/12/3 8:58
 */
public class GpioUtil {

    public static int TIMER = 500;

    private Handler handler;

    private Runnable runnable;

    private Map<Integer, GpioCBInfo> gpios = new HashMap<>();

    public void listenGPIO_RK3288(int gpio, ValueCallback<Integer> onValueChanged){
        // 开启一个定时器， 定时读取gpio的信息
        if (gpios.containsKey(gpio)){
            gpios.get(gpio).valueCallback = onValueChanged;
            return;
        } else {
            GpioCBInfo gpioCBInfo = new GpioCBInfo();
            gpioCBInfo.valueCallback = onValueChanged;
            gpios.put(gpio, gpioCBInfo);
        }
        if (handler == null) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    onTimer();
                    handler.postDelayed(this, TIMER);
                }
            };
            handler.postDelayed(runnable, TIMER);
        }
    }

    private void onTimer(){
        for (Map.Entry<Integer, GpioCBInfo> entry: gpios.entrySet()) {
            if (entry.getValue().valueCallback == null)continue;
            int value = readGPIO_RK3288(entry.getKey());
            if (value != entry.getValue().value) {
                // 两个值不相同
                entry.getValue().value = value;
                entry.getValue().valueCallback.onReceiveValue(value);
            }
        }
    }

    public int readGPIO_RK3288(int gpio){
        gpio = convertGPIO_RK3288(gpio);
        CommandUtil.CommandResult result = CommandUtil.runCommand(new String[]{
                "echo " + gpio + " > /sys/class/gpio/export",
                "echo in > /sys/class/gpio/gpio" + gpio +"/direction",
                "cat /sys/class/gpio/gpio" + gpio + "/value",
        }, true);
        if (result.result == 0 && result.successMsg != null) {
            int res = -1;
            try {
                res = Integer.valueOf(result.successMsg);
            } catch (Exception e){
            }
            return res;
        }
        return -1;
    }

    public int convertGPIO_RK3288(int gpio){
        return gpio;
    }

    public void dispose(){
        if (handler != null && runnable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (handler.hasCallbacks(runnable))
                    handler.removeCallbacks(runnable);
            } else {
                handler.removeCallbacks(runnable);
            }
        }
    }

    class GpioCBInfo{
        ValueCallback<Integer> valueCallback;

        int value = -1;
    }

}
