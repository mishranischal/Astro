package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Format degree decimal to standard astrological Deg° Min' Sec" format
 */
fun formatDMS(degree: Double): String {
    val norm = ((degree % 360.0) + 360.0) % 360.0
    val d = norm.toInt()
    val m = ((norm - d) * 60.0).toInt()
    val s = ((((norm - d) * 60.0) - m) * 60.0).toInt()
    return String.format("%02d° %02d' %02d\"", d, m, s)
}

/**
 * Vedic Grahas (Planets) including Nodes and Shadow Planets
 */
enum class Planet(
    val id: String,
    val sanskritName: String,
    val englishName: String,
    val symbol: String,
    val shortName: String,
    val ownSigns: List<Int>, // 1-based Rashi index (1 = Mesha)
    val exaltationSign: Int,
    val exaltationDegree: Double,
    val debilitationSign: Int,
    val debilitationDegree: Double,
    val moolatrikonaSign: Int,
    val moolatrikonaRange: ClosedFloatingPointRange<Double>,
    val naturalFriends: List<String>,
    val naturalEnemies: List<String>,
    val naturalNeutrals: List<String>,
    val karakaName: String
) {
    SUN(
        "sun", "Surya", "Sun", "☉", "Su",
        listOf(5), 1, 10.0, 7, 10.0, 5, 0.0..20.0,
        listOf("moon", "mars", "jupiter"), listOf("venus", "saturn"), listOf("mercury"),
        "Atmakaraka (Soul)"
    ),
    MOON(
        "moon", "Chandra", "Moon", "☽", "Mo",
        listOf(4), 2, 3.0, 8, 3.0, 2, 3.0..30.0,
        listOf("sun", "mercury"), listOf(), listOf("mars", "jupiter", "venus", "saturn"),
        "Matrukaraka (Mind/Mother)"
    ),
    MARS(
        "mars", "Mangala", "Mars", "♂", "Ma",
        listOf(1, 8), 10, 28.0, 4, 28.0, 1, 0.0..12.0,
        listOf("sun", "moon", "jupiter"), listOf("mercury"), listOf("venus", "saturn"),
        "Bhatrukaraka (Courage/Siblings)"
    ),
    MERCURY(
        "mercury", "Budha", "Mercury", "☿", "Me",
        listOf(3, 6), 6, 15.0, 12, 15.0, 6, 15.0..20.0,
        listOf("sun", "venus"), listOf("moon"), listOf("mars", "jupiter", "saturn"),
        "Jnana (Intellect/Speech)"
    ),
    JUPITER(
        "jupiter", "Guru", "Jupiter", "♃", "Ju",
        listOf(9, 12), 4, 5.0, 10, 5.0, 9, 0.0..10.0,
        listOf("sun", "moon", "mars"), listOf("mercury", "venus"), listOf("saturn"),
        "Putrakaraka (Wisdom/Guru/Children)"
    ),
    VENUS(
        "venus", "Shukra", "Venus", "♀", "Ve",
        listOf(2, 7), 12, 27.0, 6, 27.0, 7, 0.0..15.0,
        listOf("mercury", "saturn"), listOf("sun", "moon"), listOf("mars", "jupiter"),
        "Kalatrakaraka (Love/Spouse/Beauty)"
    ),
    SATURN(
        "saturn", "Shani", "Saturn", "♄", "Sa",
        listOf(10, 11), 7, 20.0, 1, 20.0, 11, 0.0..20.0,
        listOf("mercury", "venus"), listOf("sun", "moon", "mars"), listOf("jupiter"),
        "Ayushkaraka (Longevity/Karma)"
    ),
    RAHU(
        "rahu", "Rahu", "North Node", "☊", "Ra",
        listOf(11), 2, 20.0, 8, 20.0, 11, 0.0..30.0,
        listOf("mercury", "venus", "saturn"), listOf("sun", "moon", "mars"), listOf("jupiter"),
        "Maya (Desire/Illusion)"
    ),
    KETU(
        "ketu", "Ketu", "South Node", "☋", "Ke",
        listOf(8), 8, 20.0, 2, 20.0, 8, 0.0..30.0,
        listOf("mars", "venus", "saturn"), listOf("sun", "moon"), listOf("mercury", "jupiter"),
        "Mokshakaraka (Liberation/Detachment)"
    );

    companion object {
        fun fromId(id: String): Planet? = values().firstOrNull { it.id.equals(id, ignoreCase = true) }
        val MAIN_SEVEN = listOf(SUN, MOON, MARS, MERCURY, JUPITER, VENUS, SATURN)
        val NAVAGRAHA = listOf(SUN, MOON, MARS, MERCURY, JUPITER, VENUS, SATURN, RAHU, KETU)
    }
}

