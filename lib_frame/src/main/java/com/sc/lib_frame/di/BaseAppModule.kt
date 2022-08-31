package com.sc.lib_frame.di

import android.app.Application
import com.google.gson.Gson
import com.sc.lib_frame.app.AppManager
import com.sc.lib_frame.utils.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Module
class BaseAppModule {


    @Singleton
    @Provides
    fun provideSp(app: Application): SharedPreferencesManager =
            SharedPreferencesManager(app)


    @Singleton
    @Provides
    fun provideGson() = Gson()

    /**
     * 只是在这初始化，可用自带的单例方法直接访问
     */
    @Singleton
    @Provides
    fun provideAppManager(app: Application): AppManager = AppManager.appManager!!.init(app)



}
