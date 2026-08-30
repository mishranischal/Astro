package com.example.model

/**
 * Tithi (Lunar Day)
 */
data class TithiInfo(
    val index: Int, // 1 to 30 (1-15 Shukla, 16-30 Krishna)
    val name: String,
    val paksha: String, // Shukla Paksha (Waxing), Krishna Paksha (Waning)
    val deity: String,
    val percentageElapsed: Double,
    val endTime: String,
    val nature: String // Nanda, Bhadra, Jaya, Rikta, Poorna
)

/**
 * Vara (Solar Day / Weekday)
 */
data class VaraInfo(
    val dayOfWeek: String,
    val sanskritName: String,
    val rulingPlanet: Planet,
    val element: String
) {
    val name: String get() = dayOfWeek
    val tattva: String get() = element
}

/**
 * Panchanga Yoga (Solilunar combination, 27 yogas)
 */
data class SolilunarYogaInfo(
    val index: Int, // 1 to 27
    val name: String,
    val meaning: String,
    val isAuspicious: Boolean,
    val percentageElapsed: Double
)

/**
 * Karana (Half of a Tithi, 11 types: 7 Chara + 4 Sthira)
 */
data class KaranaInfo(
    val index: Int,
    val name: String,
    val deity: String,
    val type: String, // Chara (Movable) or Sthira (Fixed)
    val isBhadra: Boolean = false // Vishti Karana = Bhadra
)

/**
 * Choghadiya Period (Day/Night 8 divisions)
 */
data class ChoghadiyaPeriod(
    val name: String, // Amrit, Shubh, Labh, Char, Rog, Kaal, Udveg
    val rulingPlanet: Planet,
    val nature: String, // Auspicious, Neutral, Inauspicious
    val startTime: String,
    val endTime: String,
    val isCurrentlyActive: Boolean = false
)

/**
 * Planetary Hora (Hour)
 */
data class HoraPeriod(
    val planet: Planet,
    val startTime: String,
    val endTime: String,
    val isCurrentlyActive: Boolean = false
)

/**
 * Muhurta Timings (Auspicious and Inauspicious Windows)
 */
data class MuhurtaTimings(
    val rahuKalam: Pair<String, String>,
    val yamagandam: Pair<String, String>,
    val gulikaKalam: Pair<String, String>,
    val abhijitMuhurta: Pair<String, String>,
    val brahmaMuhurta: Pair<String, String>,
    val durmuhurtham: List<Pair<String, String>>,
    val amritKalam: Pair<String, String>,
    val varjyam: Pair<String, String>
)

/**
 * Hindu Calendar Information (Samvat & Eras)
 */
data class HinduCalendarInfo(
    val vikramSamvat: Int,
    val shakaSamvat: Int,
    val kaliYugaYear: Int,
    val masaName: String, // Chaitra, Vaishakha...
    val rituName: String, // Vasanta, Grishma, Varsha, Sharad, Hemanta, Shishira
    val ayana: String,    // Uttarayana / Dakshinayana
    val lunarPhase: String, // New Moon, Waxing Crescent, Full Moon, etc.
    val moonIlluminationPercent: Double
)

/**
 * Hindu Festival Item
 */
data class HinduFestival(
    val name: String,
    val dateString: String,
    val tithi: String,
    val description: String,
    val significance: String
) {
    val dateSummary: String get() = dateString
}

/**
 * Complete Daily Panchanga Output
 */
data class DailyPanchanga(
    val date: String,
    val location: String,
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val tithi: TithiInfo,
    val vara: VaraInfo,
    val nakshatra: Nakshatra,
    val nakshatraPada: Int,
    val nakshatraElapsedPercent: Double,
    val yoga: SolilunarYogaInfo,
    val karana: KaranaInfo,
    val sunSign: Rashi,
    val moonSign: Rashi,
    val muhurta: MuhurtaTimings,
    val calendarInfo: HinduCalendarInfo,
    val choghadiyaDay: List<ChoghadiyaPeriod>,
    val choghadiyaNight: List<ChoghadiyaPeriod>,
    val horas: List<HoraPeriod>,
    val upcomingFestivals: List<HinduFestival>
)
