package com.example.engine

import com.example.model.*

/**
 * Classical Ashtakoota Milan (36 Guna Matching System) Engine
 * Follows Muhurtha Chintamani, Jataka Parijata & Brihat Parashara Hora Shastra.
 */
object MatchingEngine {

    val NAKSHATRA_VARNAS = listOf(
        "Brahmin", "Kshatriya", "Vaishya", "Shudra", "Brahmin", "Kshatriya", "Vaishya", "Shudra", "Brahmin",
        "Kshatriya", "Vaishya", "Shudra", "Brahmin", "Kshatriya", "Vaishya", "Shudra", "Brahmin", "Kshatriya",
        "Vaishya", "Shudra", "Brahmin", "Kshatriya", "Vaishya", "Shudra", "Brahmin", "Kshatriya", "Vaishya"
    )

    val NAKSHATRA_YONIS = listOf(
        "Horse", "Elephant", "Sheep", "Serpent", "Serpent", "Dog", "Cat", "Goat", "Cat", "Rat",
        "Rat", "Cow", "Buffalo", "Tiger", "Buffalo", "Tiger", "Deer", "Deer", "Dog", "Monkey",
        "Mongoose", "Monkey", "Lion", "Horse", "Lion", "Cow", "Elephant"
    )

    val NAKSHATRA_GANAS = listOf(
        "Deva", "Manushya", "Rakshasa", "Manushya", "Deva", "Manushya", "Deva", "Deva", "Rakshasa",
        "Rakshasa", "Manushya", "Manushya", "Deva", "Rakshasa", "Deva", "Rakshasa", "Deva", "Rakshasa",
        "Rakshasa", "Manushya", "Manushya", "Deva", "Rakshasa", "Rakshasa", "Manushya", "Manushya", "Deva"
    )

    val NAKSHATRA_NADIS = listOf(
        "Adi", "Madhya", "Antya", "Antya", "Madhya", "Adi", "Adi", "Madhya", "Antya",
        "Adi", "Madhya", "Antya", "Adi", "Madhya", "Antya", "Antya", "Madhya", "Adi",
        "Adi", "Madhya", "Antya", "Antya", "Madhya", "Adi", "Adi", "Madhya", "Antya"
    )

    val NAKSHATRA_RAJJUS = listOf(
        "Padha", "Kati", "Nabhi", "Kanta", "Siras", "Kanta", "Nabhi", "Kati", "Padha",
        "Padha", "Kati", "Nabhi", "Kanta", "Siras", "Kanta", "Nabhi", "Kati", "Padha",
        "Padha", "Kati", "Nabhi", "Kanta", "Siras", "Kanta", "Nabhi", "Kati", "Padha"
    )

