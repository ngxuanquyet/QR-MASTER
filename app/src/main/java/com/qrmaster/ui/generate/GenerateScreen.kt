package com.qrmaster.ui.generate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qrmaster.R
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.ui.components.WifiDialog
import com.qrmaster.ui.theme.QRMasterColors
import com.qrmaster.ui.theme.QRMasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    type: QrCodeType,
    onNavigateBack: () -> Unit,
    onNavigateToShowQR: (Long) -> Unit,
    generateViewModel: GenerateViewModel = hiltViewModel(),
) {
    val uiState by generateViewModel.uiState.collectAsState()
    val currentConfig = typeConfigs.find { it.type == type }!!

    LaunchedEffect(uiState.idQr) {
        uiState.idQr?.let { id ->
            onNavigateToShowQR(id)
        }
    }

    LaunchedEffect(Unit) {
        generateViewModel.setType(type)
    }

    QRMasterTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentConfig.title) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = QRMasterColors.Surface
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            2.dp,
                            QRMasterColors.Accent,
                            RoundedCornerShape(16.dp)
                        )
                        .background(QRMasterColors.Background)
                        .padding(24.dp)
                ) {
                    LazyColumn {
                        item {

                            Image(
                                painter = painterResource(currentConfig.iconRes),
                                contentDescription = null,
                                modifier = Modifier
//                            .offset(y = (-40).dp)
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            if (uiState.errorMessage != null)
                                Text(
                                    text = uiState.errorMessage!!,
                                    color = QRMasterColors.Error,
                                )
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                when (type) {
                                    QrCodeType.TEXT -> TextInput(uiState, generateViewModel)
                                    QrCodeType.URL -> UrlInput(uiState, generateViewModel)
                                    QrCodeType.WIFI -> WifiInput(uiState, generateViewModel)

                                    QrCodeType.EVENT -> EventInput(uiState, generateViewModel)
                                    QrCodeType.CONTACT -> PhoneInput(uiState, generateViewModel)

                                    QrCodeType.LOCATION -> LocationInput(uiState, generateViewModel)

                                    QrCodeType.WHATSAPP -> WhatsAppInput(uiState, generateViewModel)
                                    QrCodeType.EMAIL -> EmailInput(uiState, generateViewModel)

                                    QrCodeType.TWITTER -> TwitterInput(uiState, generateViewModel)
                                    QrCodeType.INSTAGRAM -> InstagramInput(
                                        uiState,
                                        generateViewModel
                                    )

                                    QrCodeType.PHONE -> PhoneInput(uiState, generateViewModel)
                                    else -> {}
                                }
                            }
                            Spacer(modifier = Modifier.height(48.dp))
                            GenerateButton(
                                enabled = uiState.canGenerate && !uiState.isGenerating,
                                onClick = generateViewModel::generateQrCode,
                            )
                        }
                    }


                }
            }
        }
    }
}

@Composable
private fun TextInput(
    state: GenerateUIState,
    vm: GenerateViewModel
) {
    GenerateInput(
        label = "Text",
        value = state.fields["text"] ?: "",
        onValueChange = { vm.updateField("text", it) },
        placeholder = "Enter any text"
    )
}

@Composable
private fun WhatsAppInput(
    state: GenerateUIState,
    vm: GenerateViewModel
) {
    GenerateInput(
        label = "WhatsApp Number",
        placeholder = "Enter number",
        value = state.fields["whatsapp"] ?: "",
        onValueChange = { vm.updateField("whatsapp", it) },
        keyboardType = KeyboardType.Phone
    )
}

@Composable
private fun TwitterInput(
    state: GenerateUIState,
    vm: GenerateViewModel
) {
    GenerateInput(
        label = "Username",
        placeholder = "Enter twitter username",
        value = state.fields["twitter"] ?: "",
        onValueChange = { vm.updateField("twitter", it) },
    )
}

