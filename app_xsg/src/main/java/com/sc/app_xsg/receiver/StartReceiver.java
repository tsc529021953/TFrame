package com.sc.app_xsg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sc.app_xsg.activity.MainActivity;
import com.sc.app_xsg.app.AppHope;
import timber.log.Timber;

/**
 * @author tsc
 * @version 0.0.0-1
 * @date 2022/8/30 15:30
 * @description
 */
public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Timber.i(AppHope.TAG + " ACTION_BOOT_COMPLETED");
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
