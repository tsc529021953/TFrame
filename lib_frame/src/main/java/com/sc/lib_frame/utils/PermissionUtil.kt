package com.sc.lib_frame.utils

import android.app.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 权限操作相关的工具类
 */
object PermissionUtil {

    //Tells whether permissions are granted to the app.
    fun hasPermissionsGranted(activity: Activity, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun requestStoragePermission(activity: Activity, permissions: Array<String>, requestCode: Int, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRationale(activity, permissions)) {
                PermissionConfirmationDialog.newInstance()
                        .setActivity(activity)
                        .setMessage(message)
                        .setPermissions(permissions, requestCode)
                        .show(activity.fragmentManager, "dialog")
            } else {
                activity.requestPermissions(permissions, requestCode)
            }
        }
    }

    /**
     * Gets whether you should show UI with rationale for requesting the permissions.
     * @return True if the UI should be shown.
     */
    fun shouldShowRationale(activity: Activity, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * A dialog that explains about the necessary permissions.
     */
    class PermissionConfirmationDialog : DialogFragment() {
        private var mActivity: Activity? = null
        private var permissions: Array<String>? = null
        private var message: String? = null
        private var requestCode: Int = 0

        fun setActivity(activity: Activity): PermissionConfirmationDialog {
            this.mActivity = activity
            return this
        }

        fun setMessage(message: String): PermissionConfirmationDialog {
            this.message = message
            return this
        }

        fun setPermissions(permissions: Array<String>, requestCode: Int): PermissionConfirmationDialog {
            this.permissions = permissions
            this.requestCode = requestCode
            return this
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(activity)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity!!.requestPermissions(permissions!!, requestCode)
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, which -> activity!!.finish() }
                    .create()
        }

        companion object {
            fun newInstance(): PermissionConfirmationDialog {
                return PermissionConfirmationDialog()
            }
        }

    }

    fun checkFloatPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) //4.4-5.1
            return true
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { //6.0
            try {
                var cls = Class.forName("android.content.Context")
                val declaredField: Field = cls.getDeclaredField("APP_OPS_SERVICE")
                declaredField.setAccessible(true)
                var obj: Any? = declaredField.get(cls) as? String ?: return false
                val str2 = obj as String
                obj = cls.getMethod("getSystemService", String::class.java).invoke(context, str2)
                cls = Class.forName("android.app.AppOpsManager")
                val declaredField2: Field = cls.getDeclaredField("MODE_ALLOWED")
                declaredField2.setAccessible(true)
                val checkOp: Method = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String::class.java)
                val result = checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName()) as Int
                result == declaredField2.getInt(cls)
            } catch (e: Exception) {
                false
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //8
                val appOpsMgr: AppOpsManager =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager ?: return false
                val mode: Int = appOpsMgr.checkOpNoThrow(
                    "android:system_alert_window", Process.myUid(), context
                        .getPackageName()
                )
                Settings.canDrawOverlays(context) || mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
            } else {
                Settings.canDrawOverlays(context)
            }
        }
    }
}
