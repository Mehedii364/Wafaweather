package com.example.domain.model

data class FullWeatherData(
    val locationName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyWeather>,
    val dailyForecast: List<DailyWeather>,
    val alerts: List<WeatherAlertItem>,
    val lastUpdatedMillis: Long = System.currentTimeMillis(),
    val isFromCache: Boolean = false
)

data class CurrentWeather(
    val time: String,
    val temperatureC: Double,
    val apparentTemperatureC: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val windDirectionDegrees: Int,
    val uvIndex: Double,
    val pressureHpa: Double,
    val precipitationMm: Double,
    val rainMm: Double,
    val cloudCoverPercent: Int,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val todayMaxTempC: Double,
    val todayMinTempC: Double,
    val todaySunrise: String,
    val todaySunset: String
)

data class HourlyWeather(
    val timeString: String, // e.g. "2026-09-30T09:00"
    val displayHourEn: String, // e.g. "09 AM"
    val displayHourBn: String, // e.g. "০৯:০০"
    val temperatureC: Double,
    val apparentTemperatureC: Double,
    val condition: WeatherCondition,
    val precipitationProbability: Int,
    val precipitationMm: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val pressureHpa: Double,
    val uvIndex: Double,
    val visibilityMeters: Double
)

data class DailyWeather(
    val dateString: String, // "2026-09-30"
    val dayNameEn: String, // "Today", "Wednesday", etc.
    val dayNameBn: String, // "আজ", "বুধবার", etc.
    val formattedDateEn: String, // "Sep 30"
    val formattedDateBn: String, // "৩০ সেপ্টেম্বর"
    val maxTempC: Double,
    val minTempC: Double,
    val apparentMaxC: Double,
    val apparentMinC: Double,
    val condition: WeatherCondition,
    val precipitationProbabilityMax: Int,
    val precipitationSumMm: Double,
    val windSpeedMaxKmh: Double,
    val uvIndexMax: Double,
    val sunrise: String,
    val sunset: String
)

data class WeatherAlertItem(
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val severity: AlertSeverity,
    val issuedTime: String
)

enum class AlertSeverity {
    INFO,
    WARNING,
    DANGER
}
