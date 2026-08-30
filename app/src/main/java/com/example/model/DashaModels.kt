package com.example.model

/**
 * Vimshottari Dasha Level
 */
enum class DashaLevel {
    MAHADASHA,
    ANTARDASHA,
    PRATYANTAR,
    SOOKSHMA
}

/**
 * Vimshottari Dasha Node
 */
data class VimshottariPeriod(
    val planet: Planet,
    val level: DashaLevel,
    val startDate: String, // YYYY-MM-DD
    val endDate: String,   // YYYY-MM-DD
    val startMillis: Long,
    val endMillis: Long,
    val durationYears: Double,
    val isRunning: Boolean = false,
    val subPeriods: List<VimshottariPeriod> = emptyList(),
    val predictionSignificance: String = ""
)

/**
 * Yogini Dasha Node (36 years total)
 */
data class YoginiPeriod(
    val name: String, // Mangala, Pingala, Dhanya, Bhramari, Bhadrika, Ulka, Siddha, Sankata
    val rulingPlanet: Planet,
    val durationYears: Int,
    val startDate: String,
    val endDate: String,
    val isRunning: Boolean = false,
    val nature: String // Auspicious / Inauspicious
)

/**
 * Jaimini Chara Dasha Node (Rashi based)
 */
data class CharaDashaPeriod(
    val rashi: Rashi,
    val durationYears: Int,
    val startDate: String,
    val endDate: String,
    val isRunning: Boolean = false,
    val arudhaLordsPresent: List<String> = emptyList()
)

/**
 * Ashtottari Dasha Node (108 years total)
 */
data class AshtottariPeriod(
    val planet: Planet,
    val durationYears: Int,
    val startDate: String,
    val endDate: String,
    val isRunning: Boolean = false
)

/**
 * Complete Dasha Report
 */
data class DashaReport(
    val startingMahadasha: Planet,
    val birthBalanceYears: Double,
    val vimshottariList: List<VimshottariPeriod>,
    val currentMahadasha: VimshottariPeriod?,
    val currentAntardasha: VimshottariPeriod?,
    val currentPratyantar: VimshottariPeriod?,
    val yoginiList: List<YoginiPeriod>,
    val charaList: List<CharaDashaPeriod>,
    val ashtottariList: List<AshtottariPeriod>
)
