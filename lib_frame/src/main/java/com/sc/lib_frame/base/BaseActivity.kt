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
import com.sc.lib_frame.di.AndroidInjectionDelegate
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

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    protected val TAG: String = this.javaClass.simpleName

    protected abstract var layoutId: Int

    /**
     * 初始化一些东西 比如FragmentManagerUtils 这个方法最早执行
     */
    protected open fun initParam(savedInstanceState: Bundle?) {
//        when (HopeConstants.THEME) {
//            "000" -> {
//                if (HopeConstants.isScreenSmall()){
//                    setTheme(R.style.AppThemeDark_small)
//                }else{
//                    if (HopeConstants.CPU == "rk3566"){
//                        setTheme(R.style.AppThemeSkb)
//                    }else{
//                        setTheme(R.style.AppThemeDark)
//                    }
//                }
//
//            }
//            "001" -> {
//                setTheme(R.style.AppThemeLight)
//            }
//            "010" -> {
//                if (HopeConstants.CPU == "a133"){
//                    setTheme(R.style.AppThemeSkb)
//                }else{
//                    setTheme(R.style.AppThemeDark_New)
//                }
//            }
//            "011" -> {
//                if (HopeConstants.HOPE_CODE.startsWith("HK44")||HopeConstants.HOPE_CODE.startsWith("HK05")){
//                    setTheme(R.style.AppThemeSkb)
//                }else{
//                    setTheme(R.style.AppThemeLight_New)
//                }
//            }
//            "100" ,"101" -> {
//                setTheme(R.style.AppThemeDark20_New)
//            }
//            "200" -> {
//                setTheme(R.style.AppThemeDark_30)
//            }
//            else ->{
//                setTheme(R.style.AppThemeDark)
//            }
//        }
    }

    private fun changeTheme() {

    }

    /**
     * liveData订阅
     */
    protected abstract fun subscribeUi()


    /**
     * 拿数据
     */
    protected abstract fun initData()

    private var keepOrientation = true


    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjectionDelegate.inject(this)
//        setDefaultDisplay(this)
        changeTheme()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition = Explode()
            window.exitTransition = Explode()
        }
        hideSystemUI()
        ARouter.getInstance().inject(this)

        /**
         * 使用Databinding的逻辑在 {@link BaseBindingActivity}
         */
        if (this !is BaseBindingActivity<*, *>) {
            initParam(savedInstanceState)
            setContentView(layoutId)
            subscribeUi()
            initData()
        }
    }


    open fun showDialog(title: String) {

    }

    open fun dismissDialog() {

    }

    fun launchActivity(arouterPath: String, bundle: Bundle? = null) {
        HopeUtils.startActivityByArouter(arouterPath, baseContext,bundle)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI()
        onWindowFocusChangedHelper(hasFocus)
    }
    open fun onWindowFocusChangedHelper(hasFocus: Boolean){

    }

    protected fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    }

    protected fun hideSystemUI(view: View) {
        if (Build.VERSION.SDK_INT >= 19) {
            view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun keepOrientationPortrait() {
        if (keepOrientation) {
            //保持竖直方向，静止旋转
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

//    override fun onResume() {
//        super.onResume()
//        MobclickAgent.onResume(this)//好像auto模式不需要这个
//    }
//
//    override fun onPause() {
//        super.onPause()
//        MobclickAgent.onPause(this)
//    }

    override fun onResume() {
        super.onResume()
        LockScreenHelper.postResumeEvent(baseContext)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        LockScreenHelper.postTouchEvent(baseContext)
        return super.dispatchTouchEvent(ev)
    }
}