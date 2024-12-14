package com.example.alertme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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


@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Setelan",
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAF2E8))
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Profil Section
                Text(
                    text = "Profil",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A4A75),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SettingsItem(
                    icon = Icons.Filled.Person,
                    title = "Ubah nama",
                    onClick = {navController.navigate("change_name")}
                )
                SettingsItem(
                    icon = Icons.Filled.Lock,
                    title = "Ubah password",
                    onClick = {navController.navigate("change_password")}
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Histori Section
                Text(
                    text = "Kontak",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A4A75),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SettingsItem(
                    icon = Icons.Filled.DateRange,
                    title = "Panggilan Darurat",
                    onClick = {val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:119")
                    }
                        context.startActivity(intent)}

                )
            }
        }
    )
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF4A4A75),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = "Next",
            tint = Color.Gray
        )
    }
}
@Preview
@Composable
fun PreviewSettingsScreen(){
    val navController = rememberNavController() // Membuat NavController untuk preview
    SettingsScreen(navController = navController)
}