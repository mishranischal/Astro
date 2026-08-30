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
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@Composable
fun YogasScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val detectedYogas by viewModel.detectedYogas.collectAsState()
    var selectedCategoryFilter by remember { mutableStateOf<YogaCategory?>(null) }

    val filteredYogas = remember(detectedYogas, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) detectedYogas
        else detectedYogas.filter { it.category == selectedCategoryFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Header Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, VedicGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CLASSICAL ASTROLOGICAL YOGAS (ग्रहयोगाः)",
                        style = MaterialTheme.typography.titleMedium,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Identified ${detectedYogas.size} active planetary combinations across Brihat Parashara Hora Shastra, Saravali, and Phaladeepika.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilverSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 2. Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All (${detectedYogas.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VedicGold,
                            selectedLabelColor = CosmicDeepNavy,
                            containerColor = CosmicCardSurface,
                            labelColor = TextWhitePrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                items(YogaCategory.values()) { cat ->
                    val count = detectedYogas.count { it.category == cat }
                    if (count > 0) {
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text("${cat.displayName} ($count)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VedicGold,
                                selectedLabelColor = CosmicDeepNavy,
                                containerColor = CosmicCardSurface,
                                labelColor = TextWhitePrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // 3. Yoga Cards List
        if (filteredYogas.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No yogas found under selected category.", color = TextSilverSecondary)
                }
            }
        } else {
            items(filteredYogas) { yoga ->
                YogaItemCard(yoga)
            }
        }
    }
}

@Composable
fun YogaItemCard(yoga: YogaResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = yoga.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = yoga.sanskritName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF59E0B)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(VedicGold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${yoga.strengthPercent}% Strength", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VedicGold)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Source Text Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Source: ", fontSize = 11.sp, color = TextSilverSecondary)
                Text(yoga.sourceText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
            }

            Spacer(Modifier.height(6.dp))
            Text(yoga.description, fontSize = 12.sp, color = TextWhitePrimary)

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFF1E293B))
            Spacer(Modifier.height(8.dp))

            // Planets & Houses Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Planets: ${yoga.participatingPlanets.joinToString(", ") { it.englishName }}",
                    fontSize = 11.sp,
                    color = VedicSaffron
                )
                Text(
                    text = "Houses: ${yoga.participatingHouses.joinToString(", ") { "H$it" }}",
                    fontSize = 11.sp,
                    color = TextSilverSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            // Beneficial Effects Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CosmicCardSurface, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text("Classical Phala (Results):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VedicGold)
                    Spacer(Modifier.height(2.dp))
                    Text(yoga.beneficialEffects, fontSize = 12.sp, color = TextWhitePrimary)
                }
            }
        }
    }
}
