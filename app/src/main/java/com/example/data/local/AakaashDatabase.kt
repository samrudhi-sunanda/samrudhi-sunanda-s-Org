package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    fun getUserFlow(id: String = "default_user"): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    suspend fun getUser(id: String = "default_user"): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("UPDATE users SET activePersonaId = :personaId, activeDomain = :domain WHERE userId = :id")
    suspend fun updatePersona(id: String = "default_user", personaId: String, domain: String)

    @Query("UPDATE users SET isOnboarded = :isOnboarded WHERE userId = :id")
    suspend fun setOnboarded(id: String = "default_user", isOnboarded: Boolean)

    @Query("UPDATE users SET soundscapeEnabled = :enabled WHERE userId = :id")
    suspend fun setSoundscape(id: String = "default_user", enabled: Boolean)

    @Query("UPDATE users SET isDarkTheme = :isDark WHERE userId = :id")
    suspend fun updateTheme(id: String = "default_user", isDark: Boolean)
}

@Dao
interface PersonaDao {
    @Query("SELECT * FROM persona_preferences")
    fun getAllPersonasFlow(): Flow<List<PersonaPreferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(personas: List<PersonaPreferenceEntity>)

    @Query("SELECT * FROM persona_preferences WHERE personaId = :id LIMIT 1")
    suspend fun getPersonaById(id: String): PersonaPreferenceEntity?
}

@Dao
interface WeatherDao {
    @Query("SELECT * FROM cached_weather WHERE locationKey = :key LIMIT 1")
    fun getWeatherFlow(key: String = "current_location"): Flow<CachedWeatherEntity?>

    @Query("SELECT * FROM cached_weather WHERE locationKey = :key LIMIT 1")
    suspend fun getWeatherDirect(key: String = "current_location"): CachedWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeather(weather: CachedWeatherEntity)

    @Query("SELECT * FROM telemetry_history WHERE locationKey = :key ORDER BY recordedAt DESC LIMIT :limit")
    fun getTelemetryHistoryFlow(key: String = "current_location", limit: Int = 24): Flow<List<TelemetryHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetrySnapshot(snapshot: TelemetryHistoryEntity): Long

    @Query("DELETE FROM cached_weather WHERE expiresAt < :nowTimestamp")
    suspend fun evictExpiredWeather(nowTimestamp: Long = System.currentTimeMillis()): Int

    @Query("DELETE FROM telemetry_history WHERE recordedAt < :cutoffTimestamp")
    suspend fun pruneOldHistory(cutoffTimestamp: Long): Int

    @Query("DELETE FROM cached_weather")
    suspend fun clearAllCachedWeather(): Int
}

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes")
    fun getAllRoutesFlow(): Flow<List<RouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutes(routes: List<RouteEntity>)

    @Update
    suspend fun updateRoute(route: RouteEntity)
}

@Dao
interface AlertThresholdDao {
    @Query("SELECT * FROM alert_thresholds WHERE personaId = :personaId LIMIT 1")
    fun getThresholdFlow(personaId: String): Flow<AlertThresholdEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setThreshold(threshold: AlertThresholdEntity)
}

@Dao
interface BioSyncAlarmDao {
    @Query("SELECT * FROM biosync_alarms ORDER BY id DESC")
    fun getAllAlarmsFlow(): Flow<List<BioSyncAlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: BioSyncAlarmEntity): Long

    @Query("UPDATE biosync_alarms SET isApplied = :applied WHERE id = :id")
    suspend fun markApplied(id: Long, applied: Boolean)
}

@Database(
    entities = [
        UserEntity::class,
        PersonaPreferenceEntity::class,
        AlertThresholdEntity::class,
        CachedWeatherEntity::class,
        TelemetryHistoryEntity::class,
        RouteEntity::class,
        BioSyncAlarmEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AakaashDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun personaDao(): PersonaDao
    abstract fun weatherDao(): WeatherDao
    abstract fun routeDao(): RouteDao
    abstract fun alertThresholdDao(): AlertThresholdDao
    abstract fun bioSyncAlarmDao(): BioSyncAlarmDao
}
