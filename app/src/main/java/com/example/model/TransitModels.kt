package com.example.model

/**
 * Transit Position of a Planet (Gochara)
 */
data class TransitPlanet(
    val planet: Planet,
    val rashi: Rashi,
    val degreeInRashi: Double,
    val nakshatra: Nakshatra,
    val pada: Int,
    val isRetrograde: Boolean,
    val houseFromNatalMoon: Int,
    val houseFromNatalLagna: Int,
    val isFavorableFromMoon: Boolean,
    val vedhaPlanet: Planet?, // Obstructing planet if Vedha occurs
    val vedhaOccurred: Boolean,
    val ashtakavargaPointsInSign: Int, // SAV points of transit sign
    val prediction: String
)

/**
 * Sade Sati Phase Status
 */
enum class SadeSatiPhase(val displayName: String, val description: String) {
    NOT_ACTIVE("No Sade Sati", "Saturn is not transiting the 12th, 1st, or 2nd house from natal Moon."),
    RISING_PHASE("Rising Phase (Aroha / 1st Cycle)", "Saturn is in 12th house from natal Moon. Mental expenditure, relocations, inner restructuring."),
    PEAK_PHASE("Peak Phase (Janma Shani / 2nd Cycle)", "Saturn is transiting natal Moon sign directly. Deep endurance, psychological purification, high responsibilities."),
    SETTING_PHASE("Setting Phase (Avaroha / 3rd Cycle)", "Saturn is in 2nd house from natal Moon. Financial stabilization, family realignment, results fructification."),
    KANTAKA_SHANI("Kantaka Shani (4th / 8th House)", "Saturn transiting 4th (Ardhastama) or 8th (Ashtama Shani) house from Moon. Requires discipline in health and home."),
    ASHTAMA_SHANI("Ashtama Shani (8th House)", "Saturn in 8th house from Moon. Deep transformational karma, longevity discipline, legacy restructuring.")
}

data class SadeSatiReport(
    val currentPhase: SadeSatiPhase,
    val saturnCurrentRashi: Rashi,
    val natalMoonRashi: Rashi,
    val currentCycleNumber: Int, // 1st, 2nd, or 3rd in life
    val phaseStartDate: String,
    val phaseEndDate: String,
    val totalPeriodDescription: String,
    val remediesAndGuidance: List<String>
)

data class SadeSatiInfo(
    val isActive: Boolean,
    val currentPhase: String,
    val description: String,
    val recommendedRemedies: List<String> = emptyList()
)

data class GocharaResult(
    val planet: Planet,
    val transitRashi: Rashi,
    val houseFromMoon: Int,
    val isFavorable: Boolean,
    val hasVedha: Boolean,
    val classicalEffect: String
)

data class TimelineEvent(
    val year: Int,
    val planet: Planet,
    val transitSign: Rashi,
    val majorInfluence: String
)

data class TransitReport(
    val sadeSati: SadeSatiInfo,
    val gocharaResults: List<GocharaResult>,
    val multiYearTimeline: List<TimelineEvent>
)

/**
 * Long-term Transit Prediction Interval
 */
data class TransitForecastEvent(
    val year: Int,
    val dateRange: String,
    val planet: Planet,
    val transitRashi: Rashi,
    val targetHouseFromMoon: Int,
    val eventTitle: String,
    val impactLevel: String, // Major Positive, Transformational, Mixed, Challenging
    val detailedEffect: String
)

data class LongTermPredictionDashboard(
    val oneYearHighlights: List<TransitForecastEvent>,
    val fiveYearTrends: List<TransitForecastEvent>,
    val twentyFiveYearMilestones: List<TransitForecastEvent>,
    val hundredYearEphemerisKeypoints: List<TransitForecastEvent>
)

/**
 * Real-time Transit Overview
 */
data class TransitOverview(
    val calculationTimestamp: Long,
    val dateString: String,
    val transitPlanets: Map<Planet, TransitPlanet>,
    val sadeSati: SadeSatiReport,
    val favorableTransitsCount: Int,
    val challengingTransitsCount: Int,
    val overallGocharaSummary: String
)
