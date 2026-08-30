package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Vedic Dasha Calculation Engine (Vimshottari, Yogini, Chara, Ashtottari).
 */
object DashaEngine {

    // Vimshottari Dasha Order & Standard Years (Total 120)
    val VIMSHOTTARI_ORDER = listOf(
        Pair(Planet.KETU, 7.0),
        Pair(Planet.VENUS, 20.0),
        Pair(Planet.SUN, 6.0),
        Pair(Planet.MOON, 10.0),
        Pair(Planet.MARS, 7.0),
        Pair(Planet.RAHU, 18.0),
        Pair(Planet.JUPITER, 16.0),
        Pair(Planet.SATURN, 19.0),
        Pair(Planet.MERCURY, 17.0)
    )

    // Yogini Dasha Order & Years (Total 36)
    val YOGINI_ORDER = listOf(
        Triple("Mangala", Planet.MOON, 1),
        Triple("Pingala", Planet.SUN, 2),
        Triple("Dhanya", Planet.JUPITER, 3),
        Triple("Bhramari", Planet.MARS, 4),
        Triple("Bhadrika", Planet.MERCURY, 5),
        Triple("Ulka", Planet.SATURN, 6),
        Triple("Siddha", Planet.VENUS, 7),
        Triple("Sankata", Planet.RAHU, 8)
    )

    // Ashtottari Dasha Order & Years (Total 108)
    val ASHTOTTARI_ORDER = listOf(
        Pair(Planet.SUN, 6),
        Pair(Planet.MOON, 15),
        Pair(Planet.MARS, 8),
        Pair(Planet.MERCURY, 17),
        Pair(Planet.SATURN, 10),
        Pair(Planet.JUPITER, 19),
        Pair(Planet.RAHU, 12),
        Pair(Planet.VENUS, 21)
    )

    private const val DAYS_IN_YEAR = 365.2425
    private const val MILLIS_IN_YEAR = (DAYS_IN_YEAR * 24.0 * 3600.0 * 1000.0).toLong()

