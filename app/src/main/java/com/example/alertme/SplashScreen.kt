package com.example.alertme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Background dan konten
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F1E4)), // Background sesuai warna pada gambar
        contentAlignment = Alignment.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.mipmap.logo_foreground), // Ganti dengan logo kamu
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )
    }

    // Navigasi setelah delay
    androidx.compose.runtime.LaunchedEffect(key1 = true) {
        delay(3000) // Durasi splash screen (3 detik)
        navController.navigate("welcome") { // Ganti "home" dengan nama rute utama aplikasi
            popUpTo("splash") { inclusive = true }
        }
    }
}

@Preview
@Composable
fun PreviewSplashScreen(){
    val navController = rememberNavController()
    SplashScreen(navController = navController)
}