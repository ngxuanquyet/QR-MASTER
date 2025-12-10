package com.qrmaster.domain.usecase

import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

/**
 * Use case for deleting QR codes
 */
class DeleteQrCodeUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteQrCode(id)
    }
}

