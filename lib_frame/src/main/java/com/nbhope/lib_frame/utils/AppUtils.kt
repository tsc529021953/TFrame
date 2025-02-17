package com.nbhope.lib_frame.utils

import android.app.Activity
import android.app.Instrumentation
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.nbhope.lib_frame.utils.toast.ToastUtil
import com.tencent.bugly.beta.Beta


object AppUtils {
    fun getPackageInfos(context: Context, appPackageNames: List<String>): List<PackageInfo> {
        val apps = ArrayList<PackageInfo>()
        val pManager = context.packageManager
        pManager.getInstalledPackages(0).forEach {
            if (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM <= 0) {
                apps.add(it)
            } else if (appPackageNames.contains(it.packageName)) {
                apps.add(it)
            }
        }
        return apps
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getPackageInfo(context: Context, packageName: String): PackageInfo {
        return context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
    }

    fun startExtraApp(context: Context, packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) context.startActivity(intent)
        else ToastUtil.showS("没有找到应用程序(${packageName})")
    }

    fun startExtraApp(context: Context, packageName: String, activityName: String, infos: HashMap<String, String>? = null) {
        val componetName = ComponentName(
                packageName,  //这个是另外一个应用程序的包名
                activityName
        ) //这个参数是要启动的Activity的全路径名
        try {
            val intent = Intent()
            intent.component = componetName
            if (infos != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    infos.forEach { s, s2 ->
                        intent.putExtra(s, s2)
                    }
                } else {
                    for ((s, s2) in infos!!.entries) {
                        intent.putExtra(s, s2)
                    }
                }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            ToastUtil.showS("没有找到应用程序(${packageName}${activityName})")
//            Toast.makeText(context.applicationContext, "可以在这里提示用户没有找到应用程序，或者是做其他的操作！", 0).show()
        }
    }

    fun uninstallApk(context: Context, packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE)
        val packageURI = Uri.parse("package:$packageName")
        intent.data = packageURI
        context.startActivity(intent)
    }

    fun autocheckBuglyUpdate() {
        Beta.checkUpgrade(false, false)
    }

    private const val FILE_SELECT_CODE = 10086

    fun openRockExplorer(activity: Activity, path: String? = null) {
        val map = HashMap<String, String>()
        if (path != null)
            map.put("path", path)
        startExtraApp(activity, "com.android.rk", "com.android.rk.RockExplorer", map)
    }

    fun openFileExplorer(activity: Activity) {
        val uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Tmp")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        activity.startActivityForResult(intent, FILE_SELECT_CODE)
    }

    fun openSettingWifi(activity: Activity) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivityForResult(intent, FILE_SELECT_CODE)
    }

    fun openSetting(activity: Activity) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivityForResult(intent, FILE_SELECT_CODE)
    }

//    fun runBackWithActivity(context: Context, activityName: String) {
//        val cn = getTopActivity(context)
//        if (activityName == "" || (cn != null && cn.className.endsWith(activityName))) {
//            try {
//                val inst = Instrumentation()
//                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    fun runBack(context: Context) {
        try {
            val inst = Instrumentation()
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            System.out.println("iconIV click back")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun runHome(context: Context) {
        val mHomeIntent =
            Intent(Intent.ACTION_MAIN)
        mHomeIntent.addCategory(Intent.CATEGORY_HOME)
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP) // FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        context.startActivity(mHomeIntent)
    }
}
