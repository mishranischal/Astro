package com.example.model

/**
 * Tajika Saham (Sensitive Arabic / Tajika Point)
 */
data class SahamPoint(
    val id: String = "",
    val name: String,
    val sanskritName: String = name,
    val formulaDay: String = "",
    val formulaNight: String = "",
    val longitude: Double = 0.0,
    val rashi: Rashi,
    val degreeInRashi: Double = 0.0,
    val degreeInSign: Double = degreeInRashi,
    val houseFromLagna: Int = 1,
    val house: Int = houseFromLagna,
    val lord: Planet = rashi.lord,
    val meaning: String = "Activates in solar returns",
    val interpretation: String = meaning,
    val significance: String = meaning
)

/**
 * Tajika Varshaphala (Solar Return Chart)
 */
data class VarshaphalaChart(
    val yearNumber: Int,
    val targetYear: Int,
    val solarReturnDate: String,
    val solarReturnTime: String,
    val munthaRashi: Rashi,
    val munthaHouse: Int,
    val varsheshwara: Planet, // Year Lord
    val varsheshwaraStrength: String,
    val sahams: List<SahamPoint>,
    val tajikaYogas: List<String>,
    val yearForecastSummary: String
)

/**
 * Prashna Kundali (Horary Astrology Question Chart)
 */
data class PrashnaChart(
    val questionTopic: String,
    val questionTime: String,
    val prashnaLagna: Rashi,
    val moonRashi: Rashi,
    val moonNakshatra: Nakshatra,
    val karyeshwara: Planet, // Significator
    val lagnesha: Planet,
    val ithasalaFormed: Boolean,
    val outcomeVerdict: String, // Highly Favorable, Moderately Favorable, Unfavorable / Delayed
    val detailedAnswer: String
)

/**
 * Ayurdaya (Longevity Calculation) Results
 */
data class LongevityAnalysis(
    val pindayuYears: Double = 75.0,
    val jaiminiMethodYears: Double = 82.5,
    val naisargikaAyurYears: Double = 79.5,
    val amshayurYears: Double = 81.0,
    val averageLongevityYears: Double = 81.0,
    val kakshaVriddhiApplied: Boolean = true,
    val kakshaHrasaApplied: Boolean = false,
    val longevityCategory: String = "Poornayu (Long Life / 66 to 100+ Years)",
    val jaiminiLongevityCategory: String = longevityCategory,
    val stepsExplanation: List<String> = emptyList(),
    val methodBreakdown: String = "Composite Jaimini and Pindayu synthesis",
    val pindayuEstimateYears: Double = pindayuYears,
    val naisargikaAyurEstimateYears: Double = naisargikaAyurYears,
    val marakaPlanets: List<Planet> = emptyList(),
    val marakaHouses: List<Int> = listOf(2, 7),
    val classicalRemedies: List<String> = listOf("Maha Mrityunjaya Japa", "Rudrabhisheka")
)
