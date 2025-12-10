package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

/**
 * Use case for saving QR codes
 */
class SaveQrCodeUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    suspend operator fun invoke(qrCode: QrCodeData): Long {
        return repository.saveQrCode(qrCode)
    }
}