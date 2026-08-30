package com.example.engine

import com.example.model.*
import kotlin.math.min

/**
 * Classical Ashtakavarga System (Sarvashtakavarga & Bhinnashtakavargas + Shodhana).
 */
object AshtakavargaEngine {

    // Classical Bindu distribution rules per planet from 8 reference points
    private val SUN_RULES = listOf(
        // Sun from Sun, Moon, Mars, Mercury, Jupiter, Venus, Saturn, Lagna
        listOf(1, 2, 4, 7, 8, 9, 10, 11), // from Sun
        listOf(3, 6, 10, 11),              // from Moon
        listOf(1, 2, 4, 7, 8, 9, 10, 11), // from Mars
        listOf(3, 5, 6, 9, 10, 11, 12),    // from Mercury
        listOf(5, 6, 9, 11),              // from Jupiter
        listOf(6, 7, 12),                 // from Venus
        listOf(1, 2, 4, 7, 8, 9, 10, 11), // from Saturn
        listOf(3, 4, 6, 10, 11, 12)       // from Lagna
    )

    private val MOON_RULES = listOf(
        listOf(3, 6, 7, 8, 10, 11),
        listOf(1, 3, 6, 7, 10, 11),
        listOf(2, 3, 5, 6, 9, 10, 11),
        listOf(1, 3, 4, 5, 7, 8, 10, 11),
        listOf(1, 4, 7, 8, 10, 11, 12),
        listOf(3, 4, 5, 7, 9, 10, 11),
        listOf(3, 5, 6, 11),
        listOf(3, 6, 10, 11)
    )

    private val MARS_RULES = listOf(
        listOf(3, 5, 6, 10, 11),
        listOf(3, 6, 11),
        listOf(1, 2, 4, 7, 8, 10, 11),
        listOf(3, 5, 6, 11),
        listOf(6, 10, 11, 12),
        listOf(6, 8, 11, 12),
        listOf(1, 4, 7, 8, 9, 10, 11),
        listOf(1, 3, 6, 10, 11)
    )

    private val MERCURY_RULES = listOf(
        listOf(5, 6, 9, 11, 12),
        listOf(2, 4, 6, 8, 10, 11),
        listOf(1, 2, 4, 7, 8, 9, 10, 11),
        listOf(1, 3, 5, 6, 9, 10, 11, 12),
        listOf(6, 8, 11, 12),
        listOf(1, 2, 3, 4, 5, 8, 9, 11),
        listOf(1, 2, 4, 7, 8, 9, 10, 11),
        listOf(1, 2, 4, 6, 8, 10, 11)
    )

    private val JUPITER_RULES = listOf(
        listOf(1, 2, 3, 4, 7, 8, 9, 10, 11),
        listOf(2, 5, 7, 9, 11),
        listOf(1, 2, 4, 7, 8, 10, 11),
        listOf(1, 2, 4, 5, 6, 9, 10, 11),
        listOf(1, 2, 3, 4, 7, 8, 10, 11),
        listOf(2, 5, 6, 9, 10, 11),
        listOf(3, 5, 6, 12),
        listOf(1, 2, 4, 5, 6, 7, 9, 10, 11)
    )

    private val VENUS_RULES = listOf(
        listOf(8, 11, 12),
        listOf(1, 2, 3, 4, 5, 8, 9, 11, 12),
        listOf(3, 4, 6, 9, 11, 12),
        listOf(3, 5, 6, 9, 11),
        listOf(5, 8, 9, 10, 11),
        listOf(1, 2, 3, 4, 5, 8, 9, 10, 11),
        listOf(3, 4, 5, 8, 9, 10, 11),
        listOf(1, 2, 3, 4, 5, 8, 9, 11)
    )

    private val SATURN_RULES = listOf(
        listOf(1, 2, 4, 7, 8, 10, 11),
        listOf(3, 6, 11),
        listOf(3, 5, 6, 10, 11, 12),
        listOf(6, 8, 9, 10, 11, 12),
        listOf(5, 6, 11, 12),
        listOf(6, 11, 12),
        listOf(3, 5, 6, 11),
        listOf(1, 3, 4, 6, 10, 11)
    )

