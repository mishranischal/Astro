package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ShlokaLibrary
import com.example.model.ClassicalShloka
import com.example.model.EncyclopediaTopic
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@Composable
fun EncyclopediaScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Sanskrit Shlokas, 1: Jyotisha Lessons

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
                listOf("Classical Shlokas", "Jyotisha Encyclopedia").forEachIndexed { index, title ->
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
                // Sanskrit Shlokas
                items(ShlokaLibrary.shlokas) { shloka: ClassicalShloka ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(shloka.title.ifEmpty { "Vedic Shloka" }, fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 13.sp)
                                Text(shloka.sourceText, color = Color(0xFF38BDF8), fontSize = 11.sp)
                            }

                            Spacer(Modifier.height(8.dp))

                            // Sanskrit Devanagari Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CosmicCardSurface, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(shloka.textSanskrit, color = Color(0xFFF59E0B), fontSize = 13.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(Modifier.height(8.dp))
                            Text("Transliteration:", fontSize = 11.sp, color = TextSilverSecondary)
                            Text(shloka.transliteration, fontSize = 11.sp, color = TextWhitePrimary)

                            Spacer(Modifier.height(6.dp))
                            Text("English Meaning:", fontSize = 11.sp, color = TextSilverSecondary)
                            Text(shloka.englishTranslation, fontSize = 11.sp, color = TextWhitePrimary)

                            Spacer(Modifier.height(6.dp))
                            Text("Astrological Principle:", fontSize = 11.sp, color = VedicGold, fontWeight = FontWeight.Bold)
                            Text(shloka.astrologicalPrinciple, fontSize = 11.sp, color = TextWhitePrimary)
                        }
                    }
                }
            }
            1 -> {
                // Encyclopedia Topics
                items(ShlokaLibrary.encyclopediaTopics) { topic: EncyclopediaTopic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(topic.title, fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 14.sp)
                            Text(topic.sanskritName, color = Color(0xFFF59E0B), fontSize = 12.sp)

                            Spacer(Modifier.height(6.dp))
                            Text(topic.summary, fontSize = 12.sp, color = TextWhitePrimary)

                            if (topic.keyRules.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Text("Key Classical Rules:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VedicGold)
                                for (rule in topic.keyRules) {
                                    Text("• $rule", fontSize = 11.sp, color = TextWhitePrimary, modifier = Modifier.padding(top = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
