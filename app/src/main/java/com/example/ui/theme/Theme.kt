package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.domain.model.WeatherConditionType

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueSecondary,
    onPrimary = DarkBg,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = SkyBlueTertiary,
    secondary = SunnyAmber,
    onSecondary = DarkBg,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = SkyBluePrimary,
    secondary = SunnyGold,
    onSecondary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary
)

@Composable
fun WafaWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted weather theme by default for unified atmosphere
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward-compatibility wrapper for any template references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    WafaWeatherTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

object WeatherAtmosphere {
    fun getAtmosphericBrush(conditionType: WeatherConditionType, isDay: Boolean, isDarkTheme: Boolean): Brush {
        return if (!isDay || isDarkTheme) {
            when (conditionType) {
                WeatherConditionType.THUNDERSTORM -> Brush.verticalGradient(
                    listOf(Color(0xFF0A0A16), Color(0xFF1E1B4B), Color(0xFF312E81))
                )
                WeatherConditionType.RAIN, WeatherConditionType.HEAVY_RAIN -> Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF1E3A8A))
                )
                WeatherConditionType.DRIZZLE, WeatherConditionType.FOG -> Brush.verticalGradient(
                    listOf(Color(0xFF111827), Color(0xFF1F2937), Color(0xFF374151))
                )
                WeatherConditionType.CLOUDY, WeatherConditionType.PARTLY_CLOUDY -> Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                )
                WeatherConditionType.CLEAR_SUNNY -> Brush.verticalGradient(
                    listOf(Color(0xFF0B1329), Color(0xFF111D3E), Color(0xFF1E293B))
                )
                else -> Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                )
            }
        } else {
            when (conditionType) {
                WeatherConditionType.CLEAR_SUNNY -> Brush.verticalGradient(
                    listOf(Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF7DD3FC))
                )
                WeatherConditionType.PARTLY_CLOUDY -> Brush.verticalGradient(
                    listOf(Color(0xFF0369A1), Color(0xFF38BDF8), Color(0xFFBAE6FD))
                )
                WeatherConditionType.CLOUDY -> Brush.verticalGradient(
                    listOf(Color(0xFF475569), Color(0xFF64748B), Color(0xFF94A3B8))
                )
                WeatherConditionType.FOG -> Brush.verticalGradient(
                    listOf(Color(0xFF64748B), Color(0xFF94A3B8), Color(0xFFCBD5E1))
                )
                WeatherConditionType.DRIZZLE, WeatherConditionType.RAIN -> Brush.verticalGradient(
                    listOf(Color(0xFF1E40AF), Color(0xFF3B82F6), Color(0xFF60A5FA))
                )
                WeatherConditionType.HEAVY_RAIN -> Brush.verticalGradient(
                    listOf(Color(0xFF1E3A8A), Color(0xFF2563EB), Color(0xFF3B82F6))
                )
                WeatherConditionType.THUNDERSTORM -> Brush.verticalGradient(
                    listOf(Color(0xFF1E1B4B), Color(0xFF3730A3), Color(0xFF4338CA))
                )
                WeatherConditionType.SNOW -> Brush.verticalGradient(
                    listOf(Color(0xFF0284C7), Color(0xFF93C5FD), Color(0xFFE2E8F0))
                )
            }
        }
    }
}
