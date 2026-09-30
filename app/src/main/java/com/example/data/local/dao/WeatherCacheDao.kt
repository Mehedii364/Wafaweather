package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE locationKey = :key LIMIT 1")
    suspend fun getCache(key: String): WeatherCacheEntity?

    @Query("SELECT * FROM weather_cache WHERE locationKey = :key LIMIT 1")
    fun observeCache(key: String): Flow<WeatherCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCache(cache: WeatherCacheEntity)

    @Query("SELECT * FROM weather_cache ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestCache(): WeatherCacheEntity?
}
