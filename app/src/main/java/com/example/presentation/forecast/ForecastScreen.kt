package com.example.presentation.forecast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.DailyWeather
import com.example.domain.util.BanglaHelper
import com.example.presentation.WeatherUiState
import com.example.presentation.common.WeatherIconView

@Composable
fun ForecastScreen(
    uiState: WeatherUiState,
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    val language = settings.language

    val fullData = when (uiState) {
        is WeatherUiState.Success -> uiState.data
        is WeatherUiState.Error -> uiState.cachedData
        is WeatherUiState.Loading -> null
    }

    if (fullData == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (language == AppLanguage.BANGLA) "পূর্বাভাস লোড হচ্ছে..." else "Loading forecast...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("forecast_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 6.dp)) {
                Text(
                    text = if (language == AppLanguage.BANGLA) "৭ দিনের আবহাওয়ার পূর্বাভাস" else "7-Day Weather Forecast",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${fullData.locationName}, ${fullData.country}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        itemsIndexed(fullData.dailyForecast) { index, daily ->
            DailyForecastItemCard(
                daily = daily,
                isToday = index == 0,
                settings = settings
            )
        }
    }
}

@Composable
private fun DailyForecastItemCard(
    daily: DailyWeather,
    isToday: Boolean,
    settings: AppSettings
) {
    val language = settings.language
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 4.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Day & Date
                Column(modifier = Modifier.width(110.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) daily.dayNameBn else daily.dayNameEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) daily.formattedDateBn else daily.formattedDateEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Weather Icon & Condition
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    WeatherIconView(
                        condition = daily.condition,
                        size = 38.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) daily.condition.titleBn else daily.condition.titleEn,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (daily.precipitationProbabilityMax > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${BanglaHelper.formatNumber(daily.precipitationProbabilityMax, language)}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }

                // High / Low Temperatures
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = BanglaHelper.formatTemp(daily.maxTempC, settings.tempUnit, language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = BanglaHelper.formatTemp(daily.minTempC, settings.tempUnit, language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded details
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailMiniItem(
                            icon = Icons.Filled.Air,
                            label = if (language == AppLanguage.BANGLA) "সর্বোচ্চ বাতাস" else "Max Wind",
                            value = BanglaHelper.formatWindSpeed(daily.windSpeedMaxKmh, settings.windUnit, language)
                        )

                        DetailMiniItem(
                            icon = Icons.Filled.WbSunny,
                            label = if (language == AppLanguage.BANGLA) "সর্বোচ্চ ইউভি" else "Max UV",
                            value = BanglaHelper.formatNumber(String.format(java.util.Locale.US, "%.1f", daily.uvIndexMax), language)
                        )

                        if (daily.sunrise.isNotBlank()) {
                            DetailMiniItem(
                                icon = Icons.Filled.WbTwilight,
                                label = if (language == AppLanguage.BANGLA) "সূর্যোদয়" else "Sunrise",
                                value = BanglaHelper.formatIsoTimeToHour(daily.sunrise, language)
                            )
                        }

                        if (daily.sunset.isNotBlank()) {
                            DetailMiniItem(
                                icon = Icons.Filled.WbTwilight,
                                label = if (language == AppLanguage.BANGLA) "সূর্যাস্ত" else "Sunset",
                                value = BanglaHelper.formatIsoTimeToHour(daily.sunset, language)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailMiniItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
