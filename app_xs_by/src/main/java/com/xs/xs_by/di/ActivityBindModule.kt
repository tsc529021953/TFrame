package com.xs.xs_by.di

import com.xs.xs_by.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}
