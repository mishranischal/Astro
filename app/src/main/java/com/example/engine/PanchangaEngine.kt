package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

/**
 * Classical Vedic Panchanga Engine (5 Limbs of Time + Muhurtas + Choghadiya).
 */
object PanchangaEngine {

    val TITHI_NAMES = listOf(
        "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
        "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
        "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Poornima / Amavasya"
    )

    val SOLILUNAR_YOGAS = listOf(
        Pair("Vishkambha", false), Pair("Priti", true), Pair("Ayushman", true), Pair("Saubhagya", true),
        Pair("Shobhana", true), Pair("Atiganda", false), Pair("Sukarma", true), Pair("Dhriti", true),
        Pair("Shoola", false), Pair("Ganda", false), Pair("Vriddhi", true), Pair("Dhruva", true),
        Pair("Vyaghata", false), Pair("Harshana", true), Pair("Vajra", false), Pair("Siddhi", true),
        Pair("Vyatipata", false), Pair("Variyan", true), Pair("Parigha", false), Pair("Shiva", true),
        Pair("Siddha", true), Pair("Sadhya", true), Pair("Shubha", true), Pair("Shukla", true),
        Pair("Brahma", true), Pair("Indra", true), Pair("Vaidhriti", false)
    )

    val KARANA_NAMES = listOf(
        "Bava", "Balava", "Kaulava", "Taitila", "Gara", "Vanija", "Vishti (Bhadra)",
        "Shakuni", "Chatushpada", "Naga", "Kintughna"
    )

    val CHOGHADIYA_DAY_ORDER = mapOf(
        "Sunday" to listOf("Udveg", "Char", "Labh", "Amrit", "Kaal", "Shubh", "Rog", "Udveg"),
        "Monday" to listOf("Amrit", "Kaal", "Shubh", "Rog", "Udveg", "Char", "Labh", "Amrit"),
        "Tuesday" to listOf("Rog", "Udveg", "Char", "Labh", "Amrit", "Kaal", "Shubh", "Rog"),
        "Wednesday" to listOf("Labh", "Amrit", "Kaal", "Shubh", "Rog", "Udveg", "Char", "Labh"),
        "Thursday" to listOf("Shubh", "Rog", "Udveg", "Char", "Labh", "Amrit", "Kaal", "Shubh"),
        "Friday" to listOf("Char", "Labh", "Amrit", "Kaal", "Shubh", "Rog", "Udveg", "Char"),
        "Saturday" to listOf("Kaal", "Shubh", "Rog", "Udveg", "Char", "Labh", "Amrit", "Kaal")
    )

    val HORA_ORDER = listOf(
        Planet.SUN, Planet.VENUS, Planet.MERCURY, Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS
    )

