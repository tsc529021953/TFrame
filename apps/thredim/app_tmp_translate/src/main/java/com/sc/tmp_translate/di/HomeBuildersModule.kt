package com.sc.tmp_translate.di

import com.nbhope.lib_frame.di.scope.FragmentScope
import com.sc.tmp_translate.view.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeBuildersModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeTransMainFragment(): TransMainFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeTransRecordFragment(): TransRecordFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeTranslatingFragment(): TranslatingFragment

}
