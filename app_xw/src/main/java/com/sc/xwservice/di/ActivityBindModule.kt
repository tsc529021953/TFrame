package com.sc.xwservice.di

import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.xwservice.XWActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHGActivity(): XWActivity


}
