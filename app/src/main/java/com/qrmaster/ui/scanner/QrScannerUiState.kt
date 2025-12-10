package com.qrmaster.ui.scanner

data class QrScannerUiState(
    val isFlashOn: Boolean = false,
    val isFrontCamera: Boolean = false,
    val lastScannedCode: String? = null,
    val isScanning: Boolean = true,
    val errorMessage: String? = null
)