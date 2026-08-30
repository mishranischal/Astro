package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AstroEngine
import com.example.model.*
import com.example.ui.components.NorthIndianChartCanvas
import com.example.ui.components.SouthIndianChartCanvas
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KundaliOverviewScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val chart by viewModel.activeChart.collectAsState()
    val isNorthIndian by viewModel.isNorthIndianStyle.collectAsState()
    val selectedAyanamsha by viewModel.selectedAyanamsha.collectAsState()

    var showAyanamshaDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Planetary Table, 1: Bhava Chalit, 2: Special Lagnas, 3: Planetary Dignity

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, VedicGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chart.personName,
                                style = MaterialTheme.typography.titleLarge,
                                color = VedicGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${chart.birthDate} at ${chart.birthTime} • ${chart.birthPlace}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSilverSecondary
                            )
                        }

                        // Chart Style Switch Button
                        OutlinedButton(
                            onClick = { viewModel.toggleChartStyle() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VedicGold),
                            border = BorderStroke(1.dp, VedicGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (isNorthIndian) "North Style" else "South Style", fontSize = 12.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF334155))
                    Spacer(Modifier.height(12.dp))

                    // Quick Badges: Lagna, Janma Rashi, Janma Nakshatra, Tithi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AstrologicalBadge("Lagna", chart.ascendantRashi.englishName, "${Math.round(chart.ascendantDegree % 30 * 100.0) / 100.0}°")
                        val moonPos = chart.planets[Planet.MOON]
                        AstrologicalBadge("Chandra Rashi", moonPos?.rashi?.englishName ?: "-", "${moonPos?.rashi?.lord?.shortName}")
                        AstrologicalBadge("Nakshatra", moonPos?.nakshatra?.englishName ?: "-", "Pada ${moonPos?.nakshatraPada}")
                        AstrologicalBadge("Ayanamsha", selectedAyanamsha.displayName.substringBefore(" "), "${Math.round(chart.ayanamshaValue * 100.0) / 100.0}°")
                    }
                }
            }
        }

        // 2. Primary Chart Visualizer (North / South Indian Canvas)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
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
                        Text(
                            text = "D1 RASI CHART (लग्न कुण्डली)",
                            style = MaterialTheme.typography.titleMedium,
                            color = VedicGold,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showAyanamshaDialog = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Ayanamsha", tint = TextSilverSecondary)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    if (isNorthIndian) {
                        NorthIndianChartCanvas(chart = chart, modifier = Modifier.fillMaxWidth())
                    } else {
                        SouthIndianChartCanvas(chart = chart, modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Legend: (R) = Retrograde (Vakra), * = Combust (Astha). Numbers = Rashi index (1: Aries, 2: Taurus...)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilverSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Tab Selector for Detailed Astrological Breakdowns
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = VedicGold,
                edgePadding = 0.dp,
                divider = {}
            ) {
                listOf("Graha Positions", "Bhava Chalit", "Special Lagnas", "Panchadha Maitri").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) VedicGold else TextSilverSecondary
                            )
                        }
                    )
                }
            }
        }

        // 4. Tab Contents
        when (selectedTab) {
            0 -> {
                // Planetary Positions Table
                item {
                    GrahaPositionsTable(chart)
                }
            }
            1 -> {
                // Bhava Chalit & Cusps Table
                item {
                    BhavaChalitTable(chart)
                }
            }
            2 -> {
                // Special Lagnas
                item {
                    SpecialLagnasCard(chart.specialLagnas)
                }
            }
            3 -> {
                // Panchadha Maitri
                item {
                    PanchadhaMaitriCard(chart)
                }
            }
        }
    }

    // Ayanamsha Selection Dialog
    if (showAyanamshaDialog) {
        AlertDialog(
            onDismissRequest = { showAyanamshaDialog = false },
            title = { Text("Select Ayanamsha System", color = VedicGold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AyanamshaSystem.values().forEach { ayan ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setAyanamsha(ayan)
                                    showAyanamshaDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedAyanamsha == ayan,
                                onClick = {
                                    viewModel.setAyanamsha(ayan)
                                    showAyanamshaDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = VedicGold)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(ayan.displayName, color = TextWhitePrimary, fontWeight = FontWeight.SemiBold)
                                Text(ayan.description, color = TextSilverSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAyanamshaDialog = false }) {
                    Text("Close", color = VedicGold)
                }
            },
            containerColor = CosmicMidnightSurface
        )
    }
}

