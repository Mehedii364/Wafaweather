package com.example.presentation.navigation

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AppLanguage
import com.example.presentation.MainViewModel
import com.example.presentation.forecast.ForecastScreen
import com.example.presentation.home.HomeScreen
import com.example.presentation.locations.LocationsScreen
import com.example.presentation.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    var currentRoute by remember { mutableStateOf(NavigationItem.Home.route) }

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            viewModel.detectGpsLocation { success ->
                if (success) {
                    val msg = if (settings.language == AppLanguage.BANGLA) {
                        "জিপিএস অবস্থান শনাক্ত করা হয়েছে!"
                    } else {
                        "GPS location detected successfully!"
                    }
                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                }
            }
        } else {
            val msg = if (settings.language == AppLanguage.BANGLA) {
                "অবস্থানের অনুমতি প্রদান করা হয়নি। আপনি ম্যানুয়ালি শহর অনুসন্ধান করতে পারেন।"
            } else {
                "Location permission denied. You can search cities manually."
            }
            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    // On first launch, check permission and prompt user
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = MaterialTheme.colorScheme.surfaceTint.let { 4.dp }
            ) {
                NavigationItem.items.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentRoute = item.route },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.getTitle(settings.language)
                            )
                        },
                        label = {
                            Text(text = item.getTitle(settings.language))
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_${item.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                NavigationItem.Home.route -> {
                    HomeScreen(
                        uiState = uiState,
                        isRefreshing = isRefreshing,
                        settings = settings,
                        onRefresh = { viewModel.refresh() },
                        onDetectGps = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        onNavigateToSearch = {
                            currentRoute = NavigationItem.Locations.route
                        }
                    )
                }

                NavigationItem.Forecast.route -> {
                    ForecastScreen(
                        uiState = uiState,
                        settings = settings
                    )
                }

                NavigationItem.Locations.route -> {
                    LocationsScreen(
                        favorites = favorites,
                        searchResults = searchResults,
                        isSearching = isSearching,
                        settings = settings,
                        onSearch = { query -> viewModel.search(query) },
                        onSelectLocation = { location ->
                            viewModel.selectLocation(location)
                            currentRoute = NavigationItem.Home.route
                        },
                        onToggleFavorite = { location -> viewModel.toggleFavorite(location) },
                        onRemoveFavorite = { id -> viewModel.removeFavorite(id) }
                    )
                }

                NavigationItem.Settings.route -> {
                    SettingsScreen(
                        settings = settings,
                        onSetLanguage = { lang -> viewModel.setLanguage(lang) },
                        onSetThemeMode = { mode -> viewModel.setThemeMode(mode) },
                        onSetTempUnit = { unit -> viewModel.setTempUnit(unit) },
                        onSetWindUnit = { unit -> viewModel.setWindUnit(unit) },
                        onDetectGps = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
