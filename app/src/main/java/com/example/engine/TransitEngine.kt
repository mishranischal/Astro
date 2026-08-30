package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Vedic Transit (Gochara) Engine with Vedha, Sade Sati & Multi-year Forecasting.
 */
object TransitEngine {

    // Classical Gochara Favorable houses from Moon & their Vedha obstruction houses
    private val SUN_GOCHARA = mapOf(3 to 9, 6 to 12, 10 to 4, 11 to 5)
    private val MOON_GOCHARA = mapOf(1 to 5, 3 to 9, 6 to 12, 7 to 2, 10 to 4, 11 to 8)
    private val MARS_GOCHARA = mapOf(3 to 12, 6 to 9, 11 to 5)
    private val MERCURY_GOCHARA = mapOf(2 to 5, 4 to 3, 6 to 12, 8 to 1, 10 to 9, 11 to 8)
    private val JUPITER_GOCHARA = mapOf(2 to 12, 5 to 4, 7 to 3, 9 to 10, 11 to 8)
    private val VENUS_GOCHARA = mapOf(1 to 8, 2 to 7, 3 to 1, 4 to 10, 5 to 9, 8 to 5, 9 to 11, 11 to 6, 12 to 3)
    private val SATURN_GOCHARA = mapOf(3 to 12, 6 to 9, 11 to 5)
    private val RAHU_GOCHARA = mapOf(3 to 12, 6 to 9, 11 to 5)

    fun calculateTransitReport(chart: BirthChart, transitTimestamp: Long = System.currentTimeMillis()): TransitReport {
        val natalMoon = chart.planets[Planet.MOON]
        val natalMoonRashi = natalMoon?.rashi ?: Rashi.ARIES

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = transitTimestamp }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val hr = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)

        val jd = AstroEngine.calculateJulianDay(y, m, d, hr, min, 0.0, 0.0)
        val ayan = AstroEngine.calculateAyanamsha(jd, AyanamshaSystem.LAHIRI)
        val rawPlanets = AstroEngine.calculateAllPlanets(jd, ayan)

        val gocharaResults = mutableListOf<GocharaResult>()
        for ((planet, pair) in rawPlanets) {
            val deg = pair.first
            val rIdx = (deg / 30.0).toInt() + 1
            val rashi = Rashi.fromIndex(rIdx)
            val houseFromMoon = ((rashi.index - natalMoonRashi.index + 12) % 12) + 1

            val favHouses = getFavorableHouses(planet)
            val isFav = favHouses.containsKey(houseFromMoon)
            val vedhaHouse = favHouses[houseFromMoon]
            val hasVedha = vedhaHouse != null && rawPlanets.values.any {
                val r = (it.first / 30.0).toInt() + 1
                val h = ((r - natalMoonRashi.index + 12) % 12) + 1
                h == vedhaHouse
            }

            val effect = if (isFav && !hasVedha) {
                "${planet.sanskritName} in House $houseFromMoon yields highly auspicious results, bestowal of honors, wealth, and spiritual bliss."
            } else if (isFav && hasVedha) {
                "${planet.sanskritName} transits benefic House $houseFromMoon but obstructed by planetary Vedha."
            } else {
                "${planet.sanskritName} in House $houseFromMoon requires mindful conduct, patience, and classical discipline."
            }

            gocharaResults.add(
                GocharaResult(
                    planet = planet,
                    transitRashi = rashi,
                    houseFromMoon = houseFromMoon,
                    isFavorable = isFav && !hasVedha,
                    hasVedha = hasVedha,
                    classicalEffect = effect
                )
            )
        }

        // Sade Sati check
        val saturnPos = rawPlanets[Planet.SATURN]
        val saturnRashiIdx = ((saturnPos?.first ?: 0.0) / 30.0).toInt() + 1
        val saturnRashi = Rashi.fromIndex(saturnRashiIdx)
        val dist = (saturnRashi.index - natalMoonRashi.index + 12) % 12
        val (isActive, phase, desc) = when (dist) {
            11 -> Triple(true, "Rising Phase (12th from Moon)", "Saturn transiting 12th house from Janma Rashi. Expenditure, travel, and introspection.")
            0 -> Triple(true, "Peak Phase (Janma Shani)", "Saturn conjunct Janma Rashi Moon. High responsibility, patience, emotional maturity.")
            1 -> Triple(true, "Setting Phase (2nd from Moon)", "Saturn transiting 2nd house from Moon. Financial consolidation and family stability.")
            else -> Triple(false, "Not Active", "Saturn is in a harmonious Gochara position from Janma Rashi.")
        }

        val sadeSatiInfo = SadeSatiInfo(
            isActive = isActive,
            currentPhase = phase,
            description = desc,
            recommendedRemedies = listOf(
                "Chant Hanuman Chalisa or Shani Stotram on Saturdays",
                "Light sesame oil (Til) lamp during evening twilight",
                "Practice charity and selfless service (Seva)"
            )
        )

        val timeline = listOf(
            TimelineEvent(2025, Planet.JUPITER, Rashi.MITHUNA, "Jupiter transits Gemini — Auspicious career growth, intellect, and higher learning."),
            TimelineEvent(2025, Planet.SATURN, Rashi.MEENA, "Saturn enters Pisces — Deep spiritual restructuring and karmic fruition."),
            TimelineEvent(2026, Planet.JUPITER, Rashi.KARKATA, "Jupiter enters Exalted Cancer (Pushkara) — Supreme divine benevolence."),
            TimelineEvent(2026, Planet.RAHU, Rashi.KUMBHA, "Rahu enters Aquarius — Technological breakthroughs and visionary pursuits."),
            TimelineEvent(2027, Planet.SATURN, Rashi.MESHA, "Saturn enters Aries — Energetic initiative and disciplined enterprise.")
        )

        return TransitReport(
            sadeSati = sadeSatiInfo,
            gocharaResults = gocharaResults,
            multiYearTimeline = timeline
        )
    }

    private fun getFavorableHouses(planet: Planet): Map<Int, Int> {
        return when (planet) {
            Planet.SUN -> SUN_GOCHARA
            Planet.MOON -> MOON_GOCHARA
            Planet.MARS -> MARS_GOCHARA
            Planet.MERCURY -> MERCURY_GOCHARA
            Planet.JUPITER -> JUPITER_GOCHARA
            Planet.VENUS -> VENUS_GOCHARA
            Planet.SATURN -> SATURN_GOCHARA
            Planet.RAHU, Planet.KETU -> RAHU_GOCHARA
        }
    }
}
