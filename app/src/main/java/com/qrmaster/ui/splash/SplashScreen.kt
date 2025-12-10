package com.qrmaster.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmaster.R
import com.qrmaster.ui.theme.QRMasterColors
import com.qrmaster.ui.theme.QRMasterTheme
import kotlinx.coroutines.delay


/**
 * Splash screen with animated logo reveal
 */
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var showLogo by remember { mutableStateOf(false) }
    var showAppName by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Animate logo reveal
        delay(500)
        showLogo = true
        delay(500)
        showAppName = true
        delay(1000)
        // TODO: Check if onboarding is completed
        // For now, navigate to onboarding
        onNavigateToOnboarding()
    }

    QRMasterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(QRMasterColors.Accent),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated QR logo
                AnimatedLogo(visible = showLogo)

                // App name
                if (showAppName) {
                    Text(
                        text = "QR Master",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = QRMasterColors.OnPrimary,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedLogo(visible: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "logo_alpha"
    )

    Box(
        modifier = Modifier
            .size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null,
        )
    }
}

