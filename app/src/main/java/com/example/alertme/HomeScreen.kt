package com.example.alertme

import android.content.Context
import android.content.SharedPreferences
import android.webkit.CookieManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = remember {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    val authToken = sharedPreferences.getString("auth_token", null)
    var userName by remember { mutableStateOf("User") }

    fun fetchUserName() {
        if (authToken.isNullOrEmpty()) {
            Toast.makeText(context, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/me")
            .addHeader("Authorization", "$authToken")
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody)
                    val name = jsonObject.getString("name")

                    CoroutineScope(Dispatchers.Main).launch {
                        userName = name
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Gagal memuat nama pengguna: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchUserName()
    }

    fun logoutUser() {
        sharedPreferences.edit().remove("auth_token").apply()

        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()

        Toast.makeText(context, "Logout berhasil. Anda dapat login dengan akun lain.", Toast.LENGTH_SHORT).show()

        navController.navigate("login") {
            popUpTo(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AlertMe",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Filled.Build, contentDescription = "Settings", tint = Color.White)
                    }
                    IconButton(onClick = { /* Navigate to Notifications */ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                    IconButton(onClick = { logoutUser() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3E4E88))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFFAF2E8))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Greeting Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDAE3F7)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hi, $userName",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A2B68)
                        )
                    }
                }

                // Emergency and Guide Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("guide") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Panduan",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { navController.navigate("history") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Riwayat",
                            color = Color.White,
                            fontWeight = FontWeight.Bold

                        )
                    }
                }

                Text(
                    text = "Buat laporan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Report Cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReportCard(
                        title = "Pemadam kebakaran",
                        iconRes = R.mipmap.firefighter_foreground,
                        backgroundColor = Color(0xFFFFEBEB),
                        nav = "detail_pemadam",
                        navController = navController
                    )
                    ReportCard(
                        title = "Polisi",
                        iconRes = R.mipmap.police_foreground,
                        backgroundColor = Color(0xFFE1EBFF),
                        nav = "detail_polisi",
                        navController = navController
                    )
                    ReportCard(
                        title = "Rumah sakit",
                        iconRes = R.mipmap.ambulance_foreground,
                        backgroundColor = Color(0xFFF6F6F6),
                        nav = "detail_rs",
                        navController = navController
                    )
                    ReportCard(
                        title = "BPBD (Badan Penanggulangan Bencana Daerah)",
                        iconRes = R.mipmap.forest_foreground,
                        backgroundColor = Color(0xFFE6F7EB),
                        nav = "detail_bpbd",
                        navController = navController
                    )
                }
            }
        }
    )
}

@Composable
fun ReportCard(title: String, iconRes: Int, backgroundColor: Color, nav: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { navController.navigate(nav) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF444444)
            )
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController() // Preview with NavController
    HomeScreen(navController = navController)
}
