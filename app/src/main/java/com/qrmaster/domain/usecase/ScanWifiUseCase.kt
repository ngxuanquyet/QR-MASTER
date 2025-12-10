package com.qrmaster.domain.usecase

import com.qrmaster.domain.model.WifiNetwork
import com.qrmaster.domain.repository.WifiRepository
import javax.inject.Inject

/**
 * Use case for scanning WiFi networks.
 * Encapsulates the business logic for invoking the repository.
 */
class ScanWifiUseCase @Inject constructor(
    private val wifiRepository: WifiRepository
) {
    /**
     * Executes the scan operation.
     * @return List of WifiNetwork.
     */
    suspend operator fun invoke(): List<WifiNetwork> {
        return wifiRepository.scanWifiNetworks()
    }
}