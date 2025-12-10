package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.repository.QrCodeRepository
import javax.inject.Inject

class GetQrCodeByType @Inject constructor(
    private val qrCodeRepository: QrCodeRepository
) {
    operator fun invoke(type: QrCodeType) = qrCodeRepository.getQrCodesByType(type)
}