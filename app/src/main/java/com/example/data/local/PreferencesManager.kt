package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppThemeMode
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wafa_weather_prefs")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("app_language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val LAST_LAT = doublePreferencesKey("last_lat")
        val LAST_LON = doublePreferencesKey("last_lon")
        val LAST_NAME = stringPreferencesKey("last_name")
        val LAST_COUNTRY = stringPreferencesKey("last_country")
    }

    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        when (prefs[PreferencesKeys.LANGUAGE]) {
            "EN" -> AppLanguage.ENGLISH
            else -> AppLanguage.BANGLA // Default is Bangla
        }
    }

    val themeModeFlow: Flow<AppThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[PreferencesKeys.THEME_MODE]) {
            "LIGHT" -> AppThemeMode.LIGHT
            "DARK" -> AppThemeMode.DARK
            else -> AppThemeMode.SYSTEM
        }
    }

    val tempUnitFlow: Flow<TemperatureUnit> = context.dataStore.data.map { prefs ->
        when (prefs[PreferencesKeys.TEMP_UNIT]) {
            "FAHRENHEIT" -> TemperatureUnit.FAHRENHEIT
            else -> TemperatureUnit.CELSIUS
        }
    }

    val windUnitFlow: Flow<WindSpeedUnit> = context.dataStore.data.map { prefs ->
        when (prefs[PreferencesKeys.WIND_UNIT]) {
            "MS" -> WindSpeedUnit.MS
            "MPH" -> WindSpeedUnit.MPH
            else -> WindSpeedUnit.KMH
        }
    }

    val lastLocationFlow: Flow<LastLocationData> = context.dataStore.data.map { prefs ->
        LastLocationData(
            latitude = prefs[PreferencesKeys.LAST_LAT] ?: 23.8103,
            longitude = prefs[PreferencesKeys.LAST_LON] ?: 90.4125,
            name = prefs[PreferencesKeys.LAST_NAME] ?: "Dhaka",
            country = prefs[PreferencesKeys.LAST_COUNTRY] ?: "Bangladesh"
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LANGUAGE] = when (language) {
                AppLanguage.ENGLISH -> "EN"
                AppLanguage.BANGLA -> "BN"
            }
        }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setTempUnit(unit: TemperatureUnit) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.TEMP_UNIT] = unit.name
        }
    }

    suspend fun setWindUnit(unit: WindSpeedUnit) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.WIND_UNIT] = unit.name
        }
    }

    suspend fun saveLastLocation(lat: Double, lon: Double, name: String, country: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LAST_LAT] = lat
            prefs[PreferencesKeys.LAST_LON] = lon
            prefs[PreferencesKeys.LAST_NAME] = name
            prefs[PreferencesKeys.LAST_COUNTRY] = country
        }
    }
}

data class LastLocationData(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val country: String
)
