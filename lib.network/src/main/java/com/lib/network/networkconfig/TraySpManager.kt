package com.lib.network.networkconfig

import android.app.Application
import android.content.Context
import net.grandcentrix.tray.AppPreferences

/**
 *Created by ywr on 2021/2/2 12:30
 * 进程间数据同步
 */
class TraySpManager constructor(app:Context){
    private val tary=AppPreferences(app)

    fun  putString(key:String,value:String){
        tary.put(key,value)
    }
    fun getString(key: String,default:String):String?{
       return tary.getString(key,default)
    }
    fun  putInt(key: String,value: Int){
        tary.put(key,value)
    }
    fun getInt(key: String,default:Int):Int{
        return tary.getInt(key,default)
    }
    fun putBoolean(key: String,value:Boolean){
        tary.put(key,value)
    }
    fun getBoolean(key: String,default:Boolean):Boolean{
        return tary.getBoolean(key,default)
    }

    fun  putFloat(key: String,value: Float){
        tary.put(key,value)
    }
    fun getFloat(key: String,default:Float):Float{
        return tary.getFloat(key,default)
    }
    fun  putLong(key: String,value: Long){
        tary.put(key,value)
    }
    fun getLong(key: String,default:Long):Long{
        return tary.getLong(key,default)
    }
}
