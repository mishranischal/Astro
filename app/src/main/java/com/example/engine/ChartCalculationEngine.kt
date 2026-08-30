package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Main Vedic Chart Engine: Calculates Complete Janma Kundali, Bhavas, Dignities, Aspects, and Special Lagnas
 */
object ChartCalculationEngine {

    fun generateBirthChart(
        name: String,
        birthDate: String, // "YYYY-MM-DD"
        birthTime: String, // "HH:mm"
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        locationName: String,
        gender: String = "Not Specified",
        ayanamshaSystem: AyanamshaSystem = AyanamshaSystem.LAHIRI
    ): BirthChart {
        val dateParts = birthDate.split("-").map { it.toInt() }
        val timeParts = birthTime.split(":").map { it.toInt() }
        val year = dateParts[0]
        val month = dateParts[1]
        val day = dateParts[2]
        val hour = timeParts[0]
        val minute = timeParts[1]

        val localDecimalHours = hour + (minute / 60.0)
        val utHours = (localDecimalHours - timezoneOffsetHours + 24.0) % 24.0

        val julianDay = EphemerisEngine.getJulianDay(year, month, day, utHours)
        val ayanamsha = EphemerisEngine.calculateAyanamsha(julianDay, ayanamshaSystem)

        // Tropical Ascendant & Sidereal Lagna
        val tropicalAsc = EphemerisEngine.calculateTropicalAscendant(julianDay, latitude, longitude)
        val siderealAsc = ((tropicalAsc - ayanamsha) % 360.0 + 360.0) % 360.0
        val ascRashi = Rashi.fromIndex((siderealAsc / 30.0).toInt() + 1)
        val (ascNak, ascPada) = Nakshatra.fromLongitude(siderealAsc)

        // Tropical Planetary Positions -> Nirayana (Sidereal)
        val tropicalPlanets = EphemerisEngine.calculateTropicalPlanetaryPositions(julianDay)
        val rawNirayanaPositions = mutableMapOf<Planet, Pair<Double, Double>>() // long, speed

        for ((planet, pos) in tropicalPlanets) {
            val nirayanaLong = ((pos.first - ayanamsha) % 360.0 + 360.0) % 360.0
            rawNirayanaPositions[planet] = Pair(nirayanaLong, pos.second)
        }

        val sunLong = rawNirayanaPositions[Planet.SUN]?.first ?: 0.0

        // Build PlanetaryPosition objects with dignities, combustion, houses
        val planetMap = mutableMapOf<Planet, PlanetaryPosition>()
        val ascRashiIndex = ascRashi.index

        for (planet in Planet.NAVAGRAHA) {
            val (pLong, speed) = rawNirayanaPositions[planet] ?: Pair(0.0, 0.0)
            val rashiIndex = (pLong / 30.0).toInt() + 1
            val rashi = Rashi.fromIndex(rashiIndex)
            val degInRashi = pLong % 30.0
            val (nak, pada) = Nakshatra.fromLongitude(pLong)

            // House from Lagna (1 to 12)
            val house = ((rashiIndex - ascRashiIndex + 12) % 12) + 1

            // Combustion calculation from Sun
            val distToSun = calculateAngularDistance(pLong, sunLong)
            val isCombust = if (planet != Planet.SUN && planet != Planet.RAHU && planet != Planet.KETU) {
                when (planet) {
                    Planet.MOON -> distToSun <= 12.0
                    Planet.MARS -> distToSun <= 17.0
                    Planet.MERCURY -> if (speed < 0) distToSun <= 12.0 else distToSun <= 14.0
                    Planet.JUPITER -> distToSun <= 11.0
                    Planet.VENUS -> if (speed < 0) distToSun <= 8.0 else distToSun <= 10.0
                    Planet.SATURN -> distToSun <= 15.0
                    else -> false
                }
            } else false

            // Dignity
            val dignity = calculateDignity(planet, rashiIndex, degInRashi)

            // Aspecting Houses (Parashari Drishti)
            val aspectingHouses = calculateAspectingHouses(house, planet)

            val isRetrograde = speed < 0 || planet == Planet.RAHU || planet == Planet.KETU

            planetMap[planet] = PlanetaryPosition(
                planet = planet,
                longitude = pLong,
                speed = speed,
                isRetrograde = isRetrograde,
                rashi = rashi,
                degreeInRashi = degInRashi,
                nakshatra = nak,
                pada = pada,
                house = house,
                dignity = dignity,
                isCombust = isCombust,
                distanceToSun = distToSun,
                aspectingHouses = aspectingHouses
            )
        }

        // Check Graha Yuddha (Planetary War within 1° between Tara Grahas: Mars, Mercury, Jupiter, Venus, Saturn)
        val taraGrahas = listOf(Planet.MARS, Planet.MERCURY, Planet.JUPITER, Planet.VENUS, Planet.SATURN)
        for (i in taraGrahas.indices) {
            for (j in i + 1 until taraGrahas.size) {
                val p1 = taraGrahas[i]
                val p2 = taraGrahas[j]
                val pos1 = planetMap[p1]!!
                val pos2 = planetMap[p2]!!
                if (abs(pos1.longitude - pos2.longitude) <= 1.0) {
                    val p1Wins = pos1.speed > pos2.speed
                    planetMap[p1] = pos1.copy(isWarWinner = p1Wins, isWarLoser = !p1Wins)
                    planetMap[p2] = pos2.copy(isWarWinner = !p1Wins, isWarLoser = p1Wins)
                }
            }
        }

        // Fill Aspected By
        for (p in Planet.NAVAGRAHA) {
            val pos = planetMap[p]!!
            val aspectingMe = Planet.NAVAGRAHA.filter { other ->
                other != p && (planetMap[other]?.aspectingHouses?.contains(pos.house) == true)
            }
            planetMap[p] = pos.copy(aspectedBy = aspectingMe)
        }

        // 12 Bhava Details (Equal Sign / Bhava Chalit)
        val housesList = mutableListOf<BhavaDetail>()
        for (h in 1..12) {
            val houseRashiIndex = ((ascRashiIndex + h - 2) % 12) + 1
            val hRashi = Rashi.fromIndex(houseRashiIndex)
            val occupants = planetMap.values.filter { it.house == h }.map { it.planet }
            val aspectingPlanets = planetMap.values.filter { it.aspectingHouses.contains(h) }.map { it.planet }
            val cusp = ((siderealAsc + (h - 1) * 30.0) % 360.0)
            val start = (cusp - 15.0 + 360.0) % 360.0
            val end = (cusp + 15.0) % 360.0

            housesList.add(
                BhavaDetail(
                    houseNumber = h,
                    rashi = hRashi,
                    startDegree = start,
                    cuspDegree = cusp,
                    endDegree = end,
                    lord = hRashi.lord,
                    occupants = occupants,
                    aspectingPlanets = aspectingPlanets,
                    significations = getHouseSignification(h)
                )
            )
        }

        // Special Lagnas & Upagrahas (Hora, Ghati, Bhava, Indu, Upapada, Arudha, Varnada, Mandi, Gulika)
        val specialLagnas = calculateSpecialLagnas(siderealAsc, sunLong, planetMap, localDecimalHours)
        val isDayBirth = localDecimalHours in 6.0..18.0

        return BirthChart(
            id = UUID.randomUUID().toString(),
            name = name,
            birthDate = birthDate,
            birthTime = birthTime,
            latitude = latitude,
            longitude = longitude,
            timezoneOffsetHours = timezoneOffsetHours,
            locationName = locationName,
            gender = gender,
            ayanamshaSystem = ayanamshaSystem,
            ayanamshaValue = ayanamsha,
            julianDay = julianDay,
            ascendantDegree = siderealAsc,
            ascendantRashi = ascRashi,
            ascendantNakshatra = ascNak,
            ascendantPada = ascPada,
            planets = planetMap,
            houses = housesList,
            specialLagnas = specialLagnas,
            isDayBirth = isDayBirth
        )
    }

