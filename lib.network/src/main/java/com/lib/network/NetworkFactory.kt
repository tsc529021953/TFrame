package com.lib.network

import com.lib.network.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkFactory {
    fun provideApiService(): ApiService = Retrofit.Builder()
            .client(provideOkHttp())
            .baseUrl("http://47.96.159.79/")
            .addConverterFactory(GsonConverterFactory.create())
//        .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ApiService::class.java)

    private fun provideOkHttp(): OkHttpClient{
        val builder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.BASIC
        }

        builder.addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)

        return builder.build()
    }
}