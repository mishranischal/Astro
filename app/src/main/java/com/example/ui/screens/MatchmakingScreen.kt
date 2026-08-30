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
fun MatchmakingScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val matchReport by viewModel.compatibilityReport.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Match Verdict Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, VedicGold)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ASHTAKOOTA KUNDALI MILAN (36 GUNAS)",
                        style = MaterialTheme.typography.titleMedium,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${matchReport.groomName} & ${matchReport.brideName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhitePrimary
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "${matchReport.obtainedGunas} / 36.0",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (matchReport.obtainedGunas >= 24.0) Color(0xFF4ADE80) else if (matchReport.obtainedGunas >= 18.0) VedicGold else Color(0xFFF87171)
                    )
                    Text(
                        text = matchReport.verdict,
                        fontWeight = FontWeight.SemiBold,
                        color = if (matchReport.obtainedGunas >= 18.0 && !matchReport.nadiDoshaPresent) Color(0xFF4ADE80) else VedicSaffron,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (matchReport.obtainedGunas / 36.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (matchReport.obtainedGunas >= 24.0) Color(0xFF22C55E) else if (matchReport.obtainedGunas >= 18.0) VedicGold else Color(0xFFEF4444),
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }
        }

        // 2. Critical Dosha Analysis (Mangal, Nadi, Bhakoot, Rajju)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CRITICAL DOSHA AUDIT",
                        style = MaterialTheme.typography.titleSmall,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))

                    // Mangal Dosha
                    val m = matchReport.mangalDosha
                    DoshaStatusRow("Mangal (Kuja) Dosha", if (m.isCancelled) "Cancelled / Safe" else if (m.isGroomManglik || m.isBrideManglik) "Afflicted" else "Free from Dosha", m.isCancelled || (!m.isGroomManglik && !m.isBrideManglik))
                    HorizontalDivider(color = Color(0xFF1E293B))

                    // Nadi Dosha
                    DoshaStatusRow("Nadi Dosha", if (matchReport.nadiDoshaPresent) "Present (Remedies Advised)" else "No Dosha (Auspicious)", !matchReport.nadiDoshaPresent)
                    HorizontalDivider(color = Color(0xFF1E293B))

                    // Bhakoot Dosha
                    DoshaStatusRow("Bhakoot Dosha", if (matchReport.bhakootDoshaPresent) "Present (6-8 / 2-12 Sign)" else "Harmonious (No Dosha)", !matchReport.bhakootDoshaPresent)
                    HorizontalDivider(color = Color(0xFF1E293B))

                    // Rajju Dosha
                    DoshaStatusRow("Rajju Dosha", if (matchReport.rajjuDoshaPresent) "Same Rajju (Caution)" else "Different Rajjus (Favorable)", !matchReport.rajjuDoshaPresent)
                }
            }
        }

        // 3. The 8 Kootas Detailed Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ASHTAKOOTA (8-FOLD) COMPATIBILITY BREAKDOWN",
                        style = MaterialTheme.typography.titleSmall,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))

                    matchReport.kootaScores.forEachIndexed { idx, koota ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(koota.kootaName, fontWeight = FontWeight.Bold, color = TextWhitePrimary, fontSize = 13.sp)
                                Text(koota.significance, fontSize = 11.sp, color = TextSilverSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (koota.isFavorable) Color(0xFF16A34A).copy(alpha = 0.2f) else Color(0xFFDC2626).copy(alpha = 0.2f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${koota.obtainedScore} / ${koota.maxScore}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (koota.isFavorable) Color(0xFF4ADE80) else Color(0xFFF87171)
                                )
                            }
                        }
                        if (idx < matchReport.kootaScores.size - 1) HorizontalDivider(color = Color(0xFF1E293B))
                    }
                }
            }
        }
    }
}

@Composable
fun DoshaStatusRow(name: String, status: String, isSafe: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontWeight = FontWeight.SemiBold, color = TextWhitePrimary, fontSize = 12.sp)
        Text(
            text = status,
            fontWeight = FontWeight.Bold,
            color = if (isSafe) Color(0xFF4ADE80) else Color(0xFFF87171),
            fontSize = 12.sp
        )
    }
}
