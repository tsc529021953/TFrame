package com.sc.xs_control.di

import com.nbhope.lib_frame.di.scope.FragmentScope
import com.sc.xs_control.view.DeviceFragment
import com.sc.xs_control.view.LightFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeDeviceFragment(): DeviceFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeLightFragment(): LightFragment
}
