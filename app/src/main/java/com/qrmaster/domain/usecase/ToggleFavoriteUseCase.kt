package com.qrmaster.domain.usecase

import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val qrCodeRepository: QrCodeRepository
) {
    suspend operator fun  invoke(id: Long){
        qrCodeRepository.toggleFavorite(id)
    }
}