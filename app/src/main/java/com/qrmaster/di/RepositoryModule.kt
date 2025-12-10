package com.qrmaster.di

import android.content.Context
import com.qrmaster.data.repository.ICheckRepositoryImpl
import com.qrmaster.data.repository.LocationRepositoryImpl
import com.qrmaster.data.repository.QrCodeRepositoryImpl
import com.qrmaster.domain.repository.ICheckRepository
import com.qrmaster.domain.repository.LocationRepository
import com.qrmaster.domain.repository.QrCodeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for repository dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQrCodeRepository(
        qrCodeRepositoryImpl: QrCodeRepositoryImpl
    ): QrCodeRepository

    @Binds
    @Singleton
    abstract fun bindICheckRepository(
        iCheckRepositoryImpl: ICheckRepositoryImpl
    ):  ICheckRepository
}

