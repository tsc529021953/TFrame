/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nbhope.lib_frame.app


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Message
import android.view.View
import androidx.annotation.Nullable
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by zhouwentao on 2019-08-28.
 * 用于管理所有 [Activity], 和在前台的 [Activity]
 * 参考自MVPArms
 */
@Singleton
class AppManager  {
    val TAG = this.javaClass.simpleName

    /**
     * Dagger2和这个单例方法都支持
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var sAppManager: AppManager? = null


        @JvmStatic
        val appManager: AppManager?
            get() {
                if (sAppManager == null) {
                    synchronized(AppManager::class.java) {
                        if (sAppManager == null) {
                            sAppManager = AppManager()
                        }
                    }
                }
                return sAppManager
            }

    }

    private var mApplication: Application? = null
    /**
     * 管理所有存活的 Activity, 容器中的顺序仅仅是 Activity 的创建顺序, 并不能保证和 Activity 任务栈顺序一致
     */
    private var mActivityList: MutableList<Activity>? = null

    /**
     * 当前在前台的 Activity
     */
    @get:Nullable
    var currentActivity: Activity? = null

    /**
     * 当前在前台的 Activity是否在设备前台,即Launcher是否在前台显示
     */
    var currentActivityIsTop: Boolean? = false

    /**
     * 获取最近启动的一个Activity，不一定在前台
     */
    val topActivity: Activity?
        @Nullable
        get() {
            if (mActivityList == null) {
                Timber.tag(TAG).w("mActivityList == null when getTopActivity()")
                return null
            }
            return if (mActivityList!!.size > 0) mActivityList!![mActivityList!!.size - 1] else null
        }

    /**
     * 返回一个存储所有未销毁的 [Activity] 的集合
     *
     * @return
     */
    val activityList: MutableList<Activity>
        get() {
            if (mActivityList == null) {
                mActivityList = LinkedList()
            }
            return mActivityList as MutableList<Activity>
        }

    fun init(application: Application): AppManager {
        this.mApplication = application
        return appManager!!
    }




    /**
     * 释放资源
     */
    fun release() {
        mActivityList!!.clear()
        mActivityList = null
        currentActivity = null
        mApplication = null
    }

    /**
     * 添加 [Activity] 到集合
     */
    fun addActivity(activity: Activity) {
        synchronized(AppManager::class.java) {
            val activities = activityList
            if (!activities.contains(activity)) {
                activities.add(activity)
            }
        }
    }

    /**
     * 删除集合里的指定的 [Activity] 实例
     *
     * @param {@link Activity}
     */
    fun removeActivity(activity: Activity) {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when removeActivity(Activity)")
            return
        }
        synchronized(AppManager::class.java) {
            if (mActivityList!!.contains(activity)) {
                mActivityList!!.remove(activity)
            }
        }
    }

    /**
     * 删除集合里的指定位置的 [Activity]
     *
     * @param location
     */
    fun removeActivity(location: Int): Activity? {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when removeActivity(int)")
            return null
        }
        synchronized(AppManager::class.java) {
            if (location > 0 && location < mActivityList!!.size) {
                return mActivityList!!.removeAt(location)
            }
        }
        return null
    }


    /**
     * 指定的 [Activity] 实例是否存活
     *
     * @param {@link Activity}
     * @return
     */
    fun activityInstanceIsLive(activity: Activity): Boolean {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when activityInstanceIsLive(Activity)")
            return false
        }
        return mActivityList!!.contains(activity)
    }


    /**
     * 获取指定 [Activity] class 的实例,没有则返回 null(同一个 [Activity] class 有多个实例,则返回最早创建的实例)
     *
     * @param activityClass
     * @return
     */
    fun findActivity(activityClass: Class<*>): Activity? {
        if (mActivityList == null) {
            Timber.tag(TAG).w("mActivityList == null when findActivity(Class)")
            return null
        }
        for (activity in mActivityList!!) {
            if (activity.javaClass == activityClass) {
                return activity
            }
        }
        return null
    }

    /**
     * 关闭所有 [Activity]
     */
    fun killAll() {
        //        while (getActivityList().size() != 0) { //此方法只能兼容LinkedList
        //            getActivityList().remove(0).finish();
        //        }
        synchronized(AppManager::class.java) {
            val iterator = activityList.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                iterator.remove()
                next.finish()
            }
        }
    }

    /**
     * 关闭所有 [Activity],排除指定的 [Activity]
     *
     * @param excludeActivityClasses uhome_activity_register class
     */
    fun killAll(vararg excludeActivityClasses: Class<*>) {
        val excludeList = Arrays.asList(*excludeActivityClasses)
        synchronized(AppManager::class.java) {
            val iterator = activityList.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()

                if (excludeList.contains(next.javaClass))
                    continue

                iterator.remove()
                next.finish()
            }
        }
    }

    /**
     * 关闭所有 [Activity],排除指定的 [Activity]
     *
     * @param excludeActivityName [Activity] 的完整全路径
     */
    fun killAll(vararg excludeActivityName: String) {
        val excludeList = Arrays.asList(*excludeActivityName)
        synchronized(AppManager::class.java) {
            val iterator = activityList.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()

                if (excludeList.contains(next.javaClass.name))
                    continue

                iterator.remove()
                next.finish()
            }
        }
    }

    /**
     * 按照传入的acitivtyName匹配activity，并将其余在此activity栈顶的都finish
     * A打开B，B打开C，C调用此方法传入A名字，将依次杀掉C，B
     * @param activityName
     */
    fun clearTopActivity(activityName: String) {
        var index = -1
        if(mActivityList != null){
            for (i in mActivityList!!.indices) {
                if (mActivityList!![i].javaClass.simpleName == activityName) {
                    index = i
                    break
                }
            }
            if (index != -1) {
                for (j in mActivityList!!.indices.reversed()) {
                    if (j > index) {
                        mActivityList!![j].finish()
                    } else if (j == index) {
                        break
                    }
                }
            }
        }
    }

    /**
     * 退出应用程序
     *
     *
     * 此方法经测试在某些机型上并不能完全杀死 App 进程, 几乎试过市面上大部分杀死进程的方式, 但都发现没卵用, 所以此
     * 方法如果不能百分之百保证能杀死进程, 就不能贸然调用 [.release] 释放资源, 否则会造成其他问题, 如果您
     * 有测试通过的并能适用于绝大多数机型的杀死进程的方式, 望告知
     */
    fun appExit() {
        try {
            killAll()
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
        }

    }

}
