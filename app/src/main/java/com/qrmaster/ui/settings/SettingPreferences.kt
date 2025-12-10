package com.qrmaster.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsPreferences(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_VIBRATE = booleanPreferencesKey("vibrate_enabled")
        private val KEY_BEEP = booleanPreferencesKey("beep_enabled")
    }

    val vibrateEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_VIBRATE] ?: true }
    val beepEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_BEEP] ?: true }

    suspend fun setVibrate(enabled: Boolean) {
        dataStore.edit { it[KEY_VIBRATE] = enabled }
    }

    suspend fun setBeep(enabled: Boolean) {
        dataStore.edit { it[KEY_BEEP] = enabled }
    }
}