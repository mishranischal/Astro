package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JyotishyaViewModel
import kotlinx.coroutines.launch

data class NavigationItem(
    val screen: AppScreen,
    val icon: ImageVector,
    val category: String
)

val NAV_ITEMS = listOf(
    NavigationItem(AppScreen.KUNDALI_OVERVIEW, Icons.Default.Home, "Core Charts"),
    NavigationItem(AppScreen.SHODASHAVARGA, Icons.Default.DateRange, "Core Charts"),
    NavigationItem(AppScreen.SHADBALA_ASHTAKAVARGA, Icons.Default.Star, "Core Charts"),
    NavigationItem(AppScreen.DASHA_SYSTEM, Icons.Default.Timeline, "Predictive"),
    NavigationItem(AppScreen.YOGAS_ANALYSIS, Icons.Default.CheckCircle, "Predictive"),
    NavigationItem(AppScreen.TRANSIT_GOCHARA, Icons.Default.Refresh, "Predictive"),
    NavigationItem(AppScreen.PANCHANGA_MUHURTA, Icons.Default.DateRange, "Panchanga"),
    NavigationItem(AppScreen.MATCHMAKING, Icons.Default.Favorite, "Specialized"),
    NavigationItem(AppScreen.TAJIKA_LONGEVITY, Icons.Default.Build, "Specialized"),
    NavigationItem(AppScreen.SARVATOBHADRA_ECLIPSES, Icons.Default.Place, "Specialized"),
    NavigationItem(AppScreen.REVERSE_SEARCH, Icons.Default.Search, "Tools & Texts"),
    NavigationItem(AppScreen.PROFILES, Icons.Default.Person, "Tools & Texts"),
    NavigationItem(AppScreen.ENCYCLOPEDIA, Icons.Default.MenuBook, "Tools & Texts")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppNavigation(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CosmicMidnightSurface,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmicDeepNavy)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "JYOTISHYA SHASTRA",
                        style = MaterialTheme.typography.titleLarge,
                        color = VedicGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "बृहत् पाराशर होरा शास्त्रम्",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF59E0B)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "High-Precision Authentic Vedic Astrology",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilverSecondary,
                        fontSize = 11.sp
                    )
                }

                HorizontalDivider(color = Color(0xFF334155))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    val grouped = NAV_ITEMS.groupBy { it.category }
                    grouped.forEach { (category, items) ->
                        Text(
                            text = category.uppercase(),
                            color = VedicGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                        )
                        items.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = null, tint = if (isSelected) VedicGold else TextSilverSecondary) },
                                label = {
                                    Column {
                                        Text(
                                            text = item.screen.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) TextWhitePrimary else TextSilverSecondary,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = item.screen.sanskritName,
                                            fontSize = 10.sp,
                                            color = Color(0xFFF59E0B)
                                        )
                                    }
                                },
                                selected = isSelected,
                                onClick = {
                                    viewModel.setScreen(item.screen)
                                    scope.launch { drawerState.close() }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = CosmicCardSurface,
                                    unselectedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentScreen.title,
                                color = VedicGold,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = currentScreen.sanskritName,
                                color = Color(0xFFF59E0B),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = VedicGold)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleChartStyle() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Toggle Style", tint = TextSilverSecondary)
                        }
                        IconButton(onClick = { viewModel.setScreen(AppScreen.PROFILES) }) {
                            Icon(Icons.Default.Person, contentDescription = "Profiles", tint = VedicGold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CosmicMidnightSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = CosmicMidnightSurface,
                    contentColor = VedicGold
                ) {
                    val bottomTabs = listOf(
                        AppScreen.KUNDALI_OVERVIEW to Pair(Icons.Default.Home, "Kundali"),
                        AppScreen.SHODASHAVARGA to Pair(Icons.Default.DateRange, "Vargas"),
                        AppScreen.DASHA_SYSTEM to Pair(Icons.Default.Timeline, "Dashas"),
                        AppScreen.PANCHANGA_MUHURTA to Pair(Icons.Default.DateRange, "Panchanga"),
                        AppScreen.YOGAS_ANALYSIS to Pair(Icons.Default.Star, "Yogas")
                    )

                    bottomTabs.forEach { (screen, info) ->
                        val (icon, label) = info
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setScreen(screen) },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VedicGold,
                                selectedTextColor = VedicGold,
                                unselectedIconColor = TextSilverSecondary,
                                unselectedTextColor = TextSilverSecondary,
                                indicatorColor = CosmicCardSurface
                            )
                        )
                    }
                }
            },
            containerColor = CosmicDeepNavy,
            modifier = modifier
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    AppScreen.KUNDALI_OVERVIEW -> KundaliOverviewScreen(viewModel)
                    AppScreen.SHODASHAVARGA -> ShodashavargaScreen(viewModel)
                    AppScreen.DASHA_SYSTEM -> DashaScreen(viewModel)
                    AppScreen.YOGAS_ANALYSIS -> YogasScreen(viewModel)
                    AppScreen.PANCHANGA_MUHURTA -> PanchangaScreen(viewModel)
                    AppScreen.SHADBALA_ASHTAKAVARGA -> PlanetaryStrengthScreen(viewModel)
                    AppScreen.TRANSIT_GOCHARA -> TransitScreen(viewModel)
                    AppScreen.MATCHMAKING -> MatchmakingScreen(viewModel)
                    AppScreen.TAJIKA_LONGEVITY -> AdvancedAstrologyScreen(viewModel)
                    AppScreen.SARVATOBHADRA_ECLIPSES -> AdvancedAstrologyScreen(viewModel)
                    AppScreen.REVERSE_SEARCH -> ReverseSearchScreen(viewModel)
                    AppScreen.PROFILES -> ProfilesScreen(viewModel)
                    AppScreen.ENCYCLOPEDIA -> EncyclopediaScreen(viewModel)
                }
            }
        }
    }
}
