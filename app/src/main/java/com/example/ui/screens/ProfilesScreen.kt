package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.local.KundaliEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.JyotishyaViewModel

data class CityPreset(val name: String, val lat: Double, val lon: Double, val tz: Double)

val CITY_PRESETS = listOf(
    CityPreset("Varanasi, India", 25.3176, 82.9739, 5.5),
    CityPreset("New Delhi, India", 28.6139, 77.2090, 5.5),
    CityPreset("Mumbai, India", 19.0760, 72.8777, 5.5),
    CityPreset("Ayodhya, India", 26.7922, 82.1998, 5.5),
    CityPreset("Ujjain, India", 23.1765, 75.7885, 5.5),
    CityPreset("Bengaluru, India", 12.9716, 77.5946, 5.5),
    CityPreset("London, UK", 51.5074, -0.1278, 0.0),
    CityPreset("New York, USA", 40.7128, -74.0060, -5.0),
    CityPreset("Tokyo, Japan", 35.6762, 139.6503, 9.0),
    CityPreset("Dubai, UAE", 25.2048, 55.2708, 4.0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    viewModel: JyotishyaViewModel,
    modifier: Modifier = Modifier
) {
    val profiles by viewModel.savedProfiles.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Form inputs
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var birthDate by remember { mutableStateOf("1995-07-23") }
    var birthTime by remember { mutableStateOf("06:30") }
    var cityName by remember { mutableStateOf("Varanasi, India") }
    var latStr by remember { mutableStateOf("25.3176") }
    var lonStr by remember { mutableStateOf("82.9739") }
    var tzStr by remember { mutableStateOf("5.5") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
    ) {
        // 1. Header & Add Button
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
                        Column {
                            Text("SAVED HOROSCOPE PROFILES", color = VedicGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${profiles.size} offline charts stored in Room DB", color = TextSilverSecondary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VedicGold, contentColor = CosmicDeepNavy),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("New Chart", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 2. Saved Profiles List
        items(profiles) { profile ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.loadProfile(profile) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicMidnightSurface),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(profile.name, fontWeight = FontWeight.Bold, color = TextWhitePrimary, fontSize = 14.sp)
                        Text("${profile.birthDate} at ${profile.birthTime}", color = VedicGold, fontSize = 12.sp)
                        Text("${profile.cityName} (${profile.latitude}°N, ${profile.longitude}°E)", color = TextSilverSecondary, fontSize = 11.sp)
                    }

                    Row {
                        IconButton(onClick = { viewModel.loadProfile(profile) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Load Chart", tint = Color(0xFF4ADE80))
                        }
                        IconButton(onClick = { viewModel.deleteProfile(profile) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF87171))
                        }
                    }
                }
            }
        }
    }

    // Add Profile Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Generate New Vedic Chart", color = VedicGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { birthDate = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = birthTime,
                            onValueChange = { birthTime = it },
                            label = { Text("Time (HH:MM)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Text("Quick City Selection:", fontSize = 11.sp, color = VedicGold, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(CITY_PRESETS) { city ->
                            FilterChip(
                                selected = cityName == city.name,
                                onClick = {
                                    cityName = city.name
                                    latStr = "${city.lat}"
                                    lonStr = "${city.lon}"
                                    tzStr = "${city.tz}"
                                },
                                label = { Text(city.name.substringBefore(','), fontSize = 10.sp) },
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = cityName,
                        onValueChange = { cityName = it },
                        label = { Text("Birth Place") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = latStr,
                            onValueChange = { latStr = it },
                            label = { Text("Latitude") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lonStr,
                            onValueChange = { lonStr = it },
                            label = { Text("Longitude") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tzStr,
                            onValueChange = { tzStr = it },
                            label = { Text("Timezone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val lat = latStr.toDoubleOrNull() ?: 25.3176
                        val lon = lonStr.toDoubleOrNull() ?: 82.9739
                        val tz = tzStr.toDoubleOrNull() ?: 5.5
                        val personName = if (name.isNotBlank()) name else "Vedic Native"

                        viewModel.calculateNewChart(
                            name = personName,
                            gender = gender,
                            date = birthDate,
                            time = birthTime,
                            lat = lat,
                            lon = lon,
                            tz = tz,
                            place = cityName,
                            saveToDb = true
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VedicGold, contentColor = CosmicDeepNavy)
                ) {
                    Text("Calculate Kundali", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSilverSecondary)
                }
            },
            containerColor = CosmicMidnightSurface
        )
    }
}
