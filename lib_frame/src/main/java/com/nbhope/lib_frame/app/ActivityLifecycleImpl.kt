package com.nbhope.lib_frame.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.nbhope.lib_frame.base.BaseActivity
import com.nbhope.lib_frame.base.BaseFragment
import com.nbhope.lib_frame.di.Injectable
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by zhouwentao on 2019-09-02.
 * 在所有Activity生命周期实现自己的方法
 */
class ActivityLifecycleImpl @Inject constructor() : Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var appManager: AppManager

    override fun onActivityPaused(activity: Activity) {
        Timber.i("$activity - onActivityPaused")
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.i("$activity - onActivityResumed")
        appManager.currentActivity = activity
        appManager.currentActivityIsTop = true
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.i("$activity - onActivityStarted")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.i("$activity - onActivityDestroyed")
        appManager.removeActivity(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.i("$activity - onActivityStop")
        if (appManager.currentActivity == activity) {
            appManager.currentActivityIsTop = false
        }

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.i("$activity - onActivityCreated")
        handleActivity(activity)
        appManager.addActivity(activity)
    }


    //这里处理一些特殊activity或者fragment 不能继承base的
    //在这里进行注入
    private fun handleActivity(activity: Activity) {
        if (activity !is BaseActivity && activity is HasAndroidInjector){
            AndroidInjection.inject(activity)
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                            Timber.i("$f - onFragmentCreated")
                            if (f !is BaseFragment && f is Injectable) {
                                AndroidSupportInjection.inject(f)
                            }
                        }

                        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
//                            Timber.i("$f - onFragmentAttached")
                            super.onFragmentAttached(fm, f, context)
                        }

                        override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
//                            Timber.i("$f - onFragmentActivityCreated")
                            super.onFragmentActivityCreated(fm, f, savedInstanceState)
                        }

                        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
//                            Timber.i("$f - onFragmentViewCreated")
                            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                        }

                        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
//                            Timber.i("$f - onFragmentStarted")
                            super.onFragmentStarted(fm, f)
                        }

                        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                            Timber.i("$f - onFragmentResumed")
                            super.onFragmentResumed(fm, f)
                        }

                        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
//                            Timber.i("$f - onFragmentPaused")
                            super.onFragmentPaused(fm, f)
                        }

                        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
//                            Timber.i("$f - onFragmentStopped")
                            super.onFragmentStopped(fm, f)
                        }

                        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
//                            Timber.i("$f - onFragmentViewDestroyed")
                            super.onFragmentViewDestroyed(fm, f)
                        }

                        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                            Timber.i("$f - onFragmentDestroyed")
                            super.onFragmentDestroyed(fm, f)
                        }

                        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
//                            Timber.i("$f - onFragmentDetached")
                            super.onFragmentDetached(fm, f)
                        }
                    }, true)
        }
    }
}