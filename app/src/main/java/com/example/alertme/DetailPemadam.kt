package com.example.alertme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun DetailPemadam(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Pemadam kebakaran",
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
                val fireStations = listOf(
                    "Pemadam Kebakaran Kota Malang" to "Jl. Bingkil No.1, Ciptomulyo, Kec. Sukun, Kota Malang, Jawa Timur 65148",
                    "Dinas Pemadam Kebakaran Kota Malang" to "Jl. Raden Intan No.5, Arjosari, Kec. Blimbing, Kota Malang, Jawa Timur 65126",
                    "Mako Pemadam Kebakaran Kabupaten Malang" to "Gang 13 Jl. MT. Haryono No.413, Dinoyo, Kec. Lowokwaru, Kota Malang, Jawa Timur 65144",
                    "Polsek Kedungkandang" to "Jl. Ki Ageng Gribig No.96, Kedungkandang, Kec. Kedungkandang, Kota Malang, Jawa Timur 65136",
                    "POLRESTA Malang Kota" to "Jl. Jaksa Agung Suprapto No.19, Samaan, Kec. Klojen, Kota Malang, Jawa Timur 65112",

                    "Pemadam Kebakaran Kota Malang" to "Jl. Bingkil No.1, Ciptomulyo, Kec. Sukun, Kota Malang, Jawa Timur 65148",
                    "Dinas Pemadam Kebakaran Kota Malang" to "Jl. Raden Intan No.5, Arjosari, Kec. Blimbing, Kota Malang, Jawa Timur 65126",
                    "Mako Pemadam Kebakaran Kabupaten Malang" to "Gang 13 Jl. MT. Haryono No.413, Dinoyo, Kec. Lowokwaru, Kota Malang, Jawa Timur 65144",
                    "Polsek Kedungkandang" to "Jl. Ki Ageng Gribig No.96, Kedungkandang, Kec. Kedungkandang, Kota Malang, Jawa Timur 65136",
                    "POLRESTA Malang Kota" to "Jl. Jaksa Agung Suprapto No.19, Samaan, Kec. Klojen, Kota Malang, Jawa Timur 65112",                    "Pemadam Kebakaran Kota Malang" to "Jl. Bingkil No.1, Ciptomulyo, Kec. Sukun, Kota Malang, Jawa Timur 65148",
                    "Dinas Pemadam Kebakaran Kota Malang" to "Jl. Raden Intan No.5, Arjosari, Kec. Blimbing, Kota Malang, Jawa Timur 65126",
                    "Mako Pemadam Kebakaran Kabupaten Malang" to "Gang 13 Jl. MT. Haryono No.413, Dinoyo, Kec. Lowokwaru, Kota Malang, Jawa Timur 65144",
                    "Polsek Kedungkandang" to "Jl. Ki Ageng Gribig No.96, Kedungkandang, Kec. Kedungkandang, Kota Malang, Jawa Timur 65136",
                    "POLRESTA Malang Kota" to "Jl. Jaksa Agung Suprapto No.19, Samaan, Kec. Klojen, Kota Malang, Jawa Timur 65112"
                )

                // Mapping Data ke UI
                fireStations.forEach { (name, address) ->
                    FireStationCard(name = name, address = address)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    )
}


@Composable
fun FireStationCard(name: String, address: String) {
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
fun PreviewDetailPemadam() {
    val navController = rememberNavController() // Preview with NavController
    DetailPemadam(navController = navController)
}