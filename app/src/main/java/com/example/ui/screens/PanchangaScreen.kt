package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@Composable
fun PanchangaScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val dailyPanchanga by viewModel.dailyPanchanga.collectAsState()
    var activeTab by remember { mutableStateOf(0) } // 0: 5 Limbs & Solar, 1: Muhurtas & Kaalas, 2: Choghadiya & Horas, 3: Hindu Calendar & Festivals

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicDeepNavy)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
    ) {
        // --- TOP TAB SELECTOR ---
        item {
            val tabs = listOf("5 Limbs (Panchanga)", "Muhurtas & Kaalas", "Choghadiya & Horas", "Calendar & Parva")
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = VedicGold,
                edgePadding = 0.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { idx, name ->
                    Tab(
                        selected = activeTab == idx,
                        onClick = { activeTab = idx },
                        text = {
                            Text(
                                text = name,
                                fontWeight = if (activeTab == idx) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == idx) VedicGold else TextSilverSecondary
                            )
                        }
                    )
                }
            }
        }

        dailyPanchanga?.let { p ->
            when (activeTab) {
                0 -> { // 5 LIMBS & SOLAR/LUNAR EPHEMERIS
                    item {
                        // Date & Location Banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Dainika Panchanga",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = VedicGold
                                        )
                                        Text(
                                            text = "${p.date} • ${p.location}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSilverSecondary
                                        )
                                    }
                                    Surface(shape = RoundedCornerShape(8.dp), color = CosmicCardSurface) {
                                        Text(
                                            text = p.vara.sanskritName,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VedicTeal
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = CosmicCardElevated)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Solar & Lunar Timings Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TimingPill("Sunrise", p.sunrise, Icons.Default.WbSunny, ColorSun)
                                    TimingPill("Sunset", p.sunset, Icons.Default.WbTwilight, VedicSaffron)
                                    TimingPill("Moonrise", p.moonrise, Icons.Default.Nightlight, ColorMoon)
                                    TimingPill("Moonset", p.moonset, Icons.Default.Brightness3, ChandraSilver)
                                }
                            }
                        }
                    }

                    // 5 LIMBS DETAILED CARDS
                    item {
                        PanchangaLimbCard(
                            title = "1. Tithi (Lunar Day)",
                            mainValue = p.tithi.name,
                            subValue = "Deity: ${p.tithi.deity} • Nature: ${p.tithi.nature}",
                            statusText = "${String.format(java.util.Locale.US, "%.0f%%", p.tithi.percentageElapsed)} elapsed (${p.tithi.endTime})"
                        )
                    }

                    item {
                        PanchangaLimbCard(
                            title = "2. Vara (Day of Week)",
                            mainValue = "${p.vara.name} (${p.vara.sanskritName})",
                            subValue = "Ruling Lord: ${p.vara.rulingPlanet.sanskritName} • Tattva: ${p.vara.tattva}",
                            statusText = "Solar Day Ruler"
                        )
                    }

                    item {
                        PanchangaLimbCard(
                            title = "3. Nakshatra (Lunar Asterism)",
                            mainValue = "${p.nakshatra.sanskritName} (Pada ${p.nakshatraPada})",
                            subValue = "Lord: ${p.nakshatra.lord.sanskritName} • Deity: ${p.nakshatra.deity}",
                            statusText = "${String.format(java.util.Locale.US, "%.0f%%", p.nakshatraElapsedPercent)} completed"
                        )
                    }

                    item {
                        PanchangaLimbCard(
                            title = "4. Yoga (Solilunar)",
                            mainValue = "${p.yoga.name} Yoga",
                            subValue = p.yoga.meaning,
                            statusText = if (p.yoga.isAuspicious) "Auspicious (Subha)" else "Ashubha / Inauspicious",
                            isAuspicious = p.yoga.isAuspicious
                        )
                    }

                    item {
                        PanchangaLimbCard(
                            title = "5. Karana (Half-Tithi)",
                            mainValue = p.karana.name,
                            subValue = "Deity: ${p.karana.deity} • Nature: ${p.karana.type}",
                            statusText = if (p.karana.isBhadra) "Vishti (Bhadra Active - Avoid New Ventures)" else "Favorable Karana",
                            isAuspicious = !p.karana.isBhadra
                        )
                    }
                }
                1 -> { // MUHURTAS & KAALAS
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Auspicious & Inauspicious Muhurtas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VedicGold)
                                Text("Real-time solar arc divisions for the current day", style = MaterialTheme.typography.bodySmall, color = TextSilverSecondary)
                                Spacer(modifier = Modifier.height(16.dp))

                                MuhurtaRow("Abhijit Muhurta (Most Auspicious)", "${p.muhurta.abhijitMuhurta.first} - ${p.muhurta.abhijitMuhurta.second}", true)
                                MuhurtaRow("Brahma Muhurta (Meditation & Spiritual)", "${p.muhurta.brahmaMuhurta.first} - ${p.muhurta.brahmaMuhurta.second}", true)
                                MuhurtaRow("Amrit Kalam", "${p.muhurta.amritKalam.first} - ${p.muhurta.amritKalam.second}", true)

                                HorizontalDivider(color = CosmicCardElevated, modifier = Modifier.padding(vertical = 8.dp))

                                MuhurtaRow("Rahu Kalam (Inauspicious)", "${p.muhurta.rahuKalam.first} - ${p.muhurta.rahuKalam.second}", false)
                                MuhurtaRow("Yamagandam (Inauspicious)", "${p.muhurta.yamagandam.first} - ${p.muhurta.yamagandam.second}", false)
                                MuhurtaRow("Gulika Kalam", "${p.muhurta.gulikaKalam.first} - ${p.muhurta.gulikaKalam.second}", false)
                            }
                        }
                    }
                }
                2 -> { // CHOGHADIYA & HORAS
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Dainika Choghadiya (Day & Night)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VedicGold)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Day Choghadiya:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicTeal)
                                Spacer(modifier = Modifier.height(6.dp))
                                p.choghadiyaDay.forEach { chog ->
                                    ChoghadiyaRow(chog)
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Night Choghadiya:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicSaffron)
                                Spacer(modifier = Modifier.height(6.dp))
                                p.choghadiyaNight.forEach { chog ->
                                    ChoghadiyaRow(chog)
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("24-Hour Planetary Horas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VedicGold)
                                Spacer(modifier = Modifier.height(12.dp))

                                p.horas.forEach { hora ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${hora.startTime} - ${hora.endTime}", fontSize = 12.sp, color = TextSilverSecondary)
                                        Text(hora.planet.sanskritName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (hora.isCurrentlyActive) VedicGold else TextWhitePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> { // HINDU CALENDAR & FESTIVALS
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Vedic Calendar Eras", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VedicGold)
                                Spacer(modifier = Modifier.height(12.dp))

                                CalendarInfoRow("Vikram Samvat", "${p.calendarInfo.vikramSamvat}")
                                CalendarInfoRow("Shaka Samvat", "${p.calendarInfo.shakaSamvat}")
                                CalendarInfoRow("Kali Yuga Year", "${p.calendarInfo.kaliYugaYear}")
                                CalendarInfoRow("Hindu Month (Masa)", p.calendarInfo.masaName)
                                CalendarInfoRow("Season (Ritu)", p.calendarInfo.rituName)
                                CalendarInfoRow("Ayana", p.calendarInfo.ayana)
                                CalendarInfoRow("Moon Phase", "${p.calendarInfo.lunarPhase} (${String.format(java.util.Locale.US, "%.0f%%", p.calendarInfo.moonIlluminationPercent)} illumination)")
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Upcoming Parvas & Fasting Days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VedicGold)
                                Spacer(modifier = Modifier.height(12.dp))

                                p.upcomingFestivals.forEach { fest ->
                                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(fest.name, fontWeight = FontWeight.Bold, color = TextWhitePrimary)
                                            Text(fest.dateSummary, fontSize = 11.sp, color = VedicTeal)
                                        }
                                        Text(fest.description, fontSize = 11.sp, color = TextSilverSecondary)
                                    }
                                    HorizontalDivider(color = CosmicCardElevated)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PanchangaLimbCard(title: String, mainValue: String, subValue: String, statusText: String, isAuspicious: Boolean = true) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = VedicGold, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(mainValue, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextWhitePrimary)
            Text(subValue, style = MaterialTheme.typography.bodySmall, color = TextSilverSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isAuspicious) VedicTeal.copy(alpha = 0.15f) else SuryaCrimson.copy(alpha = 0.15f)
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 11.sp,
                    color = if (isAuspicious) VedicTeal else SuryaCrimson
                )
            }
        }
    }
}

@Composable
fun MuhurtaRow(name: String, time: String, isAuspicious: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontSize = 13.sp, color = if (isAuspicious) TextWhitePrimary else SuryaCrimson)
        Text(time, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isAuspicious) VedicGold else SuryaCrimson)
    }
}

@Composable
fun ChoghadiyaRow(chog: ChoghadiyaPeriod) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(chog.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhitePrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text("(${chog.rulingPlanet.shortName})", fontSize = 11.sp, color = TextMuted)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${chog.startTime} - ${chog.endTime}", fontSize = 11.sp, color = TextSilverSecondary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(chog.nature, fontSize = 10.sp, color = if (chog.nature.contains("Auspicious")) VedicGold else VedicSaffron)
        }
    }
}

@Composable
fun CalendarInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSilverSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhitePrimary)
    }
}

@Composable
fun TimingPill(label: String, time: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, color = TextMuted)
        Text(time, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhitePrimary)
    }
}
