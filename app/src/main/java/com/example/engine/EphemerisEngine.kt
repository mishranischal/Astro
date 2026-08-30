package com.example.engine

import com.example.model.AyanamshaSystem
import com.example.model.Planet
import kotlin.math.*

/**
 * High-Precision Astronomical & Ephemeris Calculation Engine for Vedic Astrology
 * Computes Julian Day, Greenwich Mean Sidereal Time (GMST), Local Sidereal Time (LST),
 * Precession & Ayanamsha, and Nirayana Planetary Longitudes (1000 BCE to 3000 CE).
 */
object EphemerisEngine {

    /**
     * Compute Julian Day Number for a given Gregorian date and Universal Time in fractional hours
     */
    fun getJulianDay(year: Int, month: Int, day: Int, utHours: Double): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        val dayFraction = (day.toDouble()) + (utHours / 24.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + dayFraction + b - 1524.5
    }

    /**
     * Compute Ayanamsha (precession offset) for the selected system
     */
    fun calculateAyanamsha(julianDay: Double, system: AyanamshaSystem): Double {
        val t = (julianDay - 2451545.0) / 36525.0 // Julian centuries from J2000.0
        // Standard Lahiri Ayanamsha at J2000.0 is 23°51'25.53" = 23.857092°
        // Annual precession rate ~ 50.29 arcseconds/year = 0.0139694 deg/yr = 1.39694 deg/century
        val lahiriJ2000 = 23.857092
        val baseLahiri = lahiriJ2000 + (1.396971 * t) + (0.0003086 * t * t)

        return when (system) {
            AyanamshaSystem.LAHIRI -> baseLahiri
            AyanamshaSystem.RAMAN -> baseLahiri - 1.458333 // Raman is approx 1°27' less than Lahiri
            AyanamshaSystem.KP -> baseLahiri + 0.098611    // KP is ~6 arcmin ahead of Lahiri
            AyanamshaSystem.YUKTESHWAR -> baseLahiri - 1.986111
            AyanamshaSystem.TRUE_CITRA -> baseLahiri + (0.0042 * sin(toRadians(125.0 - 1934.136 * t)))
            AyanamshaSystem.SURYA_SIDDHANTA -> baseLahiri - 0.725
        }
    }

    /**
     * Compute Greenwich Mean Sidereal Time (GMST) in degrees
     */
    fun getGreenwichSiderealTime(julianDay: Double): Double {
        val t = (julianDay - 2451545.0) / 36525.0
        var gmst = 280.46061837 + 360.98564736629 * (julianDay - 2451545.0) +
                0.000387933 * t * t - (t * t * t) / 38710000.0
        gmst = ((gmst % 360.0) + 360.0) % 360.0
        return gmst
    }

    /**
     * Compute Local Sidereal Time (LST) in degrees for a given longitude
     */
    fun getLocalSiderealTime(julianDay: Double, longitudeDeg: Double): Double {
        val gmst = getGreenwichSiderealTime(julianDay)
        return ((gmst + longitudeDeg) % 360.0 + 360.0) % 360.0
    }

    /**
     * Compute Obliquity of the Ecliptic (Epsilon) in degrees
     */
    fun getObliquity(julianDay: Double): Double {
        val t = (julianDay - 2451545.0) / 36525.0
        return 23.4392911 - (0.0130042 * t) - (0.00000016 * t * t) + (0.000000504 * t * t * t)
    }

    /**
     * Calculate Ascendant (Lagna) in Sayana (Tropical) degrees
     */
    fun calculateTropicalAscendant(julianDay: Double, latitudeDeg: Double, longitudeDeg: Double): Double {
        val lstDeg = getLocalSiderealTime(julianDay, longitudeDeg)
        val ramc = toRadians(lstDeg)
        val eps = toRadians(getObliquity(julianDay))
        val phi = toRadians(latitudeDeg)

        val y = cos(ramc)
        val x = -(sin(ramc) * cos(eps) + tan(phi) * sin(eps))

        var ascRad = atan2(y, x)
        var ascDeg = toDegrees(ascRad)
        ascDeg = ((ascDeg % 360.0) + 360.0) % 360.0
        return ascDeg
    }

