package com.qrmaster.domain.repository

import com.qrmaster.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getLocation(): Flow<Result<Location>>
}