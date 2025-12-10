package com.qrmaster.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.qrmaster.R
import com.qrmaster.domain.model.ICheckProductInfo
import com.qrmaster.ui.theme.QRMasterColors
import com.qrmaster.utils.detectQrCodeType
import java.util.concurrent.Executors

/**
 * Home screen with app bar, recent QR codes, and FAB
 */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGenerate: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToShowQR: (Long) -> Unit,
    onNavigateToBarcode: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val navigateToShowQr by viewModel.navigateToShowQr.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val isFlashOn by viewModel.isFlashOn.collectAsState()
    val isAutoModeOn by viewModel.isAutoModeOn.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val isFrontCamera by viewModel.isFrontCamera.collectAsState()
    val currentCamera = remember { mutableStateOf<Camera?>(null) }
    val icheckInfo by viewModel.icheckInfo.collectAsState()
    val isLoadingICheck by viewModel.isLoadingICheck.collectAsState()
    val navigateToBarcodeContent by viewModel.navigateToBarcodeContent.collectAsState()

    // Permission states
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    var shouldShowRationale by remember { mutableStateOf(false) }

    val cameraSelector = remember(isFrontCamera) {
        if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA
        else CameraSelector.DEFAULT_BACK_CAMERA
    }

    // Image picker launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onImagePickedFromGallery(uri)
        }
    }
    LaunchedEffect(error) {
        error?.let { mess ->
            Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            // Check if we should show rationale (user denied without "Don't ask again")
            shouldShowRationale = androidx.activity.ComponentActivity::class.java
                .isInstance(context) &&
                    !(context as androidx.activity.ComponentActivity)
                        .shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        }
        viewModel.onPermissionResult(isGranted)
    }

    // Settings launcher
    val openSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Re-check permission when returning from settings
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            shouldShowRationale = false
            viewModel.onPermissionResult(true)
        }
    }

    // Observe lifecycle to re-check permission when resuming
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val currentPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (currentPermission != hasPermission) {
                    hasPermission = currentPermission
                    if (currentPermission) {
                        shouldShowRationale = false
                        viewModel.onPermissionResult(true)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Request permission on first launch
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Flash control
    LaunchedEffect(isFlashOn, isFrontCamera, currentCamera.value) {
        val camera = currentCamera.value ?: return@LaunchedEffect
        val shouldEnableTorch = isFlashOn && !isFrontCamera
        camera.cameraControl.enableTorch(shouldEnableTorch)
    }

    // Navigate to show QR
    LaunchedEffect(navigateToShowQr) {
        navigateToShowQr?.let { id ->
            onNavigateToShowQR(id)
        }
    }

    LaunchedEffect(navigateToBarcodeContent) {
        navigateToBarcodeContent?.let { content ->
            onNavigateToBarcode(content)
            viewModel.clearBarcodeNavigation()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Camera preview
        if (hasPermission) {
            CameraPreview(
                context = context,
                isScanning = isScanning,
                viewModel = viewModel,
                lifecycleOwner = lifecycleOwner,
                cameraSelector = cameraSelector,
                currentCamera = currentCamera
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                isFlashOn = isFlashOn,
                isFrontCamera = isFrontCamera,
                isAutoModeOn = isAutoModeOn,
                onToggleFlash = { viewModel.toggleFlash() },
                onToggleCamera = { viewModel.toggleCamera() },
                onOpenGallery = { pickImageLauncher.launch("image/*") },
                onChangeMode = { viewModel.toggleAutoMode() }
            )

            Spacer(Modifier.height(100.dp))

            if (hasPermission) {
                Image(
                    painterResource(R.drawable.corner_scan_qr),
                    contentDescription = null,
                    modifier = Modifier.size(320.dp)
                )
            } else {
                PermissionScreen(
                    shouldShowRationale = shouldShowRationale,
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    onOpenSettings = {
                        openSettingsLauncher.launch(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BottomBar(
                onNavigateToGenerate = onNavigateToGenerate,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToHome = {},
                isHome = true,
                isAutoModeOn = isAutoModeOn,
                onCaptured = {
                    Toast.makeText(context, "Chụp ảnh", Toast.LENGTH_SHORT).show()
                }
            )
//            ICheckResultDialog(
//                info = icheckInfo,
//                isLoading = isLoadingICheck,
//                onDismiss = { viewModel.clearICheckInfo() }
//            )
        }
    }
}

@Composable
private fun PermissionScreen(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Box(
        modifier = Modifier.background(Color.Black.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = QRMasterColors.Accent,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cần quyền camera để quét QR",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (shouldShowRationale)
                    "Bạn đã từ chối quyền camera. Vui lòng cấp quyền trong Cài đặt để sử dụng tính năng quét QR."
                else
                    "Ứng dụng cần truy cập camera để quét mã QR. Vui lòng cấp quyền để tiếp tục.",
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = if (shouldShowRationale) onOpenSettings else onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = QRMasterColors.Accent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text(
                    text = if (shouldShowRationale) "Mở Cài đặt" else "Cho phép Camera",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            if (!shouldShowRationale) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { /* User can close or ignore */ }) {
                    Text(
                        text = "Để sau",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreview(
    context: Context,
    isScanning: Boolean,
    viewModel: HomeViewModel,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    currentCamera: MutableState<Camera?>
) {
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val barcodeScanner = BarcodeScanning.getClient()
                val executor = Executors.newSingleThreadExecutor()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null && isScanning) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        barcodeScanner.process(image).addOnSuccessListener { barcodes ->
                            barcodes.firstOrNull()?.let { barcode ->
                                val type = detectQrCodeType(barcode)
                                val value = barcode.rawValue.orEmpty()

                                Log.d("ccc", "Scanned QR code type: ${barcode.valueType}")

                                viewModel.onQrCodeScannedRealtime(value, type)
                            }
//                            for (barcode in barcodes) {
//                                barcode.rawValue?.let { value ->
//                                    viewModel.onQrCodeScannedRealtime(value)
//                                }
//                            }
                        }.addOnCompleteListener {
                            imageProxy.close()
                        }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    currentCamera.value = camera
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}

@Composable
fun TopBar(
    isFlashOn: Boolean,
    isFrontCamera: Boolean,
    isAutoModeOn: Boolean = false,
    onToggleFlash: () -> Unit,
    onToggleCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onChangeMode: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .clip(RoundedCornerShape(5.dp))
            .background(color = colorResource(R.color.background))
            .padding(start = 32.dp, end = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onOpenGallery) {
            Icon(imageVector = Icons.Filled.Image, null, tint = Color.White)
        }
        IconButton(onClick = onToggleFlash) {
            Icon(
                imageVector = if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                null,
                tint = if (isFlashOn) Color.Yellow else Color.White
            )
        }
        IconButton(onClick = onToggleCamera) {
            Icon(
                Icons.Filled.Cameraswitch,
                contentDescription = "Đổi camera",
                tint = Color.White
            )
        }
        IconButton(onClick = onChangeMode) {
            Icon(
                if (isAutoModeOn) {
                    painterResource(R.drawable.icon_barcode)
                } else {
                    painterResource(R.drawable.ic_qr)
                },
                contentDescription = "Change mode",
                tint = Color.White
            )
        }
    }
}

@Composable
fun BottomBar(
    isHome: Boolean,
    isAutoModeOn: Boolean = false,
    onNavigateToGenerate: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onCaptured: () -> Unit = {}
) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp)),
        containerColor = Color(0xFF1E1E1E).copy(alpha = 0.96f),
        tonalElevation = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onNavigateToGenerate) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.QrCode2,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Generate", color = Color.White, fontSize = 13.sp)
                }
            }

            Image(
                painter = painterResource(R.drawable.scan_qr_home),
                contentDescription = "Quét",
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .clickable {
                        when {
                            !isHome -> onNavigateToHome()
                            !isAutoModeOn -> onCaptured()
                        }
                    }
            )

            TextButton(onClick = onNavigateToHistory) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.History,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("History", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ICheckResultDialog(
    info: ICheckProductInfo?,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    if (isLoading || info != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = if (isLoading) "Đang tra cứu iCheck..." else "Thông tin sản phẩm",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text("Đang tìm thông tin...")
                    }
                } else {
                    info?.let {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text("Tên sản phẩm:", fontWeight = FontWeight.Medium)
                            Text(
                                it.name ?: "Không có thông tin",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("Xuất xứ:", fontWeight = FontWeight.Medium)
                            Text(
                                it.origin ?: "Không có thông tin",
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Đóng")
                }
            }
        )
    }
}