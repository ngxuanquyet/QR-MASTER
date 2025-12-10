package com.qrmaster.ui.history

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qrmaster.R
import com.qrmaster.domain.model.QrCodeData
import com.qrmaster.ui.theme.QRMasterColors
import com.qrmaster.ui.theme.QRMasterTheme
import com.qrmaster.utils.format
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

/**
 * History screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToShowQR: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val recentQrCodes by viewModel.listQr.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Scan, 1 = Create
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedFilter by viewModel.currentFilter.collectAsState()
    val isFilter by viewModel.isFilter.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()

    LaunchedEffect(selectedTab, recentQrCodes) {
        val isScanned = when (selectedTab) {
            0 -> true   // Scan
            1 -> false  // Create
            else -> null
        }
        Log.d("fjkdslajf", isScanned.toString())
        viewModel.loadQrCodes(isScanned)
        if (!isFilter) {
            viewModel.loadQrCodes(isScanned)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    FilterDrawerContent(
                        currentFilter = selectedFilter,
                        onFilterSelected = { filter ->
                            viewModel.setFilter(filter)
                            scope.launch { drawerState.close() }
                        },
                        onClose = { scope.launch { drawerState.close() } }
                    )
                }
            },
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    QRMasterTheme {
                        Scaffold(
                            topBar = {
                                if (isSelectionMode) {
                                    TopAppBar(
                                        title = { Text("${selectedIds.size} đã chọn") },
                                        navigationIcon = {
                                            IconButton(onClick = viewModel::clearSelection) {
                                                Icon(Icons.Default.Close, "Hủy")
                                            }
                                        },
                                        actions = {
                                            IconButton(onClick = {
                                                viewModel.toggleSelectAll(
                                                    recentQrCodes
                                                )
                                            }) {
                                                Icon(
                                                    if (selectedIds.size == recentQrCodes.size) Icons.Default.CheckBox
                                                    else Icons.Default.CheckBoxOutlineBlank,
                                                    "Chọn tất cả"
                                                )
                                            }
                                            IconButton(onClick = viewModel::deleteSelected) {
                                                Icon(Icons.Default.Delete, "Xóa", tint = Color.Red)
                                            }
                                        },

                                        )
                                } else {
                                    TopAppBar(
                                        title = { Text("History") },
                                        navigationIcon = {
                                            IconButton(onClick = onNavigateBack) {
                                                Icon(Icons.Default.ArrowBack, "Back")
                                            }
                                        },
                                        colors = TopAppBarDefaults.topAppBarColors(containerColor = QRMasterColors.Surface),
                                        actions = {
                                            IconButton(onClick = {
                                                scope.launch { drawerState.open() }
                                            }) {
                                                Icon(
                                                    Icons.Default.FilterList,
                                                    contentDescription = "Filter",
                                                    tint = colorResource(R.color.color_icon),
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .padding(end = 16.dp)
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        ) { padding ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(padding)
                                    .padding(24.dp)
                                    .background(QRMasterColors.Background)
                            ) {
                                SegmentedControl(
                                    items = listOf("Scan", "Create"),
                                    selectedIndex = selectedTab,
                                    onItemSelection = { selectedTab = it }
                                )

                                Spacer(Modifier.height(16.dp))

                                if (recentQrCodes.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No QR codes yet.",
                                            color = QRMasterColors.OnSurfaceVariant,
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        items(recentQrCodes) { qrCode ->
                                            QrHistoryItem(
                                                item = qrCode,
                                                isSelected = selectedIds.contains(qrCode.id),
                                                isSelectionMode = isSelectionMode,
                                                onClick = {
                                                    Log.d("HistoryScreen", "onClick")
                                                    if (isSelectionMode) {
                                                        viewModel.toggleSelection(qrCode.id)
                                                    } else {
                                                        onNavigateToShowQR(qrCode.id)
                                                    }
                                                },
                                                onLongClick = {
                                                    Log.d("HistoryScreen", "onClick")
                                                    viewModel.toggleSelection(qrCode.id)
                                                },
                                                onFavorite = { viewModel.toggleFavorite(qrCode.id) },
                                                isFavorite = qrCode.isFavorite
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun FilterDrawerContent(
    currentFilter: HistoryViewModel.FilterType,
    onFilterSelected: (HistoryViewModel.FilterType) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(QRMasterColors.Surface.copy(alpha = 0.98f))
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        FilterDrawerItem(
            text = "Tất cả mã QR",
            icon = Icons.AutoMirrored.Filled.List,
            isSelected = currentFilter == HistoryViewModel.FilterType.ALL,
            onClick = { onFilterSelected(HistoryViewModel.FilterType.ALL) }
        )
        FilterDrawerItem(
            text = "Favorite",
            icon = Icons.Default.Favorite,
            isSelected = currentFilter == HistoryViewModel.FilterType.FAVORITES,
            onClick = { onFilterSelected(HistoryViewModel.FilterType.FAVORITES) }
        )

        FilterDrawerItem(
            text = "Text",
            icon = Icons.Default.TextFields,
            isSelected = currentFilter == HistoryViewModel.FilterType.TEXT,
            onClick = { onFilterSelected(HistoryViewModel.FilterType.TEXT) }
        )

        FilterDrawerItem(
            text = "URL",
            icon = Icons.Default.Link,
            isSelected = currentFilter == HistoryViewModel.FilterType.URL,
            onClick = { onFilterSelected(HistoryViewModel.FilterType.URL) }
        )
    }
}


@Composable
private fun FilterDrawerItem(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    iconTint: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) QRMasterColors.Accent else Color.Transparent)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.White,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelection: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(24),
        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(QRMasterColors.Surface),
            horizontalArrangement = Arrangement.Center
        ) {
            items.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = { onItemSelection(index) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedIndex == index)
                            QRMasterColors.Accent
                        else
                            QRMasterColors.Surface,
                        contentColor = if (selectedIndex == index)
                            QRMasterColors.OnPrimary
                        else
                            QRMasterColors.OnSurfaceVariant
                    ),
                    shape = when (index) {
                        0 -> RoundedCornerShape(24)
                        items.size - 1 -> RoundedCornerShape(24)
                        else -> RoundedCornerShape(0)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (selectedIndex == index)
                                    FontWeight.SemiBold
                                else
                                    FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun QrHistoryItem(
    item: QrCodeData,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavorite: () -> Unit,
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_qr),
                    contentDescription = null
                )
                Spacer(Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.content,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = modifier.weight(1f)
                    )

                    IconButton(onClick = onFavorite) {
                        Icon(
                            imageVector = if (!isFavorite) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                            contentDescription = null
                        )
                    }

//                    IconButton(onClick = { showDeleteConfirm = true }) {
//                        Image(
//                            painter = painterResource(R.drawable.ic_delete),
//                            contentDescription = "Delete",
//                        )
//                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB3B3B3),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = item.createdAt.format(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}