@Composable
private fun LocationInput(
    state: GenerateUIState,
    vm: GenerateViewModel,
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GenerateInput(
            label = "Latitude",
            placeholder = "Enter latitude",
            value = state.fields["latitude"] ?: "",
            onValueChange = { vm.updateField("latitude", it) },
            keyboardType = KeyboardType.Number
        )
        GenerateInput(
            label = "Longitude",
            placeholder = "Enter longitude",
            value = state.fields["longitude"] ?: "",
            onValueChange = { vm.updateField("longitude", it) },
            keyboardType = KeyboardType.Number
        )
        GenerateInput(
            label = "Address",
            placeholder = "Enter location",
            value = state.fields["location"] ?: "",
            onValueChange = { vm.updateField("location", it) },
            isSingleLine = false
        )
        GenerateButton(
            enabled = state.canGenerate && !state.isGenerating && !state.isLocationLoading,
            onClick = {
                vm.fetchLocation(context)
            },
            text = "Get Location"
        )
    }
}

@Composable
private fun InstagramInput(
    state: GenerateUIState,
    vm: GenerateViewModel
) {
    GenerateInput(
        label = "Username",
        placeholder = "Enter Instagram username",
        value = state.fields["instagram"] ?: "",
        onValueChange = { vm.updateField("instagram", it) },
    )
}

@Composable
private fun EmailInput(
    state: GenerateUIState,
    vm: GenerateViewModel
) {
    GenerateInput(
        label = "Email",
        placeholder = "Enter email address",
        value = state.fields["email"] ?: "",
        onValueChange = { vm.updateField("email", it) },
        keyboardType = KeyboardType.Email
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WifiInput(
    state: GenerateUIState,
    generateViewModel: GenerateViewModel,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        WifiDialog(onDismiss = { showDialog = false }, onSelect = { ssid ->
            generateViewModel.updateField("ssid", ssid)
            generateViewModel.updateField("hidden", "false")
        })
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GenerateInput(
            label = "Network",
            placeholder = "Enter network name",
            value = state.fields["ssid"] ?: "",
            onValueChange = { generateViewModel.updateField("ssid", it) },
        )

        Button(onClick = { showDialog = true }) {
            Text("Select WiFi")
        }

        // Security Type Dropdown
        var expanded by remember { mutableStateOf(false) }
        val securityOptions = listOf("None", "WPA", "WEP")
        val currentSecurity = state.fields["security"] ?: "WPA"

        generateViewModel.updateField("security", currentSecurity)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            GenerateInput(
                label = "Security",
                placeholder = "Select security type",
                value = currentSecurity,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                securityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            generateViewModel.updateField("security", option)
                            expanded = false
                        }
                    )
                }
            }
        }

        GenerateInput(
            label = "Password",
            placeholder = "Enter password (leave empty if None)",
            value = state.fields["password"] ?: "",
            onValueChange = { generateViewModel.updateField("password", it) },
            keyboardType = KeyboardType.Password,
            isEnabled = currentSecurity != "None"
        )
    }
}

@Composable
private fun UrlInput(
    state: GenerateUIState,
    vm: GenerateViewModel
) {
    GenerateInput(
        label = "Website URL",
        placeholder = "www.qrcode.com",
        value = state.fields["url"] ?: "",
        onValueChange = { vm.updateField("url", it) },
        keyboardType = KeyboardType.Uri
    )
}

