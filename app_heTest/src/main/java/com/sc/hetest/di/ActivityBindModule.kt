package com.sc.hetest.di

import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.hetest.activity.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHGActivity(): HGActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeComActivity(): ComActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeVerInfoActivity(): VerInfoActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHornActivity(): HornActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMicActivity(): MicActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeWIFIActivity(): WIFIActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeBTActivity(): BTActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMemoryActivity(): MemoryActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeCamActivity(): CamActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeTouchActivity(): TouchActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeUSBCamActivity(): USBCamActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeLCDActivity(): LCDActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeBGLActivity(): BGLActivity

}