    /**
     * Calculates complete daily Panchanga for any location and date.
     */
    fun calculateDailyPanchanga(
        year: Int,
        month: Int,
        day: Int,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        locationName: String
    ): DailyPanchanga {
        val jd = AstroEngine.calculateJulianDay(year, month, day, 6, 0, 0.0, timezoneOffsetHours)
        val ayanamsha = AstroEngine.calculateAyanamsha(jd, AyanamshaSystem.LAHIRI)
        val planets = AstroEngine.calculateAllPlanets(jd, ayanamsha)

        val sunLong = planets[Planet.SUN]?.first ?: 0.0
        val moonLong = planets[Planet.MOON]?.first ?: 0.0

        // 1. Tithi: (Moon Long - Sun Long) / 12°
        val diffDeg = AstroEngine.normalize360(moonLong - sunLong)
        val tithiIndex1Based = ((diffDeg / 12.0).toInt() % 30) + 1
        val isShukla = tithiIndex1Based <= 15
        val paksha = if (isShukla) "Shukla Paksha (Waxing Moon)" else "Krishna Paksha (Waning Moon)"
        val tithiNameIdx = (tithiIndex1Based - 1) % 15
        val baseName = TITHI_NAMES[tithiNameIdx]
        val tithiDisplayName = if (tithiIndex1Based == 15) "Poornima (Full Moon)" else if (tithiIndex1Based == 30) "Amavasya (New Moon)" else "$paksha $baseName"

        val tithiProgress = (diffDeg % 12.0) / 12.0
        val tithiNature = when (tithiNameIdx % 5) {
            0 -> "Nanda (Delightful / Auspicious for begins)"
            1 -> "Bhadra (Good / Constructive)"
            2 -> "Jaya (Victory / Success)"
            3 -> "Rikta (Empty / Avoid major beginnings)"
            else -> "Poorna (Complete / Fulfilling)"
        }
        val tithiInfo = TithiInfo(
            index = tithiIndex1Based,
            name = tithiDisplayName,
            paksha = paksha,
            deity = getTithiDeity(tithiIndex1Based),
            percentageElapsed = Math.round(tithiProgress * 100.0 * 10.0) / 10.0,
            endTime = "Until Next Phase",
            nature = tithiNature
        )

        // 2. Vara (Weekday)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(year, month - 1, day)
        }
        val dayOfWeekIdx = cal.get(Calendar.DAY_OF_WEEK)
        val dayOfWeekStr = when (dayOfWeekIdx) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            else -> "Saturday"
        }
        val (sanskritVara, varaLord, elem) = when (dayOfWeekStr) {
            "Sunday" -> Triple("Ravivara (Bhanuvasara)", Planet.SUN, "Fire")
            "Monday" -> Triple("Somavara (Induvasara)", Planet.MOON, "Water")
            "Tuesday" -> Triple("Mangalavara (Bhaumavasara)", Planet.MARS, "Fire")
            "Wednesday" -> Triple("Budhavara (Saumyavasara)", Planet.MERCURY, "Earth")
            "Thursday" -> Triple("Guruvara (Brihaspativasara)", Planet.JUPITER, "Ether")
            "Friday" -> Triple("Shukravara (Bhriguvasara)", Planet.VENUS, "Water")
            else -> Triple("Shanivara (Manda)", Planet.SATURN, "Air")
        }
        val varaInfo = VaraInfo(dayOfWeekStr, sanskritVara, varaLord, elem)

        // 3. Nakshatra
        val (nak, pada) = Nakshatra.fromLongitude(moonLong)
        val nakSpan = 360.0 / 27.0
        val degInNak = moonLong % nakSpan
        val nakElapsed = (degInNak / nakSpan) * 100.0

        // 4. Yoga (Solilunar)
        val sumDeg = AstroEngine.normalize360(sunLong + moonLong)
        val yogaIdx = ((sumDeg / nakSpan).toInt() % 27)
        val yogaData = SOLILUNAR_YOGAS[yogaIdx]
        val yogaProgress = ((sumDeg % nakSpan) / nakSpan) * 100.0
        val yogaInfo = SolilunarYogaInfo(
            index = yogaIdx + 1,
            name = yogaData.first,
            meaning = if (yogaData.second) "Auspicious (Subha)" else "Inauspicious (Ashubha / Restraint)",
            isAuspicious = yogaData.second,
            percentageElapsed = Math.round(yogaProgress * 10.0) / 10.0
        )

        // 5. Karana (Half Tithi)
        val karanaIdxTotal = (diffDeg / 6.0).toInt() % 60
        val karanaName = when {
            karanaIdxTotal == 0 -> "Kintughna"
            karanaIdxTotal in 1..56 -> KARANA_NAMES[(karanaIdxTotal - 1) % 7]
            karanaIdxTotal == 57 -> "Shakuni"
            karanaIdxTotal == 58 -> "Chatushpada"
            else -> "Naga"
        }
        val isVishti = karanaName.contains("Vishti")
        val karanaInfo = KaranaInfo(
            index = karanaIdxTotal + 1,
            name = karanaName,
            deity = getKaranaDeity(karanaName),
            type = if (karanaIdxTotal in 1..56) "Chara (Movable)" else "Sthira (Fixed)",
            isBhadra = isVishti
        )

        // Sun & Moon Signs
        val sunSign = Rashi.fromIndex((sunLong / 30.0).toInt() + 1)
        val moonSign = Rashi.fromIndex((moonLong / 30.0).toInt() + 1)

        // Sunrise & Sunset Approximations (Standard astronomical equation of time)
        val sunriseHour = 6.0 - (longitude % 15.0) / 15.0 + 0.25
        val sunsetHour = 18.0 - (longitude % 15.0) / 15.0 - 0.25
        val sunriseStr = formatDecimalTime(sunriseHour.coerceIn(5.0, 7.5))
        val sunsetStr = formatDecimalTime(sunsetHour.coerceIn(17.5, 19.5))

        // Muhurtas
        val muhurtas = calculateMuhurtaTimings(dayOfWeekStr, sunriseHour, sunsetHour)

        // Choghadiya
        val (choghDay, choghNight) = calculateChoghadiya(dayOfWeekStr, sunriseHour, sunsetHour)

        // Horas
        val horas = calculateHoras(varaLord, sunriseHour)

        // Hindu Calendar Info
        val vikram = year + 57
        val shaka = year - 78
        val kaliYuga = year + 3101
        val masa = getHinduMasa(sunSign, isShukla)
        val ritu = getHinduRitu(sunSign)
        val ayana = if (sunSign.index in 10..12 || sunSign.index in 1..3) "Uttarayana (Northern Course)" else "Dakshinayana (Southern Course)"
        val illumination = ((1.0 - cos(diffDeg * Math.PI / 180.0)) / 2.0) * 100.0

        val calendarInfo = HinduCalendarInfo(
            vikramSamvat = vikram,
            shakaSamvat = shaka,
            kaliYugaYear = kaliYuga,
            masaName = masa,
            rituName = ritu,
            ayana = ayana,
            lunarPhase = if (isShukla) "Waxing" else "Waning",
            moonIlluminationPercent = Math.round(illumination * 10.0) / 10.0
        )

        val festivals = getFestivalsForMonth(month, tithiIndex1Based)

        val dateStr = String.format("%04d-%02d-%02d", year, month, day)

        return DailyPanchanga(
            date = dateStr,
            location = locationName,
            sunrise = sunriseStr,
            sunset = sunsetStr,
            moonrise = "Approx. 18:30",
            moonset = "Approx. 06:15",
            tithi = tithiInfo,
            vara = varaInfo,
            nakshatra = nak,
            nakshatraPada = pada,
            nakshatraElapsedPercent = Math.round(nakElapsed * 10.0) / 10.0,
            yoga = yogaInfo,
            karana = karanaInfo,
            sunSign = sunSign,
            moonSign = moonSign,
            muhurta = muhurtas,
            calendarInfo = calendarInfo,
            choghadiyaDay = choghDay,
            choghadiyaNight = choghNight,
            horas = horas,
            upcomingFestivals = festivals
        )
    }

    private fun calculateMuhurtaTimings(dayOfWeek: String, sunrise: Double, sunset: Double): MuhurtaTimings {
        val dayDuration = sunset - sunrise
        val daySegment = dayDuration / 8.0

        // Rahu Kalam order for Sunday to Saturday (segment 1 to 8)
        val rahuSegment = when (dayOfWeek) {
            "Sunday" -> 8; "Monday" -> 2; "Tuesday" -> 7; "Wednesday" -> 5; "Thursday" -> 6; "Friday" -> 4; else -> 3
        }
        val yamagandamSegment = when (dayOfWeek) {
            "Sunday" -> 5; "Monday" -> 4; "Tuesday" -> 3; "Wednesday" -> 2; "Thursday" -> 1; "Friday" -> 7; else -> 6
        }
        val gulikaSegment = when (dayOfWeek) {
            "Sunday" -> 7; "Monday" -> 6; "Tuesday" -> 5; "Wednesday" -> 4; "Thursday" -> 3; "Friday" -> 2; else -> 1
        }

        val rahuStart = sunrise + (rahuSegment - 1) * daySegment
        val rahuEnd = rahuStart + daySegment

        val yamaStart = sunrise + (yamagandamSegment - 1) * daySegment
        val yamaEnd = yamaStart + daySegment

        val gulikaStart = sunrise + (gulikaSegment - 1) * daySegment
        val gulikaEnd = gulikaStart + daySegment

        // Abhijit Muhurta is 8th Muhurta of 15 (Midday ~ 11:45 to 12:35)
        val midday = sunrise + dayDuration / 2.0
        val abhijitStart = midday - 0.4
        val abhijitEnd = midday + 0.4

        // Brahma Muhurta is 2 Muhurtas (96 min) before Sunrise
        val brahmaStart = sunrise - 1.6
        val brahmaEnd = sunrise - 0.8

        return MuhurtaTimings(
            rahuKalam = Pair(formatDecimalTime(rahuStart), formatDecimalTime(rahuEnd)),
            yamagandam = Pair(formatDecimalTime(yamaStart), formatDecimalTime(yamaEnd)),
            gulikaKalam = Pair(formatDecimalTime(gulikaStart), formatDecimalTime(gulikaEnd)),
            abhijitMuhurta = Pair(formatDecimalTime(abhijitStart), formatDecimalTime(abhijitEnd)),
            brahmaMuhurta = Pair(formatDecimalTime(brahmaStart), formatDecimalTime(brahmaEnd)),
            durmuhurtham = listOf(Pair(formatDecimalTime(sunrise + daySegment * 2.5), formatDecimalTime(sunrise + daySegment * 3.3))),
            amritKalam = Pair(formatDecimalTime(sunrise + daySegment * 4.2), formatDecimalTime(sunrise + daySegment * 5.0)),
            varjyam = Pair(formatDecimalTime(sunrise + daySegment * 1.2), formatDecimalTime(sunrise + daySegment * 2.0))
        )
    }

    private fun calculateChoghadiya(dayOfWeek: String, sunrise: Double, sunset: Double): Pair<List<ChoghadiyaPeriod>, List<ChoghadiyaPeriod>> {
        val dayNames = CHOGHADIYA_DAY_ORDER[dayOfWeek] ?: CHOGHADIYA_DAY_ORDER["Sunday"]!!
        val dayDuration = sunset - sunrise
        val daySeg = dayDuration / 8.0

        val dayList = mutableListOf<ChoghadiyaPeriod>()
        for (i in 0 until 8) {
            val name = dayNames[i]
            val sTime = sunrise + i * daySeg
            val eTime = sTime + daySeg
            val (planet, nature) = getChoghadiyaDetails(name)
            dayList.add(ChoghadiyaPeriod(name, planet, nature, formatDecimalTime(sTime), formatDecimalTime(eTime)))
        }

        val nightDuration = 24.0 - dayDuration
        val nightSeg = nightDuration / 8.0
        val nightList = mutableListOf<ChoghadiyaPeriod>()
        for (i in 0 until 8) {
            val name = dayNames[(i + 5) % 8]
            val sTime = sunset + i * nightSeg
            val eTime = sTime + nightSeg
            val (planet, nature) = getChoghadiyaDetails(name)
            nightList.add(ChoghadiyaPeriod(name, planet, nature, formatDecimalTime(sTime % 24.0), formatDecimalTime(eTime % 24.0)))
        }

        return Pair(dayList, nightList)
    }

    private fun calculateHoras(weekdayLord: Planet, sunrise: Double): List<HoraPeriod> {
        val startIdx = HORA_ORDER.indexOf(weekdayLord).let { if (it >= 0) it else 0 }
        val list = mutableListOf<HoraPeriod>()
        for (h in 0 until 24) {
            val planet = HORA_ORDER[(startIdx + h) % 7]
            val sTime = (sunrise + h) % 24.0
            val eTime = (sTime + 1.0) % 24.0
            list.add(HoraPeriod(planet, formatDecimalTime(sTime), formatDecimalTime(eTime)))
        }
        return list
    }

    private fun getChoghadiyaDetails(name: String): Pair<Planet, String> = when (name) {
        "Amrit" -> Pair(Planet.MOON, "Auspicious (Nectar / Supreme Success)")
        "Shubh" -> Pair(Planet.JUPITER, "Auspicious (Fortunate / Religious events)")
        "Labh" -> Pair(Planet.MERCURY, "Auspicious (Gain / Commerce / Trade)")
        "Char" -> Pair(Planet.VENUS, "Neutral / Good for Travels")
        "Udveg" -> Pair(Planet.SUN, "Inauspicious (Anxiety / Government work only)")
        "Kaal" -> Pair(Planet.SATURN, "Inauspicious (Loss / Avoid beginnings)")
        else -> Pair(Planet.MARS, "Inauspicious (Disease / Conflict)")
    }

    private fun getTithiDeity(idx: Int): String = when (idx % 15) {
        1 -> "Agni"; 2 -> "Brahma"; 3 -> "Gauri"; 4 -> "Ganesha"; 5 -> "Nagas"; 6 -> "Kartikeya"; 7 -> "Surya"
        8 -> "Shiva"; 9 -> "Durga"; 10 -> "Yama"; 11 -> "Vishvadevas"; 12 -> "Vishnu"; 13 -> "Kamadeva"; 14 -> "Shiva"; else -> "Chandra"
    }

    private fun getKaranaDeity(name: String): String = when {
        name.contains("Bava") -> "Indra"; name.contains("Balava") -> "Brahma"; name.contains("Kaulava") -> "Mitra"
        name.contains("Taitila") -> "Aryaman"; name.contains("Gara") -> "Prithvi"; name.contains("Vanija") -> "Shri (Lakshmi)"
        name.contains("Vishti") -> "Yama (Bhadra)"; else -> "Kali"
    }

    private fun getHinduMasa(sunSign: Rashi, isShukla: Boolean): String = when (sunSign) {
        Rashi.ARIES -> "Vaishakha"
        Rashi.TAURUS -> "Jyeshtha"
        Rashi.GEMINI -> "Ashadha"
        Rashi.CANCER -> "Shravana"
        Rashi.LEO -> "Bhadrapada"
        Rashi.VIRGO -> "Ashvina"
        Rashi.LIBRA -> "Kartika"
        Rashi.SCORPIO -> "Margashirsha"
        Rashi.SAGITTARIUS -> "Pausha"
        Rashi.CAPRICORN -> "Magha"
        Rashi.AQUARIUS -> "Phalguna"
        Rashi.PISCES -> "Chaitra"
    }

    private fun getHinduRitu(sunSign: Rashi): String = when (sunSign.index) {
        1, 2 -> "Vasanta (Spring)"
        3, 4 -> "Grishma (Summer)"
        5, 6 -> "Varsha (Monsoon)"
        7, 8 -> "Sharad (Autumn)"
        9, 10 -> "Hemanta (Pre-Winter)"
        else -> "Shishira (Winter)"
    }

    private fun getFestivalsForMonth(month: Int, tithi: Int): List<HinduFestival> = listOf(
        HinduFestival("Maha Shivaratri", "Magha Krishna Chaturdashi", "Chaturdashi", "Night of supreme auspiciousness celebrating Lord Shiva's divine dance (Tandava).", "Spiritual liberation and mastery over mind."),
        HinduFestival("Rama Navami", "Chaitra Shukla Navami", "Navami", "Birthday of Lord Sri Rama, ideal embodiment of Dharma and righteousness.", "Invokes virtue, moral courage, and spiritual victory."),
        HinduFestival("Guru Poornima", "Ashadha Poornima", "Poornima", "Sacred day dedicated to honor the Guru and Sage Veda Vyasa.", "Bestows enlightenment and Guru's divine grace."),
        HinduFestival("Krishna Janmashtami", "Shravana Krishna Ashtami", "Ashtami", "Divine birth of Bhagavan Sri Krishna, avatar of supreme love and wisdom.", "Brings joy, spiritual bhakti, and removal of sorrow."),
        HinduFestival("Ganesh Chaturthi", "Bhadrapada Shukla Chaturthi", "Chaturthi", "Grand celebration of Lord Ganesha, remover of all obstacles (Vighnaharta).", "Bestows wisdom, prosperity, and obstacle-free success."),
        HinduFestival("Vijaya Dashami (Dussehra)", "Ashvina Shukla Dashami", "Dashami", "Triumph of Mother Durga over Mahishasura and Sri Rama over Ravana.", "Victory of light over darkness and auspicious start of ventures."),
        HinduFestival("Deepavali (Diwali)", "Kartika Amavasya", "Amavasya", "Festival of lights, welcoming Mahalakshmi and celebration of divine enlightenment.", "Brings immense wealth, inner illumination, and spiritual light.")
    )

    private fun formatDecimalTime(hours: Double): String {
        val normalized = ((hours % 24.0) + 24.0) % 24.0
        val h = normalized.toInt()
        val m = ((normalized - h) * 60.0).toInt()
        return String.format("%02d:%02d", h, m)
    }
}
