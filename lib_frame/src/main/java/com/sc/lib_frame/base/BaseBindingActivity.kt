package com.sc.lib_frame.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.KeyEvent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import timber.log.Timber

/**
 * Created by zhouwentao on 2019-08-28.
 */
abstract class BaseBindingActivity<T : ViewDataBinding, VM : BaseViewModel> : BaseVMActivity<VM>() {
    protected lateinit var binding: T
    protected var mStateSaved = false;

    /**
     * 关联Databinding和viewModel
     */
    abstract fun linkViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        mStateSaved = false
        linkViewModel()
        subscribeOnceOnlyLiveDataCallBack()
        subscribeUi()
        initData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mStateSaved = true
    }

    override fun onResume() {
        super.onResume()
        mStateSaved = false
    }

    override fun onStop() {
        super.onStop()
        mStateSaved = true
    }

    override fun onStart() {
        super.onStart()
        mStateSaved = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (!mStateSaved) {
            super.onKeyDown(keyCode, event)
        } else {
            true
        }
    }

    override fun onBackPressed() {
        if (!mStateSaved) {
            super.onBackPressed()
        }
    }
}