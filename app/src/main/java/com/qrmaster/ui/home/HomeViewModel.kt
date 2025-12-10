package com.qrmaster.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmaster.domain.model.ICheckProductInfo
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.usecase.DeleteQrCodeUseCase
import com.qrmaster.domain.usecase.DeleteQrCodesUseCase
import com.qrmaster.domain.usecase.GetRecentQrCodesUseCase
import com.qrmaster.domain.usecase.SaveQrCodeUseCase
import com.qrmaster.domain.usecase.ScanICheckBarcodeUseCase
import com.qrmaster.domain.usecase.ScanQrFromUriUseCase
import com.qrmaster.ui.settings.SettingsPreferences
import com.qrmaster.utils.FeedbackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentQrCodesUseCase: GetRecentQrCodesUseCase,
    private val deleteQrCodeUseCase: DeleteQrCodeUseCase,
    private val saveQrCodeUseCase: SaveQrCodeUseCase,
    private val deleteQrCodesUseCase: DeleteQrCodesUseCase,
    private val scanQrFromUriUseCase: ScanQrFromUriUseCase,
    private val feedbackManager: FeedbackManager,
    private val scanICheckBarcodeUseCase: ScanICheckBarcodeUseCase,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _recentQrCodes = MutableStateFlow<List<QrCodeData>>(emptyList())
    val recentQrCodes: StateFlow<List<QrCodeData>> = _recentQrCodes.asStateFlow()

    private val _scannedQrCodeResult = MutableStateFlow<String?>(null)
    val scannedQrCodeResult: StateFlow<String?> = _scannedQrCodeResult.asStateFlow()

    private val _navigateToShowQr = MutableStateFlow<Long?>(null)
    val navigateToShowQr: StateFlow<Long?> = _navigateToShowQr.asStateFlow()

    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private val _isAutoModeOn = MutableStateFlow(false)
    val isAutoModeOn: StateFlow<Boolean> = _isAutoModeOn.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _icheckInfo = MutableStateFlow<ICheckProductInfo?>(null)
    val icheckInfo = _icheckInfo.asStateFlow()

    private val _isLoadingICheck = MutableStateFlow(false)
    val isLoadingICheck = _isLoadingICheck.asStateFlow()
    private var lastScanned: String? = null


    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission = _hasCameraPermission.asStateFlow()

    data class ScanFeedback(val vibrate: Boolean, val beep: Boolean)

    private val _scanFeedback = MutableStateFlow<ScanFeedback?>(null)
    val scanFeedback = _scanFeedback.asStateFlow()

    init {
        _navigateToShowQr.value = null
        loadRecentQrCodes()
    }

    fun onPermissionResult(granted: Boolean) {
        _hasCameraPermission.value = granted
    }

    fun loadRecentQrCodes(isScanned: Boolean? = null) {
        viewModelScope.launch {
            getRecentQrCodesUseCase(limit = 10, isScanned = isScanned).collect { qrCodes ->
                _recentQrCodes.value = qrCodes
            }
        }
    }


    fun onQrCodeScannedRealtime(content: String, qrType: QrCodeType) {
        if (lastScanned == content) return
        lastScanned = content

        _isScanning.value = false
        _scannedQrCodeResult.value = content

        viewModelScope.launch {
            val vibrate = settingsPreferences.vibrateEnabled.first()
            val beep = settingsPreferences.beepEnabled.first()

            val scan = QrCodeData(content = content, isScanned = true, type = qrType)

            val saveId = saveQrCodeUseCase(scan)

            if (content.matches(Regex("^\\d{8,14}$")) && _isAutoModeOn.value) {
                _isLoadingICheck.value = true
                _icheckInfo.value = null

                scanICheckBarcodeUseCase(content)
                    .onSuccess { info ->
                        _icheckInfo.value = info
                    }
                    .onFailure {
                        // log
                        _isFlashOn.value = false
                        _isScanning.value = true
                        _scannedQrCodeResult.value = null
                        lastScanned = null
                    }

                _isLoadingICheck.value = false
            } else {
                _navigateToShowQr.value = saveId
                _scannedQrCodeResult.value = null
            }

            feedbackManager.playScanSuccess(vibrate = vibrate, beep = beep)
        }
    }

    fun toggleFlash() {
        if (_isFrontCamera.value) {
            if (_isFlashOn.value) {
                _isFlashOn.value = false
            }
            return
        }
        _isFlashOn.value = !_isFlashOn.value
    }

    fun toggleAutoMode() {
        _isAutoModeOn.value = !_isAutoModeOn.value
    }

    fun toggleCamera() {
        if (!isFrontCamera.value && _isFlashOn.value) {
            _isFlashOn.value = false
        }
        _isFrontCamera.value = !_isFrontCamera.value
    }

    fun onImagePickedFromGallery(uri: Uri) {
//        viewModelScope.launch {
//            val content = scanQrFromUriUseCase(uri)
//            content?.let {
//                onQrCodeScannedRealtime(content)
//            } ?: run {
//                _errorMessage.value = "Lỗi khi quét mã QR từ ảnh"
//            }
//        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearScanFeedback() {
        _scanFeedback.value = null
    }

    fun clearICheckInfo() {
        _isFlashOn.value = false
        _icheckInfo.value = null
        _isScanning.value = true  // Reset để tiếp tục quét
        _scannedQrCodeResult.value = null  // Clear nội dung cũ
        lastScanned = null  // Reset để tránh skip QR trùng lặp
    }
}
