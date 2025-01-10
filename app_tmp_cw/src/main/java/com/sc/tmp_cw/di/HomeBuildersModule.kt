package com.sc.tmp_cw.di

import com.nbhope.lib_frame.di.scope.FragmentScope
import com.sc.tmp_cw.view.RightListFragment
import com.sc.tmp_cw.view.StreamMediaFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeBuildersModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeStreamMediaFragment(): StreamMediaFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeRightListFragment(): RightListFragment

}
