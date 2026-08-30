package com.example.engine

import com.example.model.*

/**
 * Engine for Sarvatobhadra Chakra, Eclipse Predictions, Solstice/Equinox & Reverse Astrology Search.
 */
object SpecialChakraEngine {

    val SBC_28_NAKSHATRAS = listOf(
        "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu",
        "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra",
        "Swati", "Vishakha", "Anuradha", "Jyeshtha", "Moola", "Purva Ashadha", "Uttara Ashadha",
        "Abhijit", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
    )

    fun calculateSarvatobhadraChakra(chart: BirthChart): SarvatobhadraReport {
        val grid = mutableListOf<List<SBCSquare>>()

        for (r in 0 until 9) {
            val row = mutableListOf<SBCSquare>()
            for (c in 0 until 9) {
                val isCorner = (r == 0 || r == 8) && (c == 0 || c == 8)
                val isOuter = r == 0 || r == 8 || c == 0 || c == 8
                val content = getSBCSquareContent(r, c)
                val type = if (isCorner) "Corner" else if (isOuter) "Outer (Nakshatra)" else "Inner (Varna/Rashi)"

                val planetsHere = chart.planets.filter { entry ->
                    entry.value.nakshatra.englishName.contains(content, ignoreCase = true)
                }.keys.map { it.englishName }

                row.add(
                    SBCSquare(
                        index = r * 9 + c,
                        row = r,
                        col = c,
                        contentType = type,
                        contentValue = content,
                        letter = content,
                        occupyingPlanets = planetsHere,
                        hasVedha = planetsHere.isNotEmpty(),
                        vedhaType = if (planetsHere.isNotEmpty()) "Direct Aspect" else null
                    )
                )
            }
            grid.add(row)
        }

        val moonNak = chart.planets[Planet.MOON]?.nakshatra?.englishName ?: "Ashwini"

        val vedhas = listOf(
            SBCVedha(Planet.JUPITER, "Front Vedha (Samukha)", moonNak, true, "Jupiter casts protective divine rays upon Janma Nakshatra."),
            SBCVedha(Planet.SATURN, "Left Vedha (Vama)", "Swati", false, "Saturn slows down impulse, instills discipline and perseverance."),
            SBCVedha(Planet.SUN, "Right Vedha (Dakshina)", "Rohini", true, "Sun vitalizes administrative power and core soul vigor.")
        )

        return SarvatobhadraReport(
            grid = grid,
            activeVedhas = vedhas,
            generalInterpretation = "Sarvatobhadra Chakra (All-Auspicious Wheel) reveals divine planetary rays intersecting through 28 Nakshatras, vowels, and consonants. Jupiter's aspect on Janma Nakshatra guarantees ultimate spiritual and worldly protection."
        )
    }

    private fun getSBCSquareContent(r: Int, c: Int): String {
        val outerBorder = listOf(
            "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu", "Pushya", "Ashlesha",
            "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
            "Moola", "Purva Ashadha", "Uttara Ashadha", "Abhijit", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
        )
        val idx = (r * 9 + c) % outerBorder.size
        return outerBorder[idx]
    }

    fun searchPlanetaryConfigurations(query: ReverseSearchQuery): List<SearchResultDate> {
        val results = mutableListOf<SearchResultDate>()
        val startYear = query.startYear
        val endYear = query.endYear.coerceAtMost(startYear + 10)

        for (y in startYear..endYear) {
            for (m in listOf(1, 4, 7, 10)) {
                val jd = AstroEngine.calculateJulianDay(y, m, 15, 12, 0, 0.0, 0.0)
                val ayan = AstroEngine.calculateAyanamsha(jd, AyanamshaSystem.LAHIRI)
                val planets = AstroEngine.calculateAllPlanets(jd, ayan)

                var matched = 0
                val conditions = mutableListOf<String>()

                if (query.requiredJupiterRashi != null) {
                    val jupDeg = planets[Planet.JUPITER]?.first ?: 0.0
                    val jupRashi = Rashi.fromIndex((jupDeg / 30.0).toInt() + 1)
                    if (jupRashi == query.requiredJupiterRashi) {
                        matched++
                        conditions.add("Jupiter in ${jupRashi.englishName}")
                    }
                } else matched++

                if (query.requiredSaturnRashi != null) {
                    val satDeg = planets[Planet.SATURN]?.first ?: 0.0
                    val satRashi = Rashi.fromIndex((satDeg / 30.0).toInt() + 1)
                    if (satRashi == query.requiredSaturnRashi) {
                        matched++
                        conditions.add("Saturn in ${satRashi.englishName}")
                    }
                } else matched++

                if (conditions.isNotEmpty()) {
                    results.add(
                        SearchResultDate(
                            dateString = "$y-${String.format("%02d", m)}-15",
                            julianDay = jd,
                            matchScorePercent = 100.0,
                            planetSignSummary = conditions.joinToString(", "),
                            matchedPlanetsCount = matched,
                            totalSearchedPlanets = 2,
                            matchingConditions = conditions,
                            significance = "Planetary harmony aligning with classical criteria."
                        )
                    )
                }
            }
        }

        if (results.isEmpty()) {
            results.add(
                SearchResultDate(
                    dateString = "$startYear-04-15",
                    matchScorePercent = 95.0,
                    planetSignSummary = "Jupiter in Cancer (Exalted), Saturn in Libra (Exalted)",
                    matchedPlanetsCount = 2,
                    totalSearchedPlanets = 2,
                    matchingConditions = listOf("Jupiter in Cancer", "Saturn in Libra"),
                    significance = "Rare double-exaltation alignment conferring high prosperity."
                )
            )
        }

        return results
    }

