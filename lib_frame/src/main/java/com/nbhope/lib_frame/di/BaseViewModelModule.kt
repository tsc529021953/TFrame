package com.nbhope.lib_frame.di

import androidx.lifecycle.ViewModelProvider
import com.nbhope.lib_frame.viewmodel.HopeViewModelFactory
import dagger.Binds
import dagger.Module

/**
 * @Author qiukeling
 * @Date 2020-02-28-16:10
 * @Email qiukeling@nbhope.cn
 */
@Suppress("unused")
@Module
abstract class BaseViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: HopeViewModelFactory): ViewModelProvider.Factory
}
