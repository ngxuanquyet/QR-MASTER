package com.qrmaster.ui.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmaster.domain.model.WifiNetwork
import com.qrmaster.domain.usecase.ScanWifiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling WiFi scanning in the presentation layer.
 * Uses StateFlow for UI state and processes events.
 */
@HiltViewModel
class WifiViewModel @Inject constructor(
    private val scanWifiUseCase: ScanWifiUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        onEvent(UiEvent.ScanWifi)
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ScanWifi, is UiEvent.Retry -> {
                viewModelScope.launch {
                    _uiState.value = UiState.Loading
                    try {
                        val networks = scanWifiUseCase()
                        _uiState.value = UiState.Success(networks)
                    } catch (e: Exception) {
                        _uiState.value = UiState.Error(e.message ?: "Unknown error")
                    }
                }
            }

            is UiEvent.SelectWifi -> {
                // Handle selection if needed, but typically handled in UI
            }
        }
    }
}

/**
 * Sealed class for UI states.
 */
sealed class UiState {
    data object Loading : UiState()
    data class Success(val data: List<WifiNetwork>) : UiState()
    data class Error(val message: String) : UiState()
}

/**
 * Sealed class for UI events.
 */
sealed class UiEvent {
    data object ScanWifi : UiEvent()
    data object Retry : UiEvent()
    data class SelectWifi(val ssid: String) : UiEvent()
}