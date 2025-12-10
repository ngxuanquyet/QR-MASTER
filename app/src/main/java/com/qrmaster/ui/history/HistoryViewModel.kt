package com.qrmaster.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.usecase.DeleteQrCodeUseCase
import com.qrmaster.domain.usecase.DeleteQrCodesUseCase
import com.qrmaster.domain.usecase.GetAllQrCodesUseCase
import com.qrmaster.domain.usecase.GetQrCodeByType
import com.qrmaster.domain.usecase.GetQrCodesFavoriteUseCase
import com.qrmaster.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.remove

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val deleteQrCodeUseCase: DeleteQrCodeUseCase,
    private val getAllQrCodesUseCase: GetAllQrCodesUseCase,
    private val getFavoriteQrCodesUseCase: GetQrCodesFavoriteUseCase,
    private val getQrCodeByType: GetQrCodeByType,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteQrCodesUseCase: DeleteQrCodesUseCase
) : ViewModel() {
    private val _listQr = MutableStateFlow<List<QrCodeData>>(emptyList())
    val listQr: StateFlow<List<QrCodeData>> = _listQr.asStateFlow()
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _currentFilter = MutableStateFlow(FilterType.ALL)
    val currentFilter: StateFlow<FilterType> = _currentFilter.asStateFlow()

    private val _isFilter = MutableStateFlow(false)
    val isFilter: StateFlow<Boolean> = _isFilter.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    init {
        loadQrCodes()
    }

    fun loadQrCodes(isScanned: Boolean? = null) {
        viewModelScope.launch {
            val list = when (_currentFilter.value) {
                FilterType.ALL -> getAllQrCodesUseCase(isScanned)
                FilterType.FAVORITES -> getFavoriteQrCodesUseCase()
                FilterType.TEXT -> getQrCodeByType(QrCodeType.TEXT)
                FilterType.URL -> getQrCodeByType(QrCodeType.URL)
                else -> {
                    getAllQrCodesUseCase(isScanned)
                }
            }.first()
            _listQr.value = list.sortedByDescending { it.isFavorite }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
            loadQrCodes()
        }
    }

    fun deleteQrCode(qrCodeId: Long) {
        viewModelScope.launch {
            deleteQrCodeUseCase(qrCodeId)
        }
    }

    enum class FilterType { ALL, TEXT, URL, WIFI, FAVORITES }

    fun setFilter(filter: FilterType) {
        if (_currentFilter.value != filter) {
            _currentFilter.value = filter
            loadQrCodes()
        }
    }

    fun toggleSelection(id: Long) {
        _selectedIds.value = _selectedIds.value.toMutableSet().apply {
            if (contains(id)) remove(id) else add(id)
        }
        updateSelectionMode()
    }

    fun toggleSelectAll(currentList: List<QrCodeData>) {
        _selectedIds.value = if (_selectedIds.value.size == currentList.size) {
            emptySet()
        } else {
            currentList.map { it.id }.toSet()
        }
        updateSelectionMode()
    }

    private fun updateSelectionMode() {
        _isSelectionMode.value = _selectedIds.value.isNotEmpty()
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
        _isSelectionMode.value = false
    }

    fun deleteSelected() {
        viewModelScope.launch {
            deleteQrCodesUseCase(_selectedIds.value.toList())
            clearSelection()
            loadQrCodes()
        }
    }

}