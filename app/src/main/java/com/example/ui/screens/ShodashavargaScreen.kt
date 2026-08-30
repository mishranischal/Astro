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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AstroEngine
import com.example.model.*
import com.example.ui.components.NorthIndianChartCanvas
import com.example.ui.components.SouthIndianChartCanvas
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@Composable
fun ShodashavargaScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val chart by viewModel.activeChart.collectAsState()
    val isNorthIndian by viewModel.isNorthIndianStyle.collectAsState()
    val selectedVarga by viewModel.selectedVargaType.collectAsState()
    val vargaChart by viewModel.currentVargaChart.collectAsState()
    val vimsopakaScores by viewModel.vimsopakaScores.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Shodashavarga Quick Selector Chips
        item {
            Column {
                Text(
                    text = "SHODASHAVARGA (16 DIVISIONAL CHARTS)",
                    style = MaterialTheme.typography.titleMedium,
                    color = VedicGold,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(VargaType.SHODASHAVARGA_LIST) { vType ->
                        val isSelected = selectedVarga == vType
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setVargaType(vType) },
                            label = { Text("${vType.code} ${vType.sanskritName.substringBefore(' ')}", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VedicGold,
                                selectedLabelColor = CosmicDeepNavy,
                                containerColor = CosmicCardSurface,
                                labelColor = TextWhitePrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) VedicGold else Color(0xFF334155))
                        )
                    }
                }
            }
        }

        // 2. Divisional Chart Visualizer Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, VedicGold.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${selectedVarga.name} - ${selectedVarga.sanskritName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = VedicGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Signification: ${selectedVarga.signification}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSilverSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (isNorthIndian) {
                        NorthIndianChartCanvas(
                            chart = chart,
                            selectedVargaPositions = vargaChart.planetPositions,
                            vargaLagnaRashi = vargaChart.lagnaRashi,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        SouthIndianChartCanvas(
                            chart = chart,
                            selectedVargaPositions = vargaChart.planetPositions,
                            vargaLagnaRashi = vargaChart.lagnaRashi,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Interpretation Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CosmicCardSurface, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Classical Text Analysis (BPHS):", color = VedicGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(vargaChart.interpretation, color = TextWhitePrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. Divisional Graha Positions Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "${selectedVarga.code} GRAHA POSITIONS & DEITIES",
                        style = MaterialTheme.typography.titleSmall,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    vargaChart.planetPositions.forEach { (planet, vPos) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(planet.englishName, fontWeight = FontWeight.SemiBold, color = TextWhitePrimary, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                            Text("${vPos.rashi.englishName} H${vPos.house}", color = VedicGold, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                            Text("Deity: ${vPos.vargaDeity}", color = TextSilverSecondary, fontSize = 11.sp, modifier = Modifier.weight(1.6f))
                        }
                        HorizontalDivider(color = Color(0xFF1E293B))
                    }
                }
            }
        }

        // 4. Vimsopaka Bala (16 Vargas Comprehensive Strength)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "VIMSOPAKA BALA (20-POINT DIVISIONAL STRENGTH)",
                        style = MaterialTheme.typography.titleSmall,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Weighted mathematical composite score across the Shodashavarga hierarchy as specified in Brihat Parashara Hora Shastra.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilverSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(Modifier.height(10.dp))

                    vimsopakaScores.forEach { item ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.planet.englishName, fontWeight = FontWeight.Bold, color = TextWhitePrimary, fontSize = 12.sp)
                                Text("${item.score} / 20.0 (${item.percentage}%)", fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (item.score / 20.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = if (item.score >= 12.0) Color(0xFF22C55E) else if (item.score >= 8.0) VedicGold else Color(0xFFEF4444),
                                trackColor = Color(0xFF1E293B)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(item.status, color = TextSilverSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
