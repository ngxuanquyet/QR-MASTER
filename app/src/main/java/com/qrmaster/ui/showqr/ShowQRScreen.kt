package com.qrmaster.ui.showqr

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.domain.model.displayName
import com.qrmaster.ui.components.ActionButtons
import com.qrmaster.ui.components.ColorPickerRow
import com.qrmaster.ui.components.InfoRow
import com.qrmaster.ui.theme.QRMasterColors
import com.qrmaster.ui.theme.QRMasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowQRScreen(
    qrCodeId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ShowQRViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(qrCodeId) { viewModel.loadQrCode(qrCodeId) }

    // Toast feedbacks
    LaunchedEffect(uiState.isCopied) {
        if (uiState.isCopied) Toast.makeText(
            context,
            "Đã copy",
            Toast.LENGTH_SHORT
        ).show()
    }
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) Toast.makeText(
            context,
            "Đã lưu vào thư viện",
            Toast.LENGTH_SHORT
        ).show()
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    QRMasterTheme {
        Scaffold(
            topBar = { ShowQRTopBar(onNavigateBack) },
            containerColor = QRMasterColors.Background
        ) { padding ->
            when {
                uiState.qrBitmap == null -> LoadingContent(Modifier.padding(padding))
                else -> SuccessContent(
                    uiState = uiState,
                    onForegroundChanged = viewModel::onForegroundColorChanged,
                    onBackgroundChanged = viewModel::onBackgroundColorChanged,
                    onCopy = viewModel::copyContentToClipboard,
                    onShare = viewModel::shareQrCode,
                    onSave = viewModel::saveQrCode,
                    onWebSearch = viewModel::webSearch,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowQRTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Xem QR Code") },
        navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = QRMasterColors.Surface)
    )
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SuccessContent(
    uiState: ShowQRUiState,
    onForegroundChanged: (String) -> Unit,
    onBackgroundChanged: (String) -> Unit,
    onCopy: () -> Unit,
    onShare: (Bitmap) -> Unit,
    onSave: (Bitmap) -> Unit,
    onWebSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DataCard(uiState.qrCode!!)
        Spacer(Modifier.height(32.dp))
        Image(
            bitmap = uiState.qrBitmap!!.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .size(300.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .border(3.dp, QRMasterColors.Accent, RoundedCornerShape(20.dp))
                .padding(16.dp)
        )
        Spacer(Modifier.height(32.dp))
        ActionButtons(
            onCopyClick = onCopy,
            onShareClick = { uiState.qrBitmap.let(onShare) },
            onSaveClick = { uiState.qrBitmap.let(onSave) },
            onWebSearchClick = onWebSearch
        )
        Spacer(Modifier.height(40.dp))
        Text("Tùy chỉnh màu sắc", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Spacer(Modifier.height(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColorPickerRow("Màu chữ", uiState.qrCode.colorForeground, onForegroundChanged)
            ColorPickerRow("Màu nền", uiState.qrCode.colorBackground, onBackgroundChanged)
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun DataCard(
    qrCodeData: QrCodeData,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = qrCodeData.title.takeIf { it.isNotBlank() }
                    ?: qrCodeData.type.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            val data = qrCodeData.data


            when (qrCodeData.type) {
                QrCodeType.TEXT -> InfoRow("Nội dung: ", data["text"].orEmpty())

                QrCodeType.URL -> InfoRow("Link: ", data["url"].orEmpty())
                QrCodeType.APP_STORE,
                QrCodeType.FACEBOOK,
                QrCodeType.TWITTER,
                QrCodeType.INSTAGRAM,
                QrCodeType.YOUTUBE,
                QrCodeType.WHATSAPP,
                QrCodeType.UNKNOWN -> InfoRow("Link", data["url"] ?: data["text"].orEmpty())

                QrCodeType.PHONE -> InfoRow("Số điện thoại", data["phone"].orEmpty())

                QrCodeType.SMS -> {
                    InfoRow("Gửi tới", data["phone"].orEmpty())
                    if (data["message"].orEmpty().isNotBlank()) {
                        InfoRow("Tin nhắn", data["message"].orEmpty())
                    }
                }

                QrCodeType.EMAIL -> {
                    InfoRow("Email", data["email"].orEmpty())
                    if (data["subject"].orEmpty().isNotBlank()) InfoRow(
                        "Chủ đề",
                        data["subject"].orEmpty()
                    )
                    if (data["body"].orEmpty().isNotBlank()) InfoRow(
                        "Nội dung",
                        data["body"].orEmpty()
                    )
                }

                QrCodeType.WIFI -> {
                    InfoRow("SSID", data["ssid"].orEmpty())
                    if (data["password"].orEmpty().isNotBlank()) InfoRow(
                        "Mật khẩu",
                        data["password"].orEmpty()
                    )
                    InfoRow("Bảo mật", data["security"].orEmpty().ifBlank { "Không mã hóa" })
                }

                QrCodeType.LOCATION -> {
                    InfoRow("Vĩ độ", data["latitude"].orEmpty())
                    InfoRow("Kinh độ", data["longitude"].orEmpty())
                }

                QrCodeType.CONTACT -> {
                    if (data["name"].orEmpty().isNotBlank()) InfoRow("Tên", data["name"].orEmpty())
                    (data["phone"] ?: data["TEL"])?.takeIf { it.isNotBlank() }
                        ?.let { InfoRow("Điện thoại", it) }
                    (data["email"] ?: data["EMAIL"])?.takeIf { it.isNotBlank() }
                        ?.let { InfoRow("Email", it) }
                    if (data["address"].orEmpty().isNotBlank()) InfoRow(
                        "Địa chỉ",
                        data["address"].orEmpty()
                    )
                }

                QrCodeType.EVENT -> {
                    if (data["title"].orEmpty().isNotBlank()) InfoRow(
                        "Sự kiện",
                        data["title"].orEmpty()
                    )
                    if (data["start"].orEmpty().isNotBlank()) InfoRow(
                        "Bắt đầu",
                        data["start"].orEmpty()
                    )
                    if (data["end"].orEmpty().isNotBlank()) InfoRow(
                        "Kết thúc",
                        data["end"].orEmpty()
                    )
                    if (data["location"].orEmpty().isNotBlank()) InfoRow(
                        "Địa điểm",
                        data["location"].orEmpty()
                    )
                }

                QrCodeType.CRYPTO_WALLET -> {
                    InfoRow("Tiền mã hóa", data["currency"].orEmpty())
                    InfoRow("Địa chỉ ví", data["address"].orEmpty(), isAddress = true)
                }

                QrCodeType.QR_CODE -> {
                    InfoRow("Nội dung: ", data["text"].orEmpty())
                }

                QrCodeType.AZTEC -> {
                    InfoRow("Nội dung: ", data["text"].orEmpty())
                }

                QrCodeType.DATA_MATRIX -> {
                    InfoRow("Nội dung: ", data["text"].orEmpty())
                }

                QrCodeType.PDF417 -> {
                    InfoRow("Nội dung: ", data["text"].orEmpty())
                }

                QrCodeType.CODE_128 -> {
                    InfoRow("Nội dung: ", data["text"].orEmpty())
                }

                QrCodeType.CODE_39 -> {
                    InfoRow("Nội dung: ", data["text"].orEmpty())
                }

                QrCodeType.CODE_93 -> {
                    InfoRow("Nội dung: ", data["text"].orEmpty())
                }

                QrCodeType.CODABAR -> {
                    InfoRow("Nội dung: ", data["text"].orEmpty())
                }

                QrCodeType.EAN_13 -> {
                    InfoRow("Mã số EAN 13: ", data["text"].orEmpty())
                }

                QrCodeType.EAN_8 -> {
                    InfoRow("Mã số EAN 8: ", data["text"].orEmpty())
                }

                QrCodeType.ITF -> {
                    InfoRow("Mã số ITF", data["text"].orEmpty())
                }

                QrCodeType.UPC_A -> {
                    InfoRow("Mã số UPC A", data["text"].orEmpty())
                }

                QrCodeType.UPC_E -> {
                    InfoRow("Mã số UPC E", data["text"].orEmpty())
                }

                QrCodeType.DRIVER_LICENSE -> {
                    InfoRow("Bằng lái xe:", data["text"].orEmpty())
                }

                QrCodeType.ISBN -> {
                    InfoRow("Mã ISBN: ", data["text"].orEmpty())
                }

                QrCodeType.PRODUCT -> {
                    InfoRow("Mã sản phẩm: ", data["text"].orEmpty())
                }
            }
        }
    }
}