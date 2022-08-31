package com.sc.app_xsg.di

import androidx.lifecycle.ViewModel
import com.sc.app_xsg.vm.HomeViewModel
import com.sc.lib_frame.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ActivityViewModelModule {


    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindInterPhoneViewModel(viewModel: HomeViewModel): ViewModel

}