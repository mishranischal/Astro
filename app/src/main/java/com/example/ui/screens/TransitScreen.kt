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
fun TransitScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val transitReport by viewModel.transitReport.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Gochara & Sade Sati, 1: Multi-Year Timeline

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Sade Sati Tracking Card
        item {
            val ss = transitReport.sadeSati
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (ss.isActive) Color(0xFF3B1D1D) else CosmicMidnightSurface
                ),
                border = BorderStroke(1.dp, if (ss.isActive) VedicSaffron else Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SHANI SADE SATI MONITOR",
                            style = MaterialTheme.typography.titleMedium,
                            color = VedicGold,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    if (ss.isActive) Color(0xFFDC2626).copy(alpha = 0.25f) else Color(0xFF16A34A).copy(alpha = 0.25f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (ss.isActive) "ACTIVE" else "NOT ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ss.isActive) Color(0xFFF87171) else Color(0xFF4ADE80)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Current Status: ${ss.currentPhase}",
                        fontWeight = FontWeight.Bold,
                        color = TextWhitePrimary,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(ss.description, fontSize = 12.sp, color = TextSilverSecondary)

                    if (ss.isActive && ss.recommendedRemedies.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFF475569))
                        Spacer(Modifier.height(8.dp))
                        Text("Classical Vedic Upayas (Remedies):", fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 11.sp)
                        ss.recommendedRemedies.forEach { remedy ->
                            Text("• $remedy", fontSize = 11.sp, color = TextWhitePrimary, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }

        // 2. Tab Selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = VedicGold,
                divider = {}
            ) {
                listOf("Real-Time Gochara", "Long-Term Timeline (25 Years)").forEachIndexed { index, title ->
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

        // 3. Tab Contents
        when (selectedTab) {
            0 -> {
                // Planetary Gochara Table
                item {
                    Text(
                        text = "Planetary Transits evaluated from Natal Moon (Janma Rashi):",
                        fontSize = 12.sp,
                        color = TextSilverSecondary
                    )
                }

                items(transitReport.gocharaResults) { gResult ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, if (gResult.isFavorable) Color(0xFF22C55E).copy(alpha = 0.4f) else Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${gResult.planet.englishName} in ${gResult.transitRashi.englishName} (House ${gResult.houseFromMoon} from Moon)",
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhitePrimary,
                                    fontSize = 13.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (gResult.isFavorable) Color(0xFF16A34A).copy(alpha = 0.2f) else Color(0xFFDC2626).copy(alpha = 0.2f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (gResult.isFavorable) "Favorable" else if (gResult.hasVedha) "Vedha Blocked" else "Neutral / Prudence",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (gResult.isFavorable) Color(0xFF4ADE80) else if (gResult.hasVedha) VedicSaffron else Color(0xFFF87171)
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(gResult.classicalEffect, fontSize = 11.sp, color = TextSilverSecondary)
                        }
                    }
                }
            }
            1 -> {
                // Multi-Year Transit Timeline
                items(transitReport.multiYearTimeline) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .background(CosmicCardSurface, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text("${event.year}", fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 14.sp)
                                Text(event.planet.shortName, fontSize = 11.sp, color = TextWhitePrimary)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${event.planet.englishName} enters ${event.transitSign.englishName}", fontWeight = FontWeight.Bold, color = TextWhitePrimary, fontSize = 13.sp)
                                Text(event.majorInfluence, fontSize = 11.sp, color = TextSilverSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
