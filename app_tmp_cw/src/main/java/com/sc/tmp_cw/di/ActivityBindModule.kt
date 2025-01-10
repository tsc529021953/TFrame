package com.sc.tmp_cw.di

import com.sc.tmp_cw.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.tmp_cw.activity.SceneryActivity
import com.sc.tmp_cw.activity.UrgentNotifyActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeSceneryActivity(): SceneryActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeUrgentNotifyActivity(): UrgentNotifyActivity
}
