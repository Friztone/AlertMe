package com.example.alertme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alertme.ui.theme.AppTopBar

@Composable
fun DetailBPBD(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "BPBD",
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
                    .verticalScroll(rememberScrollState())
            ) {
                // Array Data Lokasi
                val bpbdLocations = listOf(
                    "BPBD Malang Kota" to "Jl. Bingkil No.1, Ciptomulyo, Kec. Sukun, Kota Malang, Jawa Timur 65148",
                    "BPBD Kabupaten Malang" to "Jl. Panji Suroso No.7, Kec. Blimbing, Kota Malang, Jawa Timur 65126",
                    "BPBD Provinsi Jawa Timur" to "Jl. Raya Karanglo No.1, Singosari, Kabupaten Malang, Jawa Timur 65153",
                    "BPBD Indonesia" to "Jl. Letjen S. Parman No.87, Kec. Klojen, Kota Malang, Jawa Timur 65112",
                    "BPBD Regional Jawa Timur" to "Jl. Soekarno Hatta No.9, Lowokwaru, Kota Malang, Jawa Timur 65141"
                )

                // Mapping Data ke UI
                bpbdLocations.forEach { (name, address) ->
                    FireStationCard(name = name, address = address)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    )
}

@Composable
fun BPBDCard(name: String, address: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A4A75)
            )
            Text(
                text = address,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview
@Composable
fun PreviewDetailBPBD() {
    val navController = rememberNavController() // Preview with NavController
    DetailBPBD(navController = navController)
}