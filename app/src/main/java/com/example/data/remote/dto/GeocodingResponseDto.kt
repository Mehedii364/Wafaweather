package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponseDto(
    @Json(name = "results") val results: List<GeocodingResultDto>? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "country_code") val countryCode: String? = null,
    @Json(name = "admin1") val admin1: String? = null,
    @Json(name = "timezone") val timezone: String? = null
)
