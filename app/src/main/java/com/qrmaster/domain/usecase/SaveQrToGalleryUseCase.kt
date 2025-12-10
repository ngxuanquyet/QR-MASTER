package com.qrmaster.domain.usecase

import android.graphics.Bitmap
import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

class SaveQrToGalleryUseCase @Inject constructor(
    private val qrCodeRepository: QrCodeRepository
) {
    suspend operator fun invoke(
        bitmap: Bitmap,
        fileName: String = "QR_${System.currentTimeMillis()}.png"
    ): Result<Boolean> {
        return qrCodeRepository.saveQrToGallery(bitmap, fileName)
    }
}