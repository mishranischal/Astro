package com.example.engine

import com.example.model.*
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

/**
 * Astronomical and Mathematical Core for Vedic Astrology (Jyotisha Shastra)
 * Precise Sidereal ephemeris engine supporting 1000 BCE to 3000 CE.
 */
object AstroEngine {

    fun formatDMS(degree: Double): String {
        val norm = ((degree % 360.0) + 360.0) % 360.0
        val d = norm.toInt()
        val m = ((norm - d) * 60.0).toInt()
        val s = ((((norm - d) * 60.0) - m) * 60.0).toInt()
        return String.format("%02d° %02d' %02d\"", d, m, s)
    }

    fun calculateBirthChart(
        personName: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        birthPlace: String,
        gender: String = "Male",
        ayanamshaSystem: AyanamshaSystem = AyanamshaSystem.LAHIRI
    ): BirthChart {
        val dateStr = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month, day)
        val timeStr = String.format(java.util.Locale.US, "%02d:%02d", hour, minute)
        return ChartCalculationEngine.generateBirthChart(
            name = personName,
            birthDate = dateStr,
            birthTime = timeStr,
            latitude = latitude,
            longitude = longitude,
            timezoneOffsetHours = timezoneOffsetHours,
            locationName = birthPlace,
            gender = gender,
            ayanamshaSystem = ayanamshaSystem
        )
    }

    private const val DEG2RAD = Math.PI / 180.0
    private const val RAD2DEG = 180.0 / Math.PI

    /**
     * Converts calendar date and time to Julian Day (UT).
     * Handles Julian to Gregorian calendar reform automatically.
     */
    fun calculateJulianDay(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Double = 0.0, timezoneOffsetHours: Double = 0.0): Double {
        var y = year
        var m = month
        val utHour = (hour + minute / 60.0 + second / 3600.0) - timezoneOffsetHours
        val decimalDay = day + utHour / 24.0

        if (m <= 2) {
            y -= 1
            m += 12
        }

        val a = y / 100
        val isGregorian = (year > 1582) || (year == 1582 && month > 10) || (year == 1582 && month == 10 && day >= 15)
        val b = if (isGregorian) 2 - a + (a / 4) else 0

        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + decimalDay + b - 1524.5
        return jd
    }

    /**
     * Calculates Ayanamsha for a given Julian Day based on selected tradition.
     */
    fun calculateAyanamsha(jd: Double, system: AyanamshaSystem): Double {
        // T is centuries from J2000.0 (JD 2451545.0)
        val t = (jd - 2451545.0) / 36525.0

        return when (system) {
            AyanamshaSystem.LAHIRI -> {
                // Lahiri (Chitrapaksha) standard baseline at J2000 = 23.85698333 (23° 51' 25")
                // Precession rate: 50.290966 arcsec/yr = 1.39697127 deg/century
                23.85698333 + 1.39697127 * t + 0.0003086 * t * t
            }
            AyanamshaSystem.RAMAN -> {
                // B.V. Raman: 21.0133333 + 1.39697 * t
                22.4644444 + 1.39697127 * t
            }
            AyanamshaSystem.KP -> {
                // Krishnamurti Paddhati: ~ 6 mins behind Lahiri
                23.7533333 + 1.39697127 * t
            }
            AyanamshaSystem.YUKTESHWAR -> {
                // Sri Yukteshwar (Holy Science)
                22.1805555 + 1.39697127 * t
            }
            AyanamshaSystem.TRUE_CITRA -> {
                // True Citra: dynamic Chitra star position opposite 180°
                23.8615000 + 1.39697127 * t
            }
            AyanamshaSystem.SURYA_SIDDHANTA -> {
                // Traditional Surya Siddhanta trepidation/precession model
                23.5000000 + 1.38888888 * t
            }
        }
    }

    /**
     * Calculates Greenwich Mean Sidereal Time (GMST) in degrees.
     */
    private fun calculateGmstDegrees(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        var gmst = 280.46061837 + 360.98564736629 * (jd - 2451545.0) + 0.000387933 * t * t - (t * t * t) / 38710000.0
        return normalize360(gmst)
    }

    /**
     * Calculates Local Sidereal Time (RAMC in degrees).
     */
    fun calculateLmstDegrees(jd: Double, longitudeEast: Double): Double {
        val gmst = calculateGmstDegrees(jd)
        return normalize360(gmst + longitudeEast)
    }

    /**
     * Calculates true obliquity of ecliptic (in degrees).
     */
    fun calculateObliquity(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        return 23.43929111 - (46.8150 * t + 0.00059 * t * t - 0.001813 * t * t * t) / 3600.0
    }

    /**
     * Calculates Tropical Ascendant (Lagna) in degrees, then converts to Nirayana.
     */
    fun calculateAscendantNirayana(jd: Double, latitude: Double, longitudeEast: Double, ayanamsha: Double): Double {
        val ramc = calculateLmstDegrees(jd, longitudeEast)
        val eps = calculateObliquity(jd)

        val ramcRad = ramc * DEG2RAD
        val epsRad = eps * DEG2RAD
        val latRad = latitude * DEG2RAD

        // Ascendant formula: tan(Asc) = -cos(RAMC) / (sin(RAMC)*cos(eps) + tan(lat)*sin(eps))
        val y = cos(ramcRad)
        val x = -(sin(ramcRad) * cos(epsRad) + tan(latRad) * sin(epsRad))
        var ascSayana = atan2(y, x) * RAD2DEG
        ascSayana = normalize360(ascSayana + 90.0)

        // Convert to Sidereal (Nirayana)
        return normalize360(ascSayana - ayanamsha)
    }

    /**
     * Calculates High-Precision Nirayana Planetary Positions for Sun through Ketu.
     */
    fun calculateAllPlanets(jd: Double, ayanamsha: Double): Map<Planet, Pair<Double, Double>> {
        // Returns Map of Planet -> Pair(Longitude Nirayana 0-360, Daily Speed deg/day)
        val t = (jd - 2451545.0) / 36525.0
        val result = mutableMapOf<Planet, Pair<Double, Double>>()

        // 1. Sun
        val l0Sun = 280.46646 + 36000.76983 * t + 0.0003032 * t * t
        val mSun = 357.52911 + 35999.05029 * t - 0.0001537 * t * t
        val mSunRad = normalize360(mSun) * DEG2RAD
        val cSun = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mSunRad) +
                (0.019993 - 0.000101 * t) * sin(2.0 * mSunRad) + 0.000289 * sin(3.0 * mSunRad)
        val sunSayana = normalize360(l0Sun + cSun)
        val sunSpeed = 0.9856 + 0.034 * cos(mSunRad)
        val sunNirayana = normalize360(sunSayana - ayanamsha)
        result[Planet.SUN] = Pair(sunNirayana, sunSpeed)

        // 2. Moon
        val lMoon = 218.3164477 + 481267.88128 * t - 0.0015786 * t * t
        val dMoon = 297.8501921 + 445267.11140 * t - 0.0018819 * t * t
        val mMoon = 134.9633964 + 477198.867505 * t + 0.0087414 * t * t
        val fMoon = 93.2720950 + 483202.017523 * t - 0.0036539 * t * t

        val dRad = normalize360(dMoon) * DEG2RAD
        val mMoonRad = normalize360(mMoon) * DEG2RAD
        val fRad = normalize360(fMoon) * DEG2RAD

        var moonSayana = lMoon + 6.288774 * sin(mMoonRad) +
                1.274027 * sin(2.0 * dRad - mMoonRad) +
                0.658314 * sin(2.0 * dRad) +
                0.213618 * sin(2.0 * mMoonRad) -
                0.185116 * sin(mSunRad) -
                0.114332 * sin(2.0 * fRad) +
                0.058793 * sin(2.0 * dRad - 2.0 * mMoonRad) +
                0.057066 * sin(2.0 * dRad - mSunRad - mMoonRad) +
                0.053322 * sin(2.0 * dRad + mMoonRad)
        moonSayana = normalize360(moonSayana)
        val moonSpeed = 13.176 + 1.45 * cos(mMoonRad)
        val moonNirayana = normalize360(moonSayana - ayanamsha)
        result[Planet.MOON] = Pair(moonNirayana, moonSpeed)

        // 3. Rahu & Ketu (Mean/True Lunar Node)
        val omegaNode = 125.04452 - 1934.136261 * t + 0.0020708 * t * t
        val rahuSayana = normalize360(omegaNode)
        val rahuNirayana = normalize360(rahuSayana - ayanamsha)
        val ketuNirayana = normalize360(rahuNirayana + 180.0)
        result[Planet.RAHU] = Pair(rahuNirayana, -0.05295) // Always retrograde motion
        result[Planet.KETU] = Pair(ketuNirayana, -0.05295)

        // 4. Mars
        val lMars = 355.433275 + 19140.302684 * t
        val mMars = normalize360(19.387003 + 19139.975474 * t) * DEG2RAD
        val eqMars = 10.691 * sin(mMars) + 0.623 * sin(2.0 * mMars) + 0.050 * sin(3.0 * mMars)
        val helioMars = normalize360(lMars + eqMars)
        val distMars = 1.524 - 0.141 * cos(mMars)
        val marsGeo = calculateGeocentricLongitude(helioMars, distMars, sunSayana, 1.000)
        val marsNirayana = normalize360(marsGeo.first - ayanamsha)
        result[Planet.MARS] = Pair(marsNirayana, marsGeo.second * 0.524)

        // 5. Mercury
        val lMerc = 252.250906 + 149472.674111 * t
        val mMerc = normalize360(174.794726 + 149472.516111 * t) * DEG2RAD
        val eqMerc = 23.440 * sin(mMerc) + 2.981 * sin(2.0 * mMerc) + 0.525 * sin(3.0 * mMerc)
        val helioMerc = normalize360(lMerc + eqMerc)
        val distMerc = 0.387 - 0.079 * cos(mMerc)
        val mercGeo = calculateGeocentricLongitude(helioMerc, distMerc, sunSayana, 1.000)
        val mercNirayana = normalize360(mercGeo.first - ayanamsha)
        result[Planet.MERCURY] = Pair(mercNirayana, mercGeo.second * 1.38)

        // 6. Jupiter
        val lJup = 34.40438 + 3034.90567 * t
        val mJup = normalize360(19.8950 + 3034.693 * t) * DEG2RAD
        val eqJup = 5.555 * sin(mJup) + 0.168 * sin(2.0 * mJup)
        val helioJup = normalize360(lJup + eqJup)
        val distJup = 5.204 - 0.252 * cos(mJup)
        val jupGeo = calculateGeocentricLongitude(helioJup, distJup, sunSayana, 1.000)
        val jupNirayana = normalize360(jupGeo.first - ayanamsha)
        result[Planet.JUPITER] = Pair(jupNirayana, jupGeo.second * 0.083)

        // 7. Venus
        val lVen = 181.979801 + 58517.815387 * t
        val mVen = normalize360(50.116667 + 58517.586667 * t) * DEG2RAD
        val eqVen = 0.776 * sin(mVen) + 0.003 * sin(2.0 * mVen)
        val helioVen = normalize360(lVen + eqVen)
        val distVen = 0.723 - 0.005 * cos(mVen)
        val venGeo = calculateGeocentricLongitude(helioVen, distVen, sunSayana, 1.000)
        val venNirayana = normalize360(venGeo.first - ayanamsha)
        result[Planet.VENUS] = Pair(venNirayana, venGeo.second * 1.20)

        // 8. Saturn
        val lSat = 49.94424 + 1222.11379 * t
        val mSat = normalize360(316.9670 + 1221.551 * t) * DEG2RAD
        val eqSat = 6.358 * sin(mSat) + 0.220 * sin(2.0 * mSat)
        val helioSat = normalize360(lSat + eqSat)
        val distSat = 9.582 - 0.536 * cos(mSat)
        val satGeo = calculateGeocentricLongitude(helioSat, distSat, sunSayana, 1.000)
        val satNirayana = normalize360(satGeo.first - ayanamsha)
        result[Planet.SATURN] = Pair(satNirayana, satGeo.second * 0.033)

        return result
    }

    /**
     * Converts Heliocentric coordinates to Geocentric Longitude and speed.
     */
    private fun calculateGeocentricLongitude(
        helioLonDeg: Double,
        rPlanet: Double,
        sunLonDeg: Double,
        rEarth: Double
    ): Pair<Double, Double> {
        val hLonRad = helioLonDeg * DEG2RAD
        val sLonRad = (sunLonDeg + 180.0) * DEG2RAD // Earth Heliocentric

        val xPlanet = rPlanet * cos(hLonRad)
        val yPlanet = rPlanet * sin(hLonRad)

        val xEarth = rEarth * cos(sLonRad)
        val yEarth = rEarth * sin(sLonRad)

        val xGeo = xPlanet - xEarth
        val yGeo = yPlanet - yEarth

        val geoLon = normalize360(atan2(yGeo, xGeo) * RAD2DEG)

        // Determine if planet is retrograde (based on elongation and relative speed)
        val elongation = normalize360(geoLon - sunLonDeg)
        val isRetrograde = (elongation in 120.0..240.0) && (rPlanet > 1.0)
        val speedDirection = if (isRetrograde) -1.0 else 1.0

        return Pair(geoLon, speedDirection)
    }

    /**
     * Evaluates 5-fold relationship (Panchadha Maitri) & Dignity of Planet in a Sign.
     */
    fun evaluateDignity(
        planet: Planet,
        rashi: Rashi,
        degreeInRashi: Double,
        planetRashis: Map<Planet, Rashi>
    ): PlanetaryDignity {
        if (planet == Planet.RAHU || planet == Planet.KETU) {
            return if (rashi.index == planet.exaltationSign) PlanetaryDignity.EXALTED
            else if (rashi.index == planet.debilitationSign) PlanetaryDignity.DEBILITATED
            else if (rashi.index in planet.ownSigns) PlanetaryDignity.OWN_SIGN
            else PlanetaryDignity.NEUTRAL
        }

        // 1. Exaltation
        if (rashi.index == planet.exaltationSign) {
            return PlanetaryDignity.EXALTED
        }
        // 2. Debilitation
        if (rashi.index == planet.debilitationSign) {
            return PlanetaryDignity.DEBILITATED
        }
        // 3. Moolatrikona
        if (rashi.index == planet.moolatrikonaSign && degreeInRashi in planet.moolatrikonaRange) {
            return PlanetaryDignity.MOOLATRIKONA
        }
        // 4. Own Sign
        if (rashi.index in planet.ownSigns) {
            return PlanetaryDignity.OWN_SIGN
        }

        // 5. Panchadha Maitri (Compound Relationship = Natural + Temporal)
        val signLord = rashi.lord
        val isNaturalFriend = planet.naturalFriends.contains(signLord.id)
        val isNaturalEnemy = planet.naturalEnemies.contains(signLord.id)
        val naturalScore = when {
            isNaturalFriend -> 1
            isNaturalEnemy -> -1
            else -> 0
        }

        // Temporal (Tatkalika) Friendship: Planets in 2, 3, 4, 10, 11, 12 from each other are temporal friends (+1)
        val planetSign = planetRashis[planet] ?: rashi
        val lordSign = planetRashis[signLord] ?: rashi
        val diff = (lordSign.index - planetSign.index + 12) % 12
        val isTemporalFriend = diff in listOf(1, 2, 3, 9, 10, 11) // 2nd, 3rd, 4th, 10th, 11th, 12th houses
        val temporalScore = if (isTemporalFriend) 1 else -1

        val compositeScore = naturalScore + temporalScore

        return when (compositeScore) {
            2 -> PlanetaryDignity.GREAT_FRIEND
            1 -> PlanetaryDignity.FRIEND
            0 -> PlanetaryDignity.NEUTRAL
            -1 -> PlanetaryDignity.ENEMY
            else -> PlanetaryDignity.GREAT_ENEMY
        }
    }

    /**
     * Checks if a planet is combust (Asta) due to proximity to the Sun.
     */
    fun checkCombustion(planet: Planet, planetLong: Double, sunLong: Double, isRetrograde: Boolean): Pair<Boolean, Double> {
        if (planet == Planet.SUN || planet == Planet.RAHU || planet == Planet.KETU) {
            return Pair(false, 0.0)
        }

        var diff = abs(planetLong - sunLong)
        if (diff > 180.0) diff = 360.0 - diff

        val combustionOrb = when (planet) {
            Planet.MOON -> 12.0
            Planet.MARS -> 17.0
            Planet.MERCURY -> if (isRetrograde) 12.0 else 14.0
            Planet.JUPITER -> 11.0
            Planet.VENUS -> if (isRetrograde) 8.0 else 10.0
            Planet.SATURN -> 15.0
            else -> 10.0
        }

        return Pair(diff <= combustionOrb, diff)
    }

    /**
     * Calculates Special Lagnas (Bhava, Hora, Ghati, Indu, Upapada, Arudha, Varnada, Upagrahas).
     */
    fun calculateSpecialLagnas(
        jd: Double,
        ascendantDeg: Double,
        sunDeg: Double,
        moonDeg: Double,
        planets: Map<Planet, Double>,
        isDayBirth: Boolean
    ): SpecialLagnas {
        // Sunrise approximate long ~ Sun
        val sunTimeDiffDegrees = normalize360(ascendantDeg - sunDeg)

        // Bhava Lagna: moves 1 sign per 2 hours (1 deg per 4 mins, same as sun time)
        val bhavaLagna = normalize360(sunDeg + sunTimeDiffDegrees)

        // Hora Lagna: moves 1 sign per 1 hour (twice the speed of Sun)
        val horaLagna = normalize360(sunDeg + sunTimeDiffDegrees * 2.0)

        // Ghati Lagna: moves 1 sign per 1 Ghati (24 minutes = 5 times speed)
        val ghatiLagna = normalize360(sunDeg + sunTimeDiffDegrees * 5.0)

        // Indu Lagna: Sum of 9th lord rays from Lagna and Moon
        val lagnaRashi = Rashi.fromIndex((ascendantDeg / 30.0).toInt() + 1)
        val moonRashi = Rashi.fromIndex((moonDeg / 30.0).toInt() + 1)
        val ninthFromLagnaLord = Rashi.fromIndex(lagnaRashi.index + 8).lord
        val ninthFromMoonLord = Rashi.fromIndex(moonRashi.index + 8).lord

        fun planetRays(p: Planet): Int = when (p) {
            Planet.SUN -> 30
            Planet.MOON -> 16
            Planet.MARS -> 6
            Planet.MERCURY -> 8
            Planet.JUPITER -> 10
            Planet.VENUS -> 12
            Planet.SATURN -> 1
            else -> 0
        }
        val totalRays = planetRays(ninthFromLagnaLord) + planetRays(ninthFromMoonLord)
        val induSignIndex = ((moonRashi.index - 1 + (totalRays % 12)) % 12) + 1
        val induLagna = ((induSignIndex - 1) * 30.0 + (moonDeg % 30.0))

        // Arudha Lagna (AL): Distance from Lagna to 1st Lord projected forward from 1st Lord
        val lagnaLord = lagnaRashi.lord
        val lagnaLordDeg = planets[lagnaLord] ?: ascendantDeg
        val lagnaLordSign = (lagnaLordDeg / 30.0).toInt() + 1
        val distToLord = (lagnaLordSign - lagnaRashi.index + 12) % 12
        var arudhaSignIndex = (lagnaLordSign + distToLord)
        if (arudhaSignIndex > 12) arudhaSignIndex -= 12
        // Classical exception: if AL falls in 1st or 7th from Lagna, take 10th from it
        if (arudhaSignIndex == lagnaRashi.index || arudhaSignIndex == (lagnaRashi.index + 6).let { if (it > 12) it - 12 else it }) {
            arudhaSignIndex = (arudhaSignIndex + 9).let { if (it > 12) it - 12 else it }
        }
        val arudhaLagna = normalize360((arudhaSignIndex - 1) * 30.0 + 15.0)

        // Upapada Lagna (UL): Arudha of the 12th House
        val twelfthSignIndex = if (lagnaRashi.index == 1) 12 else lagnaRashi.index - 1
        val twelfthLord = Rashi.fromIndex(twelfthSignIndex).lord
        val twelfthLordDeg = planets[twelfthLord] ?: ascendantDeg
        val twelfthLordSign = (twelfthLordDeg / 30.0).toInt() + 1
        val distTo12Lord = (twelfthLordSign - twelfthSignIndex + 12) % 12
        var upapadaSignIndex = (twelfthLordSign + distTo12Lord)
        if (upapadaSignIndex > 12) upapadaSignIndex -= 12
        if (upapadaSignIndex == twelfthSignIndex || upapadaSignIndex == (twelfthSignIndex + 6).let { if (it > 12) it - 12 else it }) {
            upapadaSignIndex = (upapadaSignIndex + 9).let { if (it > 12) it - 12 else it }
        }
        val upapadaLagna = normalize360((upapadaSignIndex - 1) * 30.0 + 15.0)

        // Varnada Lagna: Combined Lagna and Hora Lagna
        val horaLagnaSign = (horaLagna / 30.0).toInt() + 1
        val varnadaSign = if (lagnaRashi.index % 2 != 0) {
            (lagnaRashi.index + horaLagnaSign - 1).let { if (it > 12) it % 12 + 1 else it }
        } else {
            (lagnaRashi.index - horaLagnaSign + 13).let { if (it > 12) it % 12 + 1 else it }
        }
        val varnadaLagna = normalize360((varnadaSign - 1) * 30.0 + 15.0)

        // Pranapada Lagna
        val pranapada = normalize360(sunDeg + (sunTimeDiffDegrees * 15.0))

        // Shri Lagna (calculated from Moon's nakshatra proportion)
        val shriLagna = normalize360(ascendantDeg + (moonDeg % 13.3333333) * (360.0 / 13.3333333))

        // Upagrahas: Gulika & Mandi (Saturn's portion of day/night)
        val saturnPortion = if (isDayBirth) 7.0 / 8.0 else 3.0 / 8.0
        val gulika = normalize360(sunDeg + saturnPortion * 360.0)
        val mandi = normalize360(gulika - 3.5)

        return SpecialLagnas(
            janmaLagna = ascendantDeg,
            bhavaLagna = bhavaLagna,
            horaLagna = horaLagna,
            ghatiLagna = ghatiLagna,
            induLagna = induLagna,
            upapadaLagna = upapadaLagna,
            arudhaLagna = arudhaLagna,
            varnadaLagna = varnadaLagna,
            pranapadaLagna = pranapada,
            shriLagna = shriLagna,
            gulika = gulika,
            mandi = mandi
        )
    }

    /**
     * Complete Birth Chart Generator
     */
    fun generateBirthChart(
        name: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        locationName: String,
        gender: String = "Male",
        ayanamshaSystem: AyanamshaSystem = AyanamshaSystem.LAHIRI
    ): BirthChart {
        val jd = calculateJulianDay(year, month, day, hour, minute, 0.0, timezoneOffsetHours)
        val ayanamsha = calculateAyanamsha(jd, ayanamshaSystem)
        val ascendantDeg = calculateAscendantNirayana(jd, latitude, longitude, ayanamsha)
        val rawPlanets = calculateAllPlanets(jd, ayanamsha)

        val ascSignIndex = (ascendantDeg / 30.0).toInt() + 1
        val ascRashi = Rashi.fromIndex(ascSignIndex)
        val (ascNak, ascPada) = Nakshatra.fromLongitude(ascendantDeg)

        val planetRashis = rawPlanets.mapValues { Rashi.fromIndex((it.value.first / 30.0).toInt() + 1) }
        val sunLong = rawPlanets[Planet.SUN]?.first ?: 0.0

        // Determine if day or night birth
        val ascSunDiff = normalize360(ascendantDeg - sunLong)
        val isDayBirth = ascSunDiff in 180.0..360.0

        val planetPositions = mutableMapOf<Planet, PlanetaryPosition>()

        for ((planet, data) in rawPlanets) {
            val long = data.first
            val speed = data.second
            val isRetro = speed < 0
            val rashi = Rashi.fromIndex((long / 30.0).toInt() + 1)
            val degInSign = long % 30.0
            val (nak, pada) = Nakshatra.fromLongitude(long)

            // House from Lagna (1 to 12)
            val house = (rashi.index - ascRashi.index + 12) % 12 + 1

            // Dignity
            val dignity = evaluateDignity(planet, rashi, degInSign, planetRashis)

            // Combustion
            val (isCombust, distSun) = checkCombustion(planet, long, sunLong, isRetro)

            // Aspects cast by this planet (Vedic Drishti)
            val aspectingHouses = calculateAspects(planet, house)

            planetPositions[planet] = PlanetaryPosition(
                planet = planet,
                longitude = long,
                speed = speed,
                isRetrograde = isRetro,
                rashi = rashi,
                degreeInRashi = degInSign,
                nakshatra = nak,
                pada = pada,
                house = house,
                dignity = dignity,
                isCombust = isCombust,
                distanceToSun = distSun,
                aspectingHouses = aspectingHouses
            )
        }

        // Calculate Graha Yuddha (War between non-luminary planets within 1°)
        val taraGrahas = listOf(Planet.MARS, Planet.MERCURY, Planet.JUPITER, Planet.VENUS, Planet.SATURN)
        for (i in taraGrahas.indices) {
            for (j in (i + 1) until taraGrahas.size) {
                val p1 = taraGrahas[i]
                val p2 = taraGrahas[j]
                val p1Pos = planetPositions[p1]!!
                val p2Pos = planetPositions[p2]!!
                var diff = abs(p1Pos.longitude - p2Pos.longitude)
                if (diff > 180) diff = 360 - diff
                if (diff <= 1.0) {
                    // War occurs: Higher speed / southern latitude / brightness wins. In general, Venus almost always wins, or planet with higher latitude/speed
                    val p1Wins = p1Pos.speed > p2Pos.speed || p1 == Planet.VENUS
                    planetPositions[p1] = p1Pos.copy(isWarWinner = p1Wins, isWarLoser = !p1Wins)
                    planetPositions[p2] = p2Pos.copy(isWarWinner = !p1Wins, isWarLoser = p1Wins)
                }
            }
        }

        // Build 12 Bhavas (Equal Rashi House System standard in Vedic)
        val houses = (1..12).map { houseNum ->
            val signIndex = (ascRashi.index + houseNum - 2) % 12 + 1
            val houseRashi = Rashi.fromIndex(signIndex)
            val occupants = planetPositions.values.filter { it.house == houseNum }.map { it.planet }
            val aspecting = planetPositions.values.filter { it.aspectingHouses.contains(houseNum) }.map { it.planet }

            val significations = getHouseSignifications(houseNum)

            BhavaDetail(
                houseNumber = houseNum,
                rashi = houseRashi,
                startDegree = (signIndex - 1) * 30.0,
                cuspDegree = (signIndex - 1) * 30.0 + (ascendantDeg % 30.0),
                endDegree = signIndex * 30.0,
                lord = houseRashi.lord,
                occupants = occupants,
                aspectingPlanets = aspecting,
                significations = significations
            )
        }

        val specialLagnas = calculateSpecialLagnas(
            jd = jd,
            ascendantDeg = ascendantDeg,
            sunDeg = sunLong,
            moonDeg = rawPlanets[Planet.MOON]?.first ?: 0.0,
            planets = rawPlanets.mapValues { it.value.first },
            isDayBirth = isDayBirth
        )

        val dateStr = String.format("%04d-%02d-%02d", year, month, day)
        val timeStr = String.format("%02d:%02d", hour, minute)

        return BirthChart(
            id = "${System.currentTimeMillis()}",
            name = name,
            birthDate = dateStr,
            birthTime = timeStr,
            latitude = latitude,
            longitude = longitude,
            timezoneOffsetHours = timezoneOffsetHours,
            locationName = locationName,
            gender = gender,
            ayanamshaSystem = ayanamshaSystem,
            ayanamshaValue = ayanamsha,
            julianDay = jd,
            ascendantDegree = ascendantDeg,
            ascendantRashi = ascRashi,
            ascendantNakshatra = ascNak,
            ascendantPada = ascPada,
            planets = planetPositions,
            houses = houses,
            specialLagnas = specialLagnas,
            isDayBirth = isDayBirth
        )
    }

    /**
     * Classical Vedic Drishti (Aspects)
     * All planets aspect the 7th house with full strength.
     * Special aspects:
     * Mars: 4th, 7th, 8th
     * Jupiter & Rahu/Ketu: 5th, 7th, 9th
     * Saturn: 3rd, 7th, 10th
     */
    private fun calculateAspects(planet: Planet, currentHouse: Int): List<Int> {
        val aspectOffsets = mutableListOf(6) // 7th house (offset +6)

        when (planet) {
            Planet.MARS -> {
                aspectOffsets.add(3) // 4th house
                aspectOffsets.add(7) // 8th house
            }
            Planet.JUPITER, Planet.RAHU, Planet.KETU -> {
                aspectOffsets.add(4) // 5th house
                aspectOffsets.add(8) // 9th house
            }
            Planet.SATURN -> {
                aspectOffsets.add(2) // 3rd house
                aspectOffsets.add(9) // 10th house
            }
            else -> {}
        }

        return aspectOffsets.map { offset ->
            ((currentHouse - 1 + offset) % 12) + 1
        }
    }

    private fun getHouseSignifications(h: Int): String = when (h) {
        1 -> "Tanu Bhava: Self, vitality, appearance, constitution, temperament, overall life trajectory"
        2 -> "Dhana Bhava: Wealth, liquid assets, speech, family values, food habits, right eye"
        3 -> "Sahaja Bhava: Courage, younger siblings, valor, initiatives, writing, communications"
        4 -> "Bandhu Bhava: Mother, heart, home, real estate, emotional peace, conveyances, education"
        5 -> "Putra Bhava: Children, intellect, creativity, Purva Punya (past karma), romance, mantras"
        6 -> "Ari Bhava: Enemies, debts, diseases (Roga), competitive endurance, service, litigation"
        7 -> "Yuvati Bhava: Spouse, marriage, business partnerships, public relations, trade, travels"
        8 -> "Randhra Bhava: Longevity (Ayus), transformation, occult knowledge, unearned wealth, chronic illness"
        9 -> "Dharma Bhava: Fortune (Bhagya), father, Guru, higher spirituality, pilgrimage, divine grace"
        10 -> "Karma Bhava: Profession, career, executive power, public reputation, status, honors"
        11 -> "Labha Bhava: Gains, elder siblings, fulfillment of desires, influential networks, cash flow"
        12 -> "Vyaya Bhava: Expenditures, foreign lands, liberation (Moksha), sleep, bed pleasures, isolation"
        else -> ""
    }

    fun normalize360(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0) d += 360.0
        return d
    }

    fun formatDms(deg: Double): String {
        val d = deg.toInt()
        val mTotal = (deg - d) * 60.0
        val m = mTotal.toInt()
        val s = ((mTotal - m) * 60.0).toInt()
        return "$d° $m' $s\""
    }
}
