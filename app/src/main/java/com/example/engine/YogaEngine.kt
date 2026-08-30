package com.example.engine

import com.example.model.*
import kotlin.math.abs

/**
 * Classical Yoga Detection Engine based on Brihat Parashara Hora Shastra,
 * Saravali, Jataka Parijata, and Phaladeepika.
 */
object YogaEngine {

    /**
     * Scans and detects all classical active Yogas in a Birth Chart.
     */
    fun detectYogas(chart: BirthChart): List<YogaResult> {
        val detected = mutableListOf<YogaResult>()

        val ascSign = chart.ascendantRashi
        val planets = chart.planets
        val sun = planets[Planet.SUN]
        val moon = planets[Planet.MOON]
        val mars = planets[Planet.MARS]
        val mercury = planets[Planet.MERCURY]
        val jupiter = planets[Planet.JUPITER]
        val venus = planets[Planet.VENUS]
        val saturn = planets[Planet.SATURN]
        val rahu = planets[Planet.RAHU]
        val ketu = planets[Planet.KETU]

        if (moon == null || sun == null) return emptyList()

        fun isKendra(h: Int): Boolean = h in listOf(1, 4, 7, 10)
        fun isTrikona(h: Int): Boolean = h in listOf(1, 5, 9)
        fun isDusthana(h: Int): Boolean = h in listOf(6, 8, 12)

        // Helper: House Lord mapping
        val houseLords = (1..12).map { h ->
            val signIdx = (ascSign.index + h - 2) % 12 + 1
            Rashi.fromIndex(signIdx).lord
        }

        // 1. PANCHA MAHAPURUSHA YOGAS
        val kendraHouses = listOf(1, 4, 7, 10)

        // Ruchaka Yoga (Mars)
        if (mars != null && mars.house in kendraHouses && (mars.rashi.index == Planet.MARS.exaltationSign || mars.rashi.index in Planet.MARS.ownSigns)) {
            detected.add(
                YogaResult(
                    id = "ruchaka_yoga",
                    name = "Ruchaka Mahapurusha Yoga",
                    sanskritName = "रुचक महापुरुष योग",
                    category = YogaCategory.PANCHA_MAHAPURUSHA,
                    sourceText = "Brihat Parashara Hora Shastra (Ch. 75 / Saravali Ch. 35)",
                    description = "Mars occupies a Kendra in exaltation or own sign (Aries, Scorpio, Capricorn).",
                    participatingPlanets = listOf(Planet.MARS),
                    participatingHouses = listOf(mars.house),
                    strengthPercent = if (mars.rashi.index == Planet.MARS.exaltationSign) 95 else 85,
                    classicalEffect = "Endows radiant physical valor, leadership, military or administrative power, fearless courage, real estate triumphs, and victory over adversaries."
                )
            )
        }

        // Bhadra Yoga (Mercury)
        if (mercury != null && mercury.house in kendraHouses && (mercury.rashi.index == Planet.MERCURY.exaltationSign || mercury.rashi.index in Planet.MERCURY.ownSigns)) {
            detected.add(
                YogaResult(
                    id = "bhadra_yoga",
                    name = "Bhadra Mahapurusha Yoga",
                    sanskritName = "भद्र महापुरुष योग",
                    category = YogaCategory.PANCHA_MAHAPURUSHA,
                    sourceText = "Brihat Parashara Hora Shastra (Ch. 75 / Saravali)",
                    description = "Mercury occupies a Kendra in Gemini or Virgo.",
                    participatingPlanets = listOf(Planet.MERCURY),
                    participatingHouses = listOf(mercury.house),
                    strengthPercent = if (mercury.rashi.index == Planet.MERCURY.exaltationSign) 95 else 85,
                    classicalEffect = "Bestows extraordinary intellect, eloquence, mastery over sciences and mathematics, scholarly eminence, and commercial genius."
                )
            )
        }

        // Hamsa Yoga (Jupiter)
        if (jupiter != null && jupiter.house in kendraHouses && (jupiter.rashi.index == Planet.JUPITER.exaltationSign || jupiter.rashi.index in Planet.JUPITER.ownSigns)) {
            detected.add(
                YogaResult(
                    id = "hamsa_yoga",
                    name = "Hamsa Mahapurusha Yoga",
                    sanskritName = "हंस महापुरुष योग",
                    category = YogaCategory.PANCHA_MAHAPURUSHA,
                    sourceText = "Brihat Parashara Hora Shastra (Ch. 75)",
                    description = "Jupiter is placed in a Kendra in Cancer, Sagittarius, or Pisces.",
                    participatingPlanets = listOf(Planet.JUPITER),
                    participatingHouses = listOf(jupiter.house),
                    strengthPercent = if (jupiter.rashi.index == Planet.JUPITER.exaltationSign) 100 else 90,
                    classicalEffect = "Grants saintly character, high spiritual wisdom, royal honor, reverence from scholars, benevolence, pure conduct, and enduring legacy."
                )
            )
        }

        // Malavya Yoga (Venus)
        if (venus != null && venus.house in kendraHouses && (venus.rashi.index == Planet.VENUS.exaltationSign || venus.rashi.index in Planet.VENUS.ownSigns)) {
            detected.add(
                YogaResult(
                    id = "malavya_yoga",
                    name = "Malavya Mahapurusha Yoga",
                    sanskritName = "मालव्य महापुरुष योग",
                    category = YogaCategory.PANCHA_MAHAPURUSHA,
                    sourceText = "Saravali / Jataka Parijata",
                    description = "Venus resides in a Kendra in Taurus, Libra, or Pisces.",
                    participatingPlanets = listOf(Planet.VENUS),
                    participatingHouses = listOf(venus.house),
                    strengthPercent = if (venus.rashi.index == Planet.VENUS.exaltationSign) 95 else 85,
                    classicalEffect = "Bestows magnificent artistic talents, luxurious conveyances, aesthetic refinement, deep romantic bliss, charismatic allure, and immense wealth."
                )
            )
        }

        // Sasa Yoga (Saturn)
        if (saturn != null && saturn.house in kendraHouses && (saturn.rashi.index == Planet.SATURN.exaltationSign || saturn.rashi.index in Planet.SATURN.ownSigns)) {
            detected.add(
                YogaResult(
                    id = "sasa_yoga",
                    name = "Sasa Mahapurusha Yoga",
                    sanskritName = "शश महापुरुष योग",
                    category = YogaCategory.PANCHA_MAHAPURUSHA,
                    sourceText = "Brihat Parashara Hora Shastra",
                    description = "Saturn is posited in a Kendra in Libra, Capricorn, or Aquarius.",
                    participatingPlanets = listOf(Planet.SATURN),
                    participatingHouses = listOf(saturn.house),
                    strengthPercent = if (saturn.rashi.index == Planet.SATURN.exaltationSign) 95 else 85,
                    classicalEffect = "Endows command over masses, political authority, enduring endurance, executive administrative dominance, and wealth built on steadfast diligence."
                )
            )
        }

        // 2. GAJA KESARI YOGA (Jupiter in 1, 4, 7, 10 from Moon)
        if (jupiter != null) {
            val distFromMoon = ((jupiter.house - moon.house + 12) % 12) + 1
            if (distFromMoon in kendraHouses) {
                detected.add(
                    YogaResult(
                        id = "gaja_kesari",
                        name = "Gaja Kesari Yoga",
                        sanskritName = "गजकेसरी योग",
                        category = YogaCategory.GAJAKESARI,
                        sourceText = "Phaladeepika (Ch. 6 / BPHS)",
                        description = "Jupiter is in a Kendra (1st, 4th, 7th, or 10th house) from the natal Moon.",
                        participatingPlanets = listOf(Planet.JUPITER, Planet.MOON),
                        participatingHouses = listOf(jupiter.house, moon.house),
                        strengthPercent = 90,
                        classicalEffect = "Like an elephant-lion emperor, the native is illustrious, conqueror of enemies, highly revered, endowed with high intellect, lasting fame, and prosperous progeny."
                    )
                )
            }
        }

        // 3. BUDHADITYA YOGA (Sun and Mercury conjunct in the same sign)
        if (mercury != null && sun.rashi == mercury.rashi && !mercury.isCombust) {
            detected.add(
                YogaResult(
                    id = "budhaditya_yoga",
                    name = "Budhaditya Yoga",
                    sanskritName = "बुधादित्य योग",
                    category = YogaCategory.MISCELLANEOUS,
                    sourceText = "Saravali (Ch. 14 / BPHS)",
                    description = "Sun and Mercury are conjunct in the same Rashi without severe combustion.",
                    participatingPlanets = listOf(Planet.SUN, Planet.MERCURY),
                    participatingHouses = listOf(sun.house),
                    strengthPercent = 85,
                    classicalEffect = "Bestows sharp analytical intellect, royal counsel abilities, scholastic brilliance, persuasive oratory, and administrative skill."
                )
            )
        }

        // 4. CHANDRA-MANGALA YOGA (Moon and Mars conjunct or in mutual aspect)
        if (mars != null && (moon.rashi == mars.rashi || (moon.house - mars.house + 12) % 12 == 6)) {
            detected.add(
                YogaResult(
                    id = "chandra_mangala_yoga",
                    name = "Chandra Mangala Yoga",
                    sanskritName = "चन्द्र-मङ्गल योग",
                    category = YogaCategory.DHANA_YOGA,
                    sourceText = "Jataka Parijata (Ch. 7)",
                    description = "Moon and Mars are conjunct or in mutual 7th aspect.",
                    participatingPlanets = listOf(Planet.MOON, Planet.MARS),
                    participatingHouses = listOf(moon.house, mars.house),
                    strengthPercent = 80,
                    classicalEffect = "Creates extraordinary drive for financial enterprise, business acumen, wealth accumulation, real estate gains, and quick emotional energy."
                )
            )
        }

        // 5. DHARMA KARMADHIPATI RAJA YOGA (9th and 10th Lords associated)
        val lord9 = houseLords[8]
        val lord10 = houseLords[9]
        val lord9Pos = planets[lord9]
        val lord10Pos = planets[lord10]
        if (lord9Pos != null && lord10Pos != null && (lord9Pos.rashi == lord10Pos.rashi || (lord9Pos.house - lord10Pos.house + 12) % 12 == 6)) {
            detected.add(
                YogaResult(
                    id = "dharma_karmadhipati",
                    name = "Dharma Karmadhipati Yoga",
                    sanskritName = "धर्म कर्माधिपति योग",
                    category = YogaCategory.RAJA_YOGA,
                    sourceText = "Brihat Parashara Hora Shastra (Ch. 41)",
                    description = "Lords of the 9th (Dharma/Fortune) and 10th (Karma/Profession) houses are conjunct or aspecting each other.",
                    participatingPlanets = listOf(lord9, lord10),
                    participatingHouses = listOf(lord9Pos.house, lord10Pos.house),
                    strengthPercent = 95,
                    classicalEffect = "Regarded as the pinnacle Raja Yoga in Parashari Jyotisha. Brings noble profession, sovereign honor, righteous success, high state authority, and vast auspicious fortune."
                )
            )
        }

        // 6. VIPAREETA RAJA YOGAS (Harsha, Sarala, Vimala)
        val lord6 = houseLords[5]
        val lord8 = houseLords[7]
        val lord12 = houseLords[11]
        val trikHouses = listOf(6, 8, 12)

        val lord6Pos = planets[lord6]
        if (lord6Pos != null && lord6Pos.house in trikHouses) {
            detected.add(
                YogaResult(
                    id = "harsha_yoga",
                    name = "Harsha Vipareeta Raja Yoga",
                    sanskritName = "हर्ष विपरीत राजयोग",
                    category = YogaCategory.VIPAREETA_RAJA,
                    sourceText = "Phaladeepika (Ch. 6)",
                    description = "6th Lord is posited in 6th, 8th, or 12th house.",
                    participatingPlanets = listOf(lord6),
                    participatingHouses = listOf(lord6Pos.house),
                    strengthPercent = 80,
                    classicalEffect = "Grants invincibility over rivals, exceptional immunity, happiness, conquest of fear, and sudden gain from adversaries."
                )
            )
        }

        val lord8Pos = planets[lord8]
        if (lord8Pos != null && lord8Pos.house in trikHouses) {
            detected.add(
                YogaResult(
                    id = "sarala_yoga",
                    name = "Sarala Vipareeta Raja Yoga",
                    sanskritName = "सरल विपरीत राजयोग",
                    category = YogaCategory.VIPAREETA_RAJA,
                    sourceText = "Phaladeepika (Ch. 6)",
                    description = "8th Lord is posited in 6th, 8th, or 12th house.",
                    participatingPlanets = listOf(lord8),
                    participatingHouses = listOf(lord8Pos.house),
                    strengthPercent = 85,
                    classicalEffect = "Native is fearless, prosperous, long-lived, celebrated for scholarly knowledge, and turns critical crises into breakthrough triumphs."
                )
            )
        }

        val lord12Pos = planets[lord12]
        if (lord12Pos != null && lord12Pos.house in trikHouses) {
            detected.add(
                YogaResult(
                    id = "vimala_yoga",
                    name = "Vimala Vipareeta Raja Yoga",
                    sanskritName = "विमल विपरीत राजयोग",
                    category = YogaCategory.VIPAREETA_RAJA,
                    sourceText = "Phaladeepika (Ch. 6)",
                    description = "12th Lord is posited in 6th, 8th, or 12th house.",
                    participatingPlanets = listOf(lord12),
                    participatingHouses = listOf(lord12Pos.house),
                    strengthPercent = 80,
                    classicalEffect = "Brings financial independence, spiritual purity, frugal wisdom, honorable conduct, and protection from heavy losses."
                )
            )
        }

        // 7. NEECHA BHANGA RAJA YOGA
        for ((planet, pos) in planets) {
            if (pos.dignity == PlanetaryDignity.DEBILITATED) {
                val debSignLord = pos.rashi.lord
                val debLordPos = planets[debSignLord]
                val debLordInKendra = debLordPos != null && (debLordPos.house in kendraHouses || ((debLordPos.house - moon.house + 12) % 12 + 1) in kendraHouses)

                val exaltSign = planet.exaltationSign
                val exaltLord = Rashi.fromIndex(exaltSign).lord
                val exaltLordPos = planets[exaltLord]
                val exaltLordInKendra = exaltLordPos != null && (exaltLordPos.house in kendraHouses)

                if (debLordInKendra || exaltLordInKendra) {
                    detected.add(
                        YogaResult(
                            id = "neecha_bhanga_${planet.name.lowercase()}",
                            name = "Neecha Bhanga Raja Yoga (${planet.englishName})",
                            sanskritName = "नीचभङ्ग राजयोग",
                            category = YogaCategory.NEECHA_BHANGA,
                            sourceText = "Phaladeepika (Ch. 6 / BPHS)",
                            description = "Debilitation of ${planet.englishName} in ${pos.rashi.englishName} is cancelled through classical dispositor Kendra placement.",
                            participatingPlanets = listOf(planet, debSignLord),
                            participatingHouses = listOf(pos.house),
                            strengthPercent = 90,
                            classicalEffect = "Transforms early life struggle or limitation into monumental kingly success, resilience, eminence, and triumph after adversity."
                        )
                    )
                }
            }
        }

        // 8. CHANDRA YOGAS (Sunapha, Anapha, Durdhara)
        val house2FromMoon = ((moon.house) % 12) + 1
        val house12FromMoon = ((moon.house - 2 + 12) % 12) + 1
        val planetsIn2 = planets.values.filter { it.house == house2FromMoon && it.planet != Planet.SUN && it.planet != Planet.RAHU && it.planet != Planet.KETU }
        val planetsIn12 = planets.values.filter { it.house == house12FromMoon && it.planet != Planet.SUN && it.planet != Planet.RAHU && it.planet != Planet.KETU }

        if (planetsIn2.isNotEmpty() && planetsIn12.isEmpty()) {
            detected.add(
                YogaResult(
                    id = "sunapha_yoga",
                    name = "Sunapha Yoga",
                    sanskritName = "सुनफा योग",
                    category = YogaCategory.SOLAR_LUNAR,
                    sourceText = "Brihat Jataka / BPHS",
                    description = "Benefics/Planets occupy the 2nd house from Moon (excluding Sun/Nodes).",
                    participatingPlanets = planetsIn2.map { it.planet },
                    participatingHouses = listOf(house2FromMoon),
                    strengthPercent = 75,
                    classicalEffect = "Self-earned wealth, honorable status, sharp intelligence, and righteous life enjoyment."
                )
            )
        } else if (planetsIn12.isNotEmpty() && planetsIn2.isEmpty()) {
            detected.add(
                YogaResult(
                    id = "anapha_yoga",
                    name = "Anapha Yoga",
                    sanskritName = "अनफा योग",
                    category = YogaCategory.SOLAR_LUNAR,
                    sourceText = "Brihat Jataka / BPHS",
                    description = "Planets occupy the 12th house from Moon (excluding Sun/Nodes).",
                    participatingPlanets = planetsIn12.map { it.planet },
                    participatingHouses = listOf(house12FromMoon),
                    strengthPercent = 75,
                    classicalEffect = "Well-formed body, generous disposition, high self-respect, renowned manners, and peaceful renunciation."
                )
            )
        } else if (planetsIn2.isNotEmpty() && planetsIn12.isNotEmpty()) {
            detected.add(
                YogaResult(
                    id = "durdhara_yoga",
                    name = "Durdhara Yoga",
                    sanskritName = "दुरुधरा योग",
                    category = YogaCategory.SOLAR_LUNAR,
                    sourceText = "Brihat Jataka / BPHS",
                    description = "Planets flank both 2nd and 12th houses from Moon.",
                    participatingPlanets = planetsIn2.map { it.planet } + planetsIn12.map { it.planet },
                    participatingHouses = listOf(house2FromMoon, house12FromMoon),
                    strengthPercent = 85,
                    classicalEffect = "Endowed with vehicles, unblemished fame, generous hospitality, and continuous bounty of comforts."
                )
            )
        }

        // 9. LAKSHMI YOGA (9th Lord exalted or in own sign in Kendra/Trikona)
        if (lord9Pos != null && (lord9Pos.house in kendraHouses || lord9Pos.house in listOf(1, 5, 9)) && (lord9Pos.dignity in listOf(PlanetaryDignity.EXALTED, PlanetaryDignity.OWN_SIGN, PlanetaryDignity.MOOLATRIKONA))) {
            detected.add(
                YogaResult(
                    id = "lakshmi_yoga",
                    name = "Maha Lakshmi Yoga",
                    sanskritName = "महालक्ष्मी योग",
                    category = YogaCategory.DHANA_YOGA,
                    sourceText = "Brihat Parashara Hora Shastra (Ch. 36)",
                    description = "Lord of the 9th house is exalted or in own house in Kendra/Trikona.",
                    participatingPlanets = listOf(lord9),
                    participatingHouses = listOf(lord9Pos.house),
                    strengthPercent = 95,
                    classicalEffect = "Blessed by Goddess Lakshmi with noble lineage, vast fortune, radiant character, sovereign prestige, and constant abundance."
                )
            )
        }

        // 10. SARASWATI YOGA (Jupiter, Venus, Mercury in Kendras/Trikonas/2nd house)
        val jupGood = jupiter != null && (jupiter.house in kendraHouses || jupiter.house in listOf(2, 5, 9))
        val venGood = venus != null && (venus.house in kendraHouses || venus.house in listOf(2, 5, 9))
        val mercGood = mercury != null && (mercury.house in kendraHouses || mercury.house in listOf(2, 5, 9))
        if (jupGood && venGood && mercGood) {
            detected.add(
                YogaResult(
                    id = "saraswati_yoga",
                    name = "Saraswati Yoga",
                    sanskritName = "सरस्वती योग",
                    category = YogaCategory.MISCELLANEOUS,
                    sourceText = "Jataka Parijata (Ch. 7)",
                    description = "Jupiter, Venus, and Mercury occupy Kendras, Trikonas, or the 2nd house in strength.",
                    participatingPlanets = listOf(Planet.JUPITER, Planet.VENUS, Planet.MERCURY),
                    participatingHouses = listOfNotNull(jupiter?.house, venus?.house, mercury?.house),
                    strengthPercent = 90,
                    classicalEffect = "Grants supreme literary mastery, philosophical genius, poetic talent, deep Vedic erudition, and universal renown."
                )
            )
        }

        // 11. KAAL SARPA YOGA
        if (rahu != null && ketu != null) {
            val rahuH = rahu.house
            val ketuH = ketu.house
            val sevenPlanets = listOfNotNull(sun, moon, mars, mercury, jupiter, venus, saturn)
            val allBetweenRahuKetu = sevenPlanets.all { p ->
                val dist = (p.house - rahuH + 12) % 12
                dist in 1..5
            }
            val allBetweenKetuRahu = sevenPlanets.all { p ->
                val dist = (p.house - ketuH + 12) % 12
                dist in 1..5
            }

            if (allBetweenRahuKetu || allBetweenKetuRahu) {
                val yogaNames = listOf(
                    "Ananta", "Kulika", "Vasuki", "Shankhapala", "Padma", "Mahapadma",
                    "Takshaka", "Karkotaka", "Shankhachuda", "Ghataka", "Vishadhara", "Sheshanaga"
                )
                val yogaName = "${yogaNames.getOrElse(rahuH - 1) { "Kaal Sarpa" }} Kaal Sarpa Yoga"

                detected.add(
                    YogaResult(
                        id = "kaal_sarpa_yoga",
                        name = yogaName,
                        sanskritName = "कालसर्प योग",
                        category = YogaCategory.KAAL_SARPA,
                        sourceText = "Classical Tantric & Purana Agamas",
                        description = "All seven physical grahas are hemmed within the Rahu-Ketu nodal axis.",
                        participatingPlanets = listOf(Planet.RAHU, Planet.KETU),
                        participatingHouses = listOf(rahuH, ketuH),
                        strengthPercent = 80,
                        classicalEffect = "Indicates intense karmic destiny, periodic sudden disruptions followed by monumental meteoric rises in later life after spiritual maturation."
                    )
                )
            }
        }

        return detected
    }
}
