package com.example.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.domain.model.AppLanguage

sealed class NavigationItem(
    val route: String,
    val titleEn: String,
    val titleBn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavigationItem(
        route = "home",
        titleEn = "Home",
        titleBn = "হোম",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Forecast : NavigationItem(
        route = "forecast",
        titleEn = "Forecast",
        titleBn = "পূর্বাভাস",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    object Locations : NavigationItem(
        route = "locations",
        titleEn = "Locations",
        titleBn = "স্থানসমূহ",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    )

    object Settings : NavigationItem(
        route = "settings",
        titleEn = "Settings",
        titleBn = "সেটিংস",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    fun getTitle(language: AppLanguage): String {
        return if (language == AppLanguage.BANGLA) titleBn else titleEn
    }

    companion object {
        val items = listOf(Home, Forecast, Locations, Settings)
    }
}
