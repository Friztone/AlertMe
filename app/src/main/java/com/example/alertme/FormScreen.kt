package com.example.alertme

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alertme.ui.theme.AppTopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navController: NavController, reportUuid: String) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = remember {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    val authToken = sharedPreferences.getString("auth_token", null)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Isi Formulir",
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAF2E8))
                    .padding(paddingValues)
                    .padding(32.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val titleState = remember { mutableStateOf(TextFieldValue()) }
                val descriptionState = remember { mutableStateOf(TextFieldValue()) }
                val locationState = remember { mutableStateOf(TextFieldValue()) }
                val attachmentUri = remember { mutableStateOf<Uri?>(null) }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ReportField(label = "Judul", placeholder = "Masukkan judul laporan", state = titleState)
                    ReportField(label = "Deskripsi", placeholder = "Jelaskan insiden yang Anda alami / lihat", state = descriptionState)
                    ReportField(label = "Lokasi (tautan Google Maps)", placeholder = "Tautan Google Maps", state = locationState)

                    // Upload button
                    Column {
                        Text(
                            text = "Lampiran (opsional)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                            attachmentUri.value = uri
                        }

                        Button(
                            onClick = {
                                launcher.launch("*/*")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C7157)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Unggah file / foto",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        if (attachmentUri.value != null) {
                            Text(
                                text = "File diunggah: ${attachmentUri.value.toString()}",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val title = titleState.value.text
                            val description = descriptionState.value.text
                            val location = locationState.value.text
                            val attachment = attachmentUri.value

                            if (title.isNotEmpty() && description.isNotEmpty() && location.isNotEmpty()) {
                                sendReport(
                                    navController,
                                    reportUuid,
                                    title,
                                    description,
                                    location,
                                    attachment,
                                    authToken
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Semua field wajib diisi kecuali lampiran!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E4E88)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Kirim",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    )
}

private fun sendReport(
    navController: NavController,
    reportUuid: String,
    title: String,
    description: String,
    location: String,
    attachmentUri: Uri?,
    authToken: String?
) {
    if (authToken.isNullOrEmpty()) {
        Toast.makeText(
            navController.context,
            "Token tidak ditemukan. Silakan login ulang.",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val client = OkHttpClient()
    val contentResolver = navController.context.contentResolver

    val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("kantorUuid", reportUuid)
        .addFormDataPart("name", title)
        .addFormDataPart("deskripsi", description)
        .addFormDataPart("lokasi_kejadian", location)

    attachmentUri?.let { uri ->
        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex("_display_name")
            cursor.moveToFirst()
            cursor.getString(nameIndex) ?: "uploaded_file"
        } ?: "uploaded_file"

        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile(fileName, null, navController.context.cacheDir)
        tempFile.outputStream().use {
            inputStream?.copyTo(it)
        }

        val attachmentBody = tempFile.asRequestBody(contentResolver.getType(uri)?.toMediaType())
        requestBodyBuilder.addFormDataPart("laporan", fileName, attachmentBody)
    }

    val requestBody = requestBodyBuilder.build()

    val request = Request.Builder()
        .url("http://10.0.2.2:4000/laporan")
        .addHeader("Authorization", "$authToken")
        .post(requestBody)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        navController.context,
                        "Laporan berhasil dikirim!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        navController.context,
                        "Gagal mengirim laporan: ${response.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    navController.context,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportField(label: String, placeholder: String, state: MutableState<TextFieldValue>) {
    Column {
        Text(
            text = label,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold,
        )
        TextField(
            value = state.value,
            onValueChange = { state.value = it },
            placeholder = { Text(text = placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp)),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
fun PreviewReportScreen() {
    val navController = rememberNavController()
    FormScreen(navController = navController, reportUuid = "1234567890")
}
