package com.sc.xs_control.di

import androidx.lifecycle.ViewModel
import com.sc.xs_control.vm.DeviceViewModel
import com.sc.xs_control.vm.LightViewModel
import com.sc.xs_control.vm.MainViewModel
import com.nbhope.lib_frame.di.ViewModelKey
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
    @ViewModelKey(DeviceViewModel::class)
    abstract fun bindDeviceViewModel(viewModel: DeviceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LightViewModel::class)
    abstract fun bindLightViewModel(viewModel: LightViewModel): ViewModel
}
