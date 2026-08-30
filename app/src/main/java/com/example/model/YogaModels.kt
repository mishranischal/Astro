package com.example.model

/**
 * Vedic Astrology Yoga Category
 */
enum class YogaCategory(val displayName: String, val description: String) {
    MAHAPURUSHA_YOGA("Pancha Mahapurusha Yogas", "Great human yogas formed by Mars, Mercury, Jupiter, Venus, Saturn in Kendras in own/exaltation sign"),
    PANCHA_MAHAPURUSHA("Pancha Mahapurusha Yogas", "Great human yogas formed by Mars, Mercury, Jupiter, Venus, Saturn in Kendras in own/exaltation sign"),
    RAJA_YOGA("Raja Yogas", "Royal combinations conferring leadership, fame, honor, authority and high achievement"),
    DHANA_YOGA("Dhana Yogas", "Wealth combinations linking lords of 2nd, 5th, 9th, and 11th houses"),
    NEECHA_BHANGA("Neecha Bhanga Raja Yogas", "Debilitation cancellation yogas transforming hardship into spectacular success"),
    VIPAREETA_RAJA("Vipareeta Raja Yogas", "Trik (6th, 8th, 12th) lords exchanging houses, bringing triumph out of adversity"),
    SOLAR_LUNAR("Solar & Lunar Yogas", "Planets in 2nd, 12th from Moon/Sun (Sunaphaa, Anaphaa, Dhurdhura, Vesi, Vosi, Ubhayachari)"),
    GAJAKESARI("Gajakesari & Auspicious", "Jupiter in Kendra from Moon/Lagna, conferring wisdom, eloquence, and prosperity"),
    KEMADRUMA("Kemadruma & Bhangas", "Moon isolation and subsequent cancellation checks"),
    KAAL_SARPA("Kaal Sarpa & Kaal Amrita", "All 7 planets hemmed between Rahu and Ketu axis across 12 classical types"),
    MISCELLANEOUS("Special Classical Yogas", "Budhaditya, Saraswati, Amala, Parvata, Kahala, Chamara, Shankha, etc.")
}

/**
 * Classical Yoga Definition & Detected Instance
 */
data class YogaResult(
    val id: String,
    val name: String,
    val sanskritName: String,
    val category: YogaCategory,
    val description: String,
    val participatingPlanets: List<Planet> = emptyList(),
    val participatingHouses: List<Int> = emptyList(),
    val sourceText: String = "Brihat Parashara Hora Shastra",
    val classicalSource: String = sourceText,
    val classicalEffect: String = "",
    val beneficialEffects: String = classicalEffect,
    val shlokaReference: String = "",
    val strengthPercent: Int = 85,
    val strengthScore: Double = strengthPercent.toDouble(),
    val isFormed: Boolean = true
)
