package com.sc.tmp_cw.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sc.tmp_cw.MainActivity

/**
 * @author  tsc
 * @date  2022/12/19 14:01
 * @version 0.0.0-1
 * @description
 * Intent intent= new Intent(;
intent.setAction("android.provider.Telephony.SECRET_CODE");
intent.setData(Uri.parse("android secret code://66"))
sendBroadcast(intent);
 */
class StartReceiver : BroadcastReceiver() {

    companion object {

        const val BOOT_COMPLETED = Intent.ACTION_BOOT_COMPLETED

        const val SECRET_CODE = "android.provider.Telephony.SECRET_CODE"

    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        println("接收到唤醒广播0 ${p1?.action}")
        when (p1?.action) {
            SECRET_CODE, BOOT_COMPLETED -> {
                println("接收到唤醒广播 ${p1?.action}")
                // 检查 App 是否已在运行，避免重复启动 MainActivity 导致栈内 Activity 被清除
                if (isAppRunning(p0)) {
                    println("App 已在运行，跳过重复启动 MainActivity")
                    return
                }
                val intent = Intent(p0!!, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                p0.startActivity(intent)

                System.out.println("init ?? subscribeUi22")
            }
        }
    }

    /**
     * 判断当前 App 是否已有 Activity 在运行
     */
    private fun isAppRunning(context: Context?): Boolean {
        if (context == null) return false
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
            val runningTasks = am.getRunningTasks(Int.MAX_VALUE)
            val packageName = context.packageName
            runningTasks.any { it.baseActivity?.packageName == packageName }
        } catch (e: Exception) {
            false
        }
    }
}
