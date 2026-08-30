package com.example.engine

import com.example.model.*
import kotlin.math.floor

/**
 * Classical Shodashavarga (16 Divisional Charts) Engine as per BPHS.
 */
object ShodashavargaEngine {

    /**
     * Computes the Varga Rashi and degree for any given Nirayana longitude and varga type.
     */
    fun calculateVargaPosition(
        planet: Planet?,
        longitude: Double,
        varga: VargaType,
        vargaLagnaRashi: Rashi? = null
    ): VargaPosition {
        val totalDeg = AstroEngine.normalize360(longitude)
        val rashiIndex = (totalDeg / 30.0).toInt() + 1 // 1 to 12
        val rashi = Rashi.fromIndex(rashiIndex)
        val degInRashi = totalDeg % 30.0
        val isOddSign = rashiIndex % 2 != 0

        var resultRashiIndex = 1
        var degreeInVarga = 0.0

        when (varga) {
            VargaType.D1 -> { // Rasi (1)
                resultRashiIndex = rashiIndex
                degreeInVarga = degInRashi
            }
            VargaType.D2 -> { // Hora (2): 15 deg each
                // Odd signs: 1st half Sun (Leo=5), 2nd half Moon (Cancer=4)
                // Even signs: 1st half Moon (Cancer=4), 2nd half Sun (Leo=5)
                val isFirstHalf = degInRashi < 15.0
                resultRashiIndex = if (isOddSign) {
                    if (isFirstHalf) 5 else 4
                } else {
                    if (isFirstHalf) 4 else 5
                }
                degreeInVarga = (degInRashi % 15.0) * 2.0
            }
            VargaType.D3 -> { // Drekkana (3): 10 deg each
                // 1st Decan: Same sign, 2nd: 5th from it, 3rd: 9th from it
                val decan = (degInRashi / 10.0).toInt() // 0, 1, 2
                val offset = decan * 4
                resultRashiIndex = ((rashiIndex - 1 + offset) % 12) + 1
                degreeInVarga = (degInRashi % 10.0) * 3.0
            }
            VargaType.D4 -> { // Chaturthamsa (4): 7°30' each
                // 1st: Same, 2nd: 4th from it, 3rd: 7th, 4th: 10th
                val part = (degInRashi / 7.5).toInt().coerceIn(0, 3)
                resultRashiIndex = ((rashiIndex - 1 + part * 3) % 12) + 1
                degreeInVarga = (degInRashi % 7.5) * 4.0
            }
            VargaType.D7 -> { // Saptamsa (7): 4°17'8.57" each
                val part = (degInRashi / (30.0 / 7.0)).toInt().coerceIn(0, 6)
                // Odd sign: count from same sign; Even sign: count from 7th from it
                val startSign = if (isOddSign) rashiIndex else ((rashiIndex + 5) % 12) + 1
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % (30.0 / 7.0)) * 7.0
            }
            VargaType.D9 -> { // Navamsa (9): 3°20' each (Quarter Nakshatra)
                val part = (degInRashi / (30.0 / 9.0)).toInt().coerceIn(0, 8)
                // Fiery signs (1,5,9): count from Aries (1)
                // Earthy signs (2,6,10): count from Capricorn (10)
                // Airy signs (3,7,11): count from Libra (7)
                // Watery signs (4,8,12): count from Cancer (4)
                val startSign = when (rashi.element) {
                    "Fire" -> 1
                    "Earth" -> 10
                    "Air" -> 7
                    "Water" -> 4
                    else -> 1
                }
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % (30.0 / 9.0)) * 9.0
            }
            VargaType.D10 -> { // Dasamsa (10): 3° each
                val part = (degInRashi / 3.0).toInt().coerceIn(0, 9)
                // Odd sign: count from same sign; Even sign: count from 9th from it
                val startSign = if (isOddSign) rashiIndex else ((rashiIndex + 7) % 12) + 1
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % 3.0) * 10.0
            }
            VargaType.D12 -> { // Dwadashamsa (12): 2°30' each
                val part = (degInRashi / 2.5).toInt().coerceIn(0, 11)
                // Count from the same sign
                resultRashiIndex = ((rashiIndex - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % 2.5) * 12.0
            }
            VargaType.D16 -> { // Shodasamsa (16): 1°52'30" each
                val part = (degInRashi / (30.0 / 16.0)).toInt().coerceIn(0, 15)
                // Chara (Movable): from Aries (1); Sthira (Fixed): from Leo (5); Dwiswabhava: from Sagittarius (9)
                val startSign = when (rashi.modality) {
                    "Chara (Movable)" -> 1
                    "Sthira (Fixed)" -> 5
                    else -> 9
                }
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % (30.0 / 16.0)) * 16.0
            }
            VargaType.D20 -> { // Vimsamsa (20): 1°30' each
                val part = (degInRashi / 1.5).toInt().coerceIn(0, 19)
                // Movable: from Aries (1); Fixed: from Sagittarius (9); Dual: from Leo (5)
                val startSign = when (rashi.modality) {
                    "Chara (Movable)" -> 1
                    "Sthira (Fixed)" -> 9
                    else -> 5
                }
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % 1.5) * 20.0
            }
            VargaType.D24 -> { // Chaturvimsamsa (24): 1°15' each
                val part = (degInRashi / 1.25).toInt().coerceIn(0, 23)
                // Odd sign: from Leo (5); Even sign: from Cancer (4)
                val startSign = if (isOddSign) 5 else 4
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % 1.25) * 24.0
            }
            VargaType.D27 -> { // Bhamsa / Saptavimsamsa (27): 1°6'40" each
                val part = (degInRashi / (30.0 / 27.0)).toInt().coerceIn(0, 26)
                // Fiery: Aries (1); Earthy: Cancer (4); Airy: Libra (7); Watery: Capricorn (10)
                val startSign = when (rashi.element) {
                    "Fire" -> 1
                    "Earth" -> 4
                    "Air" -> 7
                    "Water" -> 10
                    else -> 1
                }
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % (30.0 / 27.0)) * 27.0
            }
            VargaType.D30 -> { // Trimsamsa (30): Unequal divisions according to planetary rulers
                resultRashiIndex = if (isOddSign) {
                    when {
                        degInRashi < 5.0 -> 1   // Mars (Aries)
                        degInRashi < 10.0 -> 11 // Saturn (Aquarius)
                        degInRashi < 18.0 -> 9  // Jupiter (Sagittarius)
                        degInRashi < 25.0 -> 3  // Mercury (Gemini)
                        else -> 7               // Venus (Libra)
                    }
                } else {
                    when {
                        degInRashi < 5.0 -> 2   // Venus (Taurus)
                        degInRashi < 12.0 -> 6  // Mercury (Virgo)
                        degInRashi < 20.0 -> 12 // Jupiter (Pisces)
                        degInRashi < 25.0 -> 10 // Saturn (Capricorn)
                        else -> 8               // Mars (Scorpio)
                    }
                }
                degreeInVarga = degInRashi % 5.0
            }
            VargaType.D40 -> { // Khavedamsa (40): 45' each
                val part = (degInRashi / 0.75).toInt().coerceIn(0, 39)
                // Odd: from Aries (1); Even: from Libra (7)
                val startSign = if (isOddSign) 1 else 7
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % 0.75) * 40.0
            }
            VargaType.D45 -> { // Akshavedamsa (45): 40' each
                val part = (degInRashi / (40.0 / 60.0)).toInt().coerceIn(0, 44)
                // Movable: Aries (1); Fixed: Leo (5); Dual: Sagittarius (9)
                val startSign = when (rashi.modality) {
                    "Chara (Movable)" -> 1
                    "Sthira (Fixed)" -> 5
                    else -> 9
                }
                resultRashiIndex = ((startSign - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % (40.0 / 60.0)) * 45.0
            }
            VargaType.D60 -> { // Shashtiamsa (60): 30' (half a degree) each
                val part = (degInRashi / 0.5).toInt().coerceIn(0, 59)
                // Count from same sign
                resultRashiIndex = ((rashiIndex - 1 + part) % 12) + 1
                degreeInVarga = (degInRashi % 0.5) * 60.0
            }
        }

        val targetRashi = Rashi.fromIndex(resultRashiIndex)
        val house = if (vargaLagnaRashi != null) {
            ((targetRashi.index - vargaLagnaRashi.index + 12) % 12) + 1
        } else 1

        val deity = getVargaDeity(varga, (degInRashi / (30.0 / varga.division)).toInt(), isOddSign)

        val dignity = if (planet != null) {
            when {
                targetRashi.index == planet.exaltationSign -> PlanetaryDignity.EXALTED
                targetRashi.index == planet.debilitationSign -> PlanetaryDignity.DEBILITATED
                targetRashi.index in planet.ownSigns -> PlanetaryDignity.OWN_SIGN
                planet.naturalFriends.contains(targetRashi.lord.id) -> PlanetaryDignity.FRIEND
                planet.naturalEnemies.contains(targetRashi.lord.id) -> PlanetaryDignity.ENEMY
                else -> PlanetaryDignity.NEUTRAL
            }
        } else PlanetaryDignity.NEUTRAL

        return VargaPosition(
            planet = planet,
            isLagna = planet == null,
            rashi = targetRashi,
            degreeInSign = degreeInVarga,
            house = house,
            dignity = dignity,
            vargaDeity = deity
        )
    }

    /**
     * Generates a complete Divisional Chart
     */
    fun generateDivisionalChart(chart: BirthChart, vargaType: VargaType): DivisionalChart {
        val lagnaPos = calculateVargaPosition(null, chart.ascendantDegree, vargaType)
        val vargaLagnaRashi = lagnaPos.rashi

        val planetPositions = mutableMapOf<Planet, VargaPosition>()
        for ((planet, pos) in chart.planets) {
            val vPos = calculateVargaPosition(planet, pos.longitude, vargaType, vargaLagnaRashi)
            planetPositions[planet] = vPos
        }

        val interpretation = getVargaInterpretation(vargaType, vargaLagnaRashi, planetPositions)

        return DivisionalChart(
            vargaType = vargaType,
            lagnaRashi = vargaLagnaRashi,
            lagnaDegree = lagnaPos.degreeInSign,
            planetPositions = planetPositions,
            interpretation = interpretation
        )
    }

    /**
     * Computes Vimsopaka Bala for all 7 main planets across the 16 Shodashavargas (Max 20 points).
     */
    fun calculateVimsopakaBala(chart: BirthChart): List<VimsopakaScore> {
        val result = mutableListOf<VimsopakaScore>()

        for (planet in Planet.MAIN_SEVEN) {
            var totalScore = 0.0

            for (varga in VargaType.SHODASHAVARGA_LIST) {
                val pPos = chart.planets[planet] ?: continue
                val vPos = calculateVargaPosition(planet, pPos.longitude, varga)

                val dignityMultiplier = when (vPos.dignity) {
                    PlanetaryDignity.EXALTED -> 1.0
                    PlanetaryDignity.MOOLATRIKONA -> 0.9
                    PlanetaryDignity.OWN_SIGN -> 0.8
                    PlanetaryDignity.GREAT_FRIEND -> 0.7
                    PlanetaryDignity.FRIEND -> 0.55
                    PlanetaryDignity.NEUTRAL -> 0.4
                    PlanetaryDignity.ENEMY -> 0.2
                    PlanetaryDignity.GREAT_ENEMY -> 0.1
                    PlanetaryDignity.DEBILITATED -> 0.05
                }

                totalScore += varga.vimsopakaPointsD16 * dignityMultiplier
            }

            val percentage = (totalScore / 20.0) * 100.0
            val status = when {
                totalScore >= 15.0 -> "Ati Bala (Extremely Strong / >75%)"
                totalScore >= 11.0 -> "Bala (Strong / 55-75%)"
                totalScore >= 7.0 -> "Madhyama (Moderate / 35-55%)"
                else -> "Heena (Weak / <35%)"
            }

            result.add(
                VimsopakaScore(
                    planet = planet,
                    score = Math.round(totalScore * 100.0) / 100.0,
                    percentage = Math.round(percentage * 10.0) / 10.0,
                    status = status
                )
            )
        }

        return result
    }

    private fun getVargaDeity(varga: VargaType, partIndex: Int, isOdd: Boolean): String {
        return when (varga) {
            VargaType.D1 -> "Purusha / Prakriti"
            VargaType.D2 -> if (isOdd) (if (partIndex == 0) "Surya (Sun)" else "Chandra (Moon)") else (if (partIndex == 0) "Chandra" else "Surya")
            VargaType.D3 -> when (partIndex) {
                0 -> "Narada (Sattva)"
                1 -> "Agastya (Rajas)"
                else -> "Durvasa (Tamas)"
            }
            VargaType.D7 -> when (partIndex % 7) {
                0 -> "Kshara"; 1 -> "Ksheera"; 2 -> "Dadhi"; 3 -> "Ghrita"; 4 -> "Ikshu"; 5 -> "Madhu"; else -> "Suddhadaka"
            }
            VargaType.D9 -> when (partIndex % 3) {
                0 -> "Deva (Divine)"; 1 -> "Nara (Human)"; else -> "Rakshasa (Demonic)"
            }
            VargaType.D10 -> "Indra, Agni, Yama, Nirriti, Varuna, Vayu, Kubera, Ishana, Brahma, Ananta".split(", ").getOrElse(partIndex % 10) { "Dikpala" }
            VargaType.D60 -> "Ghora, Rakshasa, Deva, Kubera, Yaksha, Kinnara, Bhrashta, Kulaghna, Garala, Vahni, Maya, Purishaka, Apampati, Marutvan, Kaala, Sarpa, Amrita, Indu, Mridu, Komala".split(", ").getOrElse(partIndex % 20) { "Amrita" }
            else -> "Vedic Adhidevata"
        }
    }

    private fun getVargaInterpretation(varga: VargaType, lagna: Rashi, planets: Map<Planet, VargaPosition>): String {
        val strongPlanets = planets.filter { it.value.dignity in listOf(PlanetaryDignity.EXALTED, PlanetaryDignity.OWN_SIGN, PlanetaryDignity.FRIEND) }.keys.map { it.englishName }
        val prominentStrong = if (strongPlanets.isNotEmpty()) strongPlanets.joinToString(", ") else "None in major dignity"

        return when (varga) {
            VargaType.D1 -> "Rasi Chart represents overall physical embodiment, constitution, and macro-destiny. Lagna lord is ${lagna.lord.englishName}."
            VargaType.D9 -> "Navamsa (D9) reveals soul purpose, dharma, marital harmony, and second half of life. Strong planets in D9: $prominentStrong."
            VargaType.D10 -> "Dasamsa (D10) governs career zenith, social prestige, authority, and professional accomplishments. Auspicious influences on 10th house indicate prominence."
            VargaType.D7 -> "Saptamsa (D7) details progeny, creative fruitfulness, and generational blessings."
            VargaType.D12 -> "Dwadashamsa (D12) manifests ancestral lineage, parental blessings, and past karmic baggage."
            VargaType.D60 -> "Shashtiamsa (D60) is the supreme root chart confirming all karmic seeds from past incarnations according to Maharishi Parashara."
            else -> "${varga.sanskritName} (${varga.code}) highlights ${varga.signification}. Key favorable indicators: $prominentStrong."
        }
    }
}
