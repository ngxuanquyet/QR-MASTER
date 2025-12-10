package com.qrmaster.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qrmaster.data.local.dao.QrCodeDao
import com.qrmaster.data.local.entity.QrCodeEntity

/**
 * Room database for QR Master app
 */
@Database(
    entities = [QrCodeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QrCodeDatabase : RoomDatabase() {
    abstract fun qrCodeDao(): QrCodeDao
    
    companion object {
        const val DATABASE_NAME = "qr_master_database"
    }
}

