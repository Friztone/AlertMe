package com.example.alertme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alertme.ui.theme.AppTopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

@Composable
fun DetailRumahSakit(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Rumah Sakit",
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            val hospitalLocations = remember { mutableStateListOf<Map<String, String>>() }
            val context = LocalContext.current

            // Function to fetch data from API with token
            fun fetchHospitalLocations() {
                val client = OkHttpClient()

                // Retrieve token from SharedPreferences
                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("auth_token", null)

                if (token.isNullOrEmpty()) {
                    Toast.makeText(context, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
                    return
                }

                val request = Request.Builder()
                    .url("http://10.0.2.2:4000/rumahsakit")
                    .addHeader("Authorization", "$token")
                    .build()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            val jsonArray = JSONArray(responseBody)

                            val locations = mutableListOf<Map<String, String>>()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                locations.add(
                                    mapOf(
                                        "uuid" to jsonObject.getString("uuid"),
                                        "name" to jsonObject.getString("name"),
                                        "alamat" to jsonObject.getString("alamat")
                                    )
                                )
                            }

                            CoroutineScope(Dispatchers.Main).launch {
                                hospitalLocations.clear()
                                hospitalLocations.addAll(locations)
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    context,
                                    "Gagal memuat data: ${response.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // Fetch data when composable is loaded
            LaunchedEffect(Unit) {
                fetchHospitalLocations()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAF2E8))
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (hospitalLocations.isEmpty()) {
                    Text(
                        text = "Memuat data...",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    hospitalLocations.forEach { location ->
                        HospitalCard(
                            name = location["name"] ?: "",
                            address = location["alamat"] ?: "",
                            onClick = { navController.navigate("form/${location["uuid"]}") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun HospitalCard(name: String, address: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A4A75)
            )
            Text(
                text = address,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun PreviewDetailRumahSakit() {
    val navController = rememberNavController()
    DetailRumahSakit(navController = navController)
}
