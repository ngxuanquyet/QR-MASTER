package com.qrmaster.ui.generate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmaster.R
import com.qrmaster.ui.home.BottomBar
import com.qrmaster.ui.theme.QRMasterColors
import com.qrmaster.ui.theme.QRMasterTheme

/**
 * Generate QR Code screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGenerate: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSetting: () -> Unit,

    onNavigateToGenerateText: () -> Unit,
    onNavigateToGenerateWebsite: () -> Unit,
    onNavigateToGenerateWifi: () -> Unit,

    onNavigateToGenerateEvent: () -> Unit,
    onNavigateToGenerateContact: () -> Unit,
    onNavigateToGenerateBusiness: () -> Unit,

    onNavigateToGenerateLocation: () -> Unit,
    onNavigateToGenerateWhatsApp: () -> Unit,
    onNavigateToGenerateEmail: () -> Unit,

    onNavigateToGenerateTwitter: () -> Unit,
    onNavigateToGenerateInstagram: () -> Unit,
    onNavigateToGenerateTelephone: () -> Unit,
) {
    val qrItems = remember {
        listOf(
            QRItem("Text", R.drawable.icon_text, onNavigateToGenerateText),
            QRItem("Website", R.drawable.icon_web, onNavigateToGenerateWebsite),
            QRItem("Wi-Fi", R.drawable.icon_wifi, onNavigateToGenerateWifi),

            QRItem("Event", R.drawable.icon_event, onNavigateToGenerateEvent),
            QRItem("Contact", R.drawable.icon_contact, onNavigateToGenerateContact),
            QRItem("Business", R.drawable.icon_business, onNavigateToGenerateBusiness),

            QRItem("Location", R.drawable.icon_location, onNavigateToGenerateLocation),
            QRItem("WhatApp", R.drawable.icon_whatapp, onNavigateToGenerateWhatsApp),
            QRItem("Email", R.drawable.icon_email, onNavigateToGenerateEmail),

            QRItem("Twitter", R.drawable.icon_twister, onNavigateToGenerateTwitter),
            QRItem("Instagram", R.drawable.icon_instagram, onNavigateToGenerateInstagram),
            QRItem("Telephone", R.drawable.icon_telephone, onNavigateToGenerateTelephone),
        )
    }

    QRMasterTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Generate QR Code") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = QRMasterColors.Surface
                    ),
                    actions = {
                        IconButton(
                            onClick = onNavigateToSetting,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(56.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_setting),
                                contentDescription = "Setting",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomBar(
                    isHome = false,
                    onNavigateToGenerate = onNavigateToGenerate,
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToHistory = onNavigateToHistory
                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(QRMasterColors.Background)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .background(QRMasterColors.Background),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                items(qrItems) { item ->
                    QRGenerateItem(
                        label = item.label,
                        iconRes = item.iconRes,
                        onClick = item.onClick
                    )
                }
            }
        }

    }
}

private data class QRItem(val label: String, val iconRes: Int, val onClick: () -> Unit)

// QRItem.kt
@Composable
fun QRGenerateItem(
    label: String,
    iconRes: Int,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(QRMasterColors.Background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)

        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = QRMasterColors.Accent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .offset(
                        x = 0.dp,
                        y = (-10).dp
                    )
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(color = QRMasterColors.Accent)
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
            ) {
                Text(
                    text = label,
                    color = QRMasterColors.OnPrimary,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 12.sp
                    ),
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
            }
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "Vector",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
@Preview
fun GenerateScreenPreview() {
    GenerateListScreen(
        onNavigateBack = {},
        onNavigateToGenerate = {},
        onNavigateToHome = {},
        onNavigateToHistory = {},
        onNavigateToSetting = {},
        onNavigateToGenerateText = {},
        onNavigateToGenerateWebsite = {},
        onNavigateToGenerateWifi = {},
        onNavigateToGenerateEvent = {},
        onNavigateToGenerateContact = {},
        onNavigateToGenerateBusiness = {},
        onNavigateToGenerateLocation = {},
        onNavigateToGenerateWhatsApp = {},
        onNavigateToGenerateEmail = {},
        onNavigateToGenerateTwitter = {},
        onNavigateToGenerateInstagram = {},
        onNavigateToGenerateTelephone = {},
    )
}
