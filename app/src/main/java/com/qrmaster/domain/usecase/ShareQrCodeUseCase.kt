package com.qrmaster.domain.usecase

import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject
import android.graphics.Bitmap

class ShareQrCodeUseCase @Inject constructor(
    private val qrCodeRepository: QrCodeRepository
) {
    suspend operator fun invoke(bitmap: Bitmap) = qrCodeRepository.shareQrCode(bitmap)
}