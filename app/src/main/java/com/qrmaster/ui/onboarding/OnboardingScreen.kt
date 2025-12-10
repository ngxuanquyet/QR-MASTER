package com.qrmaster.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmaster.R
import com.qrmaster.ui.theme.QRMasterColors
import com.qrmaster.ui.theme.QRMasterTheme

/**
 * Onboarding screen with swipeable carousel
 */
@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })

    QRMasterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(QRMasterColors.Accent)
        ) {
            Image(
                painter = painterResource(R.drawable.onboard),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)

            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                // Skip button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onNavigateToHome) {
                        Text(
                            text = "Skip",
                            color = QRMasterColors.OnPrimary
                        )
                    }
                }

                // Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    OnboardingPage(page = page)
                }

                // Page indicator and button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Page indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (pagerState.currentPage == index) {
                                            QRMasterColors.Primary
                                        } else {
                                            QRMasterColors.SurfaceVariant
                                        }
                                    )
                            )
                        }
                    }

                    // Get Started button (only on last page)
                    if (pagerState.currentPage == 4) {
                        Button(
                            onClick = onNavigateToHome,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = QRMasterColors.Primary,
                                contentColor = QRMasterColors.OnPrimary
                            )
                        ) {
                            Text("Get Started")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Int) {
    val titles = listOf(
        "Generate QR Codes",
        "Scan QR Codes",
        "Customize Design",
        "Save & Share",
        "Full History"
    )

    val descriptions = listOf(
        "Create QR codes for text, URLs, WiFi, contacts, and more",
        "Scan QR codes instantly with your camera",
        "Customize colors, patterns, and add your logo",
        "Save and share your QR codes easily",
        "Keep track of all your generated and scanned codes"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = titles[page],
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = QRMasterColors.OnPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = descriptions[page],
                fontSize = 16.sp,
                color = QRMasterColors.OnPrimary,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

