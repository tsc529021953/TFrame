package com.sc.xs_cc.di

import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.xs_cc.activity.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}
