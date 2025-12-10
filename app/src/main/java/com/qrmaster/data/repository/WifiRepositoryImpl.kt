package com.qrmaster.data.repository

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.qrmaster.domain.model.WifiNetwork
import com.qrmaster.domain.repository.WifiRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WifiRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wifiManager: WifiManager
) : WifiRepository {

    override suspend fun scanWifiNetworks(): List<WifiNetwork> =
        suspendCancellableCoroutine { cont ->
            checkPrerequisites()

            var receiver: BroadcastReceiver? = null

            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context?, intent: Intent?) {
                    receiver?.let { safeUnregister(it) }

                    try {
                        val results = wifiManager.scanResults ?: emptyList()
                        val currentSsid = getCurrentSsid()

                        val groupedNetworks = results
                            .filter { it.SSID.isNotBlank() }
                            .groupBy { it.SSID }
                            .mapNotNull { (_, scans) ->
                                scans.first()
                                val best = scans.maxByOrNull { it.level } ?: return@mapNotNull null

                                WifiNetwork(
                                    ssid = best.SSID,
                                    signalLevel = WifiManager.calculateSignalLevel(best.level, 5),
                                    securityType = getSecurityType(best.capabilities),
                                    frequencyBand = getFrequencyBand(best),
                                    isCurrentConnected = best.SSID == currentSsid
                                )
                            }
                            .sortedWith(
                                compareByDescending<WifiNetwork> { it.isCurrentConnected }
                                    .thenByDescending { it.signalLevel }
                            )

                        cont.resume(groupedNetworks)
                    } catch (e: Exception) {
                        cont.resumeWithException(e)
                    }
                }
            }

            receiver = broadcastReceiver

            // Đăng ký receiver
            val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            context.registerReceiver(broadcastReceiver, filter)

            // Bắt đầu scan
            val success = wifiManager.startScan()
            if (!success) {
                safeUnregister(broadcastReceiver)
                cont.resumeWithException(IllegalStateException("Start scan failed"))
                return@suspendCancellableCoroutine
            }

            // Hủy khi coroutine bị cancel
            cont.invokeOnCancellation {
                safeUnregister(broadcastReceiver)
            }
        }

    private fun checkPrerequisites() {
        if (!wifiManager.isWifiEnabled) {
            throw IllegalStateException("WiFi is disabled")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isLocationEnabled()) {
            throw IllegalStateException("Location is disabled")
        }

        val missing = mutableListOf<String>()
        val required = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )

        required.forEach { perm ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                missing.add(
                    when (perm) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> "Location"
                        else -> perm.substringAfterLast('.')
                    }
                )
            }
        }

        if (missing.isNotEmpty()) {
            throw IllegalStateException("Missing permissions: ${missing.joinToString()}")
        }
    }

    private fun safeUnregister(receiver: BroadcastReceiver?) {
        try {
            receiver?.let { context.unregisterReceiver(it) }
        } catch (_: IllegalArgumentException) { /* already unregistered */
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm =
            context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lm.isLocationEnabled
        } else {
            lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                    lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
        }
    }

    private fun getCurrentSsid(): String? {
        return wifiManager.connectionInfo?.ssid?.removeSurrounding("\"")
    }

    private fun getSecurityType(capabilities: String): String {
        return when {
            capabilities.contains("WPA3") || capabilities.contains("WPA2") || capabilities.contains(
                "WPA"
            ) -> "WPA"

            capabilities.contains("WEP") -> "WEP"
            capabilities.isEmpty() -> "OPEN"
            else -> "OTHER"
        }
    }

    private fun getFrequencyBand(scanResult: android.net.wifi.ScanResult): WifiNetwork.FrequencyBand {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                when (scanResult.frequency.let { freq ->
                    when {
                        freq in 2400..2499 -> WifiNetwork.FrequencyBand.BAND_2GHZ
                        freq in 5000..5999 -> WifiNetwork.FrequencyBand.BAND_5GHZ
                        freq >= 5925 -> WifiNetwork.FrequencyBand.BAND_6GHZ
                        else -> WifiNetwork.FrequencyBand.UNKNOWN
                    }
                }) {
                    else -> WifiNetwork.FrequencyBand.UNKNOWN
                }
            }

            else -> {
                // Android 10-11: dùng channel để đoán (không chính xác 100% nhưng ổn)
                val freq = scanResult.frequency
                when {
                    freq in 2400..2499 -> WifiNetwork.FrequencyBand.BAND_2GHZ
                    freq in 5000..5900 -> WifiNetwork.FrequencyBand.BAND_5GHZ
                    else -> WifiNetwork.FrequencyBand.UNKNOWN
                }
            }
        }
    }
}