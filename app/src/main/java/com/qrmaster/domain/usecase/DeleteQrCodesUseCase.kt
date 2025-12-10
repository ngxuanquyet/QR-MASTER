package com.qrmaster.domain.usecase

import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

class DeleteQrCodesUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    suspend operator fun invoke(ids: List<Long>) {
        repository.deleteQrCodes(ids)
    }
}