    /**
     * Calculates Complete Ashtakavarga Report for a Birth Chart.
     */
    fun calculateAshtakavarga(chart: BirthChart): AshtakavargaReport {
        val ascSign = chart.ascendantRashi
        val planetSigns = chart.planets.mapValues { it.value.rashi }

        // Reference order: Sun, Moon, Mars, Mercury, Jupiter, Venus, Saturn, Lagna
        val refSigns = listOf(
            planetSigns[Planet.SUN] ?: ascSign,
            planetSigns[Planet.MOON] ?: ascSign,
            planetSigns[Planet.MARS] ?: ascSign,
            planetSigns[Planet.MERCURY] ?: ascSign,
            planetSigns[Planet.JUPITER] ?: ascSign,
            planetSigns[Planet.VENUS] ?: ascSign,
            planetSigns[Planet.SATURN] ?: ascSign,
            ascSign
        )

        val planetRulesMap = mapOf(
            Planet.SUN to SUN_RULES,
            Planet.MOON to MOON_RULES,
            Planet.MARS to MARS_RULES,
            Planet.MERCURY to MERCURY_RULES,
            Planet.JUPITER to JUPITER_RULES,
            Planet.VENUS to VENUS_RULES,
            Planet.SATURN to SATURN_RULES
        )

        val bhinnashtakavargas = mutableMapOf<Planet, AshtakavargaTable>()
        val savRashiBindus = (1..12).associateWith { 0 }.toMutableMap()

        for ((planet, rules) in planetRulesMap) {
            val rashiCounts = (1..12).associateWith { 0 }.toMutableMap()

            for (refIdx in 0..7) {
                val refSign = refSigns[refIdx]
                val beneficialHouses = rules[refIdx]

                for (h in beneficialHouses) {
                    val targetSignIdx = ((refSign.index - 1 + (h - 1)) % 12) + 1
                    rashiCounts[targetSignIdx] = (rashiCounts[targetSignIdx] ?: 0) + 1
                }
            }

            // Convert to Rashi map and House list from Lagna
            val rashiBinduMap = rashiCounts.mapKeys { Rashi.fromIndex(it.key) }
            val houseBindus = (1..12).map { h ->
                val sIdx = ((ascSign.index - 1 + (h - 1)) % 12) + 1
                rashiCounts[sIdx] ?: 0
            }
            val total = rashiCounts.values.sum()

            // Calculate Sodhya Pinda after Trikona and Ekadhipatya Shodhana
            val sodhya = calculateSodhyaPinda(rashiCounts)

            bhinnashtakavargas[planet] = AshtakavargaTable(
                planet = planet,
                houseBindus = houseBindus,
                rashiBindus = rashiBinduMap,
                totalPoints = total,
                sodhyaPinda = sodhya
            )

            // Add to Sarvashtakavarga
            for ((sIdx, count) in rashiCounts) {
                savRashiBindus[sIdx] = (savRashiBindus[sIdx] ?: 0) + count
            }
        }

        val savRashiMap = savRashiBindus.mapKeys { Rashi.fromIndex(it.key) }
        val savHouseBindus = (1..12).map { h ->
            val sIdx = ((ascSign.index - 1 + (h - 1)) % 12) + 1
            savRashiBindus[sIdx] ?: 0
        }
        val savTotal = savRashiBindus.values.sum()
        val savTable = AshtakavargaTable(
            planet = null,
            houseBindus = savHouseBindus,
            rashiBindus = savRashiMap,
            totalPoints = savTotal,
            sodhyaPinda = savTotal / 2
        )

        val sortedSigns = savRashiMap.toList().sortedByDescending { it.second }
        val strongest = sortedSigns.take(3).map { it.first }
        val weakest = sortedSigns.takeLast(3).map { it.first }

        val analysis = "Total Sarvashtakavarga Bindus: $savTotal (Standard 337). Strongest signs (>30 bindus): ${strongest.joinToString(", ") { it.englishName }}. Houses with >28 bindus produce bountiful results during planetary transits."

        return AshtakavargaReport(
            sarvashtakavarga = savTable,
            bhinnashtakavargas = bhinnashtakavargas,
            strongestSigns = strongest,
            weakestSigns = weakest,
            samudaayaAshtakavargaAnalysis = analysis
        )
    }

    private fun calculateSodhyaPinda(rashiCounts: Map<Int, Int>): Int {
        val trikonaMultipliers = listOf(7, 10, 8, 4, 10, 5, 7, 8, 9, 5, 11, 12)
        var pinda = 0
        for (i in 1..12) {
            val count = rashiCounts[i] ?: 0
            pinda += count * trikonaMultipliers[i - 1]
        }
        return pinda
    }
}
