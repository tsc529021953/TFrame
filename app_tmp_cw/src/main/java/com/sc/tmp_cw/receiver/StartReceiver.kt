package com.sc.tmp_cw.receiver

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
//                Toast.makeText(p0, "接收到唤醒广播！", Toast.LENGTH_LONG).show()
                val intent = Intent(p0!!, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                p0.startActivity(intent)

                System.out.println("init ?? subscribeUi22")
//                TmpServiceDelegate.getInstance().init(p0!!)
            }
        }
    }
}
