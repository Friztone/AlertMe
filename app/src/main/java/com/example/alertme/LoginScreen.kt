package com.example.alertme

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val emailState = remember { androidx.compose.runtime.mutableStateOf("") }
    val passwordState = remember { androidx.compose.runtime.mutableStateOf("") }

    fun loginUser(email: String, password: String) {
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject().apply {
            put("email", email)
            put("password", password)
        }.toString()

        val requestBody = jsonBody.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("http://10.0.2.2:4000/login")
            .post(requestBody)
            .build()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "{}")
                    val token = jsonResponse.getString("token")

                    // Simpan token ke SharedPreferences
                    val sharedPreferences = context.getSharedPreferences("AppPreferences", 0)
                    sharedPreferences.edit().putString("auth_token", token).apply()

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Login berhasil!", Toast.LENGTH_SHORT).show()
                        navController.navigate("home")
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Login gagal: ${response.message}", Toast.LENGTH_SHORT).show()
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
            .background(Color(0xFFF8F1E4))
            .padding(22.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.mipmap.logo_foreground),
            contentDescription = "Login image",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Masuk",
            fontSize = 32.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(21.dp))
                .padding(14.dp)
                .height(26.dp),
            decorationBox = { innerTextField ->
                if (emailState.value.isEmpty()) {
                    Text(
                        text = "Email",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(21.dp))

        BasicTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(21.dp))
                .padding(14.dp)
                .height(26.dp),
            visualTransformation = PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                if (passwordState.value.isEmpty()) {
                    Text(
                        text = "Password",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(21.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                when {
                    emailState.value.isEmpty() -> {
                        Toast.makeText(context, "Masukkan email terlebih dahulu.", Toast.LENGTH_LONG).show()
                    }
                    passwordState.value.isEmpty() -> {
                        Toast.makeText(context, "Masukkan password terlebih dahulu.", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        loginUser(emailState.value, passwordState.value)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color(0xFFFF9800)
            )
        ) {
            Text(
                modifier = Modifier.padding(7.dp),
                text = "Masuk",
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Belum punya akun?",
                fontSize = 15.sp,
                color = Color.Black
            )
            TextButton(
                onClick = { navController.navigate("register") },
                contentPadding = PaddingValues(5.dp)
            ) {
                Text(
                    text = "Daftar",
                    fontSize = 15.sp,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}
