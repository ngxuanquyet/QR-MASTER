package com.qrmaster.ui.generate

import android.graphics.Bitmap
import com.qrmaster.domain.model.Location
import com.qrmaster.domain.model.QrCodeType

//data class GenerateUIState(
//    val inputText: String = "",
//    val isGenerating: Boolean = false,
//    val idQr: Long? = null,
//    val errorMessage: String? = null
//)

data class GenerateUIState(
    val idQr: Long? = null,
    val type: QrCodeType = QrCodeType.TEXT,
    val fields: Map<String, String> = emptyMap(),
    val currentLocation: Location = Location(),
    val wifiList: List<String> = emptyList(),

    val isLocationLoading: Boolean = false,
    val isWifiLoading: Boolean = false,
    val isGenerating: Boolean = false,

    val errorMessage: String? = null,
) {
    val canGenerate get() = fields.all { it.value.isNotBlank() }
}