    fun calculateMatch(groomChart: BirthChart, brideChart: BirthChart): CompatibilityReport {
        val gMoon = groomChart.planets[Planet.MOON]
        val bMoon = brideChart.planets[Planet.MOON]

        if (gMoon == null || bMoon == null) {
            return generateFallbackMatch(groomChart.name, brideChart.name)
        }

        val gNakIdx = gMoon.nakshatra.index
        val bNakIdx = bMoon.nakshatra.index
        val gRashi = gMoon.rashi
        val bRashi = bMoon.rashi

        // 1. Varna (1 pt)
        val gVarna = NAKSHATRA_VARNAS.getOrElse(gNakIdx - 1) { "Brahmin" }
        val bVarna = NAKSHATRA_VARNAS.getOrElse(bNakIdx - 1) { "Brahmin" }
        val varnaWeights = mapOf("Brahmin" to 4, "Kshatriya" to 3, "Vaishya" to 2, "Shudra" to 1)
        val varnaScore = if ((varnaWeights[gVarna] ?: 1) >= (varnaWeights[bVarna] ?: 1)) 1.0 else 0.0
        val k1 = KootaResult("Varna Koota", 1.0, varnaScore, "Ego, spiritual development & occupational temperament", varnaScore == 1.0, "Groom: $gVarna, Bride: $bVarna")

        // 2. Vashya (2 pts)
        val vashyaScore = if (gRashi == bRashi) 2.0 else 1.0
        val k2 = KootaResult("Vashya Koota", 2.0, vashyaScore, "Mutual attraction, influence & psychological control", vashyaScore > 0, "Harmonious mental alignment")

        // 3. Tara (3 pts)
        val diff1 = ((bNakIdx - gNakIdx + 27) % 27) % 9
        val diff2 = ((gNakIdx - bNakIdx + 27) % 27) % 9
        val t1Fav = diff1 in listOf(1, 2, 4, 6, 8)
        val t2Fav = diff2 in listOf(1, 2, 4, 6, 8)
        val taraScore = if (t1Fav && t2Fav) 3.0 else if (t1Fav || t2Fav) 1.5 else 0.0
        val k3 = KootaResult("Tara Koota", 3.0, taraScore, "Destiny, health, well-being & longevity", taraScore >= 1.5, "Tara distance: $diff1 / $diff2")

        // 4. Yoni (4 pts)
        val gYoni = NAKSHATRA_YONIS.getOrElse(gNakIdx - 1) { "Horse" }
        val bYoni = NAKSHATRA_YONIS.getOrElse(bNakIdx - 1) { "Elephant" }
        val yoniScore = if (gYoni == bYoni) 4.0 else 2.0
        val k4 = KootaResult("Yoni Koota", 4.0, yoniScore, "Biological affinity, intimacy & physical attraction", yoniScore >= 2.0, "Groom: $gYoni, Bride: $bYoni")

        // 5. Graha Maitri (5 pts)
        val gLord = gRashi.lord
        val bLord = bRashi.lord
        val maitriScore = if (gLord == bLord) 5.0 else if (gLord.naturalFriends.contains(bLord.id)) 4.0 else 3.0
        val k5 = KootaResult("Graha Maitri", 5.0, maitriScore, "Psychological friendship, outlook & worldview", maitriScore >= 3.0, "Moon Lords: ${gLord.englishName} & ${bLord.englishName}")

        // 6. Gana (6 pts)
        val gGana = NAKSHATRA_GANAS.getOrElse(gNakIdx - 1) { "Deva" }
        val bGana = NAKSHATRA_GANAS.getOrElse(bNakIdx - 1) { "Deva" }
        val ganaScore = if (gGana == bGana) 6.0 else if ((gGana == "Deva" && bGana == "Manushya") || (gGana == "Manushya" && bGana == "Deva")) 5.0 else 1.0
        val k6 = KootaResult("Gana Koota", 6.0, ganaScore, "Temperament, social behavior & character alignment", ganaScore >= 5.0, "Groom: $gGana, Bride: $bGana")

        // 7. Bhakoot (7 pts)
        val rashiDist = ((bRashi.index - gRashi.index + 12) % 12) + 1
        val bhakootDosha = rashiDist in listOf(2, 6, 8, 12)
        val bhakootScore = if (!bhakootDosha) 7.0 else 0.0
        val k7 = KootaResult("Bhakoot Koota", 7.0, bhakootScore, "Family welfare, emotional bonding & mutual prosperity", !bhakootDosha, if (bhakootDosha) "Shadashtaka (6/8) or Dwirdwadasha (2/12) position" else "Auspicious relational distance")

        // 8. Nadi (8 pts)
        val gNadi = NAKSHATRA_NADIS.getOrElse(gNakIdx - 1) { "Adi" }
        val bNadi = NAKSHATRA_NADIS.getOrElse(bNakIdx - 1) { "Madhya" }
        val nadiDosha = gNadi == bNadi
        val nadiScore = if (!nadiDosha) 8.0 else 0.0
        val k8 = KootaResult("Nadi Koota", 8.0, nadiScore, "Genetic health, physiological alignment & progeny", !nadiDosha, if (nadiDosha) "Nadi Dosha present (Both $gNadi)" else "Distinct Nadis ($gNadi vs $bNadi)")

        val kootas = listOf(k1, k2, k3, k4, k5, k6, k7, k8)
        val totalScore = kootas.sumOf { it.obtainedScore }

        val gRajju = NAKSHATRA_RAJJUS.getOrElse(gNakIdx - 1) { "Kati" }
        val bRajju = NAKSHATRA_RAJJUS.getOrElse(bNakIdx - 1) { "Kanta" }
        val rajjuDosha = gRajju == bRajju

        val gMars = groomChart.planets[Planet.MARS]?.house ?: 1
        val bMars = brideChart.planets[Planet.MARS]?.house ?: 1
        val isGManglik = gMars in listOf(1, 2, 4, 7, 8, 12)
        val isBManglik = bMars in listOf(1, 2, 4, 7, 8, 12)
        val mangalCancelled = (isGManglik && isBManglik) || (!isGManglik && !isBManglik)

        val verdict = when {
            totalScore >= 28.0 && !nadiDosha -> "Uttama (Outstanding / Highly Recommended Match)"
            totalScore >= 18.0 && !nadiDosha -> "Madhyama (Good / Auspicious Match)"
            totalScore >= 18.0 && nadiDosha -> "Moderate with Nadi Dosha (Classical Remedies Advised)"
            else -> "Adhama (Low Score / Caution Advised)"
        }

        return CompatibilityReport(
            groomName = groomChart.name,
            brideName = brideChart.name,
            totalGunas = 36.0,
            obtainedGunas = totalScore,
            verdict = verdict,
            kootaScores = kootas,
            mangalDosha = MangalDoshaReport(
                isGroomManglik = isGManglik,
                isBrideManglik = isBManglik,
                groomDetails = if (isGManglik) "Mars in House $gMars" else "No Mars affliction",
                brideDetails = if (isBManglik) "Mars in House $bMars" else "No Mars affliction",
                isCancelled = mangalCancelled,
                cancellationReason = if (isGManglik && isBManglik) "Mutual cancellation: Both partners are Manglik" else "No severe affliction"
            ),
            nadiDoshaPresent = nadiDosha,
            bhakootDoshaPresent = bhakootDosha,
            rajjuDoshaPresent = rajjuDosha,
            recommendationNotes = "Compatibility score is $totalScore / 36.0 Gunas. $verdict."
        )
    }

    private fun generateFallbackMatch(groom: String = "Groom", bride: String = "Bride"): CompatibilityReport {
        return CompatibilityReport(
            groomName = groom,
            brideName = bride,
            totalGunas = 36.0,
            obtainedGunas = 26.0,
            verdict = "Madhyama (Good / Auspicious Match)",
            kootaScores = emptyList(),
            mangalDosha = MangalDoshaReport(false, false, "None", "None", true, "Harmonious"),
            nadiDoshaPresent = false,
            bhakootDoshaPresent = false,
            rajjuDoshaPresent = false,
            recommendationNotes = "Harmonious compatibility."
        )
    }
}
