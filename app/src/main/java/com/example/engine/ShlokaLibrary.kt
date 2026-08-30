package com.example.engine

import com.example.model.ClassicalShloka
import com.example.model.EncyclopediaTopic

/**
 * Classical Vedic Sanskrit Shlokas & Astrology Encyclopedia Repository.
 */
object ShlokaLibrary {

    val shlokas: List<ClassicalShloka> get() = ALL_SHLOKAS
    val encyclopediaTopics: List<EncyclopediaTopic> get() = ENCYCLOPEDIA_TOPICS

    val ALL_SHLOKAS = listOf(
        ClassicalShloka(
            id = "shloka_1",
            textSanskrit = "सर्वे ग्रहाः शुभफलाः स्वोच्चस्वक्षेत्रगा यदि ।\nकेन्द्रे वा त्रिकोणे वा संस्थिता नृपतेः समाः ॥",
            transliteration = "sarve grahāḥ śubhaphalāḥ svoccasvakṣetragā yadi |\nkendre vā trikoṇe vā saṁsthitā nṛpateḥ samāḥ ||",
            sourceText = "Brihat Parashara Hora Shastra (Ch. 41, Sl. 2)",
            englishTranslation = "All planets yield extremely auspicious results if they reside in their exaltation or own signs, or are posited in Kendra (1, 4, 7, 10) or Trikona (1, 5, 9) houses, making the native equal to a monarch.",
            astrologicalPrinciple = "Planetary dignity and angular placement form the foundational bedrock of all Raja Yogas and life vitality."
        ),
        ClassicalShloka(
            id = "shloka_2",
            textSanskrit = "धर्मकर्माधिपौ युक्तौ धर्मकर्मसमन्वितौ ।\nराजयोगं प्रकुर्वाते तौ चेत् केन्द्रत्रिकोणगौ ॥",
            transliteration = "dharmakarmādhipau yuktau dharmakarmasamanvitau |\nrājayogaṁ prakurvāte tau cet kendratrikoṇagau ||",
            sourceText = "Brihat Parashara Hora Shastra (Ch. 41, Sl. 38)",
            englishTranslation = "When the lords of the 9th (Dharma) and 10th (Karma) houses join together and occupy Kendras or Trikonas, they produce the supreme Dharma Karmadhipati Raja Yoga.",
            astrologicalPrinciple = "The synthesis of fortune/purpose (9th) with executive action/power (10th) guarantees highest status and ethical authority."
        ),
        ClassicalShloka(
            id = "shloka_3",
            textSanskrit = "यदा केन्द्रगतो जीवः शशाङ्कात् कोणगोऽथवा ।\nगजकेसरियोगोऽयं कीर्तिमान् बहुशास्त्रवित् ॥",
            transliteration = "yadā kendragato jīvaḥ śaśāṅkāt koṇago'thavā |\ngajakesariyogo'yaṁ kīrtimān bahuśāstravit ||",
            sourceText = "Phaladeepika (Ch. 6, Sl. 14)",
            englishTranslation = "When Jupiter is in a Kendra from the Moon, Gaja Kesari Yoga is formed. The native becomes renowned, learned in numerous sciences, and revered by kings.",
            astrologicalPrinciple = "Jupiter's divine benevolence expands the receptive mind (Chandra), creating supreme mental fortitude."
        ),
        ClassicalShloka(
            id = "shloka_4",
            textSanskrit = "नीचस्थितो जन्मनि यो ग्रहः स्यात्तद्राशिनाथोऽपि तदुच्चनाथः ।\nचन्द्रालग्नाद्यदि केन्द्रवर्ती राजा भवेद्धार्मिकचक्रवर्ती ॥",
            transliteration = "nīcasthito janmani yo grahaḥ syāttadrāśinātho'pi taducchanāthaḥ |\ncandrāllagnādyadi kendravartī rājā bhaveddhyārmikacakravartī ||",
            sourceText = "Phaladeepika (Ch. 6, Sl. 26)",
            englishTranslation = "If the lord of the sign in which a planet is debilitated, or the lord of its exaltation sign, is in a Kendra from the Lagna or the Moon, Neecha Bhanga Raja Yoga is formed, elevating the native to righteous sovereignty.",
            astrologicalPrinciple = "Karmic debility is transformed into monumental worldly resilience through angular dispositorship."
        ),
        ClassicalShloka(
            id = "shloka_5",
            textSanskrit = "षष्ठाष्टमव्ययेशानां सम्बन्धो यदि जायते ।\nविपरीतं राजयोगं जनयेत् सुमहद्यशः ॥",
            transliteration = "ṣaṣṭhāṣṭamavyayeśānāṁ sambandho yadi jāyate |\nviparītaṁ rājayogaṁ janayet sumahadyaśaḥ ||",
            sourceText = "Uttara Kalamrita (Ch. 4, Sl. 22)",
            englishTranslation = "If lords of the 6th, 8th, and 12th houses associate solely among themselves in dusthana houses, Vipareeta Raja Yoga is created, bringing sudden unprecedented triumph.",
            astrologicalPrinciple = "Negative forces neutralizing one another unleash unexpected breakthroughs and invulnerability against crises."
        ),
        ClassicalShloka(
            id = "shloka_6",
            textSanskrit = "गोचरे चन्द्रमा यत्र तस्मात् स्थानानि चिन्तयेत् ।\nवेधेन रहिताः सर्वे ग्रहाः पुष्णन्ति शोभनम् ॥",
            transliteration = "gocare candramā yatra tasmāt sthānāni cintayet |\nvedhena rahitāḥ sarve grahāḥ puṣṇanti śobhanam ||",
            sourceText = "Brihat Samhita / Gochara Deepika",
            englishTranslation = "Evaluate transit planets from the natal Moon position. All planets bestow their auspicious fruits only when free from opposing Vedha obstructions.",
            astrologicalPrinciple = "Transit efficacy depends on celestial cross-aspects (Vedha) checking or unlocking planetary energies."
        )
    )

