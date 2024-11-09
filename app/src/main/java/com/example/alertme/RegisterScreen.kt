package com.example.alertme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun RegisterScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            ,
        contentAlignment = Alignment.TopCenter
    )


    {
        Image(
            painter = painterResource(id = R.mipmap.header_foreground), // Ganti dengan resource gambar
            contentDescription = "Login Header",
            modifier = Modifier
                .fillMaxSize()
                .scale(2.4f)
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
            .background(Color.Cyan, shape = RectangleShape)
        )


        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)

            ) {
                Text(
                    text = "Daftar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Input Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Input Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Tombol Masuk
                Button(
                    onClick = {
                        // Tambahkan logika login, misalnya validasi email dan password
                        println("Email: $email, Password: $password")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E4E88)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Masuk", color = Color.White)
                }
            }

            // Social login
            Text(
                text = "Atau daftar dengan:",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.google_logo_foreground), // Ganti dengan ikon Google
                    contentDescription = "Google Login",
                    modifier = Modifier.size(40.dp)
                )
            }

            // Signup link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sudah punya akun? ", fontSize = 14.sp)
                Text(
                    text = "Masuk",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewRegisterScreen() {
    val navController = rememberNavController() // Membuat NavController untuk preview
    RegisterScreen(navController = navController)
}