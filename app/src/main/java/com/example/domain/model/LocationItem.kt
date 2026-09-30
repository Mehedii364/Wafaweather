package com.example.domain.model

data class LocationItem(
    val id: Long = 0,
    val name: String,
    val country: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val isFavorite: Boolean = false,
    val isCurrentGps: Boolean = false,
    val cachedTempC: Double? = null,
    val cachedCondition: String? = null,
    val cachedWeatherCode: Int? = null
)
