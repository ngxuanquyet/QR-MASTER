package com.qrmaster.domain.repository

import com.qrmaster.domain.model.ICheckProductInfo

interface ICheckRepository {
    suspend fun scanBarcode(barcode: String): Result<ICheckProductInfo>
}