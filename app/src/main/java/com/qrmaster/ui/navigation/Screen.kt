package com.qrmaster.ui.navigation

import com.qrmaster.domain.model.QrCodeType
import kotlinx.serialization.Serializable

/**
 * Sealed class representing all screens in the app
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Generate : Screen("generate")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Scanner : Screen("scanner")
    object ShowQR : Screen("show_qr/{qrCodeId}") {
        fun createRoute(qrCodeId: Long) = "show_qr/$qrCodeId"
    }
}

const val generateRoute = "generate/{type}"
