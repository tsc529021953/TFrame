package com.sc.hetest.di

import androidx.lifecycle.ViewModel
import com.nbhope.lib_frame.di.ViewModelKey
import com.sc.hetest.vm.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ActivityViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LightViewModel::class)
    abstract fun bindLightViewModel(viewModel: LightViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ComViewModel::class)
    abstract fun bindComViewModel(viewModel: ComViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VerInfoViewModel::class)
    abstract fun bindHomeViewModel(viewModel: VerInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HornViewModel::class)
    abstract fun bindHornViewModel(viewModel: HornViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MicViewModel::class)
    abstract fun bindMicViewModel(viewModel: MicViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InfoViewModel::class)
    abstract fun bindInfoViewModel(viewModel: InfoViewModel): ViewModel

}