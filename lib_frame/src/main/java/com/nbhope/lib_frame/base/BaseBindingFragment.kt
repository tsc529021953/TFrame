package com.nbhope.lib_frame.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import timber.log.Timber

/**
 * Created by zhouwentao on 2019-08-28.
 */
abstract class BaseBindingFragment<T : ViewDataBinding, VM : BaseViewModel> : BaseVMFragment<VM>() {

    protected lateinit var binding: T

    /**
     * 关联Databinding和viewModel
     */
    abstract fun linkViewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        linkViewModel()
        rootView = binding.root
        return binding.root
    }
}