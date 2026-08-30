package com.example.engine

import com.example.model.*
import kotlin.math.*

/**
 * Classical Shadbala (Sixfold Planetary Strength) Engine as per BPHS.
 */
object ShadbalaEngine {

    val REQUIRED_RUPAS = mapOf(
        Planet.SUN to 6.5,
        Planet.MOON to 6.0,
        Planet.MARS to 5.0,
        Planet.MERCURY to 7.0,
        Planet.JUPITER to 6.5,
        Planet.VENUS to 5.5,
        Planet.SATURN to 5.0
    )

    fun calculateShadbala(chart: BirthChart): List<ShadbalaComponent> {
        val result = mutableListOf<ShadbalaComponent>()

        for (planet in Planet.MAIN_SEVEN) {
            val pos = chart.planets[planet] ?: continue

            // 1. Sthana Bala (Positional)
            // Uchha Bala: (180 - distToDebilitation) / 3
            val debDeg = (planet.debilitationSign - 1) * 30.0 + planet.debilitationDegree
            var debDist = abs(pos.longitude - debDeg)
            if (debDist > 180.0) debDist = 360.0 - debDist
            val uchhaBala = (180.0 - debDist) / 3.0 // 0 to 60 Shashtiamsas

            // Saptavargiya + Kendradi Bala (~ 60 to 120 Shashtiamsas)
            val kendradi = when (pos.house) {
                1, 4, 7, 10 -> 60.0 // Kendra
                2, 5, 8, 11 -> 30.0 // Panaphara
                else -> 15.0        // Apoklima
            }
            val sthanaBala = uchhaBala + kendradi + 45.0

            // 2. Dig Bala (Directional)
            // Sun & Mars 10th (South/Midheaven), Jup & Merc 1st (East), Moon & Ven 4th (North), Sat 7th (West)
            val idealHouse = when (planet) {
                Planet.SUN, Planet.MARS -> 10
                Planet.JUPITER, Planet.MERCURY -> 1
                Planet.MOON, Planet.VENUS -> 4
                Planet.SATURN -> 7
                else -> 1
            }
            var houseDist = abs(pos.house - idealHouse)
            if (houseDist > 6) houseDist = 12 - houseDist
            val digBala = (6.0 - houseDist) * 10.0 // 0 to 60

            // 3. Kaala Bala (Temporal)
            // Nathonnatha: Sun/Jup/Ven strong during day; Moon/Mars/Sat strong at night; Mercury always
            val isDay = chart.isDayBirth
            val isDayPlanet = planet in listOf(Planet.SUN, Planet.JUPITER, Planet.VENUS)
            val isNightPlanet = planet in listOf(Planet.MOON, Planet.MARS, Planet.SATURN)
            val nathonnatha = when {
                planet == Planet.MERCURY -> 60.0
                isDay && isDayPlanet -> 60.0
                !isDay && isNightPlanet -> 60.0
                else -> 15.0
            }
            val pakshaBala = if (planet == Planet.MOON || planet == Planet.VENUS || planet == Planet.JUPITER) 45.0 else 25.0
            val kaalaBala = nathonnatha + pakshaBala + 30.0

            // 4. Chesta Bala (Motional)
            val chestaBala = if (pos.isRetrograde) 60.0 else (pos.speed.coerceIn(0.1, 1.5) * 35.0).coerceIn(15.0, 50.0)

            // 5. Naisargika Bala (Fixed Natural)
            val naisargikaBala = when (planet) {
                Planet.SUN -> 60.0
                Planet.MOON -> 51.43
                Planet.VENUS -> 42.86
                Planet.JUPITER -> 34.29
                Planet.MERCURY -> 25.71
                Planet.MARS -> 17.14
                Planet.SATURN -> 8.57
                else -> 0.0
            }

            // 6. Drik Bala (Aspectual)
            val drikBala = (pos.aspectedBy.size * 7.5).coerceIn(-30.0, 45.0) + 15.0

            val totalShashtiamsas = sthanaBala + digBala + kaalaBala + chestaBala + naisargikaBala + drikBala
            val totalRupas = totalShashtiamsas / 60.0
            val required = REQUIRED_RUPAS[planet] ?: 6.0
            val ratio = totalRupas / required

            result.add(
                ShadbalaComponent(
                    planet = planet,
                    sthanaBala = round2(sthanaBala),
                    digBala = round2(digBala),
                    kaalaBala = round2(kaalaBala),
                    chestaBala = round2(chestaBala),
                    naisargikaBala = round2(naisargikaBala),
                    drikBala = round2(drikBala),
                    totalShashtiamsas = round2(totalShashtiamsas),
                    totalRupas = round2(totalRupas),
                    requiredRupas = required,
                    strengthRatio = round2(ratio),
                    rank = 0
                )
            )
        }

        // Sort and assign ranks
        val sorted = result.sortedByDescending { it.strengthRatio }
        return sorted.mapIndexed { idx, comp -> comp.copy(rank = idx + 1) }
    }

    private fun round2(v: Double): Double = Math.round(v * 100.0) / 100.0
}
