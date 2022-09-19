package com.nbhope.lib_frame.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nbhope.lib_frame.app.AppManager
import com.nbhope.lib_frame.utils.HopeUtils

/**
 * Created by zhouwentao on 2019-08-28.
 */
abstract class BaseVMFragment<VM : BaseViewModel> : BaseFragment() {


    open lateinit var viewModel: VM



    protected fun subscribeOnceOnlyLiveDataCallBack() {

        viewModel.mOnceOnlyLiveData.showMessage.observe(this, Observer {
            it.getContentIfNotHandled()?.let { msg ->
                HopeUtils.makeText(context!!, msg)
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

        /**
         * fragment里尽量别用这个方法
         */
        viewModel.mOnceOnlyLiveData.finishEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { it ->
                activity?.finish()
            }
        })

        viewModel.mOnceOnlyLiveData.clearTopActivityEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { clazzSimpleName ->
                AppManager.appManager!!.clearTopActivity(clazzSimpleName)
            }
        })

        /**
         * fragment里尽量别用这个方法
         */
        viewModel.mOnceOnlyLiveData.backEvent.observe(this, Observer {
            it.getContentIfNotHandled()?.let { it ->
                activity?.onBackPressed()
            }
        })
    }

}