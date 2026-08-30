package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@Composable
fun AdvancedAstrologyScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val sahams by viewModel.tajikaSahams.collectAsState()
    val longevity by viewModel.longevityAnalysis.collectAsState()
    val sbcReport by viewModel.sarvatobhadraReport.collectAsState()
    val eclipses by viewModel.eclipsesList.collectAsState()
    val solstices by viewModel.solsticesList.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Tajika Sahams, 1: Ayurdhaya (Longevity), 2: Sarvatobhadra Chakra, 3: Eclipses & Solstices

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Selector Tab
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = VedicGold,
                edgePadding = 0.dp,
                divider = {}
            ) {
                listOf("Tajika Sahams (50+)", "Ayurdhaya (Longevity)", "Sarvatobhadra (9x9)", "Eclipses & Solstices").forEachIndexed { index, title ->
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
                // Tajika Sahams
                item {
                    Text("Classical Tajika Saham Sensitive Points (Day & Night Reversals):", color = TextSilverSecondary, fontSize = 12.sp)
                }
                items(sahams) { saham ->
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
                                Column {
                                    Text(saham.name, fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 13.sp)
                                    Text(saham.sanskritName, color = Color(0xFFF59E0B), fontSize = 11.sp)
                                }
                                Text("${saham.rashi.englishName} H${saham.house}", fontWeight = FontWeight.Bold, color = TextWhitePrimary, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(saham.meaning, fontSize = 11.sp, color = TextWhitePrimary)
                            Spacer(Modifier.height(2.dp))
                            Text("Formula: ${saham.formulaDay}", fontSize = 10.sp, color = TextSilverSecondary)
                        }
                    }
                }
            }
            1 -> {
                // Ayurdhaya Longevity Analysis
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, VedicGold)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("AYURDHAYA LONGEVITY SYNTHESIS", color = VedicGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(longevity.longevityCategory, color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Estimated Longevity Span: ~${longevity.averageLongevityYears} Years", color = TextWhitePrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFF334155))
                            Spacer(Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pindayu Method:", color = TextSilverSecondary, fontSize = 12.sp)
                                Text("${longevity.pindayuYears} Years", color = TextWhitePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Jaimini 3 Pairs Method:", color = TextSilverSecondary, fontSize = 12.sp)
                                Text("${longevity.jaiminiMethodYears} Years", color = TextWhitePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Naisargika Method:", color = TextSilverSecondary, fontSize = 12.sp)
                                Text("${longevity.naisargikaAyurYears} Years", color = TextWhitePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(Modifier.height(10.dp))
                            Text("Calculation Steps & Deductions:", color = VedicGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            longevity.stepsExplanation.forEach { step ->
                                Text(step, fontSize = 11.sp, color = TextWhitePrimary, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
            2 -> {
                // Sarvatobhadra Chakra
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("SARVATOBHADRA CHAKRA (81 SQUARES GRID)", color = VedicGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(sbcReport.generalInterpretation, color = TextSilverSecondary, fontSize = 11.sp)
                            Spacer(Modifier.height(10.dp))

                            sbcReport.activeVedhas.forEach { v ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("${v.planet.englishName} → ${v.vedhaType}", fontWeight = FontWeight.Bold, color = TextWhitePrimary, fontSize = 12.sp)
                                        Text(v.effect, fontSize = 10.sp, color = TextSilverSecondary)
                                    }
                                    Text("on ${v.targetNakshatraOrVarna}", color = VedicGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                HorizontalDivider(color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
            }
            3 -> {
                // Eclipses & Solstices
                item {
                    Text("Astronomical Eclipses (Grahan) & Solstices:", color = TextSilverSecondary, fontSize = 12.sp)
                }

                items(eclipses) { eclipse ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(eclipse.type, fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 13.sp)
                                Text(eclipse.date, color = TextWhitePrimary, fontSize = 12.sp)
                            }
                            Text("Nakshatra: ${eclipse.nakshatra} (${eclipse.rashi.englishName})", fontSize = 11.sp, color = TextSilverSecondary)
                            Text("Visibility: ${eclipse.visibility}", fontSize = 11.sp, color = TextSilverSecondary)
                            Spacer(Modifier.height(4.dp))
                            Text(eclipse.religiousSignificance, fontSize = 11.sp, color = TextWhitePrimary)
                        }
                    }
                }

                items(solstices) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(event.eventName, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 13.sp)
                                Text("${event.date} (${event.timeUTC})", color = TextWhitePrimary, fontSize = 11.sp)
                            }
                            Text("Sayana: ${event.sayanaSign} | Nirayana: ${event.nirayanaSign}", fontSize = 11.sp, color = VedicGold)
                            Spacer(Modifier.height(4.dp))
                            Text(event.spiritualSignificance, fontSize = 11.sp, color = TextWhitePrimary)
                        }
                    }
                }
            }
        }
    }
}
