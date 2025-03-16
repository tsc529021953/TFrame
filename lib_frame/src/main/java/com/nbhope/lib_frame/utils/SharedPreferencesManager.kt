package com.nbhope.lib_frame.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lib.network.networkconfig.TraySpManager
import com.nbhope.lib_frame.constants.HopeConstants
import timber.log.Timber
import java.util.*

/**
 * @Author qiukeling
 * @Date 2019-08-13-09:11
 * @Email qiukeling@nbhope.cn
 */
class SharedPreferencesManager constructor(private val app: Application) {
    companion object {
        private const val PREFS_NAME = "config.xml"
    }

    private val sp = app.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
    private val tary = TraySpManager(app)
    private val fileData = FileSpManager()

    private fun edit(): SharedPreferences.Editor = sp.edit()


    // 获取设备类型
    fun getString(key: String, type: String): String = tary!!.getString(key, type)!!
    fun setString(key: String, value: String) = tary!!.putString(key, value)
    fun getInt(key: String, def: Int): Int = tary!!.getInt(key, def)
    fun setInt(key: String, value: Int) = tary!!.putInt(key, value)
    fun getFloat(key: String, def: Float): Float = tary!!.getFloat(key, def)
    fun setFloat(key: String, value: Float) = tary!!.putFloat(key, value)
    fun getLong(key: String, def: Long): Long = tary!!.getLong(key, def)
    fun setLong(key: String, value: Long) = tary!!.putLong(key, value)
}
