package com.example.domain.model

enum class WeatherConditionType {
    CLEAR_SUNNY,
    PARTLY_CLOUDY,
    CLOUDY,
    FOG,
    DRIZZLE,
    RAIN,
    HEAVY_RAIN,
    SNOW,
    THUNDERSTORM
}

data class WeatherCondition(
    val code: Int,
    val type: WeatherConditionType,
    val titleEn: String,
    val titleBn: String,
    val emoji: String,
    val isDay: Boolean = true
) {
    companion object {
        fun fromWmoCode(code: Int, isDay: Boolean = true): WeatherCondition {
            return when (code) {
                0 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.CLEAR_SUNNY,
                    titleEn = if (isDay) "Clear Sky" else "Clear Night",
                    titleBn = if (isDay) "পরিষ্কার আকাশ" else "পরিষ্কার রাত",
                    emoji = if (isDay) "☀️" else "🌙",
                    isDay = isDay
                )
                1 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.CLEAR_SUNNY,
                    titleEn = "Mainly Clear",
                    titleBn = "অধিকাংশ সময় পরিষ্কার",
                    emoji = if (isDay) "🌤️" else "🌤️",
                    isDay = isDay
                )
                2 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.PARTLY_CLOUDY,
                    titleEn = "Partly Cloudy",
                    titleBn = "আংশিক মেঘলা",
                    emoji = if (isDay) "⛅" else "☁️",
                    isDay = isDay
                )
                3 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.CLOUDY,
                    titleEn = "Overcast Cloudy",
                    titleBn = "মেঘলা আকাশ",
                    emoji = "☁️",
                    isDay = isDay
                )
                45, 48 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.FOG,
                    titleEn = "Foggy & Mist",
                    titleBn = "কুয়াশাচ্ছন্ন",
                    emoji = "🌫️",
                    isDay = isDay
                )
                51, 53, 55 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.DRIZZLE,
                    titleEn = "Drizzle",
                    titleBn = "ঝিরিঝিরি বৃষ্টি",
                    emoji = "🌦️",
                    isDay = isDay
                )
                56, 57 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.DRIZZLE,
                    titleEn = "Freezing Drizzle",
                    titleBn = "হিমশীতল ঝিরিঝিরি বৃষ্টি",
                    emoji = "🌨️",
                    isDay = isDay
                )
                61, 63 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.RAIN,
                    titleEn = "Moderate Rain",
                    titleBn = "বৃষ্টিপাত",
                    emoji = "🌧️",
                    isDay = isDay
                )
                65 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.HEAVY_RAIN,
                    titleEn = "Heavy Rain",
                    titleBn = "ভারী বৃষ্টি",
                    emoji = "🌧️",
                    isDay = isDay
                )
                66, 67 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.RAIN,
                    titleEn = "Freezing Rain",
                    titleBn = "হিমশীতল বৃষ্টি",
                    emoji = "🌧️",
                    isDay = isDay
                )
                71, 73, 75, 77 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.SNOW,
                    titleEn = "Snowfall",
                    titleBn = "তুষারপাত",
                    emoji = "❄️",
                    isDay = isDay
                )
                80, 81 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.RAIN,
                    titleEn = "Rain Showers",
                    titleBn = "বৃষ্টির পশলা",
                    emoji = "🌦️",
                    isDay = isDay
                )
                82 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.HEAVY_RAIN,
                    titleEn = "Violent Rain Showers",
                    titleBn = "তীব্র বৃষ্টির ধারা",
                    emoji = "⛈️",
                    isDay = isDay
                )
                85, 86 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.SNOW,
                    titleEn = "Snow Showers",
                    titleBn = "তুষারঝড়",
                    emoji = "🌨️",
                    isDay = isDay
                )
                95 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.THUNDERSTORM,
                    titleEn = "Thunderstorm",
                    titleBn = "বজ্রঝড়",
                    emoji = "⛈️",
                    isDay = isDay
                )
                96, 99 -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.THUNDERSTORM,
                    titleEn = "Severe Thunderstorm with Hail",
                    titleBn = "শিলাবৃষ্টিসহ তীব্র বজ্রঝড়",
                    emoji = "⛈️",
                    isDay = isDay
                )
                else -> WeatherCondition(
                    code = code,
                    type = WeatherConditionType.PARTLY_CLOUDY,
                    titleEn = "Partly Cloudy",
                    titleBn = "আংশিক মেঘলা",
                    emoji = "⛅",
                    isDay = isDay
                )
            }
        }
    }
}
