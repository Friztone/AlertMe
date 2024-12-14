package com.example.alertme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun AppNavigation(navController: NavHostController) {
    val contentResolver = LocalContext.current.contentResolver

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("verification") { VerificationScreen(contentResolver, navController) }
        composable("home") { HomeScreen(navController) }
        composable("detail_pemadam") { DetailPemadam(navController) }
        composable("detail_polisi") { DetailPolisi(navController) }
        composable("detail_rs") { DetailRumahSakit(navController) }
        composable("detail_bpbd") { DetailBPBD(navController) }
        composable("guide") { GuideScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("change_name") { ChangeNameScreen(navController) }
        composable("change_password") { ChangePasswordScreen(navController) }
        composable("history") { HistoryScreen(navController) }

        composable("form/{uuid}") { backStackEntry ->
            val uuid = backStackEntry.arguments?.getString("uuid") ?: ""
            FormScreen(navController, uuid)
        }
        composable("detail_history/{uuid}") { backStackEntry ->
            val uuid = backStackEntry.arguments?.getString("uuid") ?: ""
            DetailHistoryScreen(
                navController = navController,
                uuid = uuid,

            )
        }
    }
}