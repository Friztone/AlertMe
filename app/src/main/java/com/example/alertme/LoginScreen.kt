package com.example.alertme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
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
                    text = "Masuk",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
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
                        if (email.isNotBlank() && password.isNotBlank()) {
                            navController.navigate("home")
                        }
                    },
                    enabled = email.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E4E88)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Masuk", color = Color.White)
                }

                Text(
                    text = "Lupa password?",
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen(){
    val navController = rememberNavController() // Membuat NavController untuk preview
    LoginScreen(navController = navController)
}