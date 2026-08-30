package com.example.model

/**
 * Ashtakoota Milan (36 Guna Matching Item)
 */
data class KootaItem(
    val name: String,
    val sanskritName: String = name,
    val maxPoints: Double,
    val obtainedPoints: Double,
    val description: String = "",
    val groomValue: String = "",
    val brideValue: String = "",
    val analysis: String = "",
    val isFavorable: Boolean = obtainedPoints > 0.0
)

data class KootaResult(
    val kootaName: String,
    val maxScore: Double,
    val obtainedScore: Double,
    val significance: String,
    val isFavorable: Boolean,
    val detailedEffect: String
)

data class MangalDoshaReport(
    val isGroomManglik: Boolean,
    val isBrideManglik: Boolean,
    val groomDetails: String,
    val brideDetails: String,
    val isCancelled: Boolean,
    val cancellationReason: String
)

data class CompatibilityReport(
    val groomName: String,
    val brideName: String,
    val totalGunas: Double = 36.0,
    val obtainedGunas: Double,
    val verdict: String,
    val kootaScores: List<KootaResult> = emptyList(),
    val mangalDosha: MangalDoshaReport = MangalDoshaReport(false, false, "None", "None", true, "Harmonious"),
    val nadiDoshaPresent: Boolean = false,
    val bhakootDoshaPresent: Boolean = false,
    val rajjuDoshaPresent: Boolean = false,
    val recommendationNotes: String = ""
)

/**
 * Dosha Status in Kundali Matching
 */
data class MatchingDosha(
    val name: String,
    val isPresent: Boolean,
    val isCancelled: Boolean,
    val cancellationReason: String = "",
    val remedyText: String = ""
)

/**
 * Complete Ashtakoota Kundali Matching Report
 */
data class AshtakootaResult(
    val totalScore: Double,
    val maxScore: Double = 36.0,
    val percentage: Double,
    val recommendation: String,
    val kootas: List<KootaItem>,
    val nadiDosha: MatchingDosha,
    val bhakootDosha: MatchingDosha,
    val mangalDoshaGroom: MatchingDosha,
    val mangalDoshaBride: MatchingDosha,
    val rajjuDosha: MatchingDosha,
    val vedhaDosha: MatchingDosha,
    val overallSummary: String
)
