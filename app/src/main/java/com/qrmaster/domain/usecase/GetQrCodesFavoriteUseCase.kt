package com.qrmaster.domain.usecase

import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

class GetQrCodesFavoriteUseCase @Inject constructor(
    private val qrCodeRepository: QrCodeRepository
) {
    suspend operator fun invoke() = qrCodeRepository.getFavoriteQrCodes()
}