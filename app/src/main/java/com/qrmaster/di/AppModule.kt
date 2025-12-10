package com.qrmaster.di

import android.content.Context
import android.net.wifi.WifiManager
import com.qrmaster.data.repository.LocationRepositoryImpl
import com.qrmaster.data.repository.VisionRepositoryImpl
import com.qrmaster.data.repository.WifiRepositoryImpl
import com.qrmaster.domain.repository.LocationRepository
import com.qrmaster.domain.repository.VisionRepository
import com.qrmaster.domain.repository.WifiRepository
import com.qrmaster.ui.settings.SettingsPreferences
import com.qrmaster.utils.FeedbackManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSettingsPreferences(@ApplicationContext context: Context): SettingsPreferences {
        return SettingsPreferences(context)
    }

    @Provides
    @Singleton
    fun provideFeedbackManager(@ApplicationContext context: Context): FeedbackManager {
        return FeedbackManager(context)
    }

    @Singleton
    @Provides
    fun provideLocationRepository(
        @ApplicationContext context: Context,
    ): LocationRepository {
        return LocationRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideWifiManager(@ApplicationContext context: Context): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    @Provides
    @Singleton
    fun provideWifiRepository(
        @ApplicationContext context: Context,
        wifiManager: WifiManager
    ): WifiRepository {
        return WifiRepositoryImpl(context, wifiManager)
    }
}