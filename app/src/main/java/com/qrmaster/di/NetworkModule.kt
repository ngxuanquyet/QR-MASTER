package com.qrmaster.di

import com.qrmaster.data.remote.ICheckApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @Named("VisionClient")
    fun provideVisionOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .build()

    @Provides
    @Named("vision_base_url")
    fun provideVisionBaseUrl(): String = "https://v98store.com/v1/chat/completions"

    @Provides
    @Named("vision_api_key")
    fun provideVisionApiKey() : String = "sk-uDVpBlsKWTkjLeIkkMZ9vkrgFCfk3njZ1fdS8juwaOU9KSK4"

    private const val BASE_URL = "https://api-social.icheck.com.vn/"
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("origin", "https://icheck.vn")
                    .addHeader("referer", "https://icheck.vn/")
                    .addHeader("user-agent", "Mozilla/5.0 (Linux; Android 13)")
                    .build()
            )
        }
        .build()

    @Provides
    @Singleton
    fun provideICheckApi(client: OkHttpClient): ICheckApiService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ICheckApiService::class.java)
}