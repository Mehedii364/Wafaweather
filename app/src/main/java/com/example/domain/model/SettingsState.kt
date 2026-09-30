package com.example.domain.model

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    BANGLA("bn", "বাংলা", "🇧🇩"),
    ENGLISH("en", "English", "🇬🇧")
}

enum class AppThemeMode(val titleEn: String, val titleBn: String) {
    SYSTEM("System Default", "সিস্টেম ডিফল্ট"),
    LIGHT("Light Mode", "লাইট মোড"),
    DARK("Dark Mode", "ডার্ক মোড")
}

enum class TemperatureUnit(val symbol: String, val labelEn: String, val labelBn: String) {
    CELSIUS("°C", "Celsius (°C)", "সেলসিয়াস (°C)"),
    FAHRENHEIT("°F", "Fahrenheit (°F)", "ফারেনহাইট (°F)")
}

enum class WindSpeedUnit(val symbolEn: String, val symbolBn: String) {
    KMH("km/h", "কিমি/ঘণ্টা"),
    MS("m/s", "মি/সেকেন্ড"),
    MPH("mph", "মাইল/ঘণ্টা")
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.BANGLA,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val tempUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windUnit: WindSpeedUnit = WindSpeedUnit.KMH
)