/**
 * 12 Rashis (Zodiac Signs)
 */
enum class Rashi(
    val index: Int, // 1-based (1=Mesha, 12=Meena)
    val sanskritName: String,
    val englishName: String,
    val symbol: String,
    val shortName: String,
    val element: String, // Fire, Earth, Air, Water
    val modality: String, // Chara (Movable), Sthira (Fixed), Dwiswabhava (Dual)
    val lord: Planet,
    val gender: String, // Male (Odd), Female (Even)
    val direction: String,
    val bodyPart: String
) {
    ARIES(1, "Mesha", "Aries", "♈", "Ar", "Fire", "Chara (Movable)", Planet.MARS, "Male", "East", "Head"),
    TAURUS(2, "Vrishabha", "Taurus", "♉", "Ta", "Earth", "Sthira (Fixed)", Planet.VENUS, "Female", "South", "Face/Neck"),
    GEMINI(3, "Mithuna", "Gemini", "♊", "Ge", "Air", "Dwiswabhava (Dual)", Planet.MERCURY, "Male", "West", "Shoulders/Arms"),
    CANCER(4, "Karka", "Cancer", "♋", "Ca", "Water", "Chara (Movable)", Planet.MOON, "Female", "North", "Chest/Heart"),
    LEO(5, "Simha", "Leo", "♌", "Le", "Fire", "Sthira (Fixed)", Planet.SUN, "Male", "East", "Upper Abdomen/Stomach"),
    VIRGO(6, "Kanya", "Virgo", "♍", "Vi", "Earth", "Dwiswabhava (Dual)", Planet.MERCURY, "Female", "South", "Digestive System"),
    LIBRA(7, "Tula", "Libra", "♎", "Li", "Air", "Chara (Movable)", Planet.VENUS, "Male", "West", "Lower Abdomen/Kidneys"),
    SCORPIO(8, "Vrishchika", "Scorpio", "♏", "Sc", "Water", "Sthira (Fixed)", Planet.MARS, "Female", "North", "Pelvic Region/Genitals"),
    SAGITTARIUS(9, "Dhanu", "Sagittarius", "♐", "Sg", "Fire", "Dwiswabhava (Dual)", Planet.JUPITER, "Male", "East", "Thighs"),
    CAPRICORN(10, "Makara", "Capricorn", "♑", "Cp", "Earth", "Chara (Movable)", Planet.SATURN, "Female", "South", "Knees"),
    AQUARIUS(11, "Kumbha", "Aquarius", "♒", "Aq", "Air", "Sthira (Fixed)", Planet.SATURN, "Male", "West", "Calves/Ankles"),
    PISCES(12, "Meena", "Pisces", "♓", "Pi", "Water", "Dwiswabhava (Dual)", Planet.JUPITER, "Female", "North", "Feet");

    companion object {
        val MESHA = ARIES
        val VRISHABHA = TAURUS
        val MITHUNA = GEMINI
        val KARKATA = CANCER
        val SIMHA = LEO
        val KANYA = VIRGO
        val TULA = LIBRA
        val VRISCHIKA = SCORPIO
        val DHANU = SAGITTARIUS
        val MAKARA = CAPRICORN
        val KUMBHA = AQUARIUS
        val MEENA = PISCES

        fun fromIndex(index: Int): Rashi {
            val adjusted = ((index - 1) % 12 + 12) % 12 + 1
            return values()[adjusted - 1]
        }
    }
}

