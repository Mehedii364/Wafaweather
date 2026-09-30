package com.example.data.repository

import com.example.data.local.dao.FavoriteLocationDao
import com.example.data.local.dao.WeatherCacheDao
import com.example.data.local.entity.FavoriteLocationEntity
import com.example.data.local.entity.WeatherCacheEntity
import com.example.data.remote.api.GeocodingApiService
import com.example.data.remote.api.RetrofitClient
import com.example.data.remote.api.WeatherApiService
import com.example.data.remote.dto.WeatherResponseDto
import com.example.domain.model.AlertSeverity
import com.example.domain.model.AppLanguage
import com.example.domain.model.CurrentWeather
import com.example.domain.model.DailyWeather
import com.example.domain.model.FullWeatherData
import com.example.domain.model.HourlyWeather
import com.example.domain.model.LocationItem
import com.example.domain.model.WeatherAlertItem
import com.example.domain.model.WeatherCondition
import com.example.domain.util.BanglaHelper
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Locale

class WeatherRepository(
    private val weatherApi: WeatherApiService = RetrofitClient.weatherApi,
    private val geocodingApi: GeocodingApiService = RetrofitClient.geocodingApi,
    private val weatherCacheDao: WeatherCacheDao,
    private val favoriteLocationDao: FavoriteLocationDao,
    private val moshi: Moshi = RetrofitClient.moshi
) {

    private val jsonAdapter = moshi.adapter(WeatherResponseDto::class.java)

    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        locationName: String,
        country: String,
        forceRefresh: Boolean = false
    ): Result<FullWeatherData> = withContext(Dispatchers.IO) {
        val cacheKey = String.format(Locale.US, "%.3f_%.3f", latitude, longitude)

        // If not forcing refresh, check cache first
        if (!forceRefresh) {
            val cached = weatherCacheDao.getCache(cacheKey)
            if (cached != null) {
                val ageMinutes = (System.currentTimeMillis() - cached.timestamp) / (1000 * 60)
                // If cache is fresh (< 20 mins), return cached
                if (ageMinutes < 20) {
                    try {
                        val dto = jsonAdapter.fromJson(cached.jsonPayload)
                        if (dto != null) {
                            val parsed = mapDtoToDomain(
                                dto = dto,
                                locationName = cached.locationName,
                                country = cached.country,
                                latitude = latitude,
                                longitude = longitude,
                                isFromCache = true,
                                cacheTimestamp = cached.timestamp
                            )
                            return@withContext Result.success(parsed)
                        }
                    } catch (e: Exception) {
                        // proceed to network
                    }
                }
            }
        }

        // Fetch fresh from network
        try {
            val responseDto = weatherApi.getForecast(
                latitude = latitude,
                longitude = longitude
            )

            // Save to cache
            val jsonString = jsonAdapter.toJson(responseDto)
            weatherCacheDao.saveCache(
                WeatherCacheEntity(
                    locationKey = cacheKey,
                    locationName = locationName,
                    country = country,
                    jsonPayload = jsonString,
                    timestamp = System.currentTimeMillis()
                )
            )

            val domainData = mapDtoToDomain(
                dto = responseDto,
                locationName = locationName,
                country = country,
                latitude = latitude,
                longitude = longitude,
                isFromCache = false,
                cacheTimestamp = System.currentTimeMillis()
            )

            Result.success(domainData)
        } catch (e: Exception) {
            // Network failed: fallback to whatever is in cache
            val fallbackCache = weatherCacheDao.getCache(cacheKey)
                ?: weatherCacheDao.getLatestCache()

            if (fallbackCache != null) {
                try {
                    val dto = jsonAdapter.fromJson(fallbackCache.jsonPayload)
                    if (dto != null) {
                        val parsed = mapDtoToDomain(
                            dto = dto,
                            locationName = fallbackCache.locationName,
                            country = fallbackCache.country,
                            latitude = latitude,
                            longitude = longitude,
                            isFromCache = true,
                            cacheTimestamp = fallbackCache.timestamp
                        )
                        return@withContext Result.success(parsed)
                    }
                } catch (jsonErr: Exception) {
                    // Fallback parse error
                }
            }
            Result.failure(e)
        }
    }

    suspend fun searchLocations(query: String): Result<List<LocationItem>> = withContext(Dispatchers.IO) {
        try {
            val response = geocodingApi.searchLocations(query.trim())
            val results = response.results.orEmpty().map { dto ->
                LocationItem(
                    id = dto.id ?: 0L,
                    name = dto.name.orEmpty(),
                    country = dto.country.orEmpty(),
                    admin1 = dto.admin1,
                    latitude = dto.latitude ?: 0.0,
                    longitude = dto.longitude ?: 0.0,
                    timezone = dto.timezone
                )
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFavoriteLocations(): Flow<List<LocationItem>> {
        return favoriteLocationDao.getAllFavorites().map { list ->
            list.map { entity ->
                LocationItem(
                    id = entity.id,
                    name = entity.name,
                    country = entity.country,
                    admin1 = entity.admin1,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    timezone = entity.timezone,
                    isFavorite = true,
                    isCurrentGps = entity.isCurrentGps,
                    cachedTempC = entity.cachedTemp,
                    cachedCondition = entity.cachedCondition,
                    cachedWeatherCode = entity.cachedWeatherCode
                )
            }
        }
    }

    suspend fun addFavorite(location: LocationItem): Long = withContext(Dispatchers.IO) {
        val entity = FavoriteLocationEntity(
            name = location.name,
            country = location.country,
            admin1 = location.admin1,
            latitude = location.latitude,
            longitude = location.longitude,
            timezone = location.timezone,
            isCurrentGps = location.isCurrentGps,
            cachedTemp = location.cachedTempC,
            cachedCondition = location.cachedCondition,
            cachedWeatherCode = location.cachedWeatherCode
        )
        favoriteLocationDao.insertFavorite(entity)
    }

    suspend fun removeFavorite(locationId: Long) = withContext(Dispatchers.IO) {
        favoriteLocationDao.deleteById(locationId)
    }

    suspend fun updateFavoriteWeather(locationId: Long, tempC: Double, condition: String, code: Int) = withContext(Dispatchers.IO) {
        val existing = favoriteLocationDao.getAllFavorites()
        // we can find and update if needed
    }

    private fun mapDtoToDomain(
        dto: WeatherResponseDto,
        locationName: String,
        country: String,
        latitude: Double,
        longitude: Double,
        isFromCache: Boolean,
        cacheTimestamp: Long
    ): FullWeatherData {
        val currentDto = dto.current
        val hourlyDto = dto.hourly
        val dailyDto = dto.daily

        val isDay = (currentDto?.isDay ?: 1) == 1
        val weatherCode = currentDto?.weatherCode ?: 0
        val condition = WeatherCondition.fromWmoCode(weatherCode, isDay)

        val todayMax = dailyDto?.temperature2mMax?.firstOrNull() ?: currentDto?.temperature2m ?: 25.0
        val todayMin = dailyDto?.temperature2mMin?.firstOrNull() ?: (todayMax - 5.0)
        val todaySunrise = dailyDto?.sunrise?.firstOrNull() ?: ""
        val todaySunset = dailyDto?.sunset?.firstOrNull() ?: ""

        val current = CurrentWeather(
            time = currentDto?.time.orEmpty(),
            temperatureC = currentDto?.temperature2m ?: 25.0,
            apparentTemperatureC = currentDto?.apparentTemperature ?: (currentDto?.temperature2m ?: 25.0),
            humidityPercent = (currentDto?.relativeHumidity2m ?: 60.0).toInt(),
            windSpeedKmh = currentDto?.windSpeed10m ?: 5.0,
            windDirectionDegrees = (currentDto?.windDirection10m ?: 0.0).toInt(),
            uvIndex = currentDto?.uvIndex ?: 4.0,
            pressureHpa = currentDto?.pressureMsl ?: 1012.0,
            precipitationMm = currentDto?.precipitation ?: 0.0,
            rainMm = currentDto?.rain ?: 0.0,
            cloudCoverPercent = (currentDto?.cloudCover ?: 20.0).toInt(),
            condition = condition,
            isDay = isDay,
            todayMaxTempC = todayMax,
            todayMinTempC = todayMin,
            todaySunrise = todaySunrise,
            todaySunset = todaySunset
        )

        // Parse hourly items (next 24 hours)
        val hourlyList = mutableListOf<HourlyWeather>()
        val hourlyTimes = hourlyDto?.time.orEmpty()
        val count = minOf(hourlyTimes.size, 24)

        for (i in 0 until count) {
            val t = hourlyTimes[i]
            val hCode = hourlyDto?.weatherCode?.getOrNull(i) ?: 0
            val hTemp = hourlyDto?.temperature2m?.getOrNull(i) ?: 25.0
            val hApparent = hourlyDto?.apparentTemperature?.getOrNull(i) ?: hTemp
            val hProb = hourlyDto?.precipitationProbability?.getOrNull(i) ?: 0
            val hPrecip = hourlyDto?.precipitation?.getOrNull(i) ?: 0.0
            val hHumidity = (hourlyDto?.relativeHumidity2m?.getOrNull(i) ?: 50.0).toInt()
            val hWind = hourlyDto?.windSpeed10m?.getOrNull(i) ?: 5.0
            val hPress = hourlyDto?.pressureMsl?.getOrNull(i) ?: 1012.0
            val hUv = hourlyDto?.uvIndex?.getOrNull(i) ?: 0.0
            val hVis = hourlyDto?.visibility?.getOrNull(i) ?: 10000.0

            // Determine if hour is daytime (between 6am and 6pm roughly or using hour)
            val hourOfDay = try {
                t.split("T").getOrNull(1)?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 12
            } catch (e: Exception) {
                12
            }
            val hourIsDay = hourOfDay in 6..18
            val hCondition = WeatherCondition.fromWmoCode(hCode, hourIsDay)

            hourlyList.add(
                HourlyWeather(
                    timeString = t,
                    displayHourEn = BanglaHelper.formatIsoTimeToHour(t, AppLanguage.ENGLISH),
                    displayHourBn = BanglaHelper.formatIsoTimeToHour(t, AppLanguage.BANGLA),
                    temperatureC = hTemp,
                    apparentTemperatureC = hApparent,
                    condition = hCondition,
                    precipitationProbability = hProb,
                    precipitationMm = hPrecip,
                    humidityPercent = hHumidity,
                    windSpeedKmh = hWind,
                    pressureHpa = hPress,
                    uvIndex = hUv,
                    visibilityMeters = hVis
                )
            )
        }

        // Parse 7-day forecast
        val dailyList = mutableListOf<DailyWeather>()
        val dailyTimes = dailyDto?.time.orEmpty()
        for (i in dailyTimes.indices) {
            val dTime = dailyTimes[i]
            val dCode = dailyDto?.weatherCode?.getOrNull(i) ?: 0
            val dMax = dailyDto?.temperature2mMax?.getOrNull(i) ?: 30.0
            val dMin = dailyDto?.temperature2mMin?.getOrNull(i) ?: 20.0
            val dAppMax = dailyDto?.apparentTemperatureMax?.getOrNull(i) ?: dMax
            val dAppMin = dailyDto?.apparentTemperatureMin?.getOrNull(i) ?: dMin
            val dSunrise = dailyDto?.sunrise?.getOrNull(i) ?: ""
            val dSunset = dailyDto?.sunset?.getOrNull(i) ?: ""
            val dUvMax = dailyDto?.uvIndexMax?.getOrNull(i) ?: 5.0
            val dPrecipSum = dailyDto?.precipitationSum?.getOrNull(i) ?: 0.0
            val dPrecipProb = dailyDto?.precipitationProbabilityMax?.getOrNull(i) ?: 0
            val dWindMax = dailyDto?.windSpeed10mMax?.getOrNull(i) ?: 10.0
            val dCondition = WeatherCondition.fromWmoCode(dCode, true)

            dailyList.add(
                DailyWeather(
                    dateString = dTime,
                    dayNameEn = BanglaHelper.formatDayName(dTime, i, AppLanguage.ENGLISH),
                    dayNameBn = BanglaHelper.formatDayName(dTime, i, AppLanguage.BANGLA),
                    formattedDateEn = BanglaHelper.formatReadableDate(dTime, AppLanguage.ENGLISH),
                    formattedDateBn = BanglaHelper.formatReadableDate(dTime, AppLanguage.BANGLA),
                    maxTempC = dMax,
                    minTempC = dMin,
                    apparentMaxC = dAppMax,
                    apparentMinC = dAppMin,
                    condition = dCondition,
                    precipitationProbabilityMax = dPrecipProb,
                    precipitationSumMm = dPrecipSum,
                    windSpeedMaxKmh = dWindMax,
                    uvIndexMax = dUvMax,
                    sunrise = dSunrise,
                    sunset = dSunset
                )
            )
        }

        // Meteorological advisories / alerts based on real physics
        val alerts = mutableListOf<WeatherAlertItem>()
        val maxWind = dailyList.maxOfOrNull { it.windSpeedMaxKmh } ?: 0.0
        val maxPrecip = dailyList.maxOfOrNull { it.precipitationSumMm } ?: 0.0
        val maxUv = dailyList.maxOfOrNull { it.uvIndexMax } ?: 0.0
        val maxTemp = dailyList.maxOfOrNull { it.maxTempC } ?: 0.0

        if (weatherCode in listOf(95, 96, 99)) {
            alerts.add(
                WeatherAlertItem(
                    titleEn = "Severe Thunderstorm Warning",
                    titleBn = "তীব্র বজ্রঝড়ের সতর্কতা",
                    descriptionEn = "Lightning and thunderstorms detected in the area. Stay indoors and seek shelter.",
                    descriptionBn = "এলাকায় বজ্রপাত ও ঝড়ের সম্ভাবনা রয়েছে। নিরাপদ আশ্রয়ে থাকুন।",
                    severity = AlertSeverity.DANGER,
                    issuedTime = "Active"
                )
            )
        } else if (maxPrecip >= 15.0) {
            alerts.add(
                WeatherAlertItem(
                    titleEn = "Heavy Rainfall Advisory",
                    titleBn = "ভারী বৃষ্টিপাতের পূর্বাভাস",
                    descriptionEn = "Significant precipitation expected. Potential waterlogging in low-lying areas.",
                    descriptionBn = "উল্লেখযোগ্য বৃষ্টিপাতের সম্ভাবনা রয়েছে। নিচু এলাকায় জলাবদ্ধতা হতে পারে।",
                    severity = AlertSeverity.WARNING,
                    issuedTime = "Active"
                )
            )
        }

        if (maxWind >= 45.0) {
            alerts.add(
                WeatherAlertItem(
                    titleEn = "Strong Wind Advisory",
                    titleBn = "দমকা বাতাসের সতর্কতা",
                    descriptionEn = "Gusty winds reaching over 45 km/h. Exercise caution during travel.",
                    descriptionBn = "দমকা বাতাস ৪৫ কিমি/ঘণ্টার বেশি হতে পারে। চলাচলে সতর্কতা অবলম্বন করুন।",
                    severity = AlertSeverity.WARNING,
                    issuedTime = "Active"
                )
            )
        }

        if (maxUv >= 8.0) {
            alerts.add(
                WeatherAlertItem(
                    titleEn = "Very High UV Radiation Alert",
                    titleBn = "অতিরিক্ত ক্ষতিকর ইউভি (UV) রশ্মি সতর্কতা",
                    descriptionEn = "UV Index is very high. Avoid direct midday sun and wear sun protection.",
                    descriptionBn = "অতিবেগুনি রশ্মির মাত্রা খুব বেশি। দুপুরে রোদে না বের হওয়ার পরামর্শ দেওয়া হচ্ছে।",
                    severity = AlertSeverity.INFO,
                    issuedTime = "Active"
                )
            )
        } else if (maxTemp >= 38.0) {
            alerts.add(
                WeatherAlertItem(
                    titleEn = "Extreme Heat Advisory",
                    titleBn = "তীব্র তাপপ্রবাহের সতর্কতা",
                    descriptionEn = "Temperatures exceeding 38°C. Stay hydrated and avoid strenuous outdoor activity.",
                    descriptionBn = "তাপমাত্রা ৩৮ ডিগ্রি সেলসিয়াসের বেশি। প্রচুর পানি পান করুন ও বিশ্রাম নিন।",
                    severity = AlertSeverity.WARNING,
                    issuedTime = "Active"
                )
            )
        }

        return FullWeatherData(
            locationName = locationName,
            country = country,
            latitude = latitude,
            longitude = longitude,
            timezone = dto.timezone.orEmpty(),
            currentWeather = current,
            hourlyForecast = hourlyList,
            dailyForecast = dailyList,
            alerts = alerts,
            lastUpdatedMillis = cacheTimestamp,
            isFromCache = isFromCache
        )
    }
}
