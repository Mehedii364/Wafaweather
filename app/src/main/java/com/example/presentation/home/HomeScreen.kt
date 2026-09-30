package com.example.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AlertSeverity
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.CurrentWeather
import com.example.domain.model.FullWeatherData
import com.example.domain.model.HourlyWeather
import com.example.domain.model.WeatherAlertItem
import com.example.domain.util.BanglaHelper
import com.example.presentation.WeatherUiState
import com.example.presentation.common.WeatherIconView
import com.example.ui.theme.WeatherAtmosphere

@Composable
fun HomeScreen(
    uiState: WeatherUiState,
    isRefreshing: Boolean,
    settings: AppSettings,
    onRefresh: () -> Unit,
    onDetectGps: () -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = settings.language

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is WeatherUiState.Loading -> {
                LoadingShimmerView(language = language)
            }
            is WeatherUiState.Error -> {
                if (uiState.cachedData != null) {
                    WeatherDashboardContent(
                        weatherData = uiState.cachedData,
                        isRefreshing = isRefreshing,
                        settings = settings,
                        onRefresh = onRefresh,
                        onDetectGps = onDetectGps,
                        onNavigateToSearch = onNavigateToSearch,
                        errorMessage = if (language == AppLanguage.BANGLA) uiState.messageBn else uiState.messageEn
                    )
                } else {
                    ErrorStateView(
                        message = if (language == AppLanguage.BANGLA) uiState.messageBn else uiState.messageEn,
                        language = language,
                        onRetry = onRefresh
                    )
                }
            }
            is WeatherUiState.Success -> {
                WeatherDashboardContent(
                    weatherData = uiState.data,
                    isRefreshing = isRefreshing,
                    settings = settings,
                    onRefresh = onRefresh,
                    onDetectGps = onDetectGps,
                    onNavigateToSearch = onNavigateToSearch,
                    errorMessage = null
                )
            }
        }
    }
}

