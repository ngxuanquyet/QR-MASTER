package com.qrmaster.ui.productInfor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmaster.domain.model.ICheckProductInfo
import com.qrmaster.domain.usecase.ScanICheckBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val scanICheckBarcodeUseCase: ScanICheckBarcodeUseCase
) : ViewModel() {

    private val _productInfo = MutableStateFlow<ICheckProductInfo?>(null)
    val productInfo: StateFlow<ICheckProductInfo?> = _productInfo.asStateFlow()

    fun loadProduct(content: String) {
        viewModelScope.launch {
            scanICheckBarcodeUseCase(content)
                .onSuccess { info ->
                    Log.d("mn", "loadProduct: $info")
                    _productInfo.value = info
                }
                .onFailure { exception ->
                     Log.d("BarcodeViewModel", "Failed to load product", exception)
                    _productInfo.value = null  // Trigger empty state in UI
                }
        }
    }
}