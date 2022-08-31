package com.sc.lib_frame.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.sc.lib_frame.app.AppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by zhouwentao on 2019-09-02.
 */
abstract class BaseVMActivity<VM : BaseViewModel> : BaseActivity() {

    protected  abstract  var viewModel: VM

    private var keepOrientation = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * 使用Databinding的逻辑在 {@link BaseBindingActivity}
         */
        if (this !is BaseBindingActivity<*,*>) {
            subscribeOnceOnlyLiveDataCallBack()
        }
        /**
         * 后期加入全局网络错误等状态改变管理
         */
    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI()
    }

    private fun keepOrientationPortrait() {
        if (keepOrientation) {
            //保持竖直方向，静止旋转
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }



    protected fun subscribeOnceOnlyLiveDataCallBack() {
        viewModel.mOnceOnlyLiveData.showMessage.observe(this, Observer {
            it.getContentIfNotHandled()?.let { msg ->
//                HopeUtils.makeText(this, msg)
                GlobalScope.launch(Dispatchers.Main){
                    Toast.makeText(this@BaseVMActivity,msg,Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.mOnceOnlyLiveData.showDialogEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { title ->
                showDialog(title)
            }
        })

        viewModel.mOnceOnlyLiveData.dismissDialogEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                dismissDialog()
            }
        })

        viewModel.mOnceOnlyLiveData.startActivityEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { param ->
                val arouterPath = param[BaseViewModel.ParameterField.PATH] as String
                val bundle = param[BaseViewModel.ParameterField.BUNDLE] as Bundle
                launchActivity(arouterPath, bundle)
            }
        })

        viewModel.mOnceOnlyLiveData.finishEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { it ->
                finish()
            }
        })

        viewModel.mOnceOnlyLiveData.clearTopActivityEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { clazzSimpleName ->
                AppManager.appManager!!.clearTopActivity(clazzSimpleName)
            }
        })

        viewModel.mOnceOnlyLiveData.backEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { it ->
                onBackPressed()
            }
        })
    }
}