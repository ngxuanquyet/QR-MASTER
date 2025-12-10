package com.qrmaster.domain.model

import java.util.Date

/**
 * Domain model for QR Code data
 */
data class QrCodeData(
    val id: Long = 0,
    val content: String = "",
    val data: Map<String, String> = emptyMap(),
    val type: QrCodeType,
    val title: String = "",
    val thumbnailPath: String? = null,
    val createdAt: Date = Date(),
    val isFavorite: Boolean = false,
    val isScanned: Boolean = false, // true if scanned, false if generated
    val colorForeground: String = "#000000",
    val colorBackground: String = "#FFFFFF",
    val patternStyle: String = "square", // square, dot, rounded
    val errorCorrectionLevel: String = "M", // L, M, Q, H
    val logoPath: String? = null,
    val size: Int = 512,
    val margin: Int = 4
)

fun QrCodeData.buildContent(): QrCodeData {
    return this.copy(content = buildContent(this))
}


fun buildContent(request: QrCodeData): String {
    return when (request.type) {
        QrCodeType.TEXT -> request.data["text"] ?: ""
        QrCodeType.URL -> request.data["url"] ?: ""
        QrCodeType.WIFI -> buildWifiContent(request.data)
        QrCodeType.CONTACT -> buildContactContent(request.data)
        QrCodeType.EMAIL -> buildEmailContent(request.data)
        QrCodeType.PHONE -> "tel:${request.data["phone"]}"
        QrCodeType.SMS -> buildSmsContent(request.data)
        QrCodeType.LOCATION -> buildLocationContent(request.data)
        QrCodeType.EVENT -> buildEventContent(request.data)
        QrCodeType.CRYPTO_WALLET -> buildCryptoWalletContent(request.data)
        else -> request.data["text"] ?: ""
    }
}

private fun buildWifiContent(data: Map<String, String>): String {
    val ssid = data["ssid"] ?: ""
    val password = data["password"] ?: ""
    val security = data["security"] ?: "WPA"
    return "WIFI:T:$security;S:$ssid;P:$password;;"
}

private fun buildContactContent(data: Map<String, String>): String {
    val name = data["name"] ?: ""
    val phone = data["phone"] ?: ""
    val email = data["email"] ?: ""
    val address = data["address"] ?: ""
    return "BEGIN:VCARD\nVERSION:3.0\nFN:$name\nTEL:$phone\nEMAIL:$email\nADR:$address\nEND:VCARD"
}

private fun buildEmailContent(data: Map<String, String>): String {
    val email = data["email"] ?: ""
    val subject = data["subject"] ?: ""
    val body = data["body"] ?: ""
    return "mailto:$email?subject=$subject&body=$body"
}

private fun buildSmsContent(data: Map<String, String>): String {
    val phone = data["phone"] ?: ""
    val message = data["message"] ?: ""
    return "sms:$phone?body=$message"
}

private fun buildLocationContent(data: Map<String, String>): String {
    val latitude = data["latitude"] ?: "0"
    val longitude = data["longitude"] ?: "0"
    return "geo:$latitude,$longitude"
}

private fun buildEventContent(data: Map<String, String>): String {
    val title = data["title"] ?: ""
    val start = data["start"] ?: ""
    val end = data["end"] ?: ""
    val location = data["location"] ?: ""
    return "BEGIN:VEVENT\nSUMMARY:$title\nDTSTART:$start\nDTEND:$end\nLOCATION:$location\nEND:VEVENT"
}

private fun buildCryptoWalletContent(data: Map<String, String>): String {
    val address = data["address"] ?: ""
    val currency = data["currency"] ?: "BTC"
    return "$currency:$address"
}

fun String.parseToRawData(type: QrCodeType): Map<String, String> {
    return when (type) {
        QrCodeType.TEXT -> mapOf("text" to this)
        QrCodeType.URL -> mapOf("url" to this)

        QrCodeType.PHONE -> mapOf("phone" to removePrefix("tel:"))

        QrCodeType.SMS -> {
            val phone = substringAfter("sms:").substringBefore("?body=")
            val message = substringAfter("body=", missingDelimiterValue = "")
            mapOf("phone" to phone, "message" to message)
        }

        QrCodeType.EMAIL -> {
            val email = substringAfter("mailto:").substringBefore("?")
            val subject = substringAfter("subject=", "").substringBefore("&body=")
            val body = substringAfter("&body=", "")
            mapOf("email" to email, "subject" to subject, "body" to body)
        }

        QrCodeType.WIFI -> {
            if (!startsWith("WIFI:")) return emptyMap()
            val map = removePrefix("WIFI:").removeSuffix(";").split(";")
                .associate {
                    val kv = it.split(":", limit = 2)
                    if (kv.size == 2) kv[0] to kv[1] else kv[0] to ""
                }
            mapOf(
                "ssid" to (map["S"] ?: ""),
                "password" to (map["P"] ?: ""),
                "security" to (map["T"] ?: "WPA")
            )
        }

        QrCodeType.LOCATION -> {
            val coords = removePrefix("geo:").split(",")
            mapOf(
                "latitude" to (coords.getOrNull(0) ?: "0"),
                "longitude" to (coords.getOrNull(1) ?: "0")
            )
        }

        QrCodeType.CONTACT -> {
            mapOf(
                "name" to substringAfter("FN:").substringBefore("\n"),
                "phone" to substringAfter("TEL:").substringBefore("\n"),
                "email" to substringAfter("EMAIL:").substringBefore("\n"),
                "address" to substringAfter("ADR:").substringBefore("\n")
            ).mapValues { it.value.takeIf { it.isNotBlank() } ?: "" }
        }

        QrCodeType.EVENT -> {
            mapOf(
                "title" to substringAfter("SUMMARY:").substringBefore("\n"),
                "start" to substringAfter("DTSTART:").substringBefore("\n"),
                "end" to substringAfter("DTEND:").substringBefore("\n"),
                "location" to substringAfter("LOCATION:").substringBefore("\n")
            ).mapValues { it.value.takeIf { it.isNotBlank() } ?: "" }
        }

        QrCodeType.CRYPTO_WALLET -> {
            val parts = split(":", limit = 2)
            mapOf(
                "currency" to (parts.getOrNull(0) ?: "BTC"),
                "address" to (parts.getOrNull(1) ?: "")
            )
        }

        else -> mapOf("text" to this)
    }
}