package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.repository.QrCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting recent QR codes
 */
class GetRecentQrCodesUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    operator fun invoke(limit: Int = 5, isScanned: Boolean? = null): Flow<List<QrCodeData>> {
        return repository.getRecentQrCodes(limit, isScanned)
    }
}

