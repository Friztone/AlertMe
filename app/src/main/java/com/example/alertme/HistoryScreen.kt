package com.example.alertme

import android.content.Context
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.json.JSONObject

data class Report(
    val uuid: String,
    val petugas: String,
    val name: String,
    val deskripsi: String,
    val lokasi: String,
    val progress: String,
    val kontak: String,
    val tanggal: String,
    val alamatKantor: String
)

@Composable
fun HistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("auth_token", null)
    val userUuid = remember { authToken?.let { getUuidFromToken(it) } }
    val reports = remember { mutableStateListOf<Report>() }

    LaunchedEffect(Unit) {
        if (authToken.isNullOrEmpty() || userUuid.isNullOrEmpty()) {
            Toast.makeText(context, "Token tidak valid. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else {
            fetchReports(userUuid, authToken, reports, context)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Riwayat Laporan",
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAF2E8))
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (reports.isEmpty()) {
                    Text(
                        text = "Tidak ada laporan ditemukan.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    reports.forEach { report ->
                        ReportCard(
                            report = report,
                            onClick = { navController.navigate("detail_history/${report.uuid}") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}

private fun fetchReports(userUuid: String, token: String, reports: MutableList<Report>, context: Context) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.0.2.2:4000/laporan?user_uuid=$userUuid")
        .addHeader("Authorization", "$token")
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (!responseBody.isNullOrEmpty()) {
                    val jsonArray = JSONArray(responseBody)
                    val fetchedReports = mutableListOf<Report>()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val report = parseReportFromJson(jsonObject)
                        fetchedReports.add(report)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        reports.clear()
                        reports.addAll(fetchedReports)
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Gagal memuat laporan: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun parseReportFromJson(jsonObject: JSONObject): Report {
    val kantor = jsonObject.getJSONObject("kantor")
    return Report(
        uuid = jsonObject.getString("uuid"),
        petugas = kantor.getString("name"),
        name = jsonObject.getString("name"),
        deskripsi = jsonObject.getString("deskripsi"),
        lokasi = jsonObject.getString("lokasi_kejadian"),
        progress = jsonObject.getString("status"),
        kontak = kantor.optString("telfon", "Tidak tersedia"), // Gunakan optString dengan default
        alamatKantor = kantor.getString("alamat"),
        tanggal = jsonObject.optString("createdAt", "Tidak tersedia")
    )
}


private fun getUuidFromToken(token: String): String? {
    return try {
        val parts = token.split(".")
        if (parts.size == 3) {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            json.optString("uuid")
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ReportCard(report: Report, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                text = report.petugas,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A4A75)
            )
            Text(
                text = report.tanggal,
                fontSize = 12.sp,
                color = Color.Black
            )
            Text(
                text = report.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = report.deskripsi,
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = report.progress,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun PreviewHistory() {
    val navController = rememberNavController()
    HistoryScreen(navController = navController)
}
