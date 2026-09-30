# Wafa Weather (ওয়াফা ওয়েদার) ☀️🌧️

A production-ready Android weather application delivering real-time forecasts, hourly projections, 7-day outlooks, and localized weather advisories.

**Developed by Mehedi364 / Md. Mehedi Hasan**

---

## 🌟 Key Features

- **Real-Time Live Weather**: Accurate temperature, feels-like temperature, humidity, wind velocity and direction, air pressure, UV radiation index, cloud cover, and precipitation data.
- **Dual Language Support**:
  - 🇧🇩 **বাংলা (Bangla)** as default language with authentic meteorological terminology and Bangla numeral conversions (e.g. ৩৩°C, ১২ কিমি/ঘণ্টা, ৫ মিনিট আগে).
  - 🇬🇧 **English** as secondary language with seamless dynamic runtime switching.
- **Hourly Forecast**: 24-hour horizontal forecast with condition icons, temperature transitions, and precipitation probabilities.
- **7-Day Detailed Forecast**: Comprehensive day-by-day forecast cards featuring high/low temperatures, sunrise & sunset times, UV index, and expandable meteorological metrics.
- **Dynamic Weather Atmosphere**: Fluid UI that visually adapts its atmospheric gradients to the current weather condition (Clear sunny, partly cloudy, overcast, rainy, heavy precipitation, thunderstorm, fog, and night sky).
- **Location & Search**:
  - Automatic GPS detection with reverse geocoding to identify city and district names.
  - Manual search powered by Open-Meteo Geocoding API supporting Bangladesh divisions (Dhaka, Chittagong, Rajshahi, Sylhet, Rangpur, Khulna, Barisal, Mymensingh, Gaibandha) and worldwide cities.
- **Favorite Locations**: Save, view, and switch between multiple saved locations locally using Room database.
- **Meteorological Advisories & Alerts**: Real-time warnings for extreme temperatures, high winds, heavy precipitation, extreme UV radiation, and thunderstorms.
- **Offline Caching & Resilience**: Local Room database caching of the latest weather responses with clear offline timestamps ("অফলাইন ডেটা - ৫ মিনিট আগে").
- **Dark & Light Mode**: Full support for Dark Theme, Light Theme, and System Default.
- **Unit Customization**: Switch between Celsius (°C) and Fahrenheit (°F), and wind speed units (km/h, m/s, mph).

---

## 🛠️ Technology Stack

- **Platform**: Android (Min SDK 26+, Compile SDK 36, Target SDK 36)
- **Language**: Kotlin 2.2+ (JVM Target 17)
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM + Clean Repository Pattern
- **Asynchronous**: Kotlin Coroutines & StateFlow
- **Networking**: Retrofit 2 + OkHttp 4 with Moshi JSON serialization
- **Persistence**:
  - Room Database (Local weather cache & favorite locations)
  - Jetpack DataStore Preferences (Language, Theme, and Units)
- **Location**: Google Play Services Location (`FusedLocationProviderClient`) & Android Geocoder
- **Zero-Key API**: Open-Meteo Weather & Geocoding API (No proprietary API keys required)

---

## 📡 Weather API Integration

This application interacts with the Open-Meteo API:
- **Weather Forecast**: `https://api.open-meteo.com/v1/forecast`
- **Geocoding**: `https://geocoding-api.open-meteo.com/v1/search`

Parameters fetched:
`temperature_2m`, `relative_humidity_2m`, `apparent_temperature`, `is_day`, `precipitation`, `rain`, `weather_code`, `cloud_cover`, `pressure_msl`, `wind_speed_10m`, `wind_direction_10m`, `uv_index`, `hourly`, `daily`, `sunrise`, `sunset`.

---

## 📱 Permissions

- `android.permission.INTERNET`: For live weather & geocoding API requests.
- `android.permission.ACCESS_NETWORK_STATE`: For checking network connectivity.
- `android.permission.ACCESS_FINE_LOCATION`: For GPS-based local weather coordinates.
- `android.permission.ACCESS_COARSE_LOCATION`: For cell/Wi-Fi based coordinates.

---

## 🔒 Privacy & Data Transparency

Wafa Weather respects user privacy:
- Location coordinates are used solely on-device to retrieve weather forecasts.
- Location data is never stored on external private servers or shared with third parties.

---

## 📦 Download Real Debug APK

The compiled and verified debug APK is directly accessible in the project file tree:
- **Primary Download Path**: `APK_DOWNLOAD/app-debug.apk`
- **Build Output Mirror**: `.build-outputs/app-debug.apk`
- **Compressed Project Archive**: `project.zip`

---

## 🏗️ Building in Android Studio

1. Clone or extract this repository into your workspace.
2. Open **Android Studio** (Ladybug / Hedgehog or newer) and select **Open**.
3. Allow Gradle to synchronize dependencies.
4. Select `app` and choose your connected Android device or emulator.
5. Click **Run** (`Shift + F10`) or build the APK via **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

To build from command line:
```bash
gradle assembleDebug
```

---

## 👨‍💻 Developer Information

- **Project**: Wafa Weather
- **Developer**: Md. Mehedi Hasan (Mehedi364)
- **Version**: 1.0.0