/**
 * 27 Nakshatras with lord, deity, yoni, gana, nadi
 */
enum class Nakshatra(
    val index: Int, // 1 to 27
    val sanskritName: String,
    val englishName: String,
    val shortName: String,
    val lord: Planet,
    val deity: String,
    val symbol: String,
    val gana: String, // Deva, Manushya, Rakshasa
    val yoni: String, // Animal
    val nadi: String, // Adi, Madhya, Antya
    val varna: String, // Brahmin, Kshatriya, Vaishya, Shudra
    val vashya: String,
    val startingDegree: Double // in Nirayana 0-360
) {
    ASHWINI(1, "Ashwini", "Ashwini", "Ash", Planet.KETU, "Ashvini Kumaras", "Horse's Head", "Deva", "Horse", "Adi", "Vaishya", "Chatushpada", 0.0),
    BHARANI(2, "Bharani", "Bharani", "Bha", Planet.VENUS, "Yama", "Yoni / Triangle", "Manushya", "Elephant", "Madhya", "Mleccha", "Manava", 13.3333333),
    KRITTIKA(3, "Krittika", "Krittika", "Kri", Planet.SUN, "Agni", "Flame / Razor", "Rakshasa", "Sheep", "Antya", "Brahmin", "Chatushpada", 26.6666667),
    ROHINI(4, "Rohini", "Rohini", "Roh", Planet.MOON, "Brahma / Prajapati", "Chariot / Temple", "Manushya", "Serpent", "Antya", "Shudra", "Chatushpada", 40.0),
    MRIGASHIRA(5, "Mrigashira", "Mrigashira", "Mri", Planet.MARS, "Soma / Moon", "Deer's Head", "Deva", "Serpent", "Madhya", "Vaishya", "Chatushpada", 53.3333333),
    ARDRA(6, "Ardra", "Ardra", "Ard", Planet.RAHU, "Rudra", "Teardrop / Jewel", "Manushya", "Dog", "Adi", "Shudra", "Manava", 66.6666667),
    PUNARVASU(7, "Punarvasu", "Punarvasu", "Pun", Planet.JUPITER, "Aditi", "Bow & Quiver", "Deva", "Cat", "Adi", "Vaishya", "Manava", 80.0),
    PUSHYA(8, "Pushya", "Pushya", "Pus", Planet.SATURN, "Brihaspati", "Lotus / Cow's Udder", "Deva", "Sheep", "Madhya", "Kshatriya", "Jalachara", 93.3333333),
    ASHLESHA(9, "Ashlesha", "Ashlesha", "Asl", Planet.MERCURY, "Nagas / Serpents", "Coiled Serpent", "Rakshasa", "Cat", "Antya", "Mleccha", "Jalachara", 106.6666667),
    MAGHA(10, "Magha", "Magha", "Mag", Planet.KETU, "Pitris (Ancestors)", "Royal Throne / Palanquin", "Rakshasa", "Rat", "Antya", "Shudra", "Chatushpada", 120.0),
    PURVA_PHALGUNI(11, "Purva Phalguni", "Purva Phalguni", "PPh", Planet.VENUS, "Bhaga", "Front legs of bed", "Manushya", "Rat", "Madhya", "Brahmin", "Manava", 133.3333333),
    UTTARA_PHALGUNI(12, "Uttara Phalguni", "Uttara Phalguni", "UPh", Planet.SUN, "Aryaman", "Back legs of bed", "Manushya", "Cow", "Adi", "Kshatriya", "Manava", 146.6666667),
    HASTA(13, "Hasta", "Hasta", "Has", Planet.MOON, "Savitr (Sun)", "Open Hand / Fist", "Deva", "Buffalo", "Adi", "Vaishya", "Manava", 160.0),
    CHITRA(14, "Chitra", "Chitra", "Chi", Planet.MARS, "Tvashtar / Vishvakarma", "Bright Jewel / Pearl", "Rakshasa", "Tiger", "Madhya", "Shudra", "Manava", 173.3333333),
    SWATI(15, "Swati", "Swati", "Swa", Planet.RAHU, "Vayu (Wind)", "Shoot of plant / Coral", "Deva", "Buffalo", "Antya", "Mleccha", "Manava", 186.6666667),
    VISHAKHA(16, "Vishakha", "Vishakha", "Vis", Planet.JUPITER, "Indra & Agni", "Triumphal Arch", "Rakshasa", "Tiger", "Antya", "Brahmin", "Manava", 200.0),
    ANURADHA(17, "Anuradha", "Anuradha", "Anu", Planet.SATURN, "Mitra", "Lotus / Staff", "Deva", "Deer", "Madhya", "Kshatriya", "Keeta", 213.3333333),
    JYESHTHA(18, "Jyeshtha", "Jyeshtha", "Jye", Planet.MERCURY, "Indra", "Earring / Umbrella", "Rakshasa", "Deer", "Adi", "Vaishya", "Keeta", 226.6666667),
    MULA(19, "Mula", "Mula", "Mul", Planet.KETU, "Nirriti", "Tied bunch of roots", "Rakshasa", "Dog", "Adi", "Shudra", "Chatushpada", 240.0),
    PURVA_ASHADHA(20, "Purva Ashadha", "Purva Ashadha", "PAs", Planet.VENUS, "Apas (Water deity)", "Elephant tusk / Winnowing fan", "Manushya", "Monkey", "Madhya", "Brahmin", "Chatushpada", 253.3333333),
    UTTARA_ASHADHA(21, "Uttara Ashadha", "Uttara Ashadha", "UAs", Planet.SUN, "Vishvadevas", "Planks of bed / Tusk", "Manushya", "Mongoose", "Antya", "Kshatriya", "Chatushpada", 266.6666667),
    SHRAVANA(22, "Shravana", "Shravana", "Shr", Planet.MOON, "Vishnu", "Three Footprints / Ear", "Deva", "Monkey", "Antya", "Mleccha", "Jalachara", 280.0),
    DHANISHTHA(23, "Dhanishta", "Dhanishta", "Dha", Planet.MARS, "Eight Vasus", "Drum (Mridanga) / Flute", "Rakshasa", "Lion", "Madhya", "Shudra", "Jalachara", 293.3333333),
    SHATABHISHA(24, "Shatabhisha", "Shatabhisha", "Sha", Planet.RAHU, "Varuna", "100 Physicians / Circle", "Rakshasa", "Horse", "Adi", "Vaishya", "Manava", 306.6666667),
    PURVA_BHADRAPADA(25, "Purva Bhadrapada", "Purva Bhadrapada", "PBh", Planet.JUPITER, "Aja Ekapada", "Front legs of funeral cot", "Manushya", "Lion", "Adi", "Brahmin", "Manava", 320.0),
    UTTARA_BHADRAPADA(26, "Uttara Bhadrapada", "Uttara Bhadrapada", "UBh", Planet.SATURN, "Ahirbudhnya", "Back legs of funeral cot", "Manushya", "Cow", "Madhya", "Kshatriya", "Jalachara", 333.3333333),
    REVATI(27, "Revati", "Revati", "Rev", Planet.MERCURY, "Pushan", "Pair of Fish / Drum", "Deva", "Elephant", "Antya", "Shudra", "Jalachara", 346.6666667);

    companion object {
        fun fromLongitude(longitude: Double): Pair<Nakshatra, Int> {
            val normalized = ((longitude % 360.0) + 360.0) % 360.0
            val nakshatraSpan = 360.0 / 27.0 // 13.3333333 degrees
            val padaSpan = nakshatraSpan / 4.0 // 3.3333333 degrees
            
            val nakIndex = (normalized / nakshatraSpan).toInt().coerceIn(0, 26)
            val degreeIntoNak = normalized - (nakIndex * nakshatraSpan)
            val pada = (degreeIntoNak / padaSpan).toInt().coerceIn(0, 3) + 1
            return Pair(values()[nakIndex], pada)
        }
    }
}

