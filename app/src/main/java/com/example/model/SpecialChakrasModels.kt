package com.example.model

/**
 * Sarvatobhadra Chakra 9x9 Grid Cell
 */
data class SBCGridCell(
    val row: Int, // 0 to 8
    val col: Int, // 0 to 8
    val content: String, // Nakshatra, Swara (Vowel), Varna (Consonant), Tithi, Rashi
    val contentType: String, // NAKSHATRA, SWARA, VARNA, TITHI, RASHI, CORNER
    val planetsPresent: List<Planet> = emptyList(),
    val hasFrontVedha: Boolean = false,
    val hasRightVedha: Boolean = false,
    val hasLeftVedha: Boolean = false,
    val isAuspiciousVedha: Boolean = true
)

/**
 * Sarvatobhadra Chakra Analysis Output
 */
data class SarvatobhadraAnalysis(
    val cells: List<SBCGridCell>,
    val janmaNakshatraVedha: List<String>,
    val karmaNakshatraVedha: List<String>,
    val adhanaNakshatraVedha: List<String>,
    val vinashakaNakshatraVedha: List<String>,
    val manasaNakshatraVedha: List<String>,
    val swaraVedhas: List<String>,
    val rashiVedhas: List<String>,
    val classicalSignificance: String
)

/**
 * Reverse Astrology Query Criteria
 */
data class ReverseSearchQuery(
    val startYear: Int = 1950,
    val endYear: Int = 2050,
    val requiredJupiterRashi: Rashi? = null,
    val requiredSaturnRashi: Rashi? = null,
    val targetPlanetPositions: Map<Planet, Rashi> = emptyMap(),
    val targetHouses: Map<Planet, Int> = emptyMap(),
    val targetNakshatras: Map<Planet, Nakshatra> = emptyMap(),
    val requireYogas: List<String> = emptyList(),
    val requireRetrograde: List<Planet> = emptyList()
)

/**
 * Matching Date Found in Reverse Search
 */
data class ReverseSearchResult(
    val dateString: String,
    val planetarySummary: String,
    val matchedCriteriaCount: Int,
    val totalCriteriaCount: Int,
    val matchingYogas: List<String>,
    val notes: String
)

/**
 * Solar & Lunar Eclipse Calculator Results
 */
data class EclipseEvent(
    val dateString: String,
    val type: String,
    val nakshatra: Nakshatra,
    val rashi: Rashi,
    val peakTimeUtc: String,
    val durationMinutes: Int,
    val vedicSignificance: String,
    val affectedSigns: List<Rashi>
)

/**
 * Solstice and Equinox Calculation Results
 */
data class SolsticeEquinoxEvent(
    val year: Int,
    val eventName: String = "Solar Cardinal Gateway",
    val date: String = "$year-03-20",
    val timeUTC: String = "08:46 UTC",
    val sayanaSign: String = "Aries 0°",
    val nirayanaSign: String = "Pisces 6°",
    val vernalEquinox: String = "$year-03-20 08:46 UTC (Sun enters Sayana Aries)",
    val summerSolstice: String = "$year-06-21 02:24 UTC (Dakshinayana begins)",
    val autumnalEquinox: String = "$year-09-22 18:05 UTC (Sun enters Sayana Libra)",
    val winterSolstice: String = "$year-12-21 14:50 UTC (Uttarayana begins)",
    val spiritualSignificance: String = "Sacred transition points marking spiritual renewal and planetary harmony."
)
