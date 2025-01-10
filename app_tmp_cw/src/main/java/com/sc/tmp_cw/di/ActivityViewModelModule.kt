package com.sc.tmp_cw.di

import androidx.lifecycle.ViewModel
import com.sc.tmp_cw.vm.MainViewModel
import com.nbhope.lib_frame.di.ViewModelKey
import com.sc.tmp_cw.vm.SceneryViewModel
import com.sc.tmp_cw.vm.StreamMediaViewModel
import com.sc.tmp_cw.vm.UrgentNotifyViewModel
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
    @ViewModelKey(UrgentNotifyViewModel::class)
    abstract fun bindUrgentNotifyViewModel(viewModel: UrgentNotifyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SceneryViewModel::class)
    abstract fun bindSceneryViewModel(viewModel: SceneryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StreamMediaViewModel::class)
    abstract fun bindStreamMediaViewModel(viewModel: StreamMediaViewModel): ViewModel
}
