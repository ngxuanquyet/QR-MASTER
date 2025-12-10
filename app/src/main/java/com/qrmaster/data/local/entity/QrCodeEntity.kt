package com.qrmaster.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.model.parseToRawData
import java.util.Date

/**
 * Room entity for QR Code
 */
@Entity(tableName = "qr_codes")
data class QrCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val type: String,
    val title: String = "",
    val thumbnailPath: String? = null,
    val createdAt: Long, // Date as timestamp
    val isFavorite: Boolean = false,
    val isScanned: Boolean = false,
    val colorForeground: String = "#000000",
    val colorBackground: String = "#FFFFFF",
    val patternStyle: String = "square",
    val errorCorrectionLevel: String = "M",
    val logoPath: String? = null
) {
    fun toDomainModel(): QrCodeData {
        return QrCodeData(
            id = id,
            content = content,
            type = QrCodeType.valueOf(type),
            data = content.parseToRawData(QrCodeType.valueOf(type)),
            title = title,
            thumbnailPath = thumbnailPath,
            createdAt = Date(createdAt),
            isFavorite = isFavorite,
            isScanned = isScanned,
            colorForeground = colorForeground,
            colorBackground = colorBackground,
            patternStyle = patternStyle,
            errorCorrectionLevel = errorCorrectionLevel,
            logoPath = logoPath
        )
    }

    companion object {
        fun fromDomainModel(qrCode: QrCodeData): QrCodeEntity {
            return QrCodeEntity(
                id = qrCode.id,
                content = qrCode.content,
                type = qrCode.type.name,
                title = qrCode.title,
                thumbnailPath = qrCode.thumbnailPath,
                createdAt = qrCode.createdAt.time,
                isFavorite = qrCode.isFavorite,
                isScanned = qrCode.isScanned,
                colorForeground = qrCode.colorForeground,
                colorBackground = qrCode.colorBackground,
                patternStyle = qrCode.patternStyle,
                errorCorrectionLevel = qrCode.errorCorrectionLevel,
                logoPath = qrCode.logoPath
            )
        }
    }
}

