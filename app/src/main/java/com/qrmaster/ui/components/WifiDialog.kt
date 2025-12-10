package com.qrmaster.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.qrmaster.ui.generate.UiEvent
import com.qrmaster.ui.generate.UiState
import com.qrmaster.ui.generate.WifiViewModel

/**
 * Composable for the WiFi selection dialog.
 * Displays loading, error, or list of networks.
 * Auto triggers scan on open.
 * @param onDismiss Called when dialog is dismissed.
 * @param onSelect Called with selected SSID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiDialog(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    viewModel: WifiViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    RequestWifiPermissions {}

    LaunchedEffect(Unit) {
        viewModel.onEvent(UiEvent.ScanWifi)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select WiFi Network") },
        text = {
            // Giới hạn chiều cao tối đa 70% màn hình
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.6f)
            ) {
                when (state) {
                    is UiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = (state as UiState.Error).message,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if ((state as UiState.Error).message.contains("WiFi") ||
                                (state as UiState.Error).message.contains("Location")
                            ) {
                                Text("Please enable WiFi and Location services.")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.onEvent(UiEvent.Retry) }) {
                                Text("Retry")
                            }
                        }
                    }

                    is UiState.Success -> {
                        val networks = (state as UiState.Success).data
                        LazyColumn {
                            items(networks) { network ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelect(network.ssid)
                                            viewModel.onEvent(UiEvent.SelectWifi(network.ssid))
                                            onDismiss()
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${network.signalLevel}/4",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = network.ssid,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (network.isCurrentConnected) Color.Green else Color.White
                                    )
                                    Text(
                                        text = network.securityType,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        properties = DialogProperties(usePlatformDefaultWidth = false), // Cho phép custom width/height
        modifier = Modifier
            .fillMaxWidth(0.9f) // Chiếm 90% chiều rộng màn hình
            .padding(16.dp)
    )
}

// Usage example in main screen:
// var showDialog by remember { mutableStateOf(false) }
// Button(onClick = { showDialog = true }) { Text("Select WiFi") }
// if (showDialog) {
//     WifiDialog(onDismiss = { showDialog = false }, onSelect = { ssid -> /* handle */ })
// }

@Composable
fun RequestWifiPermissions(
    onPermissionsGranted: () -> Unit
) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.all { it.value }) {
            onPermissionsGranted()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissions)
    }
}