    /**
     * Calculates complete Dasha Report for a Birth Chart.
     */
    fun calculateDashaReport(chart: BirthChart, targetTimestamp: Long = System.currentTimeMillis()): DashaReport {
        val moonPos = chart.planets[Planet.MOON] ?: return generateFallbackReport()
        val moonLong = moonPos.longitude
        val nakshatraSpan = 360.0 / 27.0 // 13.3333333 degrees
        val nakIndex = (moonLong / nakshatraSpan).toInt().coerceIn(0, 26)
        val janmaNak = Nakshatra.values()[nakIndex]
        val degreeInNak = moonLong - (nakIndex * nakshatraSpan)
        val fractionTraversed = (degreeInNak / nakshatraSpan).coerceIn(0.0, 1.0)
        val fractionRemaining = 1.0 - fractionTraversed

        // Parse birth date and time to millis
        val birthCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        try {
            val partsDate = chart.birthDate.split("-")
            val partsTime = chart.birthTime.split(":")
            birthCalendar.set(Calendar.YEAR, partsDate[0].toInt())
            birthCalendar.set(Calendar.MONTH, partsDate[1].toInt() - 1)
            birthCalendar.set(Calendar.DAY_OF_MONTH, partsDate[2].toInt())
            birthCalendar.set(Calendar.HOUR_OF_DAY, partsTime[0].toInt())
            birthCalendar.set(Calendar.MINUTE, partsTime[1].toInt())
            birthCalendar.set(Calendar.SECOND, 0)
            birthCalendar.set(Calendar.MILLISECOND, 0)
        } catch (e: Exception) {
            birthCalendar.timeInMillis = System.currentTimeMillis() - (30L * 365 * 24 * 3600 * 1000)
        }
        val birthMillis = birthCalendar.timeInMillis

        // 1. Vimshottari Calculation
        val firstLord = janmaNak.lord
        val firstLordIndex = VIMSHOTTARI_ORDER.indexOfFirst { it.first == firstLord }.let { if (it >= 0) it else 0 }
        val fullDurationFirstLord = VIMSHOTTARI_ORDER[firstLordIndex].second
        val balanceYears = fullDurationFirstLord * fractionRemaining

        val vimshottariList = mutableListOf<VimshottariPeriod>()
        var currentStartMillis = birthMillis

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        for (cycle in 0 until 9) {
            val orderIdx = (firstLordIndex + cycle) % 9
            val (planet, fullYears) = VIMSHOTTARI_ORDER[orderIdx]
            val durationYears = if (cycle == 0) balanceYears else fullYears
            val durationMillis = (durationYears * MILLIS_IN_YEAR).toLong()
            val endMillis = currentStartMillis + durationMillis

            val isRunning = targetTimestamp in currentStartMillis..endMillis

            // Calculate Antardashas
            val antardashasList = calculateAntardashas(
                mahadashaPlanet = planet,
                mahaStartMillis = currentStartMillis,
                mahaDurationYears = durationYears,
                fullMahaYears = fullYears,
                targetTimestamp = targetTimestamp,
                sdf = sdf
            )

            val period = VimshottariPeriod(
                planet = planet,
                level = DashaLevel.MAHADASHA,
                startDate = sdf.format(Date(currentStartMillis)),
                endDate = sdf.format(Date(endMillis)),
                startMillis = currentStartMillis,
                endMillis = endMillis,
                durationYears = durationYears,
                isRunning = isRunning,
                subPeriods = antardashasList,
                predictionSignificance = getDashaSignificance(planet, chart)
            )

            vimshottariList.add(period)
            currentStartMillis = endMillis
        }

        val currentMaha = vimshottariList.firstOrNull { it.isRunning } ?: vimshottariList.firstOrNull()
        val currentAntar = currentMaha?.subPeriods?.firstOrNull { it.isRunning } ?: currentMaha?.subPeriods?.firstOrNull()
        val currentPrat = currentAntar?.subPeriods?.firstOrNull { it.isRunning } ?: currentAntar?.subPeriods?.firstOrNull()

        // 2. Yogini Dasha Calculation
        val yoginiList = calculateYoginiDasha(janmaNak, fractionRemaining, birthMillis, targetTimestamp, sdf)

        // 3. Jaimini Chara Dasha Calculation
        val charaList = calculateCharaDasha(chart, birthMillis, targetTimestamp, sdf)

        // 4. Ashtottari Dasha Calculation
        val ashtottariList = calculateAshtottariDasha(janmaNak, birthMillis, targetTimestamp, sdf)

        return DashaReport(
            startingMahadasha = firstLord,
            birthBalanceYears = Math.round(balanceYears * 100.0) / 100.0,
            vimshottariList = vimshottariList,
            currentMahadasha = currentMaha,
            currentAntardasha = currentAntar,
            currentPratyantar = currentPrat,
            yoginiList = yoginiList,
            charaList = charaList,
            ashtottariList = ashtottariList
        )
    }

    private fun calculateAntardashas(
        mahadashaPlanet: Planet,
        mahaStartMillis: Long,
        mahaDurationYears: Double,
        fullMahaYears: Double,
        targetTimestamp: Long,
        sdf: SimpleDateFormat
    ): List<VimshottariPeriod> {
        val list = mutableListOf<VimshottariPeriod>()
        val startIdx = VIMSHOTTARI_ORDER.indexOfFirst { it.first == mahadashaPlanet }.coerceAtLeast(0)
        var curMillis = mahaStartMillis

        // Scale factor if first Mahadasha is partially expired
        val scaleFactor = mahaDurationYears / fullMahaYears

        for (i in 0 until 9) {
            val idx = (startIdx + i) % 9
            val (subPlanet, subYears) = VIMSHOTTARI_ORDER[idx]
            // Antardasha formula: (MahaYears * AntarYears) / 120
            val subDurationYears = ((fullMahaYears * subYears) / 120.0) * scaleFactor
            val subDurationMillis = (subDurationYears * MILLIS_IN_YEAR).toLong()
            val endMillis = curMillis + subDurationMillis

            val isRunning = targetTimestamp in curMillis..endMillis

            // Calculate Pratyantar Dashas
            val pratyantars = calculatePratyantardashas(
                mahaPlanet = mahadashaPlanet,
                antarPlanet = subPlanet,
                antarStartMillis = curMillis,
                antarDurationYears = subDurationYears,
                targetTimestamp = targetTimestamp,
                sdf = sdf
            )

            list.add(
                VimshottariPeriod(
                    planet = subPlanet,
                    level = DashaLevel.ANTARDASHA,
                    startDate = sdf.format(Date(curMillis)),
                    endDate = sdf.format(Date(endMillis)),
                    startMillis = curMillis,
                    endMillis = endMillis,
                    durationYears = subDurationYears,
                    isRunning = isRunning,
                    subPeriods = pratyantars,
                    predictionSignificance = "${mahadashaPlanet.englishName}-${subPlanet.englishName} Sub-period"
                )
            )

            curMillis = endMillis
        }

        return list
    }

    private fun calculatePratyantardashas(
        mahaPlanet: Planet,
        antarPlanet: Planet,
        antarStartMillis: Long,
        antarDurationYears: Double,
        targetTimestamp: Long,
        sdf: SimpleDateFormat
    ): List<VimshottariPeriod> {
        val list = mutableListOf<VimshottariPeriod>()
        val startIdx = VIMSHOTTARI_ORDER.indexOfFirst { it.first == antarPlanet }.coerceAtLeast(0)
        var curMillis = antarStartMillis

        for (i in 0 until 9) {
            val idx = (startIdx + i) % 9
            val (pratPlanet, pratYears) = VIMSHOTTARI_ORDER[idx]
            // Pratyantar formula: (AntarDuration * PratYears) / 120
            val pratDurationYears = (antarDurationYears * pratYears) / 120.0
            val pratDurationMillis = (pratDurationYears * MILLIS_IN_YEAR).toLong()
            val endMillis = curMillis + pratDurationMillis

            val isRunning = targetTimestamp in curMillis..endMillis

            list.add(
                VimshottariPeriod(
                    planet = pratPlanet,
                    level = DashaLevel.PRATYANTAR,
                    startDate = sdf.format(Date(curMillis)),
                    endDate = sdf.format(Date(endMillis)),
                    startMillis = curMillis,
                    endMillis = endMillis,
                    durationYears = pratDurationYears,
                    isRunning = isRunning,
                    predictionSignificance = "${mahaPlanet.shortName}-${antarPlanet.shortName}-${pratPlanet.shortName}"
                )
            )

            curMillis = endMillis
        }

        return list
    }

    private fun calculateYoginiDasha(
        janmaNak: Nakshatra,
        fractionRemaining: Double,
        birthMillis: Long,
        targetTimestamp: Long,
        sdf: SimpleDateFormat
    ): List<YoginiPeriod> {
        val nakIndex1Based = janmaNak.index
        // Yogini starting index: (Nakshatra number + 3) % 8
        val startingYoginiIdx = (nakIndex1Based + 3) % 8

        val list = mutableListOf<YoginiPeriod>()
        var curMillis = birthMillis

        for (cycle in 0 until 8) {
            val idx = (startingYoginiIdx + cycle) % 8
            val (name, planet, fullYears) = YOGINI_ORDER[idx]
            val durationYears = if (cycle == 0) (fullYears * fractionRemaining).coerceAtLeast(0.1) else fullYears.toDouble()
            val durMillis = (durationYears * MILLIS_IN_YEAR).toLong()
            val endMillis = curMillis + durMillis

            val isRunning = targetTimestamp in curMillis..endMillis
            val nature = if (name in listOf("Mangala", "Dhanya", "Bhadrika", "Siddha")) "Auspicious (Subha)" else "Challenging (Ashubha)"

            list.add(
                YoginiPeriod(
                    name = name,
                    rulingPlanet = planet,
                    durationYears = fullYears,
                    startDate = sdf.format(Date(curMillis)),
                    endDate = sdf.format(Date(endMillis)),
                    isRunning = isRunning,
                    nature = nature
                )
            )

            curMillis = endMillis
        }

        return list
    }

