package com.nbhope.lib_frame.di

import com.nbhope.lib_frame.activity.LogActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeLogActivity(): LogActivity
}