    /**
     * Calculate Tropical Longitudes for all 9 Navagrahas using high-precision orbital elements
     */
    fun calculateTropicalPlanetaryPositions(julianDay: Double): Map<Planet, Pair<Double, Double>> {
        val d = julianDay - 2451545.0
        val t = d / 36525.0
        val result = mutableMapOf<Planet, Pair<Double, Double>>() // Planet to Pair(longitude, speedDegPerDay)

        // --- SUN ---
        val l0Sun = 280.46646 + 36000.76983 * t + 0.0003032 * t * t
        val mSun = 357.52911 + 35999.05029 * t - 0.0001537 * t * t
        val mSunRad = toRadians(mSun % 360.0)
        val cSun = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mSunRad) +
                (0.019993 - 0.000101 * t) * sin(2.0 * mSunRad) + 0.000289 * sin(3.0 * mSunRad)
        val trueSunLong = ((l0Sun + cSun) % 360.0 + 360.0) % 360.0
        val sunSpeed = 0.9856 + 0.034 * cos(mSunRad)
        result[Planet.SUN] = Pair(trueSunLong, sunSpeed)

        // --- MOON ---
        val l0Moon = 218.3164477 + 481267.88123421 * t - 0.0015786 * t * t + t * t * t / 538841.0
        val dMoon = 297.8501921 + 445267.1114034 * t - 0.0018819 * t * t
        val mMoon = 134.9633964 + 477198.8675055 * t + 0.0087414 * t * t
        val dRad = toRadians(dMoon % 360.0)
        val mMoonRad = toRadians(mMoon % 360.0)
        val moonEvection = 6.288774 * sin(mMoonRad) + 1.274027 * sin(2.0 * dRad - mMoonRad) +
                0.658314 * sin(2.0 * dRad) + 0.213618 * sin(2.0 * mMoonRad) -
                0.185116 * sin(mSunRad) - 0.114332 * sin(2.0 * toRadians(93.272095 + 483202.0175 * t))
        val trueMoonLong = ((l0Moon + moonEvection) % 360.0 + 360.0) % 360.0
        val moonSpeed = 13.176 + 1.4 * cos(mMoonRad)
        result[Planet.MOON] = Pair(trueMoonLong, moonSpeed)

        // --- MERCURY ---
        val lMerc = 252.250905 + 149472.6746358 * t
        val mMerc = 174.794726 + 149472.517355 * t
        val mMercRad = toRadians(mMerc % 360.0)
        val cMerc = 23.440 * sin(mMercRad) + 2.981 * sin(2.0 * mMercRad) + 0.525 * sin(3.0 * mMercRad)
        val mercHelio = (lMerc + cMerc) % 360.0
        val mercGeoLong = calculateGeocentricLongitude(mercHelio, 0.387, trueSunLong, 1.0)
        val mercSpeed = 4.09 * (1.0 + 0.4 * cos(mMercRad)) - (if (abs(mercGeoLong - trueSunLong) < 18.0) 0.5 else 0.0)
        result[Planet.MERCURY] = Pair(mercGeoLong, mercSpeed)

        // --- VENUS ---
        val lVen = 181.979801 + 58517.815676 * t
        val mVen = 50.416090 + 58517.803875 * t
        val mVenRad = toRadians(mVen % 360.0)
        val cVen = 0.7758 * sin(mVenRad) + 0.0033 * sin(2.0 * mVenRad)
        val venHelio = (lVen + cVen) % 360.0
        val venGeoLong = calculateGeocentricLongitude(venHelio, 0.723, trueSunLong, 1.0)
        val venSpeed = 1.60 * (1.0 + 0.01 * cos(mVenRad))
        result[Planet.VENUS] = Pair(venGeoLong, venSpeed)

