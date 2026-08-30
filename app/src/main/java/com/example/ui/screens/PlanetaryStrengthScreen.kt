package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@Composable
fun PlanetaryStrengthScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val shadbalaList by viewModel.shadbalaList.collectAsState()
    val ashtakavarga by viewModel.ashtakavargaReport.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Shadbala (6-fold), 1: Sarvashtakavarga, 2: Bhinnashtakavargas

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Selector Tab
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = VedicGold,
                divider = {}
            ) {
                listOf("Shadbala (षड्बलम्)", "Sarvashtakavarga (337)", "Bhinnashtakavarga").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) VedicGold else TextSilverSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        }

        // 2. Tab Contents
        when (selectedTab) {
            0 -> {
                // Shadbala Ranking & Component Breakdown
                items(shadbalaList) { item ->
                    ShadbalaPlanetCard(item)
                }
            }
            1 -> {
                // Sarvashtakavarga Table & Heatmap
                item {
                    SarvashtakavargaCard(ashtakavarga)
                }
            }
            2 -> {
                // Bhinnashtakavargas Per Planet
                items(ashtakavarga.bhinnashtakavargas.toList()) { (planet, table) ->
                    BhinnashtakavargaPlanetCard(planet, table)
                }
            }
        }
    }
}

@Composable
fun ShadbalaPlanetCard(item: ShadbalaComponent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        border = BorderStroke(1.dp, if (item.strengthRatio >= 1.0) VedicGold.copy(alpha = 0.5f) else Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(VedicGold, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Rank #${item.rank}", color = CosmicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(item.planet.englishName, fontWeight = FontWeight.Bold, color = TextWhitePrimary, fontSize = 14.sp)
                }

                Text(
                    text = "${item.totalRupas} / ${item.requiredRupas} Rupas (${Math.round(item.strengthRatio * 100.0)}%)",
                    fontWeight = FontWeight.Bold,
                    color = if (item.strengthRatio >= 1.0) Color(0xFF4ADE80) else Color(0xFFF87171),
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (item.strengthRatio / 1.5).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (item.strengthRatio >= 1.0) Color(0xFF22C55E) else Color(0xFFEF4444),
                trackColor = Color(0xFF1E293B)
            )

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFF1E293B))
            Spacer(Modifier.height(8.dp))

            // 6 Sub-Bala breakdown
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BalaMiniBadge("Sthana", "${item.sthanaBala}")
                BalaMiniBadge("Dig", "${item.digBala}")
                BalaMiniBadge("Kaala", "${item.kaalaBala}")
                BalaMiniBadge("Chesta", "${item.chestaBala}")
                BalaMiniBadge("Naisargika", "${item.naisargikaBala}")
                BalaMiniBadge("Drik", "${item.drikBala}")
            }
        }
    }
}

@Composable
fun BalaMiniBadge(name: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, fontSize = 10.sp, color = TextSilverSecondary)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhitePrimary)
    }
}

@Composable
fun SarvashtakavargaCard(report: AshtakavargaReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        border = BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "SARVASHTAKAVARGA (337 BINDUS TOTAL)",
                style = MaterialTheme.typography.titleSmall,
                color = VedicGold,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(report.samudaayaAshtakavargaAnalysis, fontSize = 12.sp, color = TextSilverSecondary)
            Spacer(Modifier.height(12.dp))

            // 12 Signs Bindu Grid (4 rows x 3 columns)
            val signs = Rashi.values()
            val chunked = signs.toList().chunked(3)

            chunked.forEach { rowSigns ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowSigns.forEach { rashi ->
                        val bindus = report.sarvashtakavarga.rashiBindus[rashi] ?: 28
                        val isStrong = bindus >= 28
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isStrong) Color(0xFF16A34A).copy(alpha = 0.15f) else Color(0xFFDC2626).copy(alpha = 0.15f)
                            ),
                            border = BorderStroke(1.dp, if (isStrong) Color(0xFF22C55E).copy(alpha = 0.4f) else Color(0xFFEF4444).copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(rashi.shortName, fontSize = 11.sp, color = TextSilverSecondary)
                                Text("$bindus", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isStrong) Color(0xFF4ADE80) else Color(0xFFF87171))
                                Text(if (isStrong) "Auspicious" else "Caution", fontSize = 9.sp, color = if (isStrong) Color(0xFF4ADE80) else Color(0xFFF87171))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BhinnashtakavargaPlanetCard(planet: Planet, table: AshtakavargaTable) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${planet.englishName} Ashtakavarga", fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 13.sp)
                Text("Total: ${table.totalPoints} | Sodhya Pinda: ${table.sodhyaPinda}", color = TextWhitePrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))

            // Row of Houses 1 to 12 Bindus
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                table.houseBindus.forEachIndexed { hIdx, bCount ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("H${hIdx + 1}", fontSize = 9.sp, color = TextSilverSecondary)
                        Text("$bCount", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (bCount >= 4) Color(0xFF4ADE80) else Color(0xFFF87171))
                    }
                }
            }
        }
    }
}
