package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

class GetQrCodeByIdUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    suspend operator fun invoke(id: Long): QrCodeData? {
        return repository.getQrCodeById(id)
    }
}