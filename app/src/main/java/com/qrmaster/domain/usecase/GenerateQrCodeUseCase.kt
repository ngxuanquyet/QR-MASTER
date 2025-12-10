package com.qrmaster.domain.usecase

import android.graphics.Bitmap
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

/**
 * Use case for generating QR codes
 */
class GenerateQrCodeUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    suspend operator fun invoke(request: QrCodeData): Result<Long> {
        return repository.generateQrCode(request)
    }
}

