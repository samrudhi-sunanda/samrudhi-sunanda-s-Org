package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.AakaashDatabase
import com.example.data.local.AlertThresholdEntity
import com.example.data.local.BioSyncAlarmEntity
import com.example.data.local.CachedWeatherEntity
import com.example.data.local.PersonaPreferenceEntity
import com.example.data.local.RouteEntity
import com.example.data.local.TelemetryHistoryEntity
import com.example.data.local.UserEntity
import com.example.data.model.PersonaCatalog
import com.example.data.model.PersonaProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

class AakaashRepository private constructor(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AakaashDatabase::class.java,
        "aakaash360.db"
    ).fallbackToDestructiveMigration().build()

    private val userDao = db.userDao()
    private val personaDao = db.personaDao()
    private val weatherDao = db.weatherDao()
    private val routeDao = db.routeDao()
    private val thresholdDao = db.alertThresholdDao()
    private val alarmDao = db.bioSyncAlarmDao()

    private val scope = CoroutineScope(Dispatchers.IO)

    // In-memory real-time state for time machine simulation
    private val _timeMachineHourOffset = MutableStateFlow(0)
    val timeMachineHourOffset = _timeMachineHourOffset.asStateFlow()

    private val _isRouteBypassActive = MutableStateFlow(false)
    val isRouteBypassActive = _isRouteBypassActive.asStateFlow()

    private val _activeMapLayer = MutableStateFlow("WIND")
    val activeMapLayer = _activeMapLayer.asStateFlow()

    init {
        scope.launch {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        val existingUser = userDao.getUser()
        if (existingUser == null) {
            userDao.insertOrUpdate(
                UserEntity(
                    userId = "default_user",
                    activePersonaId = "cyclist",
                    activeDomain = "Fitness & Sports",
                    isOnboarded = true,
                    sleepTime = "22:30",
                    wakeTime = "06:30",
                    soundscapeEnabled = true
                )
            )
        }

        // Seed initial routes matching Stitch design
        val routes = listOf(
            RouteEntity(
                routeId = "alpha_direct",
                title = "Path Alpha (Direct)",
                subtitle = "14.2 km • 38 min • Wind shear alert at Mile 3",
                distanceKm = 14.2f,
                durationMin = 38,
                hazardCount = 1,
                hazardSummary = "Mile 3.2 Bottleneck Crosswind (34 km/h) & PM2.5 Spike",
                isRecommended = false,
                routeAqi = 48,
                peakCrosswindKmh = 34,
                sensorConfidencePct = 94.2f
            ),
            RouteEntity(
                routeId = "eco_bypass",
                title = "Eco-Bypass Vector",
                subtitle = "15.1 km • 41 min • Zero exposure (+3 mins)",
                distanceKm = 15.1f,
                durationMin = 41,
                hazardCount = 0,
                hazardSummary = "100% Green Zone • Calm tailwind vector (+14% aero benefit)",
                isRecommended = true,
                routeAqi = 38,
                peakCrosswindKmh = 14,
                sensorConfidencePct = 96.4f
            )
        )
        routeDao.insertRoutes(routes)

        // Seed baseline weather
        weatherDao.saveWeather(
            CachedWeatherEntity(
                locationKey = "current_location",
                locationName = "Sector 4 → Tech Corridor",
                windSpeedKmh = 24,
                windDirectionDeg = 315,
                windDirectionCardinal = "NW",
                gustSpeedKmh = 38,
                aeroDragPct = -12,
                precipWindowPct = 0,
                precipSummary = "Bone dry for next 90m",
                sightRangeKm = 9.8f,
                aqi = 42,
                aqiRating = "Good",
                pm25 = 11f,
                tireTractionPct = 98,
                uvIndex = 3.2f,
                uvRating = "Moderate Sun",
                pressureHpa = 1014,
                pressureTendency = "Steady isobar",
                stormCellRisk = "Nil",
                temperatureCelsius = 24.5f,
                feelsLikeCelsius = 25.8f,
                humidityPct = 58,
                dewPointCelsius = 15.6f,
                pm10 = 24f
            )
        )

        // Seed default thresholds
        thresholdDao.setThreshold(
            AlertThresholdEntity(
                personaId = "cyclist",
                maxWindKmh = 28f,
                maxGustKmh = 35f,
                maxAqi = 75,
                maxRainProbability = 15,
                nightBeforeAlertsEnabled = true,
                departureShiftMinutes = 25
            )
        )

        // Seed initial alarm
        alarmDao.insertAlarm(
            BioSyncAlarmEntity(
                targetTime = "06:45 AM",
                suggestedShiftMin = -25,
                message = "Strong crosswinds (34 km/h) detected at Mile 3 bottleneck. Shift departure by 25 mins to allow calm 14 km/h tailwinds.",
                isApplied = false
            )
        )
    }

    // Reactive streams
    fun getUserFlow(): Flow<UserEntity?> = userDao.getUserFlow()
    fun getWeatherFlow(): Flow<CachedWeatherEntity?> = weatherDao.getWeatherFlow()
    fun getRoutesFlow(): Flow<List<RouteEntity>> = routeDao.getAllRoutesFlow()
    fun getAlarmsFlow(): Flow<List<BioSyncAlarmEntity>> = alarmDao.getAllAlarmsFlow()
    fun getThresholdFlow(personaId: String): Flow<AlertThresholdEntity?> = thresholdDao.getThresholdFlow(personaId)
    fun getTelemetryHistoryFlow(locationKey: String = "current_location", limit: Int = 24): Flow<List<TelemetryHistoryEntity>> = 
        weatherDao.getTelemetryHistoryFlow(locationKey, limit)

    // Weather caching operations
    suspend fun cacheWeatherTelemetry(weather: CachedWeatherEntity) {
        weatherDao.saveWeather(weather)
        weatherDao.insertTelemetrySnapshot(
            TelemetryHistoryEntity(
                locationKey = weather.locationKey,
                recordedAt = weather.lastUpdated,
                windSpeedKmh = weather.windSpeedKmh,
                gustSpeedKmh = weather.gustSpeedKmh,
                aqi = weather.aqi,
                roadFrictionPct = weather.tireTractionPct,
                barometricHpa = weather.pressureHpa
            )
        )
    }

    suspend fun evictExpiredWeather() {
        weatherDao.evictExpiredWeather()
    }

    suspend fun clearCachedWeather() {
        weatherDao.clearAllCachedWeather()
    }

    // User actions
    suspend fun selectPersona(personaId: String) {
        val profile = PersonaCatalog.findPersona(personaId)
        val domain = PersonaCatalog.categories.find { it.id == profile.domainId }?.title ?: "Fitness & Sports"
        userDao.updatePersona("default_user", personaId, domain)
        
        // Dynamically compute baseline weather metrics tailored to new persona
        recalculateWeatherForPersona(profile, _timeMachineHourOffset.value)
    }

    suspend fun setOnboarded(isOnboarded: Boolean) {
        userDao.setOnboarded("default_user", isOnboarded)
    }

    suspend fun toggleSoundscape(enabled: Boolean) {
        userDao.setSoundscape("default_user", enabled)
    }

    suspend fun updateTheme(isDark: Boolean) {
        userDao.updateTheme("default_user", isDark)
    }

    fun setMapLayer(layer: String) {
        _activeMapLayer.value = layer
    }

    fun setTimeMachineHour(hoursAhead: Int) {
        _timeMachineHourOffset.value = hoursAhead
        scope.launch {
            val user = userDao.getUser() ?: return@launch
            val profile = PersonaCatalog.findPersona(user.activePersonaId)
            recalculateWeatherForPersona(profile, hoursAhead)
        }
    }

    suspend fun recalculateSafeRoute() {
        _isRouteBypassActive.value = true
        // Update recommended route state
        val routes = listOf(
            RouteEntity(
                routeId = "alpha_direct",
                title = "Path Alpha (Direct)",
                subtitle = "14.2 km • 38 min • High Crosswind Risk",
                distanceKm = 14.2f,
                durationMin = 38,
                hazardCount = 1,
                hazardSummary = "Mile 3.2 Hazard: 34 km/h Crosswind",
                isRecommended = false,
                routeAqi = 48,
                peakCrosswindKmh = 34,
                sensorConfidencePct = 94.2f
            ),
            RouteEntity(
                routeId = "eco_bypass",
                title = "Eco-Bypass Vector",
                subtitle = "15.1 km • 41 min • Zero Exposure (Active)",
                distanceKm = 15.1f,
                durationMin = 41,
                hazardCount = 0,
                hazardSummary = "100% Green Zone • Activated Eco-Bypass",
                isRecommended = true,
                routeAqi = 34,
                peakCrosswindKmh = 12,
                sensorConfidencePct = 98.1f
            )
        )
        routeDao.insertRoutes(routes)
    }

    suspend fun applyDepartureShift(alarmId: Long, shiftMinutes: Int = -25) {
        alarmDao.markApplied(alarmId, true)
    }

    suspend fun saveThreshold(threshold: AlertThresholdEntity) {
        thresholdDao.setThreshold(threshold)
    }

    private suspend fun recalculateWeatherForPersona(profile: PersonaProfile, hoursAhead: Int) {
        // Mathematical deterministic model for time-machine progression
        val wave = sin(hoursAhead * 0.35).toFloat()
        val cosWave = cos(hoursAhead * 0.28).toFloat()

        val windSpeed = (24 + (wave * 12)).toInt().coerceIn(8, 55)
        val gustSpeed = (windSpeed + 10 + (cosWave * 8)).toInt().coerceIn(15, 75)
        val aqi = (42 + (wave * 25)).toInt().coerceIn(20, 185)
        val traction = (98 - (if (hoursAhead in 4..9) 24 else 0)).coerceIn(60, 100)
        val uv = (3.2f + (sin((hoursAhead + 4) * 0.25).toFloat() * 4.5f)).coerceIn(0.1f, 11.5f)
        val pressure = (1014 + (cosWave * 8)).toInt().coerceIn(992, 1030)
        val precipPct = if (hoursAhead in 5..10) (45 + (wave * 20)).toInt() else 0

        val aeroDrag = if (windSpeed > 30) -22 else -12
        val sightRange = if (aqi > 100) 4.2f else 9.8f

        weatherDao.saveWeather(
            CachedWeatherEntity(
                locationKey = "current_location",
                locationName = "BLR Loop Sector 4",
                windSpeedKmh = windSpeed,
                windDirectionDeg = (315 + (hoursAhead * 8)) % 360,
                windDirectionCardinal = when (((315 + (hoursAhead * 8)) % 360) / 45) {
                    0 -> "N"; 1 -> "NE"; 2 -> "E"; 3 -> "SE"; 4 -> "S"; 5 -> "SW"; 6 -> "W"; else -> "NW"
                },
                gustSpeedKmh = gustSpeed,
                aeroDragPct = aeroDrag,
                precipWindowPct = precipPct,
                precipSummary = if (precipPct > 30) "Rain showers expected in ${hoursAhead}h" else "Bone dry for next 90m",
                sightRangeKm = sightRange,
                aqi = aqi,
                aqiRating = if (aqi < 50) "Good" else if (aqi < 100) "Moderate" else "Unhealthy",
                pm25 = (aqi * 0.28f),
                tireTractionPct = traction,
                uvIndex = (uv * 10).toInt() / 10f,
                uvRating = if (uv < 3) "Low Sun" else if (uv < 6) "Moderate Sun" else "Very High UV",
                pressureHpa = pressure,
                pressureTendency = if (wave < -0.3) "Falling squall line" else "Steady isobar",
                stormCellRisk = if (precipPct > 40 && windSpeed > 35) "High Alert" else "Nil"
            )
        )
    }

    companion object {
        @Volatile
        private var instance: AakaashRepository? = null

        fun getInstance(context: Context): AakaashRepository {
            return instance ?: synchronized(this) {
                instance ?: AakaashRepository(context).also { instance = it }
            }
        }
    }
}
