package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.Location
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): Flow<Result<Location>> {
        return repository.getLocation()
    }
}