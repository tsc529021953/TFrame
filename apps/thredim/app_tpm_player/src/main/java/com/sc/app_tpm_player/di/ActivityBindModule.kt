package com.sc.app_tpm_player.di

import com.sc.app_tpm_player.MainActivity
import com.nbhope.lib_frame.di.scope.ActivityScope
import com.sc.app_tpm_player.HomeActivity
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
    abstract fun contributeHomeActivity(): HomeActivity
}
