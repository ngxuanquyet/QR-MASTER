package com.qrmaster.di

import android.content.Context
import androidx.room.Room
import com.qrmaster.data.local.dao.QrCodeDao
import com.qrmaster.data.local.database.QrCodeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideQrCodeDatabase(
        @ApplicationContext context: Context
    ): QrCodeDatabase {
        return Room.databaseBuilder(
            context,
            QrCodeDatabase::class.java,
            QrCodeDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    fun provideQrCodeDao(database: QrCodeDatabase): QrCodeDao {
        return database.qrCodeDao()
    }
}