/**
 * Dignity of Planet in Chart
 */
enum class PlanetaryDignity(val label: String, val scoreWeight: Double) {
    EXALTED("Uchha (Exalted)", 1.0),
    MOOLATRIKONA("Moolatrikona", 0.85),
    OWN_SIGN("Swakshetra (Own)", 0.75),
    GREAT_FRIEND("Adhi Mitra (Great Friend)", 0.6),
    FRIEND("Mitra (Friend)", 0.45),
    NEUTRAL("Sama (Neutral)", 0.3),
    ENEMY("Shatru (Enemy)", 0.15),
    GREAT_ENEMY("Adhi Shatru (Great Enemy)", 0.05),
    DEBILITATED("Neecha (Debilitated)", 0.0)
}

/**
 * Planetary position result with all astrological parameters
 */
data class PlanetaryPosition(
    val planet: Planet,
    val longitude: Double, // 0-360 Nirayana
    val speed: Double = 0.0, // degrees per day (+ is direct, - is retrograde)
    val isRetrograde: Boolean = false,
    val rashi: Rashi,
    val degreeInRashi: Double,
    val nakshatra: Nakshatra,
    val pada: Int,
    val house: Int, // 1 to 12 from Lagna
    val dignity: PlanetaryDignity,
    val isCombust: Boolean = false,
    val distanceToSun: Double = 0.0,
    val isWarLoser: Boolean = false, // Graha Yuddha
    val isWarWinner: Boolean = false,
    val aspectedBy: List<Planet> = emptyList(),
    val aspectingHouses: List<Int> = emptyList()
) {
    val degreeInSign: Double get() = degreeInRashi
    val nakshatraPada: Int get() = pada
}

