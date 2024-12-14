package com.example.alertme

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeNameScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = remember {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    val authToken = remember { mutableStateOf(sharedPreferences.getString("auth_token", null)) }
    val userId = remember {
        mutableStateOf(authToken.value?.let { getUuidFromToken(it) })
    }

    val nameState = remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Ubah nama",
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
                // Input Field
                Column {
                    Text(
                        text = "Nama",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    TextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        placeholder = { Text(text = "Masukkan nama baru", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, shape = RoundedCornerShape(16.dp)),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7DAE8)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Batal", color = Color.Black)
                    }
                    Button(
                        onClick = {
                            val newName = nameState.value.text
                            if (newName.isNotEmpty() && authToken.value != null && userId.value != null) {
                                changeUserName(
                                    context,
                                    userId.value!!,
                                    authToken.value!!,
                                    newName,
                                    navController
                                )
                            } else {
                                Toast.makeText(context, "Nama tidak boleh kosong atau token tidak valid.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E4E88)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Kirim", color = Color.White)
                    }
                }
            }
        }
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

private fun changeUserName(
    context: Context,
    userId: String,
    authToken: String,
    newName: String,
    navController: NavController
) {
    val client = OkHttpClient()
    val jsonBody = JSONObject().apply {
        put("name", newName)
    }

    val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://10.0.2.2:4000/user/$userId/name")
        .addHeader("Authorization", "$authToken")
        .put(requestBody)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Nama berhasil diubah!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Gagal mengubah nama: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview
@Composable
fun PreviewChangeNameScreen() {
    val navController = rememberNavController()
    ChangeNameScreen(navController = navController)
}