    val ENCYCLOPEDIA_TOPICS = listOf(
        EncyclopediaTopic(
            id = "topic_1",
            title = "The 12 Bhavas (Houses) & Purusharthas",
            sanskritName = "द्वादश भावाः पुरुषार्थाश्च",
            category = "Foundations of Jyotisha",
            summary = "The 12 houses represent the entirety of human existence structured across the 4 sacred aims of life: Dharma, Artha, Kama, and Moksha.",
            detailedContent = """
                In Vedic Astrology, the 12 Bhavas map the interaction between cosmic consciousness and individual embodiment:
                
                • Dharma Houses (1, 5, 9): Spiritual purpose, soul constitution, past good karma (Purva Punya), wisdom, and ethical alignment.
                • Artha Houses (2, 6, 10): Material resources, finances, hard work, mastery over obstacles, social status, and worldly profession.
                • Kama Houses (3, 7, 11): Desires, courage, communications, marital & business partnerships, social networks, and expansive aspirations.
                • Moksha Houses (4, 8, 12): Inner emotional peace, psychological depth, secrets, transformation, meditation, dreams, and spiritual liberation.
                
                Kendras (1, 4, 7, 10) are the pillars of life (Vishnu Sthanas). Trikonas (1, 5, 9) are the abodes of grace and fortune (Lakshmi Sthanas).
            """.trimIndent()
        ),
        EncyclopediaTopic(
            id = "topic_2",
            title = "The 27 Nakshatras & Lunar Mansions",
            sanskritName = "सप्तविंशति नक्षत्राणि",
            category = "Astronomical Principles",
            summary = "The 27 Nakshatras form the deep galactic backdrop of Vedic Astrology, each spanning 13°20' and ruled by cosmic deities.",
            detailedContent = """
                While Rashis (signs) represent the solar field of external action, Nakshatras represent the lunar, subconscious, and karmic matrix:
                
                Each Nakshatra is divided into 4 Padas (3°20' each), mapping directly to the 108 Navamsas (9 Padas per sign). 
                The Moon's placement at birth determines the Janma Nakshatra, defining the native's innate psychology, foundational Dasha cycle, and life temperament (Deva, Manushya, Rakshasa).
            """.trimIndent()
        ),
        EncyclopediaTopic(
            id = "topic_3",
            title = "Vimshottari Dasha System",
            sanskritName = "विंशोत्तरी दशा पद्धतिः",
            category = "Predictive Techniques",
            summary = "The 120-year universal planetary time-clock described by Maharishi Parashara for the Kali Yuga.",
            detailedContent = """
                Vimshottari Dasha calculates the unfolding of karmic fruits through specific planetary periods:
                
                • Ketu (7y) -> Venus (20y) -> Sun (6y) -> Moon (10y) -> Mars (7y) -> Rahu (18y) -> Jupiter (16y) -> Saturn (19y) -> Mercury (17y).
                The exact point of the Moon in the birth nakshatra determines the initial balance of the opening Mahadasha. The nested sub-periods (Antardasha, Pratyantardasha, Sookshma) pinpoint exact timing of life milestones.
            """.trimIndent()
        ),
        EncyclopediaTopic(
            id = "topic_4",
            title = "Shodashavarga (16 Divisional Charts)",
            sanskritName = "षोडशवर्गाः",
            category = "Advanced Divisional Analysis",
            summary = "Microscopic magnification of the 12 signs into 16 harmonic divisions to examine specific facets of life.",
            detailedContent = """
                Maharishi Parashara declared that the Rasi chart (D1) provides the macro overview, but divisional charts are indispensable for granular accuracy:
                
                • D9 Navamsa: Dharma, marriage, second half of life.
                • D10 Dasamsa: Career prestige, public impact, and power.
                • D7 Saptamsa: Progeny and creative fertility.
                • D12 Dwadashamsa: Ancestral roots and parents.
                • D60 Shashtiamsa: Past-life karmic root verifying all life events.
            """.trimIndent()
        )
    )
}
