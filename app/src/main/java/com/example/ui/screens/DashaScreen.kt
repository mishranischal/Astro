package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@Composable
fun DashaScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val dashaReport by viewModel.dashaReport.collectAsState()
    var selectedSystemTab by remember { mutableStateOf(0) } // 0: Vimshottari, 1: Yogini, 2: Jaimini Chara, 3: Ashtottari
    var expandedMahaIdx by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Current Active Running Dasha Highlight Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, VedicGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CURRENT RUNNING DASHA",
                            style = MaterialTheme.typography.titleSmall,
                            color = VedicGold,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF22C55E).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("ACTIVE NOW", fontSize = 10.sp, color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    val curMaha = dashaReport.currentMahadasha
                    val curAntar = dashaReport.currentAntardasha
                    val curPrat = dashaReport.currentPratyantar

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        DashaPeriodBadge("Mahadasha", curMaha?.planet?.englishName ?: "-", "${curMaha?.startDate} - ${curMaha?.endDate}")
                        DashaPeriodBadge("Antardasha", curAntar?.planet?.englishName ?: "-", "${curAntar?.startDate} - ${curAntar?.endDate}")
                        DashaPeriodBadge("Pratyantar", curPrat?.planet?.englishName ?: "-", "${curPrat?.startDate} - ${curPrat?.endDate}")
                    }

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF334155))
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = curMaha?.predictionSignificance ?: "Major unfolding of planetary karmic cycles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhitePrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 2. Dasha System Selector Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSystemTab,
                containerColor = Color.Transparent,
                contentColor = VedicGold,
                edgePadding = 0.dp,
                divider = {}
            ) {
                listOf("Vimshottari (120y)", "Yogini (36y)", "Jaimini Chara", "Ashtottari (108y)").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSystemTab == index,
                        onClick = { selectedSystemTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedSystemTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSystemTab == index) VedicGold else TextSilverSecondary
                            )
                        }
                    )
                }
            }
        }

        // 3. Detailed Dasha System Timelines
        when (selectedSystemTab) {
            0 -> {
                // Vimshottari Mahadashas Collapsible List
                item {
                    Text(
                        text = "Birth Balance: ${dashaReport.startingMahadasha.englishName} Mahadasha (${dashaReport.birthBalanceYears} years remaining at birth)",
                        fontSize = 12.sp,
                        color = TextSilverSecondary
                    )
                }

                items(dashaReport.vimshottariList.indices.toList()) { index ->
                    val maha = dashaReport.vimshottariList[index]
                    val isExpanded = expandedMahaIdx == index

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedMahaIdx = if (isExpanded) null else index },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (maha.isRunning) Color(0xFF1E293B) else CosmicMidnightSurface
                        ),
                        border = BorderStroke(1.dp, if (maha.isRunning) VedicGold else Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = VedicGold
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${maha.planet.englishName} Mahadasha (${Math.round(maha.durationYears * 10.0) / 10.0} yrs)",
                                            fontWeight = FontWeight.Bold,
                                            color = if (maha.isRunning) VedicGold else TextWhitePrimary,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${maha.startDate} to ${maha.endDate}",
                                            fontSize = 11.sp,
                                            color = TextSilverSecondary
                                        )
                                    }
                                }

                                if (maha.isRunning) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF22C55E).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Running", fontSize = 10.sp, color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Expanded Antardashas
                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp, start = 16.dp)) {
                                    HorizontalDivider(color = Color(0xFF334155))
                                    Spacer(Modifier.height(8.dp))
                                    Text("Antardashas (Sub-periods):", color = VedicGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(4.dp))

                                    maha.subPeriods.forEach { antar ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${maha.planet.shortName} - ${antar.planet.englishName}",
                                                fontSize = 11.sp,
                                                fontWeight = if (antar.isRunning) FontWeight.Bold else FontWeight.Normal,
                                                color = if (antar.isRunning) Color(0xFF4ADE80) else TextWhitePrimary
                                            )
                                            Text(
                                                text = "${antar.startDate} → ${antar.endDate}",
                                                fontSize = 11.sp,
                                                color = TextSilverSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Yogini Dasha
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "YOGINI DASHA (36-YEAR CYCLICAL WHEEL)",
                                style = MaterialTheme.typography.titleSmall,
                                color = VedicGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))

                            dashaReport.yoginiList.forEach { yPeriod ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${yPeriod.name} (${yPeriod.rulingPlanet.englishName}) - ${yPeriod.durationYears}y",
                                            fontWeight = if (yPeriod.isRunning) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (yPeriod.isRunning) VedicGold else TextWhitePrimary,
                                            fontSize = 12.sp
                                        )
                                        Text(yPeriod.nature, fontSize = 10.sp, color = TextSilverSecondary)
                                    }
                                    Text(
                                        text = "${yPeriod.startDate} → ${yPeriod.endDate}",
                                        fontSize = 11.sp,
                                        color = TextSilverSecondary
                                    )
                                }
                                HorizontalDivider(color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
            }
            2 -> {
                // Jaimini Chara Dasha
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "JAIMINI CHARA DASHA (RASHI PERIODS)",
                                style = MaterialTheme.typography.titleSmall,
                                color = VedicGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))

                            dashaReport.charaList.forEach { cPeriod ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${cPeriod.rashi.englishName} Rashi (${cPeriod.durationYears} Years)",
                                            fontWeight = if (cPeriod.isRunning) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (cPeriod.isRunning) VedicGold else TextWhitePrimary,
                                            fontSize = 12.sp
                                        )
                                        Text("Occupants: ${if (cPeriod.arudhaLordsPresent.isNotEmpty()) cPeriod.arudhaLordsPresent.joinToString(", ") else "None"}", fontSize = 10.sp, color = TextSilverSecondary)
                                    }
                                    Text(
                                        text = "${cPeriod.startDate} → ${cPeriod.endDate}",
                                        fontSize = 11.sp,
                                        color = TextSilverSecondary
                                    )
                                }
                                HorizontalDivider(color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
            }
            3 -> {
                // Ashtottari Dasha
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ASHTOTTARI DASHA (108-YEAR CYCLE)",
                                style = MaterialTheme.typography.titleSmall,
                                color = VedicGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))

                            dashaReport.ashtottariList.forEach { aPeriod ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${aPeriod.planet.englishName} (${aPeriod.durationYears} Years)",
                                        fontWeight = if (aPeriod.isRunning) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (aPeriod.isRunning) VedicGold else TextWhitePrimary,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${aPeriod.startDate} → ${aPeriod.endDate}",
                                        fontSize = 11.sp,
                                        color = TextSilverSecondary
                                    )
                                }
                                HorizontalDivider(color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashaPeriodBadge(level: String, planetName: String, dates: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(CosmicCardSurface, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(level, fontSize = 10.sp, color = TextSilverSecondary)
        Text(planetName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VedicGold)
        Text(dates, fontSize = 9.sp, color = TextWhitePrimary, maxLines = 1)
    }
}
