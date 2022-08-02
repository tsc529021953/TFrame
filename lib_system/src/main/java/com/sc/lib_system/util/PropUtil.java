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
public class PropUtil {

    public static int TIMER = 500;

    private Handler handler;

    private Runnable runnable;

    private Map<Integer, CBInfo> gpios = new HashMap<>();

    private String value;

    ValueCallback<String> onValueChanged;

    public void listenPROP_RK3288(ValueCallback<String> onValueChanged){
        this.onValueChanged = onValueChanged;
        // 开启一个定时器， 定时读取信息
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
        String v = readPROP_RK3288();
        if (v != value) {
            value = v;
            if (onValueChanged != null)
                onValueChanged.onReceiveValue(value);
        }
    }

    public String readPROP_RK3288(){
        CommandUtil.CommandResult result = CommandUtil.runCommand(new String[]{
                "getprop | grep input",
        }, true);
        String v = null;
        if (result.result == 0 && result.successMsg != null) {
            v = result.successMsg;
            if (v.contains("[0]"))return "0";
            else if (v.contains("[1]")) return "1";
            else return v;
        } else {
            v = result.toString();
        }
        return v;
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

    class CBInfo{
        ValueCallback<Integer> valueCallback;

        int value = -1;
    }

}
