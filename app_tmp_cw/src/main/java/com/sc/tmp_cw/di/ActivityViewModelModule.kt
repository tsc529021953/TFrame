package com.sc.tmp_cw.di

import androidx.lifecycle.ViewModel
import com.nbhope.lib_frame.di.ViewModelKey
import com.nbhope.lib_frame.viewmodel.LogViewModel
import com.sc.tmp_cw.vm.*
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
    @ViewModelKey(StationNotifyViewModel::class)
    abstract fun bindStationNotifyViewModel(viewModel: StationNotifyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SceneryViewModel::class)
    abstract fun bindSceneryViewModel(viewModel: SceneryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindSettingViewModel(viewModel: SettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StreamMediaViewModel::class)
    abstract fun bindStreamMediaViewModel(viewModel: StreamMediaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TravelViewModel::class)
    abstract fun bindTravelViewModel(viewModel: TravelViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CateViewModel::class)
    abstract fun bindCateViewModel(viewModel: CateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroduceViewModel::class)
    abstract fun bindIntroduceViewModel(viewModel: IntroduceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocalViewModel::class)
    abstract fun bindLocalViewModel(viewModel: LocalViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ParamViewModel::class)
    abstract fun bindParamViewModel(viewModel: ParamViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistViewModel::class)
    abstract fun bindPlaylistViewModel(viewModel: PlaylistViewModel): ViewModel
}