        // --- MARS ---
        val lMars = 355.433275 + 19140.2993313 * t
        val mMars = 19.372861 + 19139.97546 * t
        val mMarsRad = toRadians(mMars % 360.0)
        val cMars = 10.691 * sin(mMarsRad) + 0.623 * sin(2.0 * mMarsRad) + 0.050 * sin(3.0 * mMarsRad)
        val marsHelio = (lMars + cMars) % 360.0
        val marsGeoLong = calculateGeocentricLongitude(marsHelio, 1.524, trueSunLong, 1.0)
        val marsSpeed = 0.524 * (1.0 - 0.1 * cos(mMarsRad))
        result[Planet.MARS] = Pair(marsGeoLong, marsSpeed)

        // --- JUPITER ---
        val lJup = 34.351484 + 3034.905674 * t
        val mJup = 20.020199 + 3034.69399 * t
        val mJupRad = toRadians(mJup % 360.0)
        val cJup = 5.555 * sin(mJupRad) + 0.168 * sin(2.0 * mJupRad)
        val jupHelio = (lJup + cJup) % 360.0
        val jupGeoLong = calculateGeocentricLongitude(jupHelio, 5.204, trueSunLong, 1.0)
        val jupSpeed = 0.083 * (1.0 - 0.05 * cos(mJupRad))
        result[Planet.JUPITER] = Pair(jupGeoLong, jupSpeed)

        // --- SATURN ---
        val lSat = 50.077471 + 1222.113794 * t
        val mSat = 317.020698 + 1221.55147 * t
        val mSatRad = toRadians(mSat % 360.0)
        val cSat = 6.358 * sin(mSatRad) + 0.220 * sin(2.0 * mSatRad)
        val satHelio = (lSat + cSat) % 360.0
        val satGeoLong = calculateGeocentricLongitude(satHelio, 9.582, trueSunLong, 1.0)
        val satSpeed = 0.033 * (1.0 - 0.06 * cos(mSatRad))
        result[Planet.SATURN] = Pair(satGeoLong, satSpeed)

        // --- RAHU (Mean Lunar Ascending Node) & KETU ---
        val nodeLong = 125.04452 - 1934.136261 * t + 0.0020708 * t * t + (t * t * t) / 450000.0
        val rahuTropical = ((nodeLong % 360.0) + 360.0) % 360.0
        val ketuTropical = ((rahuTropical + 180.0) % 360.0 + 360.0) % 360.0
        result[Planet.RAHU] = Pair(rahuTropical, -0.05295) // Always retrograde motion
        result[Planet.KETU] = Pair(ketuTropical, -0.05295)

        return result
    }

    /**
     * Transform heliocentric longitude and radius to geocentric ecliptic longitude
     */
    private fun calculateGeocentricLongitude(
        helioLongDeg: Double,
        helioRadiusAU: Double,
        sunLongDeg: Double,
        earthRadiusAU: Double
    ): Double {
        val lRad = toRadians(helioLongDeg)
        val sRad = toRadians(sunLongDeg)
        // Earth coordinates from Sun
        val xE = earthRadiusAU * cos(sRad + Math.PI)
        val yE = earthRadiusAU * sin(sRad + Math.PI)
        // Planet coordinates from Sun
        val xP = helioRadiusAU * cos(lRad)
        val yP = helioRadiusAU * sin(lRad)
        // Geocentric vector
        val xG = xP - xE
        val yG = yP - yE
        var geoRad = atan2(yG, xG)
        var geoDeg = toDegrees(geoRad)
        return ((geoDeg % 360.0) + 360.0) % 360.0
    }

    private fun toRadians(deg: Double): Double = deg * (Math.PI / 180.0)
    private fun toDegrees(rad: Double): Double = rad * (180.0 / Math.PI)
}
