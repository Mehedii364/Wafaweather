package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.LastLocationData
import com.example.data.local.PreferencesManager
import com.example.data.repository.WeatherRepository
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.AppThemeMode
import com.example.domain.model.FullWeatherData
import com.example.domain.model.LocationItem
import com.example.domain.model.TemperatureUnit
import com.example.domain.model.WindSpeedUnit
import com.example.domain.util.LocationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val data: FullWeatherData) : WeatherUiState
    data class Error(val messageEn: String, val messageBn: String, val cachedData: FullWeatherData? = null) : WeatherUiState
}

class MainViewModel(
    private val repository: WeatherRepository,
    private val preferencesManager: PreferencesManager,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val settings: StateFlow<AppSettings> = combine(
        preferencesManager.languageFlow,
        preferencesManager.themeModeFlow,
        preferencesManager.tempUnitFlow,
        preferencesManager.windUnitFlow
    ) { language, themeMode, tempUnit, windUnit ->
        AppSettings(language, themeMode, tempUnit, windUnit)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val lastLocation: StateFlow<LastLocationData> = preferencesManager.lastLocationFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LastLocationData(23.8103, 90.4125, "Dhaka", "Bangladesh")
        )

    val favorites: StateFlow<List<LocationItem>> = repository.getFavoriteLocations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchResults = MutableStateFlow<List<LocationItem>>(emptyList())
    val searchResults: StateFlow<List<LocationItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null
    private var activeLat: Double = 23.8103
    private var activeLon: Double = 90.4125
    private var activeName: String = "Dhaka"
    private var activeCountry: String = "Bangladesh"

    init {
        // Initial load
        viewModelScope.launch {
            preferencesManager.lastLocationFlow.collect { loc ->
                activeLat = loc.latitude
                activeLon = loc.longitude
                activeName = loc.name
                activeCountry = loc.country
                loadWeather(activeLat, activeLon, activeName, activeCountry, forceRefresh = false)
            }
        }
    }

    fun loadWeather(
        lat: Double,
        lon: Double,
        name: String,
        country: String,
        forceRefresh: Boolean = false
    ) {
        activeLat = lat
        activeLon = lon
        activeName = name
        activeCountry = country

        viewModelScope.launch {
            if (_uiState.value !is WeatherUiState.Success) {
                _uiState.value = WeatherUiState.Loading
            }
            if (forceRefresh) {
                _isRefreshing.value = true
            }

            val result = repository.getWeatherData(
                latitude = lat,
                longitude = lon,
                locationName = name,
                country = country,
                forceRefresh = forceRefresh
            )

            _isRefreshing.value = false

            result.fold(
                onSuccess = { fullData ->
                    _uiState.value = WeatherUiState.Success(fullData)
                    preferencesManager.saveLastLocation(lat, lon, name, country)
                },
                onFailure = { error ->
                    val currentData = (_uiState.value as? WeatherUiState.Success)?.data
                    _uiState.value = WeatherUiState.Error(
                        messageEn = "Unable to fetch weather data. Please check connection and try again.",
                        messageBn = "আবহাওয়ার তথ্য এখন পাওয়া যাচ্ছে না। ইন্টারনেট সংযোগ পরীক্ষা করে আবার চেষ্টা করুন।",
                        cachedData = currentData
                    )
                }
            )
        }
    }

    fun refresh() {
        loadWeather(activeLat, activeLon, activeName, activeCountry, forceRefresh = true)
    }

    fun detectGpsLocation(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isRefreshing.value = true
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                val (city, country) = locationHelper.reverseGeocode(location.latitude, location.longitude)
                loadWeather(location.latitude, location.longitude, city, country, forceRefresh = true)
                onResult(true)
            } else {
                _isRefreshing.value = false
                onResult(false)
            }
        }
    }

    fun search(query: String) {
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            _isSearching.value = true
            val result = repository.searchLocations(query)
            _isSearching.value = false
            result.onSuccess { list ->
                _searchResults.value = list
            }.onFailure {
                _searchResults.value = emptyList()
            }
        }
    }

    fun selectLocation(item: LocationItem) {
        loadWeather(item.latitude, item.longitude, item.name, item.country, forceRefresh = true)
    }

    fun toggleFavorite(item: LocationItem) {
        viewModelScope.launch {
            if (item.isFavorite) {
                repository.removeFavorite(item.id)
            } else {
                val currentData = (_uiState.value as? WeatherUiState.Success)?.data
                val updatedItem = if (currentData != null && currentData.latitude == item.latitude) {
                    item.copy(
                        cachedTempC = currentData.currentWeather.temperatureC,
                        cachedCondition = currentData.currentWeather.condition.titleEn,
                        cachedWeatherCode = currentData.currentWeather.condition.code
                    )
                } else {
                    item
                }
                repository.addFavorite(updatedItem)
            }
        }
    }

    fun removeFavorite(locationId: Long) {
        viewModelScope.launch {
            repository.removeFavorite(locationId)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun setTempUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            preferencesManager.setTempUnit(unit)
        }
    }

    fun setWindUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            preferencesManager.setWindUnit(unit)
        }
    }
}

class MainViewModelFactory(
    private val repository: WeatherRepository,
    private val preferencesManager: PreferencesManager,
    private val locationHelper: LocationHelper
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, preferencesManager, locationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
