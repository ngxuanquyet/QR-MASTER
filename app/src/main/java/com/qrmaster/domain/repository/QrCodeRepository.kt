package com.qrmaster.domain.repository

import android.graphics.Bitmap
import com.qrmaster.data.local.entity.QrCodeEntity
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for QR code operations
 */
interface QrCodeRepository {
    suspend fun generateQrCode(request: QrCodeData): Result<Long>
    suspend fun saveQrCode(qrCode: QrCodeData): Long
    suspend fun saveQrToGallery(bitmap: Bitmap, fileName: String): Result<Boolean>
    suspend fun deleteQrCode(id: Long)
    suspend fun deleteQrCodes(ids: List<Long>)
    suspend fun updateQrCode(qrCode: QrCodeData)
    suspend fun getQrCodeById(id: Long): QrCodeData?
    fun getAllQrCodes(isScanned: Boolean? = null): Flow<List<QrCodeData>>
    fun getRecentQrCodes(limit: Int = 5, isScanned: Boolean? = null): Flow<List<QrCodeData>>
    fun searchQrCodes(query: String): Flow<List<QrCodeData>>
    fun getFavoriteQrCodes(): Flow<List<QrCodeData>>
    suspend fun shareQrCode(bitmap: Bitmap): Result<Unit>
    suspend fun toggleFavorite(id: Long)
    fun getTextQrCodes(): Flow<List<QrCodeData>>
    fun getQrCodesByType(type: QrCodeType): Flow<List<QrCodeData>>
    fun getQrCodes(
        isFavorite: Boolean? = null,
        isScanned: Boolean? = null,
        type: QrCodeType? = null
    ): Flow<List<QrCodeData>>
}