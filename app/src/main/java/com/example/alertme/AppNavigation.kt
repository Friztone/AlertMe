package com.example.alertme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun AppNavigation(navController: NavHostController) {
    val ContentResolver = LocalContext.current.contentResolver

    NavHost(navController = navController, startDestination = "home") {
        composable("splash") { SplashScreen(navController) }
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("verification") { VerificationScreen(contentResolver = ContentResolver, navController) }
        composable("home") { HomeScreen(navController) }
        composable("detail_pemadam") { DetailPemadam(navController) }
        composable("detail_polisi") { DetailPolisi(navController) }
        composable("detail_rs") { DetailRumahSakit(navController) }
        composable("detail_bpbd") { DetailBPBD(navController) }



    }
}

