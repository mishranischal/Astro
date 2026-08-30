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
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

@Composable
fun ReverseSearchScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val results by viewModel.reverseSearchResults.collectAsState()
    var startYearStr by remember { mutableStateOf("2025") }
    var endYearStr by remember { mutableStateOf("2032") }
    var selectedJupSign by remember { mutableStateOf<Rashi?>(Rashi.CANCER) }
    var selectedSatSign by remember { mutableStateOf<Rashi?>(Rashi.LIBRA) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Search Query Parameters Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, VedicGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "COSMIC REVERSE ASTROLOGY SEARCH",
                        style = MaterialTheme.typography.titleMedium,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Search across celestial ephemeris to discover exact historical or future dates matching specific planetary configurations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilverSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startYearStr,
                            onValueChange = { startYearStr = it },
                            label = { Text("Start Year") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endYearStr,
                            onValueChange = { endYearStr = it },
                            label = { Text("End Year") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text("Selected Query Alignments:", fontSize = 12.sp, color = VedicGold, fontWeight = FontWeight.SemiBold)
                    Text("• Jupiter: ${selectedJupSign?.englishName ?: "Any"} (Exalted in Cancer)", fontSize = 11.sp, color = TextWhitePrimary)
                    Text("• Saturn: ${selectedSatSign?.englishName ?: "Any"} (Exalted in Libra)", fontSize = 11.sp, color = TextWhitePrimary)

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val sY = startYearStr.toIntOrNull() ?: 2025
                            val eY = endYearStr.toIntOrNull() ?: 2032
                            viewModel.setReverseSearchQuery(
                                ReverseSearchQuery(
                                    startYear = sY,
                                    endYear = eY,
                                    requiredJupiterRashi = selectedJupSign,
                                    requiredSaturnRashi = selectedSatSign
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = VedicGold, contentColor = CosmicDeepNavy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Search Astronomical Ephemeris", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Search Results List
        item {
            Text("Matching Celestial Dates (${results.size} Found):", color = TextSilverSecondary, fontSize = 12.sp)
        }

        items(results) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.dateString, fontWeight = FontWeight.Bold, color = VedicGold, fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF16A34A).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Matched Alignment", fontSize = 10.sp, color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    item.matchingConditions.forEach { cond ->
                        Text("✓ $cond", fontSize = 11.sp, color = TextWhitePrimary)
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(item.significance, fontSize = 11.sp, color = TextSilverSecondary)
                }
            }
        }
    }
}
