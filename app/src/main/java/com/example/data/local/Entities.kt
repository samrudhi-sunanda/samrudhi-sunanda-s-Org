package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String = "default_user",
    val activePersonaId: String = "cyclist",
    val activeDomain: String = "Fitness & Outdoor Sports",
    val isOnboarded: Boolean = true,
    val sleepTime: String = "22:30",
    val wakeTime: String = "06:30",
    val soundscapeEnabled: Boolean = true,
    val isDarkTheme: Boolean = false
)

@Entity(tableName = "persona_preferences")
data class PersonaPreferenceEntity(
    @PrimaryKey val personaId: String,
    val domain: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val primaryMetricsJson: String,
    val telemetryPrimed: Boolean = true
)

@Entity(tableName = "alert_thresholds")
data class AlertThresholdEntity(
    @PrimaryKey val personaId: String,
    val maxWindKmh: Float = 25f,
    val maxGustKmh: Float = 35f,
    val maxAqi: Int = 100,
    val maxRainProbability: Int = 20,
    val nightBeforeAlertsEnabled: Boolean = true,
    val departureShiftMinutes: Int = 25
)

/**
 * Primary local cache table for real-time weather telemetry data.
 * Caches Doppler radar readings, kinetic wind vectors, air matrix, and road friction.
 */
@Entity(
    tableName = "cached_weather",
    indices = [Index(value = ["locationKey"]), Index(value = ["lastUpdated"])]
)
data class CachedWeatherEntity(
    @PrimaryKey val locationKey: String = "current_location",
    val locationName: String = "BLR Loop Sector 4",
    val windSpeedKmh: Int = 24,
    val windDirectionDeg: Int = 315,
    val windDirectionCardinal: String = "NW",
    val gustSpeedKmh: Int = 38,
    val aeroDragPct: Int = -12,
    val precipWindowPct: Int = 0,
    val precipSummary: String = "Bone dry for next 90m",
    val sightRangeKm: Float = 9.8f,
    val aqi: Int = 42,
    val aqiRating: String = "Good",
    val pm25: Float = 11f,
    val tireTractionPct: Int = 98,
    val uvIndex: Float = 3.2f,
    val uvRating: String = "Moderate Sun",
    val pressureHpa: Int = 1014,
    val pressureTendency: String = "Steady isobar",
    val stormCellRisk: String = "Nil",
    val source: String = "DOPPLER_MESONET",
    val temperatureCelsius: Float = 24.5f,
    val feelsLikeCelsius: Float = 25.8f,
    val humidityPct: Int = 58,
    val dewPointCelsius: Float = 15.6f,
    val pm10: Float = 24f,
    val lastUpdated: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30 * 60 * 1000L) // 30-min TTL
)

/**
 * Historical telemetry snapshots table for time-series charts, timeline analysis, and offline playback.
 */
@Entity(
    tableName = "telemetry_history",
    indices = [Index(value = ["locationKey"]), Index(value = ["recordedAt"])]
)
data class TelemetryHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val locationKey: String,
    val recordedAt: Long = System.currentTimeMillis(),
    val windSpeedKmh: Int,
    val gustSpeedKmh: Int,
    val aqi: Int,
    val roadFrictionPct: Int,
    val temperatureCelsius: Float = 24.5f,
    val barometricHpa: Int
)

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val routeId: String,
    val title: String,
    val subtitle: String,
    val distanceKm: Float,
    val durationMin: Int,
    val hazardCount: Int,
    val hazardSummary: String,
    val isRecommended: Boolean,
    val routeAqi: Int,
    val peakCrosswindKmh: Int,
    val sensorConfidencePct: Float
)

@Entity(tableName = "biosync_alarms")
data class BioSyncAlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetTime: String,
    val suggestedShiftMin: Int,
    val message: String,
    val isApplied: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
