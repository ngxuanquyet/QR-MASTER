package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.repository.QrCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all QR codes
 */
class GetAllQrCodesUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    operator fun invoke(isScanned: Boolean? = null): Flow<List<QrCodeData>> {
        return repository.getAllQrCodes(isScanned)
    }
}