/**
 * House (Bhava) details
 */
data class BhavaDetail(
    val houseNumber: Int, // 1 to 12
    val rashi: Rashi,
    val startDegree: Double,
    val cuspDegree: Double,
    val endDegree: Double,
    val lord: Planet,
    val occupants: List<Planet>,
    val aspectingPlanets: List<Planet>,
    val significations: String
)

/**
 * Special Lagnas & Upagrahas
 */
data class SpecialLagnas(
    val janmaLagna: Double,
    val bhavaLagna: Double,
    val horaLagna: Double,
    val ghatiLagna: Double,
    val induLagna: Double,
    val upapadaLagna: Double,
    val arudhaLagna: Double,
    val varnadaLagna: Double,
    val pranapadaLagna: Double,
    val shriLagna: Double,
    val gulika: Double,
    val mandi: Double
) {
    val sriLagna: Double get() = shriLagna
}

/**
 * Ayanamsha system selection
 */
enum class AyanamshaSystem(val displayName: String, val description: String) {
    LAHIRI("Lahiri (Chitrapaksha)", "Official Indian Govt standard; Spica fixed at 180°"),
    RAMAN("B.V. Raman", "Traditional South Indian calculation by Dr. B.V. Raman"),
    KP("Krishnamurti Paddhati (KP)", "Standard for KP Astrology stellar system"),
    YUKTESHWAR("Sri Yukteshwar", "Holy Science precessional cycle by Sri Yukteshwar"),
    TRUE_CITRA("True Citra", "Astronomically precise dynamic Spica centering"),
    SURYA_SIDDHANTA("Surya Siddhanta", "Traditional canonical Vedic astronomical treatise")
}

/**
 * Chart display style
 */
enum class ChartStyle(val displayName: String) {
    NORTH_INDIAN("North Indian (Diamond)"),
    SOUTH_INDIAN("South Indian (Box)"),
    EAST_INDIAN("East Indian (Bangla)"),
    WESTERN_CIRCULAR("Circular (Western Wheel)")
}

/**
 * Complete Birth Chart (Kundali) Data Object
 */
data class BirthChart(
    val id: String = "",
    val name: String,
    val birthDate: String, // YYYY-MM-DD
    val birthTime: String, // HH:mm
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double,
    val locationName: String,
    val gender: String = "Not Specified",
    val ayanamshaSystem: AyanamshaSystem = AyanamshaSystem.LAHIRI,
    val ayanamshaValue: Double,
    val julianDay: Double,
    val ascendantDegree: Double,
    val ascendantRashi: Rashi,
    val ascendantNakshatra: Nakshatra,
    val ascendantPada: Int,
    val planets: Map<Planet, PlanetaryPosition>,
    val houses: List<BhavaDetail>,
    val specialLagnas: SpecialLagnas,
    val isDayBirth: Boolean
) {
    val personName: String get() = name
    val birthPlace: String get() = locationName
    val timezoneOffset: Double get() = timezoneOffsetHours
}

