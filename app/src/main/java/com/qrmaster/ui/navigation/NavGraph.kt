package com.qrmaster.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.qrmaster.domain.model.QrCodeType
import com.qrmaster.ui.generate.GenerateListScreen
import com.qrmaster.ui.generate.GenerateScreen
import com.qrmaster.ui.history.HistoryScreen
import com.qrmaster.ui.home.HomeScreen
import com.qrmaster.ui.onboarding.OnboardingScreen
import com.qrmaster.ui.productInfor.BarcodeScreen
import com.qrmaster.ui.scanner.ScannerScreen
import com.qrmaster.ui.settings.SettingsScreen
import com.qrmaster.ui.showqr.ShowQRScreen
import com.qrmaster.ui.splash.SplashScreen

/**
 * Navigation graph for the app
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToGenerate = {
                    navController.navigate(Screen.Generate.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToScanner = {
                    navController.navigate(Screen.Scanner.route)
                },
                onNavigateToShowQR = { qrCodeId ->
                    navController.navigate(Screen.ShowQR.createRoute(qrCodeId))
                },
                onNavigateToBarcode = { content ->
                    navController.navigate("barcode/$content")
                }
            )
        }

        composable(Screen.Generate.route) {
            GenerateListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGenerate = {},
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSetting = { navController.navigate(Screen.Settings.route) },

                onNavigateToGenerateText = { navController.navigate("generate/text") },
                onNavigateToGenerateWebsite = { navController.navigate("generate/url") },
                onNavigateToGenerateWifi = { navController.navigate("generate/wifi") },

                onNavigateToGenerateEvent = { navController.navigate("generate/event") },
                onNavigateToGenerateContact = { navController.navigate("generate/contact") },
                onNavigateToGenerateBusiness = { navController.navigate("generate/contact") },

                onNavigateToGenerateLocation = { navController.navigate("generate/location") },
                onNavigateToGenerateWhatsApp = { navController.navigate("generate/whatsapp") },
                onNavigateToGenerateEmail = { navController.navigate("generate/email") },

                onNavigateToGenerateTwitter = { navController.navigate("generate/twitter") },
                onNavigateToGenerateInstagram = { navController.navigate("generate/instagram") },
                onNavigateToGenerateTelephone = { navController.navigate("generate/phone") },
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToShowQR = { qrCodeId ->
                    navController.navigate(Screen.ShowQR.createRoute(qrCodeId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Scanner.route) {
            ScannerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ShowQR.route,
            arguments = listOf(
                navArgument("qrCodeId") { type = androidx.navigation.NavType.LongType }
            )
        ) { backStackEntry ->
            val qrCodeId = backStackEntry.arguments?.getLong("qrCodeId") ?: 0L
            ShowQRScreen(
                qrCodeId = qrCodeId,
                onNavigateBack = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(
            route = generateRoute,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments!!.getString("type")!!
            val qrType = enumValueOf<QrCodeType>(type.uppercase())

            GenerateScreen(
                type = qrType,
                onNavigateToShowQR = { qrCodeId ->
                    navController.navigate(Screen.ShowQR.createRoute(qrCodeId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Barcode.route,
            arguments = listOf(
                navArgument("content") { type = NavType.StringType }
            )) { backStackEntry ->
            val content = backStackEntry.arguments?.getString("content") ?: "0"
            BarcodeScreen(
                content = content,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

