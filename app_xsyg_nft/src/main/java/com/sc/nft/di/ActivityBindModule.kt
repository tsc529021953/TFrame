package com.sc.nft.di

import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.nft.activity.FileImg2Activity
import com.sc.nft.activity.FileImg3Activity
import com.sc.nft.activity.MainActivity
import com.sc.nft.activity.ScreenActivity
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
    abstract fun contributeScreenActivity(): ScreenActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeFileImg2Activity(): FileImg2Activity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeFileImg3Activity(): FileImg3Activity

}
