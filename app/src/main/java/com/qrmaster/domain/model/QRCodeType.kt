package com.qrmaster.domain.model

/**
 * Enum representing different types of QR code data
 */
enum class QrCodeType {
    // Barcode formats
    QR_CODE,
    AZTEC,
    DATA_MATRIX,
    PDF417,
    CODE_128,
    CODE_39,
    CODE_93,
    CODABAR,
    EAN_13,
    EAN_8,
    ITF,
    UPC_A,
    UPC_E,

    // Value types
    TEXT,
    URL,
    EMAIL,
    PHONE,
    SMS,
    WIFI,
    LOCATION,
    CONTACT,
    EVENT,
    DRIVER_LICENSE,
    ISBN,
    PRODUCT,

    CRYPTO_WALLET,
    APP_STORE,
    FACEBOOK,
    TWITTER,
    INSTAGRAM,
    YOUTUBE,
    WHATSAPP,

    // Unknown/Other
    UNKNOWN
}

val QrCodeType.displayName: String
    get() = when (this) {
        QrCodeType.TEXT -> "Văn bản"
        QrCodeType.URL -> "Website"
        QrCodeType.PHONE -> "Số điện thoại"
        QrCodeType.SMS -> "SMS"
        QrCodeType.EMAIL -> "Email"
        QrCodeType.WIFI -> "Wi-Fi"
        QrCodeType.LOCATION -> "Vị trí"
        QrCodeType.CONTACT -> "Danh bạ"
        QrCodeType.EVENT -> "Sự kiện"
        QrCodeType.CRYPTO_WALLET -> "Ví tiền mã hóa"
        QrCodeType.APP_STORE -> "Cửa hàng ứng dụng"
        QrCodeType.FACEBOOK -> "Facebook"
        QrCodeType.TWITTER -> "X"
        QrCodeType.INSTAGRAM -> "Instagram"
        QrCodeType.YOUTUBE -> "YouTube"
        QrCodeType.WHATSAPP -> "WhatsApp"
        QrCodeType.PRODUCT -> "Sản phẩm"

        QrCodeType.QR_CODE -> "Mã QR"
        QrCodeType.AZTEC -> "Aztec"
        QrCodeType.DATA_MATRIX -> "Data Matrix"
        QrCodeType.PDF417 -> "PDF417"

        // Mã vạch 1D phổ biến
        QrCodeType.CODE_128 -> "Code 128"
        QrCodeType.CODE_39 -> "Code 39"
        QrCodeType.CODE_93 -> "Code 93"
        QrCodeType.CODABAR -> "Codabar"
        QrCodeType.EAN_13 -> "EAN-13"
        QrCodeType.EAN_8 -> "EAN-8"
        QrCodeType.ITF -> "ITF"
        QrCodeType.UPC_A -> "UPC-A"
        QrCodeType.UPC_E -> "UPC-E"

        // Đặc biệt
        QrCodeType.DRIVER_LICENSE -> "Bằng lái xe"
        QrCodeType.ISBN -> "ISBN (Sách)"

        QrCodeType.UNKNOWN -> "Không xác định"
    }