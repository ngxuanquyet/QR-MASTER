package com.qrmaster.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmaster.utils.uploadTempImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun uploadPhoto(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true) }
            try {
                uploadTempImage(file)
                _uiState.update { it.copy(isSuccess = true, isUploading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isUploading = false) }
            } finally {
                file.delete()
            }
        }
    }

}

data class CameraUiState(
    val isUploading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)