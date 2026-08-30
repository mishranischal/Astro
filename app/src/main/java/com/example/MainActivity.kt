package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JyotishyaViewModel

data class NavCategory(
    val screen: AppScreen,
    val icon: ImageVector,
    val shortLabel: String
)

val PRIMARY_NAV_ITEMS = listOf(
    NavCategory(AppScreen.KUNDALI_OVERVIEW, Icons.Default.AutoAwesome, "Kundali"),
    NavCategory(AppScreen.SHODASHAVARGA, Icons.Default.GridView, "Vargas"),
    NavCategory(AppScreen.DASHA_SYSTEM, Icons.Default.Timeline, "Dashas"),
    NavCategory(AppScreen.YOGAS_ANALYSIS, Icons.Default.Stars, "Yogas"),
    NavCategory(AppScreen.SHADBALA_ASHTAKAVARGA, Icons.Default.BarChart, "Strength"),
    NavCategory(AppScreen.PANCHANGA_MUHURTA, Icons.Default.CalendarMonth, "Panchang"),
    NavCategory(AppScreen.TRANSIT_GOCHARA, Icons.Default.Public, "Transits"),
    NavCategory(AppScreen.MATCHMAKING, Icons.Default.Favorite, "Matching")
)

val SECONDARY_NAV_ITEMS = listOf(
    NavCategory(AppScreen.TAJIKA_LONGEVITY, Icons.Default.HourglassBottom, "Tajika & Ayur"),
    NavCategory(AppScreen.SARVATOBHADRA_ECLIPSES, Icons.Default.Shield, "Chakras"),
    NavCategory(AppScreen.REVERSE_SEARCH, Icons.Default.Search, "Cosmic Search"),
    NavCategory(AppScreen.ENCYCLOPEDIA, Icons.Default.MenuBook, "Shlokas"),
    NavCategory(AppScreen.PROFILES, Icons.Default.AccountCircle, "Profiles")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: JyotishyaViewModel = viewModel()
                JyotishyaMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JyotishyaMainApp(viewModel: JyotishyaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(
                            text = "Jyotishya",
                            fontWeight = FontWeight.Bold,
                            color = VedicGold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• ${currentScreen.title}",
                            color = TextSilverSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleChartStyle() },
                        modifier = Modifier.testTag("toggle_chart_style_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch North/South Indian Chart",
                            tint = VedicGold
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setScreen(AppScreen.PROFILES) },
                        modifier = Modifier.testTag("top_app_bar_profiles")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Saved Charts",
                            tint = if (currentScreen == AppScreen.PROFILES) VedicGold else TextSilverSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CosmicMidnightSurface)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(CosmicMidnightSurface)) {
                // Secondary quick chips for specialized modules
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SECONDARY_NAV_ITEMS.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setScreen(item.screen) },
                            label = { Text(item.shortLabel, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isSelected) CosmicDeepNavy else VedicGold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VedicGold,
                                selectedLabelColor = CosmicDeepNavy,
                                containerColor = CosmicCardSurface,
                                labelColor = TextSilverSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = CosmicCardSurface,
                                selectedBorderColor = VedicGold
                            )
                        )
                    }
                }

                // Primary Navigation Bar
                NavigationBar(
                    containerColor = CosmicMidnightSurface,
                    contentColor = VedicGold,
                    tonalElevation = 8.dp
                ) {
                    PRIMARY_NAV_ITEMS.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setScreen(item.screen) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.shortLabel,
                                    tint = if (isSelected) VedicGold else TextSilverSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = item.shortLabel,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) VedicGold else TextSilverSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = CosmicCardSurface
                            ),
                            modifier = Modifier.testTag("nav_${item.shortLabel.lowercase()}")
                        )
                    }
                }
            }
        },
        containerColor = CosmicDeepNavy
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.KUNDALI_OVERVIEW -> KundaliOverviewScreen(viewModel = viewModel)
                AppScreen.SHODASHAVARGA -> ShodashavargaScreen(viewModel = viewModel)
                AppScreen.DASHA_SYSTEM -> DashaScreen(viewModel = viewModel)
                AppScreen.YOGAS_ANALYSIS -> YogasScreen(viewModel = viewModel)
                AppScreen.SHADBALA_ASHTAKAVARGA -> PlanetaryStrengthScreen(viewModel = viewModel)
                AppScreen.PANCHANGA_MUHURTA -> PanchangaScreen(viewModel = viewModel)
                AppScreen.TRANSIT_GOCHARA -> TransitScreen(viewModel = viewModel)
                AppScreen.MATCHMAKING -> MatchmakingScreen(viewModel = viewModel)
                AppScreen.TAJIKA_LONGEVITY -> AdvancedAstrologyScreen(viewModel = viewModel)
                AppScreen.SARVATOBHADRA_ECLIPSES -> AdvancedAstrologyScreen(viewModel = viewModel)
                AppScreen.REVERSE_SEARCH -> ReverseSearchScreen(viewModel = viewModel)
                AppScreen.PROFILES -> ProfilesScreen(viewModel = viewModel)
                AppScreen.ENCYCLOPEDIA -> EncyclopediaScreen(viewModel = viewModel)
            }
        }
    }
}