    private fun calculateCharaDasha(
        chart: BirthChart,
        birthMillis: Long,
        targetTimestamp: Long,
        sdf: SimpleDateFormat
    ): List<CharaDashaPeriod> {
        val list = mutableListOf<CharaDashaPeriod>()
        val lagnaRashi = chart.ascendantRashi
        var curMillis = birthMillis

        // 12 signs from Lagna
        for (i in 0 until 12) {
            val signIndex = ((lagnaRashi.index - 1 + i) % 12) + 1
            val sign = Rashi.fromIndex(signIndex)
            val lord = sign.lord
            val lordSign = chart.planets[lord]?.rashi ?: sign

            // Standard Jaimini period computation (count distance from sign to lord)
            var years = abs(lordSign.index - sign.index)
            if (years == 0) years = 12

            val durMillis = (years * MILLIS_IN_YEAR)
            val endMillis = curMillis + durMillis
            val isRunning = targetTimestamp in curMillis..endMillis

            val occupants = chart.planets.filter { it.value.rashi == sign }.keys.map { it.englishName }

            list.add(
                CharaDashaPeriod(
                    rashi = sign,
                    durationYears = years,
                    startDate = sdf.format(Date(curMillis)),
                    endDate = sdf.format(Date(endMillis)),
                    isRunning = isRunning,
                    arudhaLordsPresent = occupants
                )
            )

            curMillis = endMillis
        }

        return list
    }

    private fun calculateAshtottariDasha(
        janmaNak: Nakshatra,
        birthMillis: Long,
        targetTimestamp: Long,
        sdf: SimpleDateFormat
    ): List<AshtottariPeriod> {
        val list = mutableListOf<AshtottariPeriod>()
        val startIdx = (janmaNak.index % 8)
        var curMillis = birthMillis

        for (i in 0 until 8) {
            val idx = (startIdx + i) % 8
            val (planet, years) = ASHTOTTARI_ORDER[idx]
            val durMillis = (years * MILLIS_IN_YEAR)
            val endMillis = curMillis + durMillis
            val isRunning = targetTimestamp in curMillis..endMillis

            list.add(
                AshtottariPeriod(
                    planet = planet,
                    durationYears = years,
                    startDate = sdf.format(Date(curMillis)),
                    endDate = sdf.format(Date(endMillis)),
                    isRunning = isRunning
                )
            )

            curMillis = endMillis
        }

        return list
    }

    private fun getDashaSignificance(planet: Planet, chart: BirthChart): String {
        val pos = chart.planets[planet] ?: return "Governs general planetary significations"
        return "Major period of ${planet.englishName} located in House ${pos.house} (${pos.rashi.englishName}). Lord of Houses ${planet.ownSigns.map { (it - chart.ascendantRashi.index + 12) % 12 + 1 }.joinToString(", ")}. Promotes ${planet.karakaName} and ${pos.dignity.label} results."
    }

    private fun generateFallbackReport(): DashaReport {
        return DashaReport(
            startingMahadasha = Planet.JUPITER,
            birthBalanceYears = 10.0,
            vimshottariList = emptyList(),
            currentMahadasha = null,
            currentAntardasha = null,
            currentPratyantar = null,
            yoginiList = emptyList(),
            charaList = emptyList(),
            ashtottariList = emptyList()
        )
    }
}