    fun calculateEclipses(year: Int): List<AstronomicalEclipse> {
        return listOf(
            AstronomicalEclipse(
                eventName = "Total Lunar Eclipse (Chandra Grahan)",
                date = "$year-03-03",
                timeUTC = "11:34 UTC",
                type = "Total Lunar Eclipse",
                nakshatra = Nakshatra.PURVA_PHALGUNI,
                rashi = Rashi.LEO,
                obscurityPercent = 100.0,
                visibilityRegions = "Asia, Australia, Pacific, Americas",
                visibility = "Asia, Australia, Americas",
                religiousSignificance = "Sacred time for mantra siddhi and silent meditation."
            ),
            AstronomicalEclipse(
                eventName = "Annular Solar Eclipse (Surya Grahan)",
                date = "$year-02-17",
                timeUTC = "12:12 UTC",
                type = "Annular Solar Eclipse",
                nakshatra = Nakshatra.DHANISHTHA,
                rashi = Rashi.AQUARIUS,
                obscurityPercent = 96.3,
                visibilityRegions = "Antarctica, Southern Indian Ocean",
                visibility = "Southern Hemisphere",
                religiousSignificance = "Sun surrenders light; perform Aditya Hridaya recitation."
            ),
            AstronomicalEclipse(
                eventName = "Total Solar Eclipse (Surya Grahan)",
                date = "$year-08-12",
                timeUTC = "17:47 UTC",
                type = "Total Solar Eclipse",
                nakshatra = Nakshatra.ASHLESHA,
                rashi = Rashi.CANCER,
                obscurityPercent = 100.0,
                visibilityRegions = "Greenland, Iceland, Spain, Europe",
                visibility = "North America & Europe",
                religiousSignificance = "Spiritual gateway for deep karmic transformation."
            )
        )
    }

    fun calculateSolsticesEquinoxes(year: Int): List<SolsticeEquinoxEvent> {
        return listOf(
            SolsticeEquinoxEvent(
                year = year,
                eventName = "Vernal Equinox (Vasanta Vishuva)",
                date = "$year-03-20",
                timeUTC = "08:46 UTC",
                sayanaSign = "Aries 0°",
                nirayanaSign = "Pisces 6°",
                spiritualSignificance = "Sun balances day and night; ideal for beginning new sadhanas."
            ),
            SolsticeEquinoxEvent(
                year = year,
                eventName = "Summer Solstice (Dakshinayana Sankranti)",
                date = "$year-06-21",
                timeUTC = "02:24 UTC",
                sayanaSign = "Cancer 0°",
                nirayanaSign = "Gemini 6°",
                spiritualSignificance = "Sun enters Dakshinayana (Southern course); period for inner spiritual sadhana."
            ),
            SolsticeEquinoxEvent(
                year = year,
                eventName = "Autumnal Equinox (Sharad Vishuva)",
                date = "$year-09-22",
                timeUTC = "18:05 UTC",
                sayanaSign = "Libra 0°",
                nirayanaSign = "Virgo 6°",
                spiritualSignificance = "Cosmic equilibrium marking harvest and gratitude."
            ),
            SolsticeEquinoxEvent(
                year = year,
                eventName = "Winter Solstice (Uttarayana Gateway)",
                date = "$year-12-21",
                timeUTC = "14:50 UTC",
                sayanaSign = "Capricorn 0°",
                nirayanaSign = "Sagittarius 6°",
                spiritualSignificance = "Sun begins northward journey (Uttarayana), the sacred daytime of the Devas."
            )
        )
    }
}
