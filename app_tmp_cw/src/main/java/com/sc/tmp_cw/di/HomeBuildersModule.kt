package com.sc.tmp_cw.di

import com.nbhope.lib_frame.di.scope.FragmentScope
import com.sc.tmp_cw.view.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeBuildersModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeStreamMediaFragment(): StreamMediaFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeInteractiveFragment(): InteractiveFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeInteractiveGuideFragment(): InteractiveGuideFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeTravelFragment(): TravelFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeStationInfoSearchFragment(): StationInfoSearchFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeLineFragment(): LineFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeCateFragment(): CateFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeLocalFragment(): LocalFragment

}
