package com.sc.app_xsg.di

import androidx.lifecycle.ViewModel
import com.sc.app_xsg.vm.HomeViewModel
import com.nbhope.lib_frame.di.ViewModelKey
import com.sc.app_xsg.vm.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ActivityViewModelModule {


    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

}