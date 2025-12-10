package com.qrmaster.domain.model

// File: domain/model/WifiNetwork.kt
data class WifiNetwork(
    val ssid: String,
    val signalLevel: Int,
    val securityType: String,
    val frequencyBand: FrequencyBand,
    val isCurrentConnected: Boolean
) {
    enum class FrequencyBand {
        BAND_2GHZ, BAND_5GHZ, BAND_6GHZ, UNKNOWN;

        override fun toString(): String = when (this) {
            BAND_2GHZ -> "2.4 GHz"
            BAND_5GHZ -> "5 GHz"
            BAND_6GHZ -> "6 GHz"
            UNKNOWN -> "Unknown"
        }
    }
}