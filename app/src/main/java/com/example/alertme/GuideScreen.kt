package com.example.alertme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alertme.ui.theme.AppTopBar

@Composable
fun GuideScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Panduan",
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
                // Konten Panduan
                GuideSection(
                    title = "Polisi",
                    content = listOf(
                        "Anda menyaksikan atau menjadi korban kejahatan, seperti perampokan, pencurian, penipuan, atau tindak kekerasan.",
                        "Terjadi kecelakaan terjadi tanpa korban serius tetapi membutuhkan penanganan aparat hukum atau ketika kecelakaan melibatkan kejahatan.",
                        "Terdapat kerusuhan, gangguan keamanan atau ketertiban umum, seperti perkelahian massal atau ancaman kekerasan.",
                        "Jika seseorang hilang dan tidak dapat ditemukan setelah dilakukan pencarian atau ada seseorang yang diduga dalam bahaya.",
                        "dsb."
                    )
                )

                GuideSection(
                    title = "Pemadam kebakaran",
                    content = listOf(
                        "Anda melihat atau mengalami kebakaran baik di rumah, tempat kerja, kendaraan, atau tempat umum.",
                        "Ada hal berpotensi memicu kebakaran atau ledakan seperti kebocoran gas atau korsleting listrik.",
                        "Terdapat kejadian lainnya yang membutuhkan penyelamatan. Misalnya penyelamatan hewan atau manusia dari situasi yang berbahaya seperti jatuh ke sumur atau terjebak dalam bangunan.",
                        "Jika ada tumpahan atau kebocoran bahan kimia berbahaya yang berpotensi menimbulkan kebakaran.",
                        "dsb."
                    )
                )

                GuideSection(
                    title = "Rumah sakit (Ambulans)",
                    content = listOf(
                        "Ada seseorang yang mengalami kecelakaan parah, serangan jantung, stroke, atau kondisi medis yang membutuhkan perawatan segera.",
                        "Jika terjadi kecelakaan lalu lintas dengan korban yang mengalami luka berat atau tak sadarkan diri.",
                        "Terdapat seseorang yang menunjukkan gejala keracunan serius atau overdosis obat.",
                        "Terdapat seorang wanita mengalami persalinan atau kontraksi yang kuat sebelum waktunya.",
                        "Jika seseorang mengalami keracunan makanan, obat-obatan, atau bahan kimia.",
                        "dsb."
                    )
                )

                GuideSection(
                    title = "BPBD (Badan Penanggulangan Bencana Daerah)",
                    content = listOf(
                        "Anda menghadapi atau mengetahui adanya bencana alam seperti banjir, gempa bumi, tanah longsor, tsunami, atau kebakaran hutan.",
                        "Terjadi bencana non-alam seperti kebakaran hutan yang meluas atau kecelakaan industri besar.",
                        "Diperlukan bantuan evakuasi dalam situasi bencana atau darurat alam.",
                        "Terdapat kerusakan akibat bencana dan butuh bantuan dari pemerintah daerah terkait.",
                        "dsb."
                    )
                )
            }
        }
    )
}

@Composable
fun GuideSection(title: String, content: List<String>) {
    Column(modifier = Modifier
        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A4A75),
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
        )
        Box(modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .shadow(0.dp, shape = RoundedCornerShape(8.dp)),


        ){
            Column (
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ){
                content.forEach { point ->
                    Text(
                        text = "• $point",
                        textAlign = TextAlign.Justify,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(12.dp, 4.dp),
                        style = TextStyle(lineHeight = 18.sp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGuideScreen(){
    val navController = rememberNavController()
    GuideScreen(navController = navController)
}

