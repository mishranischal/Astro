package com.example.model

/**
 * Detailed Sthana Bala (Positional Strength) Breakdown in Virupas
 */
data class SthanaBala(
    val uchhaBala: Double = 0.0,
    val saptavargajaBala: Double = 0.0,
    val ojayugmarasyamsaBala: Double = 0.0,
    val kendradiBala: Double = 0.0,
    val drekkanaBala: Double = 0.0,
    val totalSthanaBala: Double = 0.0
)

/**
 * Sixfold Shadbala Component for UI & Computation
 */
data class ShadbalaComponent(
    val planet: Planet,
    val sthanaBala: Double,
    val digBala: Double,
    val kaalaBala: Double,
    val chestaBala: Double,
    val naisargikaBala: Double,
    val drikBala: Double,
    val totalShashtiamsas: Double,
    val totalRupas: Double,
    val requiredRupas: Double,
    val strengthRatio: Double,
    val rank: Int = 1
)

/**
 * Ashtakavarga Single Planet Table
 */
data class AshtakavargaTable(
    val planet: Planet?,
    val houseBindus: List<Int> = emptyList(), // 12 values
    val rashiBindus: Map<Rashi, Int> = emptyMap(),
    val totalPoints: Int = 0,
    val trikonaShodhana: List<Int> = emptyList(),
    val ekadhipatyaShodhana: List<Int> = emptyList(),
    val sodhyaPinda: Int = 0
)

/**
 * Comprehensive Ashtakavarga System Report
 */
data class AshtakavargaReport(
    val sarvashtakavarga: AshtakavargaTable,
    val bhinnashtakavargas: Map<Planet, AshtakavargaTable>,
    val strongestSigns: List<Rashi> = emptyList(),
    val weakestSigns: List<Rashi> = emptyList(),
    val samudaayaAshtakavargaAnalysis: String = "Sarvashtakavarga distribution reflects auspicious karmic balance."
)

/**
 * Complete 6-Fold Shadbala Breakdown for a Planet
 */
data class ShadbalaPlanetBreakdown(
    val planet: Planet,
    val sthanaBala: SthanaBala,
    val digBala: Double,
    val kaalaBala: Double,
    val cheshtaBala: Double,
    val naisargikaBala: Double,
    val drikBala: Double,
    val totalVirupas: Double,
    val totalRupas: Double,
    val minimumRequirementRupas: Double,
    val strengthRatio: Double,
    val relativeRank: Int
)

/**
 * Complete Ashtakavarga Data
 */
data class AshtakavargaData(
    val bhinnashtakavarga: Map<Planet, List<Int>>,
    val lagnaBAV: List<Int>,
    val sarvashtakavarga: List<Int>,
    val trikonaShodhana: Map<Planet, List<Int>>,
    val ekadhipatyaShodhana: Map<Planet, List<Int>>,
    val shodhyaPinda: Map<Planet, Int>
)

/**
 * Full Planetary Strength & Ashtakavarga Report
 */
data class PlanetaryStrengthReport(
    val shadbalaMap: Map<Planet, ShadbalaPlanetBreakdown> = emptyMap(),
    val ashtakavarga: AshtakavargaData = AshtakavargaData(emptyMap(), emptyList(), emptyList(), emptyMap(), emptyMap(), emptyMap())
)
