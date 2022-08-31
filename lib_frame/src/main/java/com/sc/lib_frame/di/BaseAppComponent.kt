package com.sc.lib_frame.di

import android.app.Application
import com.google.gson.Gson
import com.sc.lib_frame.audio.HopeAudioRecorder
import com.lib.network.api.AmapApiService
import com.lib.network.api.ApiService
import com.lib.network.di.NetworkAppModule
import com.sc.lib_frame.app.AppDelegate
import com.sc.lib_frame.app.AppManager
import com.sc.lib_frame.network.NetworkCallback
import com.sc.lib_frame.utils.SharedPreferencesManager
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            BaseAppModule::class,
            NetworkAppModule::class
        ]
)
interface BaseAppComponent {

    val sharedPreferencesManager: SharedPreferencesManager

    val gson: Gson

    val appManager: AppManager

    val amapApiService:AmapApiService

    val apiService: ApiService

    val application: Application

    val okHttpClient: OkHttpClient

    val audioRecorder: HopeAudioRecorder

    val networkCallback: NetworkCallback

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): BaseAppComponent
    }

    fun inject(delegate: AppDelegate)
}