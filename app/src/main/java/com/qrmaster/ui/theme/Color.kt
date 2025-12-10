package com.qrmaster.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palette for QR Master app
 * Dark theme with yellow/orange accents
 */
object QRMasterColors {
    // Primary colors
    val Primary = Color(0xFFFFC107) // Amber/Yellow
    val PrimaryDark = Color(0xFFFFA000) // Darker amber
    val PrimaryLight = Color(0xFFFFE082) // Light amber
    
    // Accent colors
    val Accent = Color(0xFFFF9800) // Orange
    val AccentDark = Color(0xFFF57C00) // Dark orange
    
    // Background colors
    val Background = Color(0xFF000000) // Pure black
    val Surface = Color(0xFF1E1E1E) // Dark gray
    val SurfaceVariant = Color(0xFF2C2C2C) // Lighter dark gray
    
    // Text colors
    val OnPrimary = Color(0xFF000000) // Black text on yellow
    val OnBackground = Color(0xFFFFFFFF) // White text
    val OnSurface = Color(0xFFFFFFFF) // White text
    val OnSurfaceVariant = Color(0xFFB0B0B0) // Light gray text
    
    // Error colors
    val Error = Color(0xFFCF6679)
    val OnError = Color(0xFF000000)
    
    // Success colors
    val Success = Color(0xFF4CAF50)
    
    // Gradient colors
    val GradientStart = Color(0xFFFFC107) // Yellow
    val GradientEnd = Color(0xFFFF9800) // Orange
}

