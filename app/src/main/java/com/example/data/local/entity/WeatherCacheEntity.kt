package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val locationKey: String, // format "lat_lon"
    val locationName: String,
    val country: String,
    val jsonPayload: String,
    val timestamp: Long = System.currentTimeMillis()
)
