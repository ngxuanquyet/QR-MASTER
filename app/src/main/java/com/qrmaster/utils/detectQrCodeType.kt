package com.qrmaster.utils

import com.google.mlkit.vision.barcode.common.Barcode
import com.qrmaster.domain.model.QrCodeType

fun detectQrCodeType(barcode: Barcode): QrCodeType {
    return when (barcode.valueType) {
        Barcode.TYPE_TEXT -> QrCodeType.TEXT
        Barcode.TYPE_URL -> detectSpecialUrl(barcode.url?.url)
        Barcode.TYPE_EMAIL -> QrCodeType.EMAIL
        Barcode.TYPE_PHONE -> QrCodeType.PHONE
        Barcode.TYPE_SMS -> QrCodeType.SMS
        Barcode.TYPE_WIFI -> QrCodeType.WIFI
        Barcode.TYPE_GEO -> QrCodeType.LOCATION
        Barcode.TYPE_CONTACT_INFO -> QrCodeType.CONTACT
        Barcode.TYPE_CALENDAR_EVENT -> QrCodeType.EVENT
        Barcode.TYPE_DRIVER_LICENSE -> QrCodeType.DRIVER_LICENSE
        Barcode.TYPE_ISBN -> QrCodeType.ISBN
        Barcode.TYPE_PRODUCT -> QrCodeType.PRODUCT
        else -> detectFormat(barcode)
    }
}

private fun detectFormat(barcode: Barcode): QrCodeType {
    return when (barcode.format) {
        Barcode.FORMAT_QR_CODE -> QrCodeType.QR_CODE
        Barcode.FORMAT_AZTEC -> QrCodeType.AZTEC
        Barcode.FORMAT_DATA_MATRIX -> QrCodeType.DATA_MATRIX
        Barcode.FORMAT_PDF417 -> QrCodeType.PDF417
        Barcode.FORMAT_CODE_128 -> QrCodeType.CODE_128
        Barcode.FORMAT_CODE_39 -> QrCodeType.CODE_39
        Barcode.FORMAT_CODE_93 -> QrCodeType.CODE_93
        Barcode.FORMAT_CODABAR -> QrCodeType.CODABAR
        Barcode.FORMAT_EAN_13 -> QrCodeType.EAN_13
        Barcode.FORMAT_EAN_8 -> QrCodeType.EAN_8
        Barcode.FORMAT_ITF -> QrCodeType.ITF
        Barcode.FORMAT_UPC_A -> QrCodeType.UPC_A
        Barcode.FORMAT_UPC_E -> QrCodeType.UPC_E
        else -> QrCodeType.UNKNOWN
    }
}

private fun detectSpecialUrl(url: String?): QrCodeType {
    if (url.isNullOrBlank()) return QrCodeType.URL
    val lower = url.lowercase()
    return when {
        lower.contains("crypto") || lower.contains("wallet") || lower.matches(Regex("^([a-zA-Z0-9]+):0x[0-9a-fA-F]{40}$")) ->
            QrCodeType.CRYPTO_WALLET

        lower.startsWith("https://play.google.com") || lower.startsWith("https://apps.apple.com") ->
            QrCodeType.APP_STORE

        lower.contains("facebook.com") -> QrCodeType.FACEBOOK
        lower.contains("twitter.com") || lower.contains("x.com") -> QrCodeType.TWITTER
        lower.contains("instagram.com") -> QrCodeType.INSTAGRAM
        lower.contains("youtube.com") || lower.contains("youtu.be") -> QrCodeType.YOUTUBE
        lower.contains("wa.me") || lower.contains("whatsapp.com") -> QrCodeType.WHATSAPP
        else -> QrCodeType.URL
    }
}