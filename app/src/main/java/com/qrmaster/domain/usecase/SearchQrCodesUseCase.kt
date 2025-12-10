package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.repository.QrCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching QR codes
 */
class SearchQrCodesUseCase @Inject constructor(
    private val repository: QrCodeRepository
) {
    operator fun invoke(query: String): Flow<List<QrCodeData>> {
        return repository.searchQrCodes(query)
    }
}

