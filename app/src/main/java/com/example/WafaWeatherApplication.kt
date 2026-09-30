package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.local.PreferencesManager
import com.example.data.repository.WeatherRepository
import com.example.domain.util.LocationHelper

class WafaWeatherApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var repository: WeatherRepository
        private set

    lateinit var locationHelper: LocationHelper
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)
        repository = WeatherRepository(
            weatherCacheDao = database.weatherCacheDao(),
            favoriteLocationDao = database.favoriteLocationDao()
        )
        locationHelper = LocationHelper(this)
    }

    companion object {
        lateinit var instance: WafaWeatherApplication
            private set
    }
}
