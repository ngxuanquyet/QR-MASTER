package com.qrmaster.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.qrmaster.data.local.dao.QrCodeDao
import com.qrmaster.data.local.entity.QrCodeEntity
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.model.buildContent
import com.qrmaster.domain.repository.QrCodeRepository
import com.qrmaster.utils.QrCodeGenerator
import com.qrmaster.utils.saveQrToGallery
import com.qrmaster.utils.shareQrCodeBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of QrCodeRepository
 */
class QrCodeRepositoryImpl @Inject constructor(
    private val qrCodeDao: QrCodeDao,
    @ApplicationContext private val context: Context
) : QrCodeRepository {

    override suspend fun generateQrCode(request: QrCodeData): Result<Long> {
        val id = saveQrCode(request.buildContent())
        return Result.success(id)
    }

    override suspend fun saveQrCode(qrCode: QrCodeData): Long {
        return qrCodeDao.insertQrCode(QrCodeEntity.fromDomainModel(qrCode))
    }

    override suspend fun saveQrToGallery(
        bitmap: Bitmap,
        fileName: String
    ): Result<Boolean> = runCatching {
        context.saveQrToGallery(bitmap, fileName)
    }

    override suspend fun deleteQrCode(id: Long) {
        qrCodeDao.deleteQrCodeById(id)
    }

    override suspend fun deleteQrCodes(ids: List<Long>) {
        qrCodeDao.deleteQrCodesByIds(ids)
    }

    override suspend fun updateQrCode(qrCode: QrCodeData) {
        qrCodeDao.updateQrCode(QrCodeEntity.fromDomainModel(qrCode))
    }

    override suspend fun getQrCodeById(id: Long): QrCodeData? {
        return qrCodeDao.getQrCodeById(id)?.toDomainModel()
    }

    override fun getAllQrCodes(isScanned: Boolean?): Flow<List<QrCodeData>> {
        return qrCodeDao.getAllQrCodes(isScanned).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getRecentQrCodes(limit: Int, isScanned: Boolean?): Flow<List<QrCodeData>> {
        return qrCodeDao.getRecentQrCodes(limit, isScanned = isScanned).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchQrCodes(query: String): Flow<List<QrCodeData>> {
        return qrCodeDao.searchQrCodes(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getFavoriteQrCodes(): Flow<List<QrCodeData>> {
        return qrCodeDao.getFavoriteQrCodes().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun shareQrCode(bitmap: Bitmap): Result<Unit> = runCatching {
        context.shareQrCodeBitmap(bitmap)
    }

    override suspend fun toggleFavorite(id: Long) {
        val entity = qrCodeDao.getQrCodeById(id) ?: return
        val updatedEntity = entity.copy(isFavorite = !entity.isFavorite)
        qrCodeDao.updateQrCode(updatedEntity)
    }

    override fun getTextQrCodes(): Flow<List<QrCodeData>> {
        return qrCodeDao.getTextQrCodes().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getQrCodesByType(type: QrCodeType): Flow<List<QrCodeData>> {
        return qrCodeDao.getQrCodesByType(type).map { entities ->
            entities.map {
                it.toDomainModel()
            }
        }
    }

    override fun getQrCodes(
        isFavorite: Boolean?,
        isScanned: Boolean?,
        type: QrCodeType?
    ): Flow<List<QrCodeData>> {
        return qrCodeDao.getQrCodes(isFavorite, isScanned, type).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}

