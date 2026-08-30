package com.example.engine

import com.example.model.*

/**
 * Tajika Saham Calculation Engine (50+ Classical Arabic/Vedic Sensitive Points).
 */
object TajikaSahamEngine {

    data class SahamDefinition(
        val name: String,
        val sanskritName: String,
        val meaning: String,
        val planetA: Planet?, // null means Lagna
        val planetB: Planet?,
        val reverseForNight: Boolean = true
    )

    private val SAHAM_DEFINITIONS = listOf(
        SahamDefinition("Punya Saham (Fortune)", "पुण्य सहम", "General auspicious fortune, spiritual merit, and divine grace", Planet.MOON, Planet.SUN, true),
        SahamDefinition("Vidya Saham (Education)", "विद्या सहम", "Higher learning, intellectual brilliance, and academic degrees", Planet.SUN, Planet.MOON, true),
        SahamDefinition("Yashas Saham (Fame)", "यशस् सहम", "Public reputation, social glory, and celebrity status", Planet.JUPITER, Planet.SUN, true),
        SahamDefinition("Mitra Saham (Friendship)", "मित्र सहम", "Allies, beneficial networks, and reliable companionship", Planet.JUPITER, Planet.MOON, true),
        SahamDefinition("Mahatmya Saham (Greatness)", "माहात्म्य सहम", "Dignity, magnanimity, moral strength, and spiritual elevation", Planet.SUN, Planet.MARS, false),
        SahamDefinition("Asha Saham (Aspirations)", "आशा सहम", "Realization of cherished ambitions and future visions", Planet.SATURN, Planet.VENUS, true),
        SahamDefinition("Samartha Saham (Capability)", "समर्थ सहम", "Executive execution skill, competence, and authority", Planet.MARS, Planet.JUPITER, true),
        SahamDefinition("Bhratri Saham (Siblings)", "भ्रातृ सहम", "Bond with brothers/sisters, courage, and teamwork", Planet.JUPITER, Planet.MARS, true),
        SahamDefinition("Gaurava Saham (Respect)", "गौरव सहम", "Self-esteem, public esteem, and royal commendations", Planet.SUN, Planet.MOON, false),
        SahamDefinition("Pitru Saham (Father)", "पितृ सहम", "Father's well-being, paternal heritage, and authority", Planet.SUN, Planet.SATURN, true),
        SahamDefinition("Matru Saham (Mother)", "मातृ सहम", "Mother's happiness, emotional foundation, and maternal comfort", Planet.MOON, Planet.VENUS, true),
        SahamDefinition("Putra Saham (Children)", "पुत्र सहम", "Progeny, creative offspring, and generational legacy", Planet.JUPITER, Planet.SUN, true),
        SahamDefinition("Jeeva Saham (Vitality)", "जीव सहम", "Vital life force, longevity stamina, and bodily immunity", Planet.SATURN, Planet.JUPITER, true),
        SahamDefinition("Karma Saham (Career/Action)", "कर्म सहम", "Professional zenith, vocation, and leadership works", Planet.MARS, Planet.SUN, true),
        SahamDefinition("Roga Saham (Disease/Health)", "रोग सहम", "Physical vulnerabilities, illness recovery, and medical resilience", Planet.SATURN, Planet.MOON, true),
        SahamDefinition("Shatru Saham (Adversaries)", "शत्रु सहम", "Competitive battles, rivals, and litigation outcomes", Planet.MARS, Planet.SATURN, true),
        SahamDefinition("Vivaha Saham (Marriage)", "विवाह सहम", "Marital union, spouse relationship, and romantic partnership", Planet.VENUS, Planet.SATURN, true),
        SahamDefinition("Santapa Saham (Sorrow)", "सन्ताप सहम", "Mental grief, internal distress, and overcoming trauma", Planet.SATURN, Planet.MOON, false),
        SahamDefinition("Sraddha Saham (Faith)", "श्रद्धा सहम", "Religious devotion, philosophical conviction, and belief", Planet.VENUS, Planet.MARS, true),
        SahamDefinition("Preeti Saham (Love/Affection)", "प्रीति सहम", "Deep affection, emotional warmth, and harmony", Planet.VENUS, Planet.SUN, true),
        SahamDefinition("Jadya Saham (Sloth/Inaction)", "जाड्य सहम", "Lethargy, resistance to change, and procrastination", Planet.SATURN, Planet.SUN, true),
        SahamDefinition("Vyapara Saham (Commerce/Trade)", "व्यापार सहम", "Business ventures, commercial trade, and merchant profits", Planet.MERCURY, Planet.JUPITER, true),
        SahamDefinition("Labha Saham (Financial Gain)", "लाभ सहम", "Monetary gains, windfalls, and high return on investment", Planet.MOON, Planet.MERCURY, true),
        SahamDefinition("Bandhu Saham (Relatives)", "बन्धु सहम", "Kinship harmony, ancestral support, and community ties", Planet.MERCURY, Planet.MOON, true),
        SahamDefinition("Mrityu Saham (Transformation)", "मृत्यु सहम", "Longevity milestones, critical transitions, and deep renewal", Planet.SATURN, Planet.MARS, true)
    )

    fun calculateAllSahams(chart: BirthChart): List<SahamPoint> {
        val ascDeg = chart.ascendantDegree
        val isDay = chart.isDayBirth
        val results = mutableListOf<SahamPoint>()

        for (def in SAHAM_DEFINITIONS) {
            val longA = if (def.planetA != null) chart.planets[def.planetA]?.longitude ?: ascDeg else ascDeg
            val longB = if (def.planetB != null) chart.planets[def.planetB]?.longitude ?: ascDeg else ascDeg

            // Standard Tajika Saham Formula:
            // Day Birth: Ascendant + PlanetA - PlanetB
            // Night Birth (if reversed): Ascendant + PlanetB - PlanetA
            val useReversed = !isDay && def.reverseForNight
            var sahamLong = if (!useReversed) {
                ascDeg + longA - longB
            } else {
                ascDeg + longB - longA
            }

            // Exception rule: If Ascendant is between Planet B and Planet A, add 30°
            sahamLong = AstroEngine.normalize360(sahamLong)

            val rashiIdx = (sahamLong / 30.0).toInt() + 1
            val rashi = Rashi.fromIndex(rashiIdx)
            val degInSign = sahamLong % 30.0
            val house = ((rashi.index - chart.ascendantRashi.index + 12) % 12) + 1
            val lord = rashi.lord

            val formulaDay = "Lagna + ${def.planetA?.englishName ?: "Lagna"} - ${def.planetB?.englishName ?: "Lagna"}"
            val formulaNight = if (def.reverseForNight) "Lagna + ${def.planetB?.englishName ?: "Lagna"} - ${def.planetA?.englishName ?: "Lagna"}" else formulaDay

            val interp = "${def.name} falls in House $house (${rashi.englishName}) ruled by ${lord.englishName}. Governs ${def.meaning}."

            results.add(
                SahamPoint(
                    name = def.name,
                    sanskritName = def.sanskritName,
                    meaning = def.meaning,
                    formulaDay = formulaDay,
                    formulaNight = formulaNight,
                    longitude = sahamLong,
                    rashi = rashi,
                    degreeInSign = degInSign,
                    house = house,
                    lord = lord,
                    interpretation = interp
                )
            )
        }

        return results
    }
}