@Composable
fun AstrologicalBadge(title: String, mainVal: String, subVal: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(CosmicCardSurface, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(title, fontSize = 10.sp, color = TextSilverSecondary)
        Text(mainVal, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhitePrimary)
        Text(subVal, fontSize = 11.sp, color = VedicGold)
    }
}

@Composable
fun GrahaPositionsTable(chart: BirthChart) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "PLANETARY DETAILED COORDINATES (ग्रह स्थितिः)",
                style = MaterialTheme.typography.titleSmall,
                color = VedicGold,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicCardSurface, RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Graha", modifier = Modifier.weight(1.2f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicGold)
                Text("Rashi & Deg", modifier = Modifier.weight(1.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicGold)
                Text("Nakshatra", modifier = Modifier.weight(1.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicGold)
                Text("House", modifier = Modifier.weight(0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicGold, textAlign = TextAlign.Center)
                Text("Dignity", modifier = Modifier.weight(1.4f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicGold, textAlign = TextAlign.End)
            }

            Spacer(Modifier.height(4.dp))

            chart.planets.values.forEachIndexed { index, pos ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (index % 2 == 0) Color.Transparent else CosmicCardSurface.copy(alpha = 0.5f))
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Planet Name + Retrograde/Combust indicators
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(
                            text = pos.planet.englishName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhitePrimary
                        )
                        Row {
                            if (pos.isRetrograde) {
                                Text("R ", fontSize = 10.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            }
                            if (pos.isCombust) {
                                Text("Combust", fontSize = 10.sp, color = VedicSaffron)
                            }
                        }
                    }

                    // Rashi & Degree in sign
                    Text(
                        text = "${pos.rashi.shortName} ${AstroEngine.formatDMS(pos.degreeInSign)}",
                        modifier = Modifier.weight(1.8f),
                        fontSize = 11.sp,
                        color = TextWhitePrimary
                    )

                    // Nakshatra & Pada
                    Text(
                        text = "${pos.nakshatra.shortName}-${pos.nakshatraPada}",
                        modifier = Modifier.weight(1.8f),
                        fontSize = 11.sp,
                        color = TextSilverSecondary
                    )

                    // House
                    Text(
                        text = "H${pos.house}",
                        modifier = Modifier.weight(0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VedicGold,
                        textAlign = TextAlign.Center
                    )

                    // Dignity Badge
                    Box(
                        modifier = Modifier
                            .weight(1.4f)
                            .wrapContentWidth(Alignment.End)
                            .background(
                                color = when (pos.dignity) {
                                    PlanetaryDignity.EXALTED -> Color(0xFF16A34A).copy(alpha = 0.25f)
                                    PlanetaryDignity.OWN_SIGN, PlanetaryDignity.MOOLATRIKONA -> Color(0xFF2563EB).copy(alpha = 0.25f)
                                    PlanetaryDignity.DEBILITATED -> Color(0xFFDC2626).copy(alpha = 0.25f)
                                    PlanetaryDignity.FRIEND, PlanetaryDignity.GREAT_FRIEND -> Color(0xFF0D9488).copy(alpha = 0.25f)
                                    else -> Color(0xFF475569).copy(alpha = 0.25f)
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = pos.dignity.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (pos.dignity) {
                                PlanetaryDignity.EXALTED -> Color(0xFF4ADE80)
                                PlanetaryDignity.DEBILITATED -> Color(0xFFF87171)
                                PlanetaryDignity.OWN_SIGN -> VedicGold
                                else -> TextSilverSecondary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BhavaChalitTable(chart: BirthChart) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "BHAVA CHALIT & CUSPS (भाव चलित चक्रम्)",
                style = MaterialTheme.typography.titleSmall,
                color = VedicGold,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            chart.houses.forEachIndexed { idx, house ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "House ${house.houseNumber} (${house.rashi.englishName})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhitePrimary,
                        modifier = Modifier.weight(1.5f)
                    )
                    Text(
                        text = "Cusp: ${AstroEngine.formatDMS(house.startDegree % 30.0)}",
                        fontSize = 11.sp,
                        color = TextSilverSecondary,
                        modifier = Modifier.weight(1.2f)
                    )
                    Text(
                        text = "Lord: ${house.rashi.lord.englishName}",
                        fontSize = 11.sp,
                        color = VedicGold,
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.End
                    )
                }
                if (idx < 11) HorizontalDivider(color = Color(0xFF1E293B))
            }
        }
    }
}

@Composable
fun SpecialLagnasCard(lagnas: SpecialLagnas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "SPECIAL JAIMINI & PARASHARI LAGNAS (विशेष लग्नानि)",
                style = MaterialTheme.typography.titleSmall,
                color = VedicGold,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))

            val lagnaList = listOf(
                Pair("Hora Lagna (HL)", Pair(lagnas.horaLagna, "Wealth, financial accumulation, and monetary vitality")),
                Pair("Ghati Lagna (GL)", Pair(lagnas.ghatiLagna, "Power, official authority, fame, and political stature")),
                Pair("Bhava Lagna (BL)", Pair(lagnas.bhavaLagna, "Physical vitality, core health, and longevity")),
                Pair("Varnada Lagna (VL)", Pair(lagnas.varnadaLagna, "Social status, professional duty, and peer relations")),
                Pair("Sri Lagna (SL)", Pair(lagnas.sriLagna, "Divine blessings of Goddess Lakshmi and fortune")),
                Pair("Indu Lagna", Pair(lagnas.induLagna, "Extraordinary financial prosperity and sudden wealth")),
                Pair("Upapada Lagna (UL)", Pair(lagnas.upapadaLagna, "Marital destiny, spouse nature, and marital harmony"))
            )

            lagnaList.forEachIndexed { index, item ->
                val (name, pair) = item
                val (deg, desc) = pair
                val rashi = Rashi.fromIndex((deg / 30.0).toInt() + 1)
                val degInSign = deg % 30.0

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(name, fontWeight = FontWeight.Bold, color = TextWhitePrimary, fontSize = 13.sp)
                        Text("${rashi.englishName} ${AstroEngine.formatDMS(degInSign)}", color = VedicGold, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Text(desc, color = TextSilverSecondary, fontSize = 11.sp)
                }
                if (index < lagnaList.size - 1) HorizontalDivider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
fun PanchadhaMaitriCard(chart: BirthChart) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "PANCHADHA MAITRI (FIVE-FOLD PLANETARY FRIENDSHIP)",
                style = MaterialTheme.typography.titleSmall,
                color = VedicGold,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Combines Naisargika (Natural) friendship with Tatkalika (Temporal distance: 2, 3, 4, 10, 11, 12 = Friend; others = Enemy) to yield the final 5-fold relationship.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSilverSecondary,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(10.dp))

            Planet.MAIN_SEVEN.forEach { p ->
                val pos = chart.planets[p]
                if (pos != null) {
                    Text("${p.englishName} (in ${pos.rashi.englishName} H${pos.house})", fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 12.sp)
                    val friendLords = p.naturalFriends.mapNotNull { Planet.fromId(it)?.englishName }.joinToString(", ")
                    val enemyLords = p.naturalEnemies.mapNotNull { Planet.fromId(it)?.englishName }.joinToString(", ")
                    Text("• Natural Friends: ${if (friendLords.isNotEmpty()) friendLords else "None"}", fontSize = 11.sp, color = TextWhitePrimary)
                    Text("• Natural Enemies: ${if (enemyLords.isNotEmpty()) enemyLords else "None"}", fontSize = 11.sp, color = TextSilverSecondary)
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}
