package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AakaashDatabase
import com.example.data.local.CachedWeatherEntity
import com.example.data.local.TelemetryHistoryEntity
import com.example.data.model.PersonaCatalog
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AakaashDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AakaashDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Mausam", appName)
    }

    @Test
    fun `verify persona catalog contains all 28 personas`() {
        val totalPersonas = PersonaCatalog.categories.sumOf { it.personas.size }
        assertEquals(28, totalPersonas)

        val cyclist = PersonaCatalog.findPersona("cyclist")
        assertNotNull(cyclist)
        assertEquals("Cyclists & Road Bikers", cyclist.title)
    }

    @Test
    fun `test Room local storage caches weather telemetry data correctly`() = runBlocking {
        val weatherDao = db.weatherDao()
        val testWeather = CachedWeatherEntity(
            locationKey = "blr_sector_4",
            locationName = "Sector 4 → Tech Corridor",
            windSpeedKmh = 28,
            windDirectionDeg = 315,
            windDirectionCardinal = "NW",
            gustSpeedKmh = 42,
            aeroDragPct = -14,
            precipWindowPct = 5,
            precipSummary = "Dry conditions for next 60m",
            sightRangeKm = 9.5f,
            aqi = 45,
            aqiRating = "Good",
            pm25 = 12.6f,
            tireTractionPct = 96,
            uvIndex = 4.1f,
            uvRating = "Moderate Sun",
            pressureHpa = 1012,
            pressureTendency = "Steady isobar",
            stormCellRisk = "Nil",
            source = "DOPPLER_MESONET",
            lastUpdated = 1700000000000L,
            expiresAt = 1700001800000L
        )

        // Cache weather telemetry in Room
        weatherDao.saveWeather(testWeather)

        // Retrieve from local storage
        val cached = weatherDao.getWeatherDirect("blr_sector_4")
        assertNotNull("Weather telemetry should be cached locally in Room database", cached)
        assertEquals(28, cached?.windSpeedKmh)
        assertEquals(42, cached?.gustSpeedKmh)
        assertEquals(45, cached?.aqi)
        assertEquals(96, cached?.tireTractionPct)
        assertEquals(1012, cached?.pressureHpa)
        assertEquals("DOPPLER_MESONET", cached?.source)
    }

    @Test
    fun `test Room telemetry history snapshots and eviction`() = runBlocking {
        val weatherDao = db.weatherDao()

        // Insert historical telemetry snapshot
        val snapshot = TelemetryHistoryEntity(
            locationKey = "blr_sector_4",
            recordedAt = System.currentTimeMillis(),
            windSpeedKmh = 24,
            gustSpeedKmh = 38,
            aqi = 42,
            roadFrictionPct = 98,
            temperatureCelsius = 24.0f,
            barometricHpa = 1014
        )
        val id = weatherDao.insertTelemetrySnapshot(snapshot)
        assertNotNull(id)

        // Insert expired weather record
        val expiredWeather = CachedWeatherEntity(
            locationKey = "expired_station",
            lastUpdated = 1000L,
            expiresAt = 2000L
        )
        weatherDao.saveWeather(expiredWeather)

        // Evict with current time > 2000L
        val evictedCount = weatherDao.evictExpiredWeather(5000L)
        assertEquals(1, evictedCount)

        val checkExpired = weatherDao.getWeatherDirect("expired_station")
        assertNull("Expired record must be evicted from Room cache", checkExpired)
    }

    @Test
    fun `verify Gemini model configuration matches requested models`() {
        assertEquals("gemini-3.5-flash", com.example.data.gemini.GeminiModels.GEMINI_3_5_FLASH)
        assertEquals("gemini-3.1-pro-preview", com.example.data.gemini.GeminiModels.GEMINI_3_1_PRO)
        assertEquals("gemini-3.1-flash-lite-preview", com.example.data.gemini.GeminiModels.GEMINI_3_1_FLASH_LITE)
        assertEquals("gemini-3.5-transcribe", com.example.data.gemini.GeminiModels.GEMINI_3_5_TRANSCRIBE)
        assertEquals("gemini-3.8-live", com.example.data.gemini.GeminiModels.GEMINI_3_8_LIVE)

        val chatModels = com.example.data.gemini.GeminiModels.CHAT_MODELS
        assertEquals(3, chatModels.size)
    }

    @Test
    fun `verify multi-turn chat message and grounding citations`() {
        val citation = com.example.data.gemini.GroundingCitation(
            title = "Tech Corridor Wind Alert",
            uri = "https://maps.google.com/?q=Tech+Corridor",
            sourceType = com.example.data.gemini.GroundingType.GOOGLE_MAPS
        )
        val message = com.example.data.gemini.ChatMessage(
            sender = com.example.data.gemini.MessageSender.ASSISTANT,
            text = "Crosswinds are high at Tech Corridor.",
            modelUsed = com.example.data.gemini.GeminiModels.GEMINI_3_5_FLASH,
            groundingSources = listOf(citation)
        )

        assertEquals("Tech Corridor Wind Alert", message.groundingSources.first().title)
        assertEquals(com.example.data.gemini.GroundingType.GOOGLE_MAPS, message.groundingSources.first().sourceType)
    }

    @Test
    fun `verify environmental telemetry calculations and bento model mappings`() {
        val telemetry = com.example.ui.components.bento.EnvironmentalTelemetry(
            temperatureCelsius = 25f,
            feelsLikeCelsius = 26f,
            humidityPct = 50,
            dewPointCelsius = 14f,
            aqi = 40
        )

        assertEquals("Optimal Comfort", telemetry.humidityStatus)
        assertEquals(com.example.ui.components.bento.AqiLevel.GOOD, telemetry.aqiLevel)

        val fahrenheit = com.example.ui.components.bento.TemperatureUnit.FAHRENHEIT.fromCelsius(25f)
        assertEquals(77f, fahrenheit, 0.1f)

        assertTrue("Comfort score should be high under optimal baseline conditions", telemetry.comfortIndexScore > 75)
    }
}
