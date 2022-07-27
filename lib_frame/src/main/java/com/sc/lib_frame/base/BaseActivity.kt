package com.sc.lib_frame.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.sc.lib_frame.R
import com.sc.lib_frame.constants.HopeConstants
import com.sc.lib_frame.di.Injectable
import com.sc.lib_frame.utils.HopeUtils
import com.sc.lib_frame.utils.LockScreenHelper
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

/**
 *Created by ywr on 2021/11/10 15:11
 */
abstract class BaseActivity : AppCompatActivity(), HasAndroidInjector, Injectable {

}