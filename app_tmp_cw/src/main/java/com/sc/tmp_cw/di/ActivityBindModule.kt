package com.sc.tmp_cw.di

import com.nbhope.lib_frame.activity.LogActivity
import com.sc.tmp_cw.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.tmp_cw.activity.*
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
    abstract fun contributeSettingActivity(): SettingActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeUrgentNotifyActivity(): UrgentNotifyActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeStationNotifyActivity(): StationNotifyActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeIntroduceActivity(): IntroduceActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributePlatylistActivity(): PlatylistActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeParamActivity(): ParamActivity
}
