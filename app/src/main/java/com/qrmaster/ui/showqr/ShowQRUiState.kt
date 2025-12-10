package com.qrmaster.ui.showqr

import android.graphics.Bitmap
import com.qrmaster.domain.model.QrCodeData

data class ShowQRUiState(
    val qrCode: QrCodeData? = null,
    val qrBitmap: Bitmap? = null,
    val isSaved: Boolean = false,
    val isCopied: Boolean = false,

    val errorMessage: String? = null
)