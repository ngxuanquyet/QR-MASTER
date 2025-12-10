package com.qrmaster.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark color scheme for QR Master
 */
private val DarkColorScheme = darkColorScheme(
    primary = QRMasterColors.Primary,
    onPrimary = QRMasterColors.OnPrimary,
    primaryContainer = QRMasterColors.PrimaryDark,
    onPrimaryContainer = QRMasterColors.OnPrimary,
    secondary = QRMasterColors.Accent,
    onSecondary = QRMasterColors.OnPrimary,
    secondaryContainer = QRMasterColors.AccentDark,
    onSecondaryContainer = QRMasterColors.OnPrimary,
    tertiary = QRMasterColors.PrimaryLight,
    onTertiary = QRMasterColors.OnPrimary,
    error = QRMasterColors.Error,
    onError = QRMasterColors.OnError,
    background = QRMasterColors.Background,
    onBackground = QRMasterColors.OnBackground,
    surface = QRMasterColors.Surface,
    onSurface = QRMasterColors.OnSurface,
    surfaceVariant = QRMasterColors.SurfaceVariant,
    onSurfaceVariant = QRMasterColors.OnSurfaceVariant
)

@Composable
fun QRMasterTheme(
    darkTheme: Boolean = true, // Always dark theme
    dynamicColor: Boolean = false, // Disable dynamic color
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        else -> DarkColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = QRMasterTypography,
        content = content
    )
}

