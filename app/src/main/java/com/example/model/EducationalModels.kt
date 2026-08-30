package com.example.model

/**
 * Classical Sanskrit Shloka Library Entry
 */
data class SanskritShloka(
    val id: String,
    val title: String,
    val sourceText: String, // Brihat Parashara Hora Shastra, Jataka Parijata, Saravali, Phaladeepika, etc.
    val chapterAndVerse: String,
    val devanagari: String,
    val transliteration: String,
    val wordByWordMeaning: String,
    val englishTranslation: String,
    val practicalApplication: String,
    val topicTag: String // Lagnas, Yogas, Dashas, Planets, Houses, Shodashavarga, Muhurta
)

/**
 * Educational Lesson
 */
data class JyotishaLesson(
    val id: String,
    val level: String, // Beginner, Intermediate, Advanced, Master
    val title: String,
    val category: String, // Fundamentals, Vargas, Dashas, Yogas, Transits, Muhurta, Remedial
    val summary: String,
    val contentMarkdown: String,
    val keyTakeaways: List<String>,
    val classicalReferences: List<String>
)

/**
 * Planet Detailed Guide
 */
data class PlanetStudyGuide(
    val planet: Planet,
    val mantras: List<String>,
    val gemstone: String,
    val metal: String,
    val color: String,
    val presidingDeity: String,
    val psychologicalSignificance: String,
    val healthSignifications: String,
    val karakaAttributes: List<String>,
    val classicalDescription: String
)

/**
 * House (Bhava) Detailed Guide
 */
data class HouseStudyGuide(
    val houseNumber: Int,
    val sanskritName: String,
    val classification: String, // Kendra, Trikona, Dusthana, Trishadaya, Upachaya, Maraka
    val karakaPlanets: List<Planet>,
    val coreSignifications: List<String>,
    val bodyParts: List<String>,
    val lifeDomains: String,
    val detailedSignificance: String
)
