package com.example.presentation.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.AppThemeMode
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.WindSpeedUnit

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSetLanguage: (AppLanguage) -> Unit,
    onSetThemeMode: (AppThemeMode) -> Unit,
    onSetTempUnit: (TemperatureUnit) -> Unit,
    onSetWindUnit: (WindSpeedUnit) -> Unit,
    onDetectGps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = settings.language

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (language == AppLanguage.BANGLA) "সেটিংস ও পছন্দসমূহ" else "Settings & Preferences",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == AppLanguage.BANGLA) "ভাষা, থিম এবং পরিমাপের একক কাস্টমাইজ করুন" else "Customize language, theme, and measurement units",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Language Setting
        item {
            SettingsSectionCard(
                icon = Icons.Filled.Language,
                title = if (language == AppLanguage.BANGLA) "ভাষা নির্বাচন (Language)" else "Language Selection"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        FilterChip(
                            selected = settings.language == lang,
                            onClick = { onSetLanguage(lang) },
                            label = {
                                Text("${lang.flag} ${lang.displayName}")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. Theme Setting
        item {
            SettingsSectionCard(
                icon = Icons.Filled.Brightness4,
                title = if (language == AppLanguage.BANGLA) "থিম ও ডিসপ্লে" else "Theme & Appearance"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.values().forEach { mode ->
                        FilterChip(
                            selected = settings.themeMode == mode,
                            onClick = { onSetThemeMode(mode) },
                            label = {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) mode.titleBn else mode.titleEn,
                                    fontSize = 12.sp
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Units: Temperature & Wind
        item {
            SettingsSectionCard(
                icon = Icons.Filled.Thermostat,
                title = if (language == AppLanguage.BANGLA) "তাপমাত্রার একক" else "Temperature Unit"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TemperatureUnit.values().forEach { unit ->
                        FilterChip(
                            selected = settings.tempUnit == unit,
                            onClick = { onSetTempUnit(unit) },
                            label = {
                                Text(if (language == AppLanguage.BANGLA) unit.labelBn else unit.labelEn)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            SettingsSectionCard(
                icon = Icons.Filled.Navigation,
                title = if (language == AppLanguage.BANGLA) "বাতাসের গতির একক" else "Wind Speed Unit"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WindSpeedUnit.values().forEach { unit ->
                        FilterChip(
                            selected = settings.windUnit == unit,
                            onClick = { onSetWindUnit(unit) },
                            label = {
                                Text(if (language == AppLanguage.BANGLA) unit.symbolBn else unit.symbolEn)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. GPS Location settings
        item {
            SettingsSectionCard(
                icon = Icons.Filled.MyLocation,
                title = if (language == AppLanguage.BANGLA) "অবস্থান ও জিপিএস অনুমতি" else "Location & GPS Detection"
            ) {
                Text(
                    text = if (language == AppLanguage.BANGLA) {
                        "আপনার স্থানীয় আবহাওয়ার নিখুঁত পূর্বাভাসের জন্য জিপিএস অবস্থান ব্যবহৃত হয়। কোনো ব্যাকগ্রাউন্ড ট্র্যাকিং করা হয় না।"
                    } else {
                        "GPS location is used solely to fetch accurate weather forecasts for your coordinates. No background tracking is performed."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDetectGps,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Filled.MyLocation, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "বর্তমান জিপিএস অবস্থান শনাক্ত করুন" else "Detect Current GPS Location"
                    )
                }
            }
        }

        // 5. About App & Developer
        item {
            SettingsSectionCard(
                icon = Icons.Filled.Info,
                title = if (language == AppLanguage.BANGLA) "অ্যাপ সম্পর্কিত তথ্য" else "About Wafa Weather"
            ) {
                Text(
                    text = "Wafa Weather",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (language == AppLanguage.BANGLA) "রিয়েল-টাইম আধুনিক আবহাওয়া অ্যাপ্লিকেশন" else "Real-time weather application.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ডেভেলপার" else "Developer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Md. Mehedi Hasan (Mehedi364)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ভার্সন" else "App Version",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "1.0.0 (Production)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "আবহাওয়া প্রদানকারী" else "Weather Data Provider",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Open-Meteo API",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 6. Privacy Information Card
        item {
            SettingsSectionCard(
                icon = Icons.Filled.PrivacyTip,
                title = if (language == AppLanguage.BANGLA) "গোপনীয়তা ও নিরাপত্তা" else "Privacy & Transparency"
            ) {
                Text(
                    text = if (language == AppLanguage.BANGLA) {
                        "Wafa Weather আপনার গোপনীয়তাকে সর্বোচ্চ অগ্রাধিকার দেয়। অবস্থান সংক্রান্ত তথ্য শুধুমাত্র ডিভাইস থেকে আবহাওয়ার তথ্য সংগ্রহের জন্য সরাসরি Open-Meteo API-তে পাঠানো হয়। কোনো ব্যক্তিগত সার্ভারে ডেটা সংরক্ষণ বা কোনো থার্ড-পার্টির সাথে শেয়ার করা হয় না।"
                    } else {
                        "Wafa Weather prioritizes your privacy. Coordinates are sent directly to Open-Meteo to fetch weather data for your area. Your location is never logged, stored on external private servers, or shared with third parties."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}
