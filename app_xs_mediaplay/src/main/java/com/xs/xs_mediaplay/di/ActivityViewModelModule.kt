package com.xs.xs_mediaplay.di

import androidx.lifecycle.ViewModel
import com.xs.xs_mediaplay.vm.MainViewModel
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
}
