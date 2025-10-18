package com.sc.common.di

import com.sc.common.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.common.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHomeActivity(): HomeActivity
}
