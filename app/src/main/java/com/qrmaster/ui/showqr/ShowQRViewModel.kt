package com.qrmaster.ui.showqr

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.usecase.GetQrCodeByIdUseCase
import com.qrmaster.domain.usecase.SaveQrToGalleryUseCase
import com.qrmaster.domain.usecase.ShareQrCodeUseCase
import com.qrmaster.utils.QrCodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class ShowQRViewModel @Inject constructor(
    private val application: Application,
    private val getQrCodeByIdUseCase: GetQrCodeByIdUseCase,
    private val saveQrToGalleryUseCase: SaveQrToGalleryUseCase,
    private val shareQrCodeUseCase: ShareQrCodeUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShowQRUiState())
    val uiState: StateFlow<ShowQRUiState> = _uiState.asStateFlow()

    private var currentFgColor: String = "#000000"
    private var currentBgColor: String = "#FFFFFF"

    fun loadQrCode(id: Long) = viewModelScope.launch {
        val qrCode = getQrCodeByIdUseCase(id) ?: return@launch
        updateQrCode(qrCode)
    }

    fun onForegroundColorChanged(hex: String) {
        currentFgColor = hex
        regenerateWithCurrentColors()
    }

    fun onBackgroundColorChanged(hex: String) {
        currentBgColor = hex
        regenerateWithCurrentColors()
    }

    fun copyContentToClipboard() {
        val content = _uiState.value.qrCode?.content ?: return
        val clipboard = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("QR Code content", content))
        _uiState.value = _uiState.value.copy(isCopied = true)
    }

    fun shareQrCode(bitmap: Bitmap) = viewModelScope.launch {
        shareQrCodeUseCase(bitmap)
    }

    fun saveQrCode(bitmap: Bitmap) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isSaved = false)
        saveQrToGalleryUseCase(bitmap)
            .onSuccess { _uiState.value = _uiState.value.copy(isSaved = true) }
            .onFailure { _uiState.value = _uiState.value.copy(errorMessage = it.message) }
    }

    fun webSearch() {
        val content = _uiState.value.qrCode?.content ?: return
        val searchUrl = "https://www.google.com/search?q=${Uri.encode(content)}"
        val intent = Intent(Intent.ACTION_VIEW, searchUrl.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }

    private fun updateQrCode(qrCode: QrCodeData) {
        currentFgColor = qrCode.colorForeground
        currentBgColor = qrCode.colorBackground
        _uiState.value = _uiState.value.copy(qrCode = qrCode)
        regenerateQr(qrCode)
    }

    private fun regenerateWithCurrentColors() {
        val qrCode = _uiState.value.qrCode ?: return
        val updated =
            qrCode.copy(colorForeground = currentFgColor, colorBackground = currentBgColor)
        regenerateQr(updated)
    }

    private fun regenerateQr(qrCode: QrCodeData) = viewModelScope.launch(Dispatchers.Default) {
        QrCodeGenerator.generateQrCode(qrCode)
            .onSuccess { _uiState.value = _uiState.value.copy(qrBitmap = it, errorMessage = null) }
            .onFailure { _uiState.value = _uiState.value.copy(errorMessage = it.message) }
    }
}