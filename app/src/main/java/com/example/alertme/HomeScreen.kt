package com.example.alertme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AlertMe",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { /* Navigate to Settings */ }) {
                        Icon(Icons.Filled.Build, contentDescription = "Settings", tint = Color.White)
                    }
                    IconButton(onClick = { /* Navigate to Notifications */ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                    IconButton(onClick = { /* Handle Logout */ }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3E4E88))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFF7F6F2))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Greeting Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDAE3F7)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hi, John Doe",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A2B68)
                        )
                    }
                }

                // Emergency and Guide Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(text = "Panduan", color = Color.White)
                    }
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5C5C))
                    ) {
                        Text(text = "Darurat", color = Color.White)
                    }
                }

                Text(
                    text = "Buat laporan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Report Cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReportCard(
                        title = "Pemadam kebakaran",
                        iconRes = R.mipmap.firefighter_foreground,
                        backgroundColor = Color(0xFFFFEBEB),
                        nav = "detail_pemadam",
                        navController = navController
                    )
                    ReportCard(
                        title = "Polisi",
                        iconRes = R.mipmap.police_foreground,
                        backgroundColor = Color(0xFFE1EBFF),
                        nav = "detail_polisi",
                        navController = navController
                    )
                    ReportCard(
                        title = "Rumah sakit",
                        iconRes = R.mipmap.ambulance_foreground,
                        backgroundColor = Color(0xFFF6F6F6),
                        nav = "detail_rs",
                        navController = navController
                    )
                    ReportCard(
                        title = "BPBD (Badan Penanggulangan Bencana Daerah)",
                        iconRes = R.mipmap.forest_foreground,
                        backgroundColor = Color(0xFFE6F7EB),
                        nav = "detail_bpbd",
                        navController = navController
                    )
                }
            }
        }
    )
}

@Composable
fun ReportCard(title: String, iconRes: Int, backgroundColor: Color, nav: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { navController.navigate(nav) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF444444)
            )
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController() // Preview with NavController
    HomeScreen(navController = navController)
}