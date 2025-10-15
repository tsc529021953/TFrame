package com.sc.tmp_translate.di

import com.sc.tmp_translate.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.tmp_translate.DisplayActivity
import com.sc.tmp_translate.TranslateActivity
import com.sc.tmp_translate.TranslateOneDisplayActivity
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
    abstract fun contributeDisplayActivity(): DisplayActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeBuildersModule::class])
    abstract fun contributeTranslateActivity(): TranslateActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeBuildersModule::class])
    abstract fun contributeTranslateOneDisplayActivity(): TranslateOneDisplayActivity
}
