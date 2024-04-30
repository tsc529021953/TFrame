package com.nbhope.lib_frame.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * @author  tsc
 * @date  2024/4/7 16:19
 * @version 0.0.0-1
 * @description
 */
class InstallUtil {

    fun silentInstall(context: Context, apkPath: String?): Boolean {
        Timber.i("apkPath=$apkPath")
        if (apkPath == null) return false
//        context.packageManager.javaClass
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                Runtime.getRuntime().exec("pm install -r $apkPath")
            } else {
                installApkInP(context, apkPath)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e( e.toString())
        }
        return false
    }

    /**
     * 函数入口
     **/
    private fun installApkInP(context: Context, apkFilePath: String) {
        val apkFile = File(apkFilePath)
        val packageInstaller = context.packageManager.packageInstaller
        val sessionParams = PackageInstaller.SessionParams(
            PackageInstaller
                .SessionParams.MODE_FULL_INSTALL
        )
        sessionParams.setSize(apkFile.length())
        val sessionId = createSession(packageInstaller, sessionParams)
        Timber.i("sessionId=$sessionId")
        if (sessionId != -1) {
            val copySuccess = copyApkFile(packageInstaller, sessionId, apkFilePath)
            Timber.i("copySuccess=$copySuccess")
            if (copySuccess) {
                install(context, sessionId)
            }
        }
    }


    /**
     * 根据 sessionParams 创建 Session
     **/
    private fun createSession(
        packageInstaller: PackageInstaller,
        sessionParams: PackageInstaller.SessionParams
    ): Int {
        var sessionId = -1
        try {
            sessionId = packageInstaller.createSession(sessionParams)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sessionId
    }

    /**
     * 最后提交 session，并且设置回调
     **/
    private fun install(context: Context, sessionId: Int) {
        try {
            context.packageManager.packageInstaller.openSession(sessionId).use { session ->
                val intent = Intent()
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    1, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                session.commit(pendingIntent.intentSender)
            }
        } catch (e: IOException) {
            Timber.d("install e$e")
            e.printStackTrace()
        }
    }


    /**
     * 将 apk 文件输入 session
     **/
    private fun copyApkFile(
        packageInstaller: PackageInstaller,
        sessionId: Int, apkFilePath: String
    ): Boolean {
        var success = false
        val apkFile = File(apkFilePath)
        try {
            packageInstaller.openSession(sessionId).use { session ->
                session.openWrite("app.apk", 0, apkFile.length()).use { out ->
                    FileInputStream(apkFile).use { input ->
                        var read: Int
                        val buffer = ByteArray(65536)
                        while (input.read(buffer).also { read = it } != -1) {
                            out.write(buffer, 0, read)
                        }
                        session.fsync(out)
                        success = true
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return success
    }
}
