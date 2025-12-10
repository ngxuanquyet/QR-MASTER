package com.qrmaster.domain.repository

import com.qrmaster.domain.model.WifiNetwork

interface WifiRepository {
    /**
     * Scans for available WiFi networks and returns a list of WifiNetwork.
     * Handles permissions and states internally.
     * @return List of WifiNetwork or throws exception on error.
     */
    suspend fun scanWifiNetworks(): List<WifiNetwork>
}