package com.sc.app_xsg.di

import com.sc.app_xsg.activity.HomeActivity
import com.sc.app_xsg.activity.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHomeActivity(): HomeActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}
