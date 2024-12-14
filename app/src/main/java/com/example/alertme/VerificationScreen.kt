package com.example.alertme

import android.content.ContentResolver
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun VerificationScreen(contentResolver: ContentResolver, navController: NavController) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for picking an image from gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            imageUri = saveBitmapToUri(contentResolver, bitmap)
        }
    }

    // Function to upload the selected image
    fun uploadImage(imageUri: Uri) {
        val client = OkHttpClient()
        val contentResolver = context.contentResolver

        // Dapatkan nama file asli
        val fileName = contentResolver.query(imageUri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex("_display_name")
            cursor.moveToFirst()
            cursor.getString(nameIndex) ?: "temp_image.jpg" // Default jika nama tidak ditemukan
        } ?: "temp_image.jpg"

        // Simpan file sementara dengan nama dan ekstensi asli
        val tempFile = File(context.cacheDir, fileName)
        val inputStream = contentResolver.openInputStream(imageUri)
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }

        val requestBody: RequestBody = tempFile.asRequestBody("image/*".toMediaType())
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("ktp", fileName, requestBody) // Gunakan nama file asli
            .build()

        // Ambil token dari SharedPreferences
        val sharedPreferences = context.getSharedPreferences("AppPreferences", 0)
        val token = sharedPreferences.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val request = Request.Builder()
            .url("http://10.0.2.2:4000/user/ktp")
            .addHeader("Authorization", "$token") // Pastikan format header benar
            .post(multipartBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Foto berhasil dikirim!", Toast.LENGTH_SHORT).show()
                        navController.navigate("home")
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Gagal mengirim foto: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF2E8))
            .padding(top = 128.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ID-Verification-Page",
            fontSize = 32.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display the selected image
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "No Image Selected",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to upload a photo
        Button(
            onClick = { galleryLauncher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4A75)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
        ) {
            Text(
                text = "Unggah foto",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        // Button to take a photo
        Button(
            onClick = { cameraLauncher.launch(null) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC4C4D1)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
        ) {
            Text(
                text = "Ambil foto",
                color = Color(0xFF4A4A75),
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Button to send the uploaded photo
        Button(
            onClick = {
                if (imageUri != null) {
                    uploadImage(imageUri!!)
                } else {
                    Toast.makeText(context, "Pilih foto terlebih dahulu!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
        ) {
            Text(
                text = "Kirim",
                color = Color.White,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
fun PreviewVerificationScreen() {
    val navController = rememberNavController() // Preview with NavController
    VerificationScreen(LocalContext.current.contentResolver, navController)
}
