package com.sc.lib_system.util;

import android.content.Context;
import android.os.PowerManager;

public class SystemUtil {

    public static void reboot(Context context){
        PowerManager pManager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pManager.reboot(null);//重启
    }



}
