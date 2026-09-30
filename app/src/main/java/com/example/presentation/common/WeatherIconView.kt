package com.example.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.WeatherCondition
import com.example.domain.model.WeatherConditionType

@Composable
fun WeatherIconView(
    condition: WeatherCondition,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val sunColor = Color(0xFFFBBF24)
        val moonColor = Color(0xFFE2E8F0)
        val cloudColor = Color(0xFFCBD5E1)
        val rainColor = Color(0xFF60A5FA)
        val stormColor = Color(0xFFFBBF24)
        val snowColor = Color(0xFF93C5FD)

        when (condition.type) {
            WeatherConditionType.CLEAR_SUNNY -> {
                if (condition.isDay) {
                    Icon(
                        imageVector = Icons.Filled.WbSunny,
                        contentDescription = condition.titleEn,
                        tint = sunColor,
                        modifier = Modifier.size(size)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.NightsStay,
                        contentDescription = condition.titleEn,
                        tint = moonColor,
                        modifier = Modifier.size(size)
                    )
                }
            }
            WeatherConditionType.PARTLY_CLOUDY -> {
                Box(contentAlignment = Alignment.Center) {
                    if (condition.isDay) {
                        Icon(
                            imageVector = Icons.Filled.WbSunny,
                            contentDescription = null,
                            tint = sunColor,
                            modifier = Modifier
                                .size(size * 0.7f)
                                .offset(x = (-size * 0.2f), y = (-size * 0.15f))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.NightsStay,
                            contentDescription = null,
                            tint = moonColor,
                            modifier = Modifier
                                .size(size * 0.65f)
                                .offset(x = (-size * 0.2f), y = (-size * 0.15f))
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Cloud,
                        contentDescription = condition.titleEn,
                        tint = cloudColor,
                        modifier = Modifier
                            .size(size * 0.75f)
                            .offset(x = (size * 0.15f), y = (size * 0.15f))
                    )
                }
            }
            WeatherConditionType.CLOUDY -> {
                Icon(
                    imageVector = Icons.Filled.WbCloudy,
                    contentDescription = condition.titleEn,
                    tint = cloudColor,
                    modifier = Modifier.size(size)
                )
            }
            WeatherConditionType.FOG -> {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Cloud,
                        contentDescription = condition.titleEn,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(size * 0.8f)
                    )
                    Icon(
                        imageVector = Icons.Filled.Grain,
                        contentDescription = null,
                        tint = Color(0xFFE2E8F0),
                        modifier = Modifier
                            .size(size * 0.5f)
                            .offset(y = size * 0.2f)
                    )
                }
            }
            WeatherConditionType.DRIZZLE -> {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Cloud,
                        contentDescription = condition.titleEn,
                        tint = cloudColor,
                        modifier = Modifier.size(size * 0.8f)
                    )
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = rainColor,
                        modifier = Modifier
                            .size(size * 0.35f)
                            .offset(y = size * 0.28f)
                    )
                }
            }
            WeatherConditionType.RAIN -> {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Cloud,
                        contentDescription = condition.titleEn,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(size * 0.8f)
                    )
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = rainColor,
                        modifier = Modifier
                            .size(size * 0.32f)
                            .offset(x = (-size * 0.15f), y = size * 0.28f)
                    )
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = rainColor,
                        modifier = Modifier
                            .size(size * 0.32f)
                            .offset(x = (size * 0.15f), y = size * 0.28f)
                    )
                }
            }
            WeatherConditionType.HEAVY_RAIN -> {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Cloud,
                        contentDescription = condition.titleEn,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(size * 0.85f)
                    )
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier
                            .size(size * 0.32f)
                            .offset(x = (-size * 0.2f), y = size * 0.28f)
                    )
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier
                            .size(size * 0.32f)
                            .offset(x = 0.dp, y = size * 0.32f)
                    )
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier
                            .size(size * 0.32f)
                            .offset(x = (size * 0.2f), y = size * 0.28f)
                    )
                }
            }
            WeatherConditionType.THUNDERSTORM -> {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Cloud,
                        contentDescription = condition.titleEn,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(size * 0.85f)
                    )
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = stormColor,
                        modifier = Modifier
                            .size(size * 0.5f)
                            .offset(y = size * 0.25f)
                    )
                }
            }
            WeatherConditionType.SNOW -> {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Cloud,
                        contentDescription = condition.titleEn,
                        tint = cloudColor,
                        modifier = Modifier.size(size * 0.8f)
                    )
                    Icon(
                        imageVector = Icons.Filled.AcUnit,
                        contentDescription = null,
                        tint = snowColor,
                        modifier = Modifier
                            .size(size * 0.45f)
                            .offset(y = size * 0.25f)
                    )
                }
            }
        }
    }
}
