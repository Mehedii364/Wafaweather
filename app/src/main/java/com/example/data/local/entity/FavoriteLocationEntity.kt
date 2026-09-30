package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_locations")
data class FavoriteLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val country: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val isCurrentGps: Boolean = false,
    val cachedTemp: Double? = null,
    val cachedCondition: String? = null,
    val cachedWeatherCode: Int? = null,
    val addedAt: Long = System.currentTimeMillis()
)
