package com.example.ui.components.bento

import androidx.compose.ui.graphics.Color
import com.example.data.local.CachedWeatherEntity

/**
 * Temperature display unit enumeration.
 */
enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F");

    fun fromCelsius(celsius: Float): Float = when (this) {
        CELSIUS -> celsius
        FAHRENHEIT -> (celsius * 9f / 5f) + 32f
    }
}

/**
 * Air Quality tier evaluation model.
 */
enum class AqiLevel(
    val label: String,
    val color: Color,
    val description: String,
    val activityAdvice: String
) {
    GOOD("Good", Color(0xFF00E676), "0-50 AQI", "Ideal for all outdoor activities"),
    MODERATE("Moderate", Color(0xFFFFD600), "51-100 AQI", "Acceptable; sensitive groups should monitor"),
    UNHEALTHY_SENSITIVE("Sensitive", Color(0xFFFF9100), "101-150 AQI", "Reduce prolonged heavy outdoor exertion"),
    UNHEALTHY("Unhealthy", Color(0xFFFF3D00), "151-200 AQI", "Limit heavy outdoor cardio sessions"),
    VERY_UNHEALTHY("Very Unhealthy", Color(0xFFD500F9), "201-300 AQI", "Avoid outdoor activities, wear mask"),
    HAZARDOUS("Hazardous", Color(0xFF880E4F), "301+ AQI", "Emergency health warnings active");

    companion object {
        fun fromScore(score: Int): AqiLevel = when {
            score <= 50 -> GOOD
            score <= 100 -> MODERATE
            score <= 150 -> UNHEALTHY_SENSITIVE
            score <= 200 -> UNHEALTHY
            score <= 300 -> VERY_UNHEALTHY
            else -> HAZARDOUS
        }
    }
}

/**
 * Clean data carrier for dynamic environmental telemetry.
 */
data class EnvironmentalTelemetry(
    val temperatureCelsius: Float = 24.5f,
    val feelsLikeCelsius: Float = 25.8f,
    val tempMinCelsius: Float = 19.2f,
    val tempMaxCelsius: Float = 28.6f,
    val humidityPct: Int = 58,
    val dewPointCelsius: Float = 15.6f,
    val aqi: Int = 42,
    val pm25: Float = 11f,
    val pm10: Float = 24f,
    val uvIndex: Float = 3.2f,
    val uvRating: String = "Moderate Sun",
    val pressureHpa: Int = 1014,
    val pressureTendency: String = "Steady isobar",
    val tireTractionPct: Int = 98,
    val hourlyTemperaturesCelsius: List<Float> = listOf(21f, 22f, 24.5f, 26f, 28f, 25f),
    val isLiveSensorConnected: Boolean = true
) {
    val aqiLevel: AqiLevel get() = AqiLevel.fromScore(aqi)

    val humidityStatus: String
        get() = when {
            humidityPct < 30 -> "Dry Air"
            humidityPct in 30..60 -> "Optimal Comfort"
            humidityPct in 61..75 -> "Moderate Humid"
            else -> "High Humidity"
        }

    val comfortIndexScore: Int
        get() {
            // Composite comfort score between 0 and 100 based on Temp (ideal 22°C), Humidity (ideal 50%), AQI (<50)
            val tempPenalty = kotlin.math.abs(temperatureCelsius - 22f) * 2.5f
            val humidityPenalty = kotlin.math.abs(humidityPct - 50) * 0.5f
            val aqiPenalty = (aqi * 0.35f)
            return (100f - tempPenalty - humidityPenalty - aqiPenalty).toInt().coerceIn(10, 99)
        }
}

/**
 * Mapper extension from database entity to unified telemetry model.
 */
fun CachedWeatherEntity.toEnvironmentalTelemetry(): EnvironmentalTelemetry {
    return EnvironmentalTelemetry(
        temperatureCelsius = this.temperatureCelsius,
        feelsLikeCelsius = this.feelsLikeCelsius,
        tempMinCelsius = (this.temperatureCelsius - 4.5f).coerceAtLeast(-10f),
        tempMaxCelsius = this.temperatureCelsius + 4.1f,
        humidityPct = this.humidityPct,
        dewPointCelsius = this.dewPointCelsius,
        aqi = this.aqi,
        pm25 = this.pm25,
        pm10 = this.pm10,
        uvIndex = this.uvIndex,
        uvRating = this.uvRating,
        pressureHpa = this.pressureHpa,
        pressureTendency = this.pressureTendency,
        tireTractionPct = this.tireTractionPct,
        hourlyTemperaturesCelsius = listOf(
            (this.temperatureCelsius - 3f),
            (this.temperatureCelsius - 1.5f),
            this.temperatureCelsius,
            (this.temperatureCelsius + 2.2f),
            (this.temperatureCelsius + 3.8f),
            (this.temperatureCelsius + 1.1f)
        )
    )
}