@Composable
private fun WeatherDashboardContent(
    weatherData: FullWeatherData,
    isRefreshing: Boolean,
    settings: AppSettings,
    onRefresh: () -> Unit,
    onDetectGps: () -> Unit,
    onNavigateToSearch: () -> Unit,
    errorMessage: String?
) {
    val language = settings.language
    val current = weatherData.currentWeather

    val infiniteTransition = rememberInfiniteTransition(label = "refresh_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_weather_list"),
        contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Top Section (Location, GPS icon, Refresh, Search)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToSearch() }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = weatherData.locationName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (weatherData.country.isNotBlank()) {
                            Text(
                                text = weatherData.country,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDetectGps,
                        modifier = Modifier.testTag("gps_detect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationSearching,
                            contentDescription = "Detect GPS Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.testTag("refresh_weather_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh Weather",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = if (isRefreshing) Modifier.rotate(rotation) else Modifier
                        )
                    }
                }
            }
        }

        // Cache / Network status alert if offline or error
        if (weatherData.isFromCache || errorMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WifiOff,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) {
                                "অফলাইন ডেটা প্রদর্শিত হচ্ছে (${BanglaHelper.formatRelativeTime(weatherData.lastUpdatedMillis, language)})"
                            } else {
                                "Showing cached data (${BanglaHelper.formatRelativeTime(weatherData.lastUpdatedMillis, language)})"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Main Weather Card (Atmospheric Gradient, Large Temp, Condition, High/Low)
        item {
            MainWeatherCard(
                current = current,
                settings = settings
            )
        }

        // 3. Weather Alerts Section
        item {
            WeatherAlertsSection(
                alerts = weatherData.alerts,
                language = language
            )
        }

        // 4. Hourly Forecast Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "আজকের ঘণ্টাভিত্তিক পূর্বাভাস" else "Hourly Forecast",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (language == AppLanguage.BANGLA) "২৪ ঘণ্টা" else "24 Hours",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(weatherData.hourlyForecast) { hourly ->
                        HourlyForecastCard(
                            hourly = hourly,
                            settings = settings
                        )
                    }
                }
            }
        }

        // 5. Weather Details Grid
        item {
            Column {
                Text(
                    text = if (language == AppLanguage.BANGLA) "আবহাওয়ার বিস্তারিত তথ্য" else "Weather Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                WeatherMetricsGrid(
                    current = current,
                    settings = settings
                )
            }
        }

        // 6. Last updated time footer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.BANGLA) {
                        "শেষ আপডেট: ${BanglaHelper.formatRelativeTime(weatherData.lastUpdatedMillis, language)}"
                    } else {
                        "Last updated: ${BanglaHelper.formatRelativeTime(weatherData.lastUpdatedMillis, language)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun MainWeatherCard(
    current: CurrentWeather,
    settings: AppSettings
) {
    val language = settings.language
    val condition = current.condition
    val backgroundBrush = WeatherAtmosphere.getAtmosphericBrush(
        conditionType = condition.type,
        isDay = current.isDay,
        isDarkTheme = true
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("main_weather_hero_card"),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundBrush)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Weather icon with glowing elevation
                WeatherIconView(
                    condition = condition,
                    size = 88.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Temperature
                Text(
                    text = BanglaHelper.formatTemp(current.temperatureC, settings.tempUnit, language),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 58.sp
                    ),
                    color = Color.White
                )

                // Condition Title
                Text(
                    text = if (language == AppLanguage.BANGLA) condition.titleBn else condition.titleEn,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White.copy(alpha = 0.95f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Feels like
                Text(
                    text = if (language == AppLanguage.BANGLA) {
                        "অনুভূত তাপমাত্রা: ${BanglaHelper.formatTemp(current.apparentTemperatureC, settings.tempUnit, language)}"
                    } else {
                        "Feels like ${BanglaHelper.formatTemp(current.apparentTemperatureC, settings.tempUnit, language)}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // High / Low Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.22f),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val highLabel = if (language == AppLanguage.BANGLA) "সর্বোচ্চ" else "H"
                        val lowLabel = if (language == AppLanguage.BANGLA) "সর্বনিম্ন" else "L"
                        val highVal = BanglaHelper.formatTemp(current.todayMaxTempC, settings.tempUnit, language)
                        val lowVal = BanglaHelper.formatTemp(current.todayMinTempC, settings.tempUnit, language)

                        Text(
                            text = "$highLabel $highVal  •  $lowLabel $lowVal",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyForecastCard(
    hourly: HourlyWeather,
    settings: AppSettings
) {
    val language = settings.language

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(82.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (language == AppLanguage.BANGLA) hourly.displayHourBn else hourly.displayHourEn,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            WeatherIconView(
                condition = hourly.condition,
                size = 36.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = BanglaHelper.formatTemp(hourly.temperatureC, settings.tempUnit, language),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (hourly.precipitationProbability > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${BanglaHelper.formatNumber(hourly.precipitationProbability, language)}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFF38BDF8)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherAlertsSection(
    alerts: List<WeatherAlertItem>,
    language: AppLanguage
) {
    if (alerts.isEmpty()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (language == AppLanguage.BANGLA) {
                        "এই অবস্থানের জন্য কোনো দুর্যোগ সতর্কতা নেই।"
                    } else {
                        "No weather alerts available for this location."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            alerts.forEach { alert ->
                val (bgColor, tintColor) = when (alert.severity) {
                    AlertSeverity.DANGER -> Pair(Color(0xFF7F1D1D), Color(0xFFF87171))
                    AlertSeverity.WARNING -> Pair(Color(0xFF78350F), Color(0xFFFBBF24))
                    AlertSeverity.INFO -> Pair(Color(0xFF1E3A8A), Color(0xFF60A5FA))
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = tintColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.BANGLA) alert.titleBn else alert.titleEn,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) alert.descriptionBn else alert.descriptionEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricsGrid(
    current: CurrentWeather,
    settings: AppSettings
) {
    val language = settings.language

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                icon = Icons.Filled.WaterDrop,
                iconTint = Color(0xFF38BDF8),
                title = if (language == AppLanguage.BANGLA) "আর্দ্রতা" else "Humidity",
                value = "${BanglaHelper.formatNumber(current.humidityPercent, language)}%",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = Icons.Filled.Air,
                iconTint = Color(0xFF60A5FA),
                title = if (language == AppLanguage.BANGLA) "বাতাস" else "Wind",
                value = BanglaHelper.formatWindSpeed(current.windSpeedKmh, settings.windUnit, language),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                icon = Icons.Filled.WbSunny,
                iconTint = Color(0xFFFBBF24),
                title = if (language == AppLanguage.BANGLA) "ইউভি সূচক" else "UV Index",
                value = BanglaHelper.formatNumber(String.format(java.util.Locale.US, "%.1f", current.uvIndex), language),
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = Icons.Filled.Umbrella,
                iconTint = Color(0xFF818CF8),
                title = if (language == AppLanguage.BANGLA) "বৃষ্টিপাত" else "Precipitation",
                value = "${BanglaHelper.formatNumber(current.precipitationMm, language)} mm",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                icon = Icons.Filled.Compress,
                iconTint = Color(0xFFA78BFA),
                title = if (language == AppLanguage.BANGLA) "বায়ুচাপ" else "Pressure",
                value = "${BanglaHelper.formatNumber(current.pressureHpa.toInt(), language)} hPa",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = Icons.Filled.Visibility,
                iconTint = Color(0xFF34D399),
                title = if (language == AppLanguage.BANGLA) "মেঘের পরিমাণ" else "Cloud Cover",
                value = "${BanglaHelper.formatNumber(current.cloudCoverPercent, language)}%",
                modifier = Modifier.weight(1f)
            )
        }

        if (current.todaySunrise.isNotBlank() && current.todaySunset.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    icon = Icons.Filled.WbTwilight,
                    iconTint = Color(0xFFF97316),
                    title = if (language == AppLanguage.BANGLA) "সূর্যোদয়" else "Sunrise",
                    value = BanglaHelper.formatIsoTimeToHour(current.todaySunrise, language),
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    icon = Icons.Filled.WbTwilight,
                    iconTint = Color(0xFFC084FC),
                    title = if (language == AppLanguage.BANGLA) "সূর্যাস্ত" else "Sunset",
                    value = BanglaHelper.formatIsoTimeToHour(current.todaySunset, language),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun LoadingShimmerView(language: AppLanguage) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (language == AppLanguage.BANGLA) {
                    "আবহাওয়ার লাইভ তথ্য সংগ্রহ করা হচ্ছে..."
                } else {
                    "Fetching live weather data..."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorStateView(
    message: String,
    language: AppLanguage,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (language == AppLanguage.BANGLA) "ইন্টারনেট সংযোগ নেই" else "No Connection",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("retry_button")
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "পুনরায় চেষ্টা করুন" else "Retry"
                    )
                }
            }
        }
    }
}
