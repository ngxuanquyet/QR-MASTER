package com.qrmaster.ui.productInfor

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.qrmaster.R
import com.qrmaster.ui.theme.QRMasterColors

/**
 * Barcode screen displaying product details from iCheck API response.
 * Fetches full info by productId using ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScreen(
    content: String,
    onNavigateBack: () -> Unit,
    viewModel: BarcodeViewModel = hiltViewModel()
) {
    val productInfo by viewModel.productInfo.collectAsState()
    val scrollState = rememberScrollState()

    // Load data when screen mounts
    LaunchedEffect(content) {
        if (content.isNotBlank() && content != "0") {
            viewModel.loadProduct(content)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QRMasterColors.Surface)
            )
        }
    ) { paddingValues: PaddingValues ->
        when {
            productInfo == null -> {  // Loading or error
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(QRMasterColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = QRMasterColors.Accent)
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(QRMasterColors.Background)
                        .verticalScroll(scrollState)
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Product Image
                    val mediaList = productInfo!!.media.filter { it.isNotBlank() }
                        .map { it.replace("http://", "https://") }

                    if (mediaList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(QRMasterColors.Surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_qr),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = QRMasterColors.OnSurfaceVariant
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            val pagerState = rememberPagerState(pageCount = { mediaList.size })

                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(mediaList[page])
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Product image ${page + 1}",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Fit,
                                    placeholder = painterResource(R.drawable.ic_qr),
                                    error = painterResource(R.drawable.ic_qr)
                                )
                            }

                            if (mediaList.size > 1) {
                                Row(
                                    Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(mediaList.size) { index ->
                                        val color = if (pagerState.currentPage == index) {
                                            QRMasterColors.Accent
                                        } else {
                                            QRMasterColors.OnSurfaceVariant.copy(alpha = 0.4f)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .size(if (pagerState.currentPage == index) 9.dp else 7.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rating = productInfo!!.rating.coerceIn(0.0, 5.0)
                            val fullStars = rating.toInt()
                            val hasHalfStar = rating - fullStars >= 0.5f
                            val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0
                            repeat(fullStars) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = QRMasterColors.Accent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (hasHalfStar) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.StarHalf,
                                    contentDescription = null,
                                    tint = QRMasterColors.Accent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            repeat(emptyStars) {
                                Icon(
                                    imageVector = Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = QRMasterColors.OnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "${productInfo!!.rating}/5 (${productInfo!!.reviewCount} đánh giá)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = QRMasterColors.OnSurfaceVariant
                        )
                    }

                    // Product Name
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = productInfo!!.name ?: "Không có thông tin",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = QRMasterColors.OnSurface
                        )
                    }

                    // Country
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Xuất xứ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = QRMasterColors.OnSurface
                            )
                            Text(
                                text = productInfo!!.country ?: "Không có thông tin",
                                style = MaterialTheme.typography.bodyLarge,
                                color = QRMasterColors.OnSurfaceVariant
                            )
                        }
                    }

                    // Company Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Doanh nghiệp sở hữu",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = QRMasterColors.OnSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = productInfo!!.ownerName ?: "Không có thông tin",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = QRMasterColors.OnSurface,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = productInfo!!.ownerAddress ?: "Không có thông tin",
                                style = MaterialTheme.typography.bodyMedium,
                                color = QRMasterColors.OnSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Short Content
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Tóm tắt",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = QRMasterColors.OnSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = productInfo!!.shortContent
                                    ?: "Không có thông tin tóm tắt.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = QRMasterColors.OnSurfaceVariant
                            )
                        }
                    }

                    // Full Content
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = QRMasterColors.Surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Chi tiết sản phẩm",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = QRMasterColors.OnSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = productInfo!!.content ?: "Không có thông tin chi tiết.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = QRMasterColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}