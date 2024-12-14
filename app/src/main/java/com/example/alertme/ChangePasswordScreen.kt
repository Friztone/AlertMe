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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

@Composable
fun ChangePasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = remember {
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    }

    val authToken = remember { mutableStateOf(sharedPreferences.getString("auth_token", null)) }
    val userId = remember {
        mutableStateOf(authToken.value?.let { getUuidFromToken(it) })
    }

    val oldPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val newPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val confirmPasswordState = remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Ubah password",
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
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Input for old password
                    PasswordField(
                        label = "Password lama",
                        placeholder = "Masukkan password lama",
                        passwordState = oldPasswordState
                    )

                    // Input for new password
                    PasswordField(
                        label = "Password baru",
                        placeholder = "Masukkan password baru",
                        passwordState = newPasswordState
                    )

                    // Input for confirm password
                    PasswordField(
                        label = "Konfirmasi password baru",
                        placeholder = "Konfirmasi password baru",
                        passwordState = confirmPasswordState
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
                            if (authToken.value.isNullOrEmpty() || userId.value.isNullOrEmpty()) {
                                Toast.makeText(context, "Token atau ID pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show()
                            } else {
                                handleChangePassword(
                                    context,
                                    userId.value!!,
                                    authToken.value!!,
                                    oldPasswordState.value.text,
                                    newPasswordState.value.text,
                                    confirmPasswordState.value.text,
                                    navController
                                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(label: String, placeholder: String, passwordState: MutableState<TextFieldValue>) {
    Column {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp)),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

private fun getUuidFromToken(token: String): String? {
    return try {
        val parts = token.split(".")
        if (parts.size == 3) {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            json.optString("uuid") // Sesuaikan dengan field UUID pada payload token
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun handleChangePassword(
    context: Context,
    userId: String,
    authToken: String,
    oldPassword: String,
    newPassword: String,
    confirmPassword: String,
    navController: NavController
) {
    if (newPassword != confirmPassword) {
        Toast.makeText(context, "Password baru dan konfirmasi tidak cocok!", Toast.LENGTH_SHORT).show()
        return
    }

    val client = OkHttpClient()
    val jsonBody = JSONObject().apply {
        put("oldPassword", oldPassword)
        put("newPassword", newPassword)
        put("confPassword", confirmPassword)
    }

    val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://10.0.2.2:4000/user/$userId/password")
        .addHeader("Authorization", "$authToken")
        .put(requestBody)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Password berhasil diubah!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Gagal mengubah password: ${response.message}", Toast.LENGTH_SHORT).show()
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
fun PreviewChangePasswordScreen() {
    val navController = rememberNavController() // Preview with NavController
    ChangePasswordScreen(navController = navController)
}
