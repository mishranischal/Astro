package com.example.model

/**
 * 16 Classical Divisional Charts (Shodashavargas) as per BPHS
 */
enum class VargaType(
    val code: String,
    val division: Int,
    val sanskritName: String,
    val englishName: String,
    val signification: String,
    val vimsopakaPointsD16: Double // Out of 20 in Shodashavarga scheme
) {
    D1("D1", 1, "Rasi", "Physical Body & General Life", "Overall destiny, physical health, vitality, appearance", 3.5),
    D2("D2", 2, "Hora", "Wealth & Resources", "Accumulated wealth, financial prosperity, speech, family wealth", 1.0),
    D3("D3", 3, "Drekkana", "Siblings & Courage", "Brothers/sisters, valor, initiative, short travels, motivation", 1.0),
    D4("D4", 4, "Chaturthamsa", "Fortune & Property", "Fixed assets, real estate, mother's happiness, landed property", 0.5),
    D7("D7", 7, "Saptamsa", "Children & Progeny", "Children, grandchildren, legacy, creative fertility", 0.5),
    D9("D9", 9, "Navamsa", "Dharma & Marriage", "Spouse, married life, inner potential, soul destiny, fortune", 3.0),
    D10("D10", 10, "Dasamsa", "Career & Profession", "Vocation, public standing, honors, power, profession, achievements", 0.5),
    D12("D12", 12, "Dwadashamsa", "Parents & Ancestry", "Father, mother, ancestral lineage, past life karma inheritance", 0.5),
    D16("D16", 16, "Shodasamsa (Kalamsa)", "Vehicles & Comforts", "Vehicles, conveyances, material comforts, luxuries, pleasures", 2.0),
    D20("D20", 20, "Vimsamsa", "Spiritual Life & Upasana", "Spiritual inclination, meditation, mantras, divine grace, devotion", 1.0),
    D24("D24", 24, "Chaturvimsamsa (Siddhamsa)", "Learning & Higher Intellect", "Academic achievements, higher knowledge, intellect, skills", 0.5),
    D27("D27", 27, "Bhamsa (Saptavimsamsa)", "Strengths & Weaknesses", "General physical strength, subconscious weaknesses, stamina", 1.0),
    D30("D30", 30, "Trimsamsa", "Misfortunes & Arishta", "Evils, health afflictions, hidden dangers, karmic impediments", 1.0),
    D40("D40", 40, "Khavedamsa", "Auspicious & Inauspicious effects", "Subtle auspicious/inauspicious karmic influences on life", 0.5),
    D45("D45", 45, "Akshavedamsa", "General Well-being & All Matters", "Purity of character, general well-being across all domains", 0.5),
    D60("D60", 60, "Shashtiamsa", "Past Life Karma (Root Chart)", "Past life karmic seed, definitive confirmation of all life events", 4.0);

    companion object {
        val SHODASHAVARGA_LIST = values().toList()
        val SAPTAVARGA_LIST = listOf(D1, D2, D3, D7, D9, D12, D30)
        val DASHAVARGA_LIST = listOf(D1, D2, D3, D7, D9, D10, D12, D16, D30, D60)
    }
}

/**
 * Position of a planet or lagna in a specific Varga chart
 */
data class VargaPosition(
    val planet: Planet?, // null for Lagna
    val isLagna: Boolean = false,
    val rashi: Rashi,
    val degreeInSign: Double,
    val house: Int, // 1 to 12 from Varga Lagna
    val dignity: PlanetaryDignity,
    val vargaDeity: String = ""
)

/**
 * Complete Divisional Chart
 */
data class DivisionalChart(
    val vargaType: VargaType,
    val lagnaRashi: Rashi,
    val lagnaDegree: Double,
    val planetPositions: Map<Planet, VargaPosition>,
    val interpretation: String
)

/**
 * Vimsopaka Bala strength calculation (out of 20 points)
 */
data class VimsopakaScore(
    val planet: Planet,
    val score: Double, // 0.0 to 20.0
    val percentage: Double,
    val status: String // Excellent (>15), Good (10-15), Moderate (5-10), Weak (<5)
)
