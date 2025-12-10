package com.qrmaster.ui.scanner

import android.app.Application
import androidx.camera.core.CameraSelector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.usecase.SaveQrCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    application: Application,
    private val saveQrScanUseCase: SaveQrCodeUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState: StateFlow<QrScannerUiState> = _uiState.asStateFlow()

    val cameraSelector: CameraSelector
        get() = if (_uiState.value.isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

    fun onQrCodeScanned(content: String) {
        if (_uiState.value.isScanning && content != _uiState.value.lastScannedCode) {
            _uiState.value = _uiState.value.copy(
                lastScannedCode = content,
                isScanning = false
            )

            // Lưu vào database
            viewModelScope.launch {
                try {
                    val qrType = detectQrCodeType(content)
                    val scan = QrCodeData(
                        content = content,
                        isScanned = true,
                        type = qrType
                    )
                    saveQrScanUseCase(scan)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Lỗi khi lưu: ${e.message}"
                    )
                }
            }
        }
    }

    fun resumeScanning() {
        _uiState.value = _uiState.value.copy(
            isScanning = true,
            lastScannedCode = null,
            errorMessage = null
        )
    }

    fun toggleFlash() {
        _uiState.value = _uiState.value.copy(
            isFlashOn = !_uiState.value.isFlashOn
        )
    }

    fun switchCamera() {
        _uiState.value = _uiState.value.copy(
            isFrontCamera = !_uiState.value.isFrontCamera
        )
    }

    private fun detectQrCodeType(content: String): QrCodeType {
        return when {
            content.startsWith("http://") || content.startsWith("https://") -> QrCodeType.URL
            content.startsWith("mailto:") -> QrCodeType.EMAIL
            content.startsWith("tel:") -> QrCodeType.PHONE
            content.startsWith("sms:") -> QrCodeType.SMS
            content.startsWith("WIFI:") -> QrCodeType.WIFI
            content.startsWith("geo:") -> QrCodeType.LOCATION
            content.startsWith("BEGIN:VCARD") -> QrCodeType.CONTACT
            else -> QrCodeType.TEXT
        }
    }
}
