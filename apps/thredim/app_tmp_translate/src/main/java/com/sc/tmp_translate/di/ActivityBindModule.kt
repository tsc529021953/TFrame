package com.sc.tmp_translate.di

import com.sc.tmp_translate.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.tmp_translate.DisplayActivity
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
    abstract fun contributDisplayActivity(): DisplayActivity
}
