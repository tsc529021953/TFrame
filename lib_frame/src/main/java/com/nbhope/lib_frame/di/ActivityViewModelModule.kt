package com.nbhope.lib_frame.di

import androidx.lifecycle.ViewModel
import com.nbhope.lib_frame.viewmodel.LogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ActivityViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LogViewModel::class)
    abstract fun bindLogViewModel(viewModel: LogViewModel): ViewModel
}
