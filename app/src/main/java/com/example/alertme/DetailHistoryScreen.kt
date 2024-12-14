package com.example.alertme

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alertme.ui.theme.AppTopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailHistoryScreen(
    navController: NavController,
    uuid: String
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("auth_token", null)

    var report by remember { mutableStateOf<Report?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load data from API
    LaunchedEffect(Unit) {
        if (authToken.isNullOrEmpty()) {
            Toast.makeText(context, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else {
            fetchReportDetail(uuid, authToken, context) { fetchedReport, error ->
                if (fetchedReport != null) {
                    report = fetchedReport
                } else {
                    errorMessage = error
                }
            }
        }
    }

    if (report != null) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Detail Laporan",
                    onBackClick = { navController.popBackStack() }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFAF2E8))
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Petugas
                        Text(
                            text = report!!.petugas,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A4A75)
                        )

                        // Nama Laporan
                        DetailItem(label = "Nama Laporan", value = report!!.name)

                        // Tanggal Pelaporan
                        DetailItem(label = "Tanggal pelaporan", value = report!!.tanggal)

                        // Deskripsi
                        DetailItem(label = "Deskripsi", value = report!!.deskripsi)

                        // Lokasi
                        DetailItem(
                            label = "Lokasi",
                            value = report!!.lokasi,
                            isLink = true,
                            onClick = {
                                // Aksi saat link lokasi diklik, misalnya membuka Maps
                            }
                        )

                        // Progress
                        DetailItem(label = "Progress", value = report!!.progress)

                        // Kontak
                        ContactItem(
                            label = "Kontak",
                            contact = report!!.kontak,
                            onCopy = {
                                // Aksi untuk menyalin nomor kontak ke clipboard
                            }
                        )
                    }
                }
            }
        )
    } else if (!errorMessage.isNullOrEmpty()) {
        // Jika terjadi error
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Error",
                    onBackClick = { navController.popBackStack() }
                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFAF2E8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage!!,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }
        )
    }
}

private fun fetchReportDetail(
    uuid: String,
    token: String,
    context: Context,
    callback: (Report?, String?) -> Unit
) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.0.2.2:4000/laporan/$uuid")
        .addHeader("Authorization", "$token")
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (!responseBody.isNullOrEmpty()) {
                    val jsonObject = JSONObject(responseBody)
                    val report = parseReportFromJson(jsonObject)
                    callback(report, null)
                } else {
                    callback(null, "Data laporan kosong.")
                }
            } else {
                callback(null, "Gagal memuat laporan: ${response.message}")
            }
        } catch (e: Exception) {
            callback(null, "Terjadi kesalahan: ${e.message}")
        }
    }
}

private fun parseReportFromJson(jsonObject: JSONObject): Report {
    val kantor = jsonObject.getJSONObject("kantor") // Ambil objek kantor terlebih dahulu

    return Report(
        uuid = jsonObject.getString("uuid"),
        petugas = kantor.getString("name"), // Ambil nama petugas dari kantor
        name = jsonObject.getString("name"),
        deskripsi = jsonObject.getString("deskripsi"),
        lokasi = jsonObject.getString("lokasi_kejadian"),
        progress = jsonObject.getString("status"),
        alamatKantor = kantor.getString("alamat"), // Ambil alamat dari objek kantor
        kontak = kantor.getString("telfon"), // Ambil telfon dari objek kantor
        tanggal = jsonObject.optString("createdAt", "Tidak tersedia") // Perbaiki untuk createdAt
    )
}



@Composable
fun DetailItem(label: String, value: String, isLink: Boolean = false, onClick: () -> Unit = {}) {
    Column {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isLink) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF3E4E88),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onClick)
            )
        } else {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ContactItem(label: String, contact: String, onCopy: () -> Unit) {
    Column {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = contact,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = "Salin",
                fontSize = 14.sp,
                color = Color(0xFF3E4E88),
                modifier = Modifier.clickable(onClick = onCopy)
            )
        }
    }
}