/**
 * Classical Shloka Model
 */
data class ClassicalShloka(
    val id: String,
    val title: String = "",
    val textSanskrit: String,
    val transliteration: String,
    val sourceText: String,
    val chapterVerse: String = "",
    val englishTranslation: String,
    val astrologicalPrinciple: String
) {
    val sanskritText: String get() = textSanskrit
}

/**
 * Encyclopedia Topic Model
 */
data class EncyclopediaTopic(
    val id: String,
    val title: String,
    val sanskritName: String,
    val category: String,
    val summary: String,
    val detailedContent: String,
    val keyRules: List<String> = emptyList()
) {
    val sanskritSubtitle: String get() = sanskritName
    val contentSummary: String get() = summary
}

/**
 * Sarvatobhadra Chakra Models
 */
data class SBCSquare(
    val index: Int = 0,
    val row: Int = 0,
    val col: Int = 0,
    val letter: String = "",
    val contentType: String = "Outer",
    val contentValue: String = letter,
    val occupyingPlanets: List<String> = emptyList(),
    val hasVedha: Boolean = false,
    val vedhaType: String? = null,
    val rashi: Rashi? = null,
    val nakshatra: Nakshatra? = null,
    val tithi: String? = null,
    val planetsPresent: List<Planet> = emptyList()
)

data class SBCVedha(
    val transitPlanet: Planet,
    val vedhaType: String,
    val targetNakshatraOrVarna: String,
    val isBenefic: Boolean = true,
    val effect: String = "",
    val afflictedNakshatra: Nakshatra = Nakshatra.ASHWINI,
    val afflictedLetter: String = "",
    val impact: String = effect
) {
    val planet: Planet get() = transitPlanet
}

data class SarvatobhadraReport(
    val grid: List<List<SBCSquare>> = emptyList(),
    val squares: List<SBCSquare> = emptyList(),
    val activeVedhas: List<SBCVedha> = emptyList(),
    val specialNakshatraStatus: Map<String, Nakshatra> = emptyMap(),
    val overallAuspiciousness: String = "Auspicious",
    val generalInterpretation: String = overallAuspiciousness
)

/**
 * Historical Reverse Search Result
 */
data class SearchResultDate(
    val dateString: String,
    val julianDay: Double = 0.0,
    val matchScorePercent: Double = 100.0,
    val planetSignSummary: String = "",
    val matchedPlanetsCount: Int = 4,
    val totalSearchedPlanets: Int = 4,
    val matchingConditions: List<String> = listOf(planetSignSummary),
    val significance: String = "High planetary harmonic alignment"
)

/**
 * Astronomical Eclipse Model
 */
data class AstronomicalEclipse(
    val eventName: String,
    val date: String,
    val timeUTC: String,
    val type: String,
    val nakshatra: Nakshatra = Nakshatra.ASHWINI,
    val rashi: Rashi = Rashi.ARIES,
    val obscurityPercent: Double = 100.0,
    val visibilityRegions: String = "Global",
    val visibility: String = visibilityRegions,
    val religiousSignificance: String = "Spiritual japa, meditation, and charity recommended."
)

/**
 * Equinox and Solstice Event Model
 */
data class EquinoxSolsticeReport(
    val year: Int,
    val eventName: String = "Solar Gateway",
    val date: String = "$year-03-20",
    val timeUTC: String = "08:46 UTC",
    val sayanaSign: String = "Aries 0°",
    val nirayanaSign: String = "Pisces 6°",
    val vernalEquinox: String = "$year-03-20 08:46 UTC",
    val summerSolstice: String = "$year-06-21 02:24 UTC",
    val autumnalEquinox: String = "$year-09-22 18:05 UTC",
    val winterSolstice: String = "$year-12-21 14:50 UTC"
)

