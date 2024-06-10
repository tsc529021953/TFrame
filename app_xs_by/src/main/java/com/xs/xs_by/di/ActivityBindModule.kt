package com.xs.xs_by.di

import com.xs.xs_by.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import com.xs.xs_by.activity.LauncherActivity
import com.xs.xs_by.activity.OneCtrlActivity
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
    abstract fun contributeOneCtrlActivity(): OneCtrlActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeLauncherActivity(): LauncherActivity
}
