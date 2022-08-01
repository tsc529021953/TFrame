package com.sc.lib_frame.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window

/**
 * @author  tsc
 * @date  2022/8/1 9:58
 * @version 0.0.0-1
 * @description
 */
class ViewUtil {
    companion object{
        @JvmStatic
        fun immersionTitle(activity: Activity) {
//        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);//字体默认白色
//        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);//透明背景
//        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);

//        View decorView = activity.getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        decorView.setSystemUiVisibility(uiOptions);
//        activity. getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);

            //在你的activity的onCreate方法下添加下列代码（如果是自定义的BaseActivity的话，就在你自定义的Activity下添加，这样的话只要其他的继承你的BaseActivity，只需要改一次就可以）
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                val v = activity.window.decorView
                v.systemUiVisibility = View.GONE
            } else if (Build.VERSION.SDK_INT >= 19) {
                //for new api versions.
                val decorView = activity.window.decorView
                val uiOptions = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                //                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.systemUiVisibility = uiOptions
            }

//        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            activity.window.statusBarColor = Color.TRANSPARENT //透明背景
            activity.window.navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                activity.window.navigationBarDividerColor = Color.TRANSPARENT
            }
        }

        @JvmStatic
        fun immersionTitle(window: Window) {
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                val v = window.decorView
                v.systemUiVisibility = View.GONE
            } else if (Build.VERSION.SDK_INT >= 19) {
                //for new api versions.
                val decorView = window.decorView
                val uiOptions = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                //                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.systemUiVisibility = uiOptions
            }

//        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            window.statusBarColor = Color.TRANSPARENT //透明背景
            window.navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.navigationBarDividerColor = Color.TRANSPARENT
            }
        }
    }
}