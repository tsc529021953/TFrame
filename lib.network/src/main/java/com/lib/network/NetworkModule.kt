package com.lib.network

import android.app.Application
import android.os.Environment
//import com.facebook.stetho.okhttp3.StethoInterceptor
import com.lib.network.api.ApiService
import com.lib.network.interceptor.TimeOutInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    companion object {
        private const val TIME_OUT = 10
        private const val HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = (20 * 1024 * 1024).toLong()
    }

    @Singleton
    @Provides
    fun provideApiService(client: OkHttpClient): ApiService = Retrofit.Builder()
        .client(client)
//        .baseUrl("http://${BuildConfig.HTTP_IP}:${BuildConfig.HTTP_PORT}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideOkHttp(app:Application): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.BASIC
        }

        builder.addInterceptor(logging)
            .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor(TimeOutInterceptor())
            .retryOnConnectionFailure(true)
            .cache(Cache(File(getCacheDir(app),"Http_Cache")
                , HTTP_RESPONSE_DISK_CACHE_MAX_SIZE))
        return builder.build()
    }

    private fun getCacheDir(app:Application):String{
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
            if (app.externalCacheDir == null){
                Environment.getExternalStorageDirectory().path
            }else{
                app.externalCacheDir!!.path
            }
        }else{
            app.cacheDir.path
        }
    }
}