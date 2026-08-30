package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.KundaliEntity
import com.example.engine.*
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppScreen(val title: String, val sanskritName: String) {
    KUNDALI_OVERVIEW("Janma Kundali", "जन्म कुण्डली"),
    SHODASHAVARGA("Divisional Charts", "षोडशवर्गाः"),
    DASHA_SYSTEM("Dasha Systems", "दशा पद्धतिः"),
    YOGAS_ANALYSIS("Yogas & Combinations", "ग्रहयोगाः"),
    PANCHANGA_MUHURTA("Daily Panchanga", "पञ्चाङ्गम्"),
    SHADBALA_ASHTAKAVARGA("Planetary Strength", "षड्बलम् अष्टकवर्गश्च"),
    TRANSIT_GOCHARA("Gochara & Sade Sati", "गोचरः साढे साती"),
    MATCHMAKING("Kundali Milan", "कुण्डली मिलनम्"),
    TAJIKA_LONGEVITY("Advanced Jyotisha", "सहमम् आयुर्दायः"),
    SARVATOBHADRA_ECLIPSES("Special Chakras", "सर्वतोभद्र चक्रम्"),
    REVERSE_SEARCH("Cosmic Search", "ज्योतिष अनुसन्धानम्"),
    PROFILES("Saved Charts", "जातकावलिः"),
    ENCYCLOPEDIA("Shlokas & Texts", "शास्त्र ग्रन्थावली")
}

class JyotishyaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val kundaliDao = db.kundaliDao()

    // 1. Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.KUNDALI_OVERVIEW)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // 2. Chart Display Style (North Indian vs South Indian)
    private val _isNorthIndianStyle = MutableStateFlow(true)
    val isNorthIndianStyle: StateFlow<Boolean> = _isNorthIndianStyle.asStateFlow()

    // 3. Active Ayanamsha System
    private val _selectedAyanamsha = MutableStateFlow(AyanamshaSystem.LAHIRI)
    val selectedAyanamsha: StateFlow<AyanamshaSystem> = _selectedAyanamsha.asStateFlow()

    // 4. Saved Profiles from Database
    val savedProfiles: StateFlow<List<KundaliEntity>> = kundaliDao.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. Active Birth Chart
    private val _activeChart = MutableStateFlow(createDefaultSampleChart())
    val activeChart: StateFlow<BirthChart> = _activeChart.asStateFlow()

    // 6. Selected Divisional Varga Chart (D1 to D60)
    private val _selectedVargaType = MutableStateFlow(VargaType.D9)
    val selectedVargaType: StateFlow<VargaType> = _selectedVargaType.asStateFlow()

    val currentVargaChart: StateFlow<DivisionalChart> = combine(_activeChart, _selectedVargaType) { chart, varga ->
        ShodashavargaEngine.generateDivisionalChart(chart, varga)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ShodashavargaEngine.generateDivisionalChart(_activeChart.value, VargaType.D9))

    val vimsopakaScores: StateFlow<List<VimsopakaScore>> = _activeChart.map { chart ->
        ShodashavargaEngine.calculateVimsopakaBala(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 7. Dasha Reports
    val dashaReport: StateFlow<DashaReport> = _activeChart.map { chart ->
        DashaEngine.calculateDashaReport(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashaEngine.calculateDashaReport(_activeChart.value))

    // 8. Detected Yogas
    val detectedYogas: StateFlow<List<YogaResult>> = _activeChart.map { chart ->
        YogaEngine.detectYogas(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 9. Shadbala & Ashtakavarga
    val shadbalaList: StateFlow<List<ShadbalaComponent>> = _activeChart.map { chart ->
        ShadbalaEngine.calculateShadbala(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ashtakavargaReport: StateFlow<AshtakavargaReport> = _activeChart.map { chart ->
        AshtakavargaEngine.calculateAshtakavarga(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AshtakavargaEngine.calculateAshtakavarga(_activeChart.value))

    // 10. Daily Panchanga
    private val _panchangaDate = MutableStateFlow(Calendar.getInstance())
    val dailyPanchanga: StateFlow<DailyPanchanga> = _panchangaDate.map { cal ->
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        PanchangaEngine.calculateDailyPanchanga(
            year = y,
            month = m,
            day = d,
            latitude = 28.6139, // New Delhi default
            longitude = 77.2090,
            timezoneOffsetHours = 5.5,
            locationName = "New Delhi, Bharat (28.61° N, 77.21° E)"
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PanchangaEngine.calculateDailyPanchanga(2025, 1, 1, 28.6139, 77.2090, 5.5, "New Delhi"))

    // 11. Transit & Sade Sati
    val transitReport: StateFlow<TransitReport> = _activeChart.map { chart ->
        TransitEngine.calculateTransitReport(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransitEngine.calculateTransitReport(_activeChart.value))

    // 12. Matchmaking (Groom & Bride)
    private val _groomChart = MutableStateFlow(createDefaultSampleChart("Sri Rama", 1990, 4, 15, 11, 30, 26.79, 82.20, 5.5, "Ayodhya"))
    private val _brideChart = MutableStateFlow(createDefaultSampleChart("Sita Devi", 1992, 5, 20, 9, 15, 26.72, 85.92, 5.5, "Janakpur"))
    val compatibilityReport: StateFlow<CompatibilityReport> = combine(_groomChart, _brideChart) { g, b ->
        MatchingEngine.calculateMatch(g, b)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatchingEngine.calculateMatch(_groomChart.value, _brideChart.value))

    // 13. Tajika Sahams & Longevity & Special Chakras
    val tajikaSahams: StateFlow<List<SahamPoint>> = _activeChart.map { chart ->
        TajikaSahamEngine.calculateAllSahams(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val longevityAnalysis: StateFlow<LongevityAnalysis> = _activeChart.map { chart ->
        LongevityEngine.calculateLongevity(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LongevityEngine.calculateLongevity(_activeChart.value))

    val sarvatobhadraReport: StateFlow<SarvatobhadraReport> = _activeChart.map { chart ->
        SpecialChakraEngine.calculateSarvatobhadraChakra(chart)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpecialChakraEngine.calculateSarvatobhadraChakra(_activeChart.value))

    val eclipsesList: StateFlow<List<AstronomicalEclipse>> = MutableStateFlow(SpecialChakraEngine.calculateEclipses(2025)).asStateFlow()
    val solsticesList: StateFlow<List<SolsticeEquinoxEvent>> = MutableStateFlow(SpecialChakraEngine.calculateSolsticesEquinoxes(2025)).asStateFlow()

    // 14. Reverse Search Query & Results
    private val _reverseSearchQuery = MutableStateFlow(
        ReverseSearchQuery(
            startYear = 2025,
            endYear = 2030,
            requiredJupiterRashi = Rashi.CANCER, // Exalted
            requiredSaturnRashi = Rashi.LIBRA   // Exalted
        )
    )
    val reverseSearchResults: StateFlow<List<SearchResultDate>> = _reverseSearchQuery.map { query ->
        SpecialChakraEngine.searchPlanetaryConfigurations(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed default profile if empty
        viewModelScope.launch {
            if (kundaliDao.getProfileCount() == 0) {
                val sampleEntity = KundaliEntity(
                    name = "Vedic Seeker",
                    gender = "Male",
                    birthDate = "1995-07-23",
                    birthTime = "06:30",
                    locationName = "Varanasi, India",
                    latitude = 25.3176,
                    longitude = 82.9739,
                    timezoneOffsetHours = 5.5,
                    ayanamshaName = "LAHIRI",
                    notes = "Sacred birth chart at holy Kashi Ghats."
                )
                kundaliDao.insertProfile(sampleEntity)
            }
        }
    }

    // Actions
    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleChartStyle() {
        _isNorthIndianStyle.value = !_isNorthIndianStyle.value
    }

    fun setVargaType(vargaType: VargaType) {
        _selectedVargaType.value = vargaType
    }

    fun setAyanamsha(ayanamsha: AyanamshaSystem) {
        _selectedAyanamsha.value = ayanamsha
        recalculateChart(_activeChart.value.personName, _activeChart.value.birthDate, _activeChart.value.birthTime, _activeChart.value.latitude, _activeChart.value.longitude, _activeChart.value.timezoneOffset, _activeChart.value.birthPlace)
    }

    fun calculateNewChart(
        name: String,
        gender: String,
        date: String,
        time: String,
        lat: Double,
        lon: Double,
        tz: Double,
        place: String,
        saveToDb: Boolean = true
    ) {
        recalculateChart(name, date, time, lat, lon, tz, place)

        if (saveToDb) {
            viewModelScope.launch {
                kundaliDao.insertProfile(
                    KundaliEntity(
                        name = name,
                        gender = gender,
                        birthDate = date,
                        birthTime = time,
                        locationName = place,
                        latitude = lat,
                        longitude = lon,
                        timezoneOffsetHours = tz,
                        ayanamshaName = _selectedAyanamsha.value.name,
                        notes = "Saved Vedic Chart"
                    )
                )
            }
        }
    }

    fun loadProfile(entity: KundaliEntity) {
        val ayan = try { AyanamshaSystem.valueOf(entity.ayanamshaSystem) } catch (e: Exception) { AyanamshaSystem.LAHIRI }
        _selectedAyanamsha.value = ayan
        recalculateChart(
            name = entity.name,
            date = entity.birthDate,
            time = entity.birthTime,
            lat = entity.latitude,
            lon = entity.longitude,
            tz = entity.timezoneOffset,
            place = entity.cityName
        )
        _currentScreen.value = AppScreen.KUNDALI_OVERVIEW
    }

    fun deleteProfile(entity: KundaliEntity) {
        viewModelScope.launch {
            kundaliDao.deleteProfile(entity)
        }
    }

    fun updatePanchangaDate(offsetDays: Int) {
        val newCal = (_panchangaDate.value.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, offsetDays)
        }
        _panchangaDate.value = newCal
    }

    fun setReverseSearchQuery(query: ReverseSearchQuery) {
        _reverseSearchQuery.value = query
    }

    private fun recalculateChart(
        name: String,
        date: String,
        time: String,
        lat: Double,
        lon: Double,
        tz: Double,
        place: String
    ) {
        try {
            val dParts = date.split("-")
            val tParts = time.split(":")
            val y = dParts[0].toInt()
            val m = dParts[1].toInt()
            val d = dParts[2].toInt()
            val hr = tParts[0].toInt()
            val min = tParts[1].toInt()

            val chart = AstroEngine.calculateBirthChart(
                personName = name,
                year = y,
                month = m,
                day = d,
                hour = hr,
                minute = min,
                latitude = lat,
                longitude = lon,
                timezoneOffsetHours = tz,
                birthPlace = place,
                ayanamshaSystem = _selectedAyanamsha.value
            )
            _activeChart.value = chart
        } catch (e: Exception) {
            // retain fallback
        }
    }

    private fun createDefaultSampleChart(
        name: String = "Vedic Seeker",
        y: Int = 1995,
        m: Int = 7,
        d: Int = 23,
        hr: Int = 6,
        min: Int = 30,
        lat: Double = 25.3176,
        lon: Double = 82.9739,
        tz: Double = 5.5,
        place: String = "Varanasi, India"
    ): BirthChart {
        return AstroEngine.calculateBirthChart(
            personName = name,
            year = y,
            month = m,
            day = d,
            hour = hr,
            minute = min,
            latitude = lat,
            longitude = lon,
            timezoneOffsetHours = tz,
            birthPlace = place,
            ayanamshaSystem = AyanamshaSystem.LAHIRI
        )
    }
}
