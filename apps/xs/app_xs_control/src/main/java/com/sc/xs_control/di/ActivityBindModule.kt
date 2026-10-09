package com.sc.xs_control.di

import com.sc.xs_control.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}
