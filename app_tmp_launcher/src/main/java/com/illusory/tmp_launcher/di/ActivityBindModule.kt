package com.illusory.tmp_launcher.di

import com.illusory.tmp_launcher.MainActivity
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
