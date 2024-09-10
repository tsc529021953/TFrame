package com.xs.xs_ctrl.di

import com.xs.xs_ctrl.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import com.xs.xs_ctrl.activity.LauncherActivity
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
    abstract fun contributeLauncherActivity(): LauncherActivity
}
