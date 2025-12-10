package com.qrmaster.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {
    val isVibrate = settingsPreferences.vibrateEnabled.stateIn(
        viewModelScope, SharingStarted.Eagerly, initialValue = true
    )

    val isBeep = settingsPreferences.beepEnabled.stateIn(
        viewModelScope, SharingStarted.Eagerly, initialValue = true
    )

    fun toggleVibrate() = viewModelScope.launch {
        settingsPreferences.setVibrate(!isVibrate.value)
    }

    fun toggleBeep() = viewModelScope.launch {
        settingsPreferences.setBeep(!isBeep.value)
    }
}