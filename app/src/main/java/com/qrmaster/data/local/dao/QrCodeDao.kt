package com.qrmaster.data.local.dao

import androidx.room.*
import com.qrmaster.data.local.entity.QrCodeEntity
import com.qrmaster.domain.model.QrCodeType
import kotlinx.coroutines.flow.Flow

/**
 * DAO for QR Code operations
 */
@Dao
interface QrCodeDao {
    @Query("SELECT * FROM qr_codes WHERE (:isScanned IS NULL OR isScanned = :isScanned) ORDER BY createdAt DESC")
    fun getAllQrCodes(isScanned: Boolean? = null): Flow<List<QrCodeEntity>>

    @Query("SELECT * FROM qr_codes WHERE (:isScanned IS NULL OR isScanned = :isScanned) ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentQrCodes(limit: Int, isScanned: Boolean? = null): Flow<List<QrCodeEntity>>

    @Query("SELECT * FROM qr_codes WHERE id = :id")
    suspend fun getQrCodeById(id: Long): QrCodeEntity?

    @Query("SELECT * FROM qr_codes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchQrCodes(query: String): Flow<List<QrCodeEntity>>

    @Query("SELECT * FROM qr_codes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteQrCodes(): Flow<List<QrCodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQrCode(qrCode: QrCodeEntity): Long

    @Update
    suspend fun updateQrCode(qrCode: QrCodeEntity)

    @Delete
    suspend fun deleteQrCode(qrCode: QrCodeEntity)

    @Query("DELETE FROM qr_codes WHERE id = :id")
    suspend fun deleteQrCodeById(id: Long)

    @Query("DELETE FROM qr_codes WHERE id IN (:ids)")
    suspend fun deleteQrCodesByIds(ids: List<Long>)

    @Query("SELECT * FROM qr_codes WHERE type = 'TEXT' ORDER BY createdAt DESC")
    fun getTextQrCodes(): Flow<List<QrCodeEntity>>

    @Query("SELECT * FROM qr_codes WHERE type = :type ORDER BY createdAt DESC")
    fun getQrCodesByType(type: QrCodeType): Flow<List<QrCodeEntity>>

    @Query("""
    SELECT * FROM qr_codes 
    WHERE (:isFavorite IS NULL OR isFavorite = :isFavorite)
      AND (:isScanned IS NULL OR isScanned = :isScanned)
      AND (:type IS NULL OR type = :type)
    ORDER BY createdAt DESC
""")
    fun getQrCodes(
        isFavorite: Boolean? = null,
        isScanned: Boolean? = null,
        type: QrCodeType? = null
    ): Flow<List<QrCodeEntity>>

}
