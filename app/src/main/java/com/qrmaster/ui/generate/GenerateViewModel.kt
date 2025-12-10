package com.qrmaster.ui.generate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.usecase.GenerateQrCodeUseCase
import com.qrmaster.domain.usecase.GetCurrentLocationUseCase
import com.qrmaster.utils.hasLocationPermission
import com.qrmaster.utils.isInternetAvailable
import com.qrmaster.utils.isLocationEnabled
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val generateQrCodeUseCase: GenerateQrCodeUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerateUIState())
    val uiState = _uiState.asStateFlow()

    fun updateField(key: String, value: String) {
        _uiState.update { it.copy(fields = it.fields + (key to value)) }
    }

    fun setType(type: QrCodeType) {
        _uiState.update { it.copy(type = type) }
    }

    fun generateQrCode() {
        _uiState.update { it.copy(isGenerating = true) }
        val s = _uiState.value
        val request = QrCodeData(
            type = s.type,
            data = s.fields,
        )

        viewModelScope.launch {
            generateQrCodeUseCase(request)
                .onSuccess { idQr ->
                    _uiState.update {
                        it.copy(
                            idQr = idQr,
                            isGenerating = false
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            errorMessage = exception.message ?: "Generate failed"
                        )
                    }
                }
        }
    }

    fun fetchLocation(context: Context) {
        viewModelScope.launch {
            if (!isInternetAvailable(context)) {
                _uiState.update {
                    it.copy(errorMessage = "No internet connection")
                }
            }
            if (!hasLocationPermission(context)) {
                _uiState.update {
                    it.copy(errorMessage = "Location permission is required")
                }
            }
            if (!isLocationEnabled(context)) {
                _uiState.update {
                    it.copy(errorMessage = "Please enable location services")
                }
            }
            _uiState.update { it.copy(isLocationLoading = true) }
            getCurrentLocationUseCase().catch {
                _uiState.update {
                    Log.d("Location", it.toString())
                    it.copy(
                        isLocationLoading = false,
                        errorMessage = "Location failed"
                    )
                }
            }.collect { result ->
                Log.d("Location", result.toString())
                result.onSuccess { location ->
                    updateField("latitude", location.latitude.toString())
                    updateField("longitude", location.longitude.toString())
                    updateField("location", location.fullAddress)
                    _uiState.update {
                        it.copy(
                            currentLocation = location,
                            isLocationLoading = false
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            isLocationLoading = false,
                            errorMessage = "Location failed"
                        )
                    }
                }
            }
        }
    }


}
