package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.ICheckProductInfo
import com.qrmaster.domain.repository.ICheckRepository
import javax.inject.Inject

class ScanICheckBarcodeUseCase @Inject constructor(
    private val repository: ICheckRepository
) {
    suspend operator fun invoke(barcode: String) : Result<ICheckProductInfo> =
        repository.scanBarcode(barcode)
}