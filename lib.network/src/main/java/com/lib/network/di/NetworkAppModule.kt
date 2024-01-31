package com.lib.network.di

import com.lib.frame.dynconfig.HopeDynConfig
import com.lib.network.api.AMAP_API_URL
import com.lib.network.api.AmapApiService
import com.lib.network.api.ApiService
import com.lib.network.utils.ApiDns
import com.lib.network.utils.LoggingInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @Author qiukeling
 * @Date 2019-09-10-13:42
 * @Email qiukeling@nbhope.cn
 */
@Module
class NetworkAppModule {

    companion object {
        private const val TIME_OUT = 10
    }

    @Singleton
    @Provides
    fun provideApiService(client: OkHttpClient): ApiService = Retrofit.Builder()
            .client(client)
//            .baseUrl("http://192.168.2.9:8080/")
            .baseUrl("${HopeDynConfig.instence.getHttp()}://${HopeDynConfig.instence.getHttpIp()}:${HopeDynConfig.instence.getHttpPort()}/")
            .addConverterFactory(GsonConverterFactory.create())
//        .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val logging = LoggingInterceptor()
//        if (BuildConfig.DEBUG) {
//            logging.level = HttpLoggingInterceptor.Level.BODY
//        } else {
//            logging.level = HttpLoggingInterceptor.Level.BASIC
//        }
        builder.addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .dns(ApiDns())

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideAmapApiService(client: OkHttpClient):AmapApiService = Retrofit.Builder()
            .client(client)
            .baseUrl(AMAP_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AmapApiService::class.java)
}