@Composable
private fun PhoneInput(
    state: GenerateUIState,
    vm: GenerateViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GenerateInput(
            label = "Name",
            placeholder = "Enter name",
            value = state.fields["name"] ?: "",
            onValueChange = { vm.updateField("name", it) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenerateInput(
                label = "Company",
                placeholder = "Enter company",
                value = state.fields["company"] ?: "",
                onValueChange = { vm.updateField("company", it) },
                modifier = Modifier.weight(1f)
            )
            GenerateInput(
                label = "Job",
                placeholder = "Enter job",
                value = state.fields["job"] ?: "",
                onValueChange = { vm.updateField("job", it) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenerateInput(
                label = "Phone",
                placeholder = "Enter phone",
                value = state.fields["phone"] ?: "",
                onValueChange = { vm.updateField("phone", it) },
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Phone
            )
            GenerateInput(
                label = "Email",
                placeholder = "Enter email",
                value = state.fields["email"] ?: "",
                onValueChange = { vm.updateField("email", it) },
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Email
            )
        }
        GenerateInput(
            label = "Website",
            placeholder = "Enter website",
            value = state.fields["website"] ?: "",
            onValueChange = { vm.updateField("website", it) },
            keyboardType = KeyboardType.Uri
        )
        GenerateInput(
            label = "Address",
            placeholder = "Enter address",
            value = state.fields["address"] ?: "",
            onValueChange = { vm.updateField("address", it) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenerateInput(
                label = "City",
                placeholder = "Enter city",
                value = state.fields["city"] ?: "",
                onValueChange = { vm.updateField("city", it) },
                modifier = Modifier.weight(1f)
            )
            GenerateInput(
                label = "Country",
                placeholder = "Enter country",
                value = state.fields["country"] ?: "",
                onValueChange = { vm.updateField("country", it) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun EventInput(
    state: GenerateUIState,
    vm: GenerateViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GenerateInput(
            label = "Event Name",
            placeholder = "Enter name",
            value = state.fields["title"] ?: "",
            onValueChange = { vm.updateField("title", it) },
        )
        GenerateInput(
            label = "Start Date and Time",
            placeholder = "12 Dec 2022, 10:40 pm",
            value = state.fields["start"] ?: "",
            onValueChange = { vm.updateField("start", it) },
        )
        GenerateInput(
            label = "End Date and Time",
            placeholder = "12 Dec 2022, 10:40 pm",
            value = state.fields["end"] ?: "",
            onValueChange = { vm.updateField("end", it) },
        )
        GenerateInput(
            label = "Event Location",
            placeholder = "location",
            value = state.fields["location"] ?: "",
            onValueChange = { vm.updateField("location", it) },
        )
        GenerateInput(
            label = "Description",
            placeholder = "Enter any details",
            value = state.fields["description"] ?: "",
            onValueChange = { vm.updateField("description", it) },
        )
    }

}


@Composable
fun GenerateInput(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSingleLine: Boolean = true,
    isEnabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge
        )
        OutlinedTextField(
            enabled = isEnabled,
            readOnly = readOnly,
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF888888),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            singleLine = isSingleLine,
            visualTransformation = if (keyboardType == KeyboardType.Password)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White,
                cursorColor = Color.White,
                focusedBorderColor = Color.White.copy(alpha = 0.8f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                disabledBorderColor = Color.White.copy(alpha = 0.5f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
        )
    }
}


@Composable
fun GenerateButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: String = "Generate QR Code",
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(text)
    }
}

private data class QrTypeConfig(
    val type: QrCodeType,
    val title: String,
    val iconRes: Int
)

private val typeConfigs = listOf(
    QrTypeConfig(QrCodeType.TEXT, "Text", R.drawable.icon_text),
    QrTypeConfig(QrCodeType.URL, "Website URL", R.drawable.ic_website),
    QrTypeConfig(QrCodeType.WIFI, "Wi-Fi", R.drawable.icon_wifi),
    QrTypeConfig(QrCodeType.EVENT, "Event", R.drawable.icon_event),
    QrTypeConfig(QrCodeType.CONTACT, "Contact", R.drawable.icon_contact),
    QrTypeConfig(QrCodeType.LOCATION, "Location", R.drawable.ic_location),
    QrTypeConfig(QrCodeType.PHONE, "Phone", R.drawable.ic_telephone),
    QrTypeConfig(QrCodeType.EMAIL, "Email", R.drawable.icon_email),
    QrTypeConfig(QrCodeType.WHATSAPP, "WhatsApp", R.drawable.ic_whatapp),
    QrTypeConfig(QrCodeType.TWITTER, "Twitter", R.drawable.ic_twitter),
    QrTypeConfig(QrCodeType.INSTAGRAM, "Instagram", R.drawable.icon_instagram),
)
