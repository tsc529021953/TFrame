package com.sc.tmp_translate.di

import androidx.lifecycle.ViewModel
import com.sc.tmp_translate.vm.MainViewModel
import com.nbhope.lib_frame.di.ViewModelKey
import com.sc.tmp_translate.vm.TransMainViewModel
import com.sc.tmp_translate.vm.TransRecordViewModel
import com.sc.tmp_translate.vm.TranslateViewModel
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
    @ViewModelKey(TranslateViewModel::class)
    abstract fun bindTranslateViewModel(viewModel: TranslateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransMainViewModel::class)
    abstract fun bindTransMainViewModel(viewModel: TransMainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransRecordViewModel::class)
    abstract fun bindTransRecordViewModel(viewModel: TransRecordViewModel): ViewModel
}
