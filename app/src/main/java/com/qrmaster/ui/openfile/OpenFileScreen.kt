package com.qrmaster.ui.openfile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmaster.R
import com.qrmaster.ui.components.ActionButtons
import com.qrmaster.ui.theme.QRMasterColors
import com.qrmaster.ui.theme.QRMasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenFileScreen(
    onNavigateBack: () -> Unit
) {
    QRMasterTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("QR Code") },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QrResultCard(
                    "dataaa",
                    "https://example.com/",
                    "16 Dec 2022, 9:30 pm",
                    {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(com.qrmaster.R.drawable.ic_qr),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .border(
                            width = 2.dp,
                            color = QRMasterColors.Accent,
                            shape = RoundedCornerShape(16.dp),
                        )
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActionButtons(
                    onShareClick = { },
                    onSaveClick = { }
                )
            }
        }
    }
}


@Composable
fun QrResultCard(
    data: String,
    url: String,
    timestamp: String,
    onShowQrClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_qr),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = data,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = timestamp,
                        color = Color(0xFFB3B3B3),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color(0x33FFFFFF),
                thickness = 1.dp
            )

            // URL content
            Text(
                text = url,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show QR Code button
            TextButton(
                onClick = onShowQrClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = QRMasterColors.Accent
                )
            ) {
                Text(
                    text = "Show QR Code",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
fun OpenFileScreenPreview() {
    OpenFileScreen(onNavigateBack = {})
}