    private fun calculateAngularDistance(deg1: Double, deg2: Double): Double {
        val diff = abs(deg1 - deg2) % 360.0
        return if (diff > 180.0) 360.0 - diff else diff
    }

    private fun calculateDignity(planet: Planet, rashiIndex: Int, degInRashi: Double): PlanetaryDignity {
        if (rashiIndex == planet.exaltationSign) {
            return PlanetaryDignity.EXALTED
        }
        if (rashiIndex == planet.debilitationSign) {
            return PlanetaryDignity.DEBILITATED
        }
        if (rashiIndex == planet.moolatrikonaSign && degInRashi in planet.moolatrikonaRange) {
            return PlanetaryDignity.MOOLATRIKONA
        }
        if (planet.ownSigns.contains(rashiIndex)) {
            return PlanetaryDignity.OWN_SIGN
        }

        val signLord = Rashi.fromIndex(rashiIndex).lord
        val isNaturalFriend = planet.naturalFriends.contains(signLord.id)
        val isNaturalEnemy = planet.naturalEnemies.contains(signLord.id)

        return when {
            isNaturalFriend -> PlanetaryDignity.FRIEND
            isNaturalEnemy -> PlanetaryDignity.ENEMY
            else -> PlanetaryDignity.NEUTRAL
        }
    }

    private fun calculateAspectingHouses(house: Int, planet: Planet): List<Int> {
        val aspects = mutableListOf<Int>()
        // All planets aspect 7th from their placement
        val seventh = ((house + 6 - 1) % 12) + 1
        aspects.add(seventh)

        when (planet) {
            Planet.MARS -> {
                aspects.add(((house + 3 - 1) % 12) + 1) // 4th aspect
                aspects.add(((house + 7 - 1) % 12) + 1) // 8th aspect
            }
            Planet.JUPITER, Planet.RAHU, Planet.KETU -> {
                aspects.add(((house + 4 - 1) % 12) + 1) // 5th aspect
                aspects.add(((house + 8 - 1) % 12) + 1) // 9th aspect
            }
            Planet.SATURN -> {
                aspects.add(((house + 2 - 1) % 12) + 1) // 3rd aspect
                aspects.add(((house + 9 - 1) % 12) + 1) // 10th aspect
            }
            else -> {}
        }
        return aspects.distinct()
    }

    private fun calculateSpecialLagnas(
        ascLong: Double,
        sunLong: Double,
        planets: Map<Planet, PlanetaryPosition>,
        localHours: Double
    ): SpecialLagnas {
        // Bhava Lagna: Moves 1 sign per 2 hours from Sun at sunrise
        val hoursFromSunrise = (localHours - 6.0 + 24.0) % 24.0
        val bhavaLagna = (sunLong + hoursFromSunrise * 15.0) % 360.0

        // Hora Lagna (HL): Moves 1 sign per 1 hour from Sun at sunrise
        val horaLagna = (sunLong + hoursFromSunrise * 30.0) % 360.0

        // Ghati Lagna (GL): Moves 1 sign per 1 Ghati (24 min) = 1.25 signs / hour
        val ghatiLagna = (sunLong + hoursFromSunrise * 75.0) % 360.0

        // Indu Lagna (Wealth Lagna from 9th lords of Lagna & Moon)
        val moonPos = planets[Planet.MOON]
        val moonRashi = moonPos?.rashi?.index ?: 1
        val ascRashi = (ascLong / 30.0).toInt() + 1
        val ninthFromLagnaLord = Rashi.fromIndex(((ascRashi + 8 - 1) % 12) + 1).lord
        val ninthFromMoonLord = Rashi.fromIndex(((moonRashi + 8 - 1) % 12) + 1).lord
        val rayLagna = getRays(ninthFromLagnaLord)
        val rayMoon = getRays(ninthFromMoonLord)
        val totalRays = rayLagna + rayMoon
        val induRashiIndex = (((moonRashi + (totalRays % 12) - 1) - 1) % 12) + 1
        val induLagna = ((induRashiIndex - 1) * 30.0 + (moonPos?.degreeInRashi ?: 0.0)) % 360.0

        // Arudha Lagna (AL): Distance from Lagna Lord to Lagna projected forward
        val lagnaLord = Rashi.fromIndex(ascRashi).lord
        val lagnaLordHouse = planets[lagnaLord]?.house ?: 1
        var arudhaHouse = ((lagnaLordHouse + (lagnaLordHouse - 1) - 1) % 12) + 1
        // Parashari Exception: If AL falls in 1st or 7th, take 10th from it
        if (arudhaHouse == 1 || arudhaHouse == 7) {
            arudhaHouse = ((arudhaHouse + 9 - 1) % 12) + 1
        }
        val arudhaRashi = ((ascRashi + arudhaHouse - 2) % 12) + 1
        val arudhaLagna = (arudhaRashi - 1) * 30.0

        // Upapada Lagna (UL - Arudha of 12th house)
        val twelfthLord = Rashi.fromIndex(((ascRashi + 11 - 1) % 12) + 1).lord
        val twelfthLordHouse = planets[twelfthLord]?.house ?: 1
        val distFrom12th = ((twelfthLordHouse - 12 + 12) % 12)
        var upapadaHouse = ((twelfthLordHouse + distFrom12th - 1) % 12) + 1
        if (upapadaHouse == 12 || upapadaHouse == 6) {
            upapadaHouse = ((upapadaHouse + 9 - 1) % 12) + 1
        }
        val upapadaRashi = ((ascRashi + upapadaHouse - 2) % 12) + 1
        val upapadaLagna = (upapadaRashi - 1) * 30.0

        // Gulika & Mandi (Saturn's sons)
        val gulikaPortion = if (localHours in 6.0..18.0) 7.0 / 8.0 else 3.0 / 8.0
        val gulika = (ascLong + gulikaPortion * 180.0) % 360.0
        val mandi = (gulika - 2.5 + 360.0) % 360.0

        return SpecialLagnas(
            janmaLagna = ascLong,
            bhavaLagna = bhavaLagna,
            horaLagna = horaLagna,
            ghatiLagna = ghatiLagna,
            induLagna = induLagna,
            upapadaLagna = upapadaLagna,
            arudhaLagna = arudhaLagna,
            varnadaLagna = (ascLong + 60.0) % 360.0,
            pranapadaLagna = (sunLong + hoursFromSunrise * 120.0) % 360.0,
            shriLagna = (moonPos?.longitude ?: 0.0) + (ascLong / 12.0) % 360.0,
            gulika = gulika,
            mandi = mandi
        )
    }

    private fun getRays(planet: Planet): Int = when (planet) {
        Planet.SUN -> 30
        Planet.MOON -> 16
        Planet.MARS -> 6
        Planet.MERCURY -> 8
        Planet.JUPITER -> 10
        Planet.VENUS -> 12
        Planet.SATURN -> 1
        else -> 4
    }

    private fun getHouseSignification(house: Int): String = when (house) {
        1 -> "Tanu Bhava: Physical Self, Vitality, Temperament, Appearance, Head"
        2 -> "Dhana Bhava: Wealth, Speech, Family, Food, Right Eye, Face"
        3 -> "Sahaja Bhava: Siblings, Courage, Valor, Short Travels, Hands"
        4 -> "Sukha Bhava: Mother, Home, Conveyance, Peace of Mind, Heart, Land"
        5 -> "Putra Bhava: Children, Intellect, Purva Punya, Creativity, Romance"
        6 -> "Ari Bhava: Enemies, Debts, Diseases, Obstacles, Daily Work, Digestion"
        7 -> "Yuvati Bhava: Spouse, Partnerships, Business, Public Relations, Marital Bond"
        8 -> "Randhra Bhava: Longevity, Transformation, Hidden Knowledge, Occult, Sudden Gains"
        9 -> "Dharma Bhava: Higher Wisdom, Guru, Fortune, Father, Pilgrimage, Dharma"
        10 -> "Karma Bhava: Profession, Status, Reputation, Public Deeds, Authority, Career"
        11 -> "Labha Bhava: Gains, Aspirations, Elder Siblings, Social Network, Fulfillment"
        12 -> "Vyaya Bhava: Expenditure, Foreign Lands, Moksha, Solitude, Sleep, Losses"
        else -> ""
    }
}
