package com.qrmaster.domain.usecase

import com.qrmaster.data.local.entity.QrCodeEntity
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.repository.QrCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQrCodesUseCase @Inject constructor(
    private val qrCodeRepository: QrCodeRepository
) {
    operator fun invoke(
        isFavorite: Boolean? = null,
        isScanned: Boolean? = null,
        type: QrCodeType? = null
    ): Flow<List<QrCodeData>> {
        return qrCodeRepository.getQrCodes(isFavorite, isScanned, type)
    }
}