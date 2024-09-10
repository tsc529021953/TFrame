package com.xs.xs_ctrl.di

import androidx.lifecycle.ViewModel
import com.xs.xs_ctrl.vm.MainViewModel
import com.nbhope.lib_frame.di.ViewModelKey
import com.xs.xs_ctrl.vm.LauncherViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ActivityViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LauncherViewModel::class)
    abstract fun bindLauncherViewModel(viewModel: LauncherViewModel): ViewModel
}
