package com.example.presentation.locations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.domain.model.LocationItem
import com.example.domain.util.BanglaHelper

data class QuickCity(
    val nameEn: String,
    val nameBn: String,
    val lat: Double,
    val lon: Double,
    val country: String = "Bangladesh"
)

val BANGLADESH_DIVISIONS = listOf(
    QuickCity("Dhaka", "ঢাকা", 23.8103, 90.4125),
    QuickCity("Chittagong", "চট্টগ্রাম", 22.3569, 91.7832),
    QuickCity("Rajshahi", "রাজশাহী", 24.3745, 88.6042),
    QuickCity("Sylhet", "সিলেট", 24.8949, 91.8687),
    QuickCity("Rangpur", "রংপুর", 25.7439, 89.2752),
    QuickCity("Khulna", "খুলনা", 22.8456, 89.5403),
    QuickCity("Barisal", "বরিশাল", 22.7010, 90.3535),
    QuickCity("Mymensingh", "ময়মনসিংহ", 24.7471, 90.4203),
    QuickCity("Gaibandha", "গাইবান্ধা", 25.3288, 89.5414)
)

@Composable
fun LocationsScreen(
    favorites: List<LocationItem>,
    searchResults: List<LocationItem>,
    isSearching: Boolean,
    settings: AppSettings,
    onSearch: (String) -> Unit,
    onSelectLocation: (LocationItem) -> Unit,
    onToggleFavorite: (LocationItem) -> Unit,
    onRemoveFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = settings.language
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("locations_screen_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Column {
                Text(
                    text = if (language == AppLanguage.BANGLA) "স্থান অনুসন্ধান ও প্রিয় স্থান" else "Locations & Favorites",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == AppLanguage.BANGLA) {
                        "যেকোনো শহর অনুসন্ধান করুন অথবা প্রিয়তে সংরক্ষণ করুন"
                    } else {
                        "Search any city worldwide or manage your favorites"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    onSearch(query)
                },
                placeholder = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) {
                            "শহরের নাম লিখুন (যেমন: Dhaka, London...)"
                        } else {
                            "Search city (e.g. Dhaka, London...)"
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (isSearching) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onSearch("")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_location_input")
            )
        }

        // 3. Bangladesh Quick Divisions Chips
        item {
            Column {
                Text(
                    text = if (language == AppLanguage.BANGLA) "বাংলাদেশ বিভাগ ও শহর" else "Popular Bangladesh Cities",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(BANGLADESH_DIVISIONS) { city ->
                        AssistChip(
                            onClick = {
                                onSelectLocation(
                                    LocationItem(
                                        name = if (language == AppLanguage.BANGLA) city.nameBn else city.nameEn,
                                        country = city.country,
                                        latitude = city.lat,
                                        longitude = city.lon
                                    )
                                )
                            },
                            label = {
                                Text(if (language == AppLanguage.BANGLA) city.nameBn else city.nameEn)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.LocationCity,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        }

        // 4. Search Results (if searching)
        if (searchQuery.trim().length >= 2) {
            item {
                Text(
                    text = if (language == AppLanguage.BANGLA) "অনুসন্ধানের ফলাফল" else "Search Results",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (searchResults.isEmpty() && !isSearching) {
                item {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "কোনো ফলাফল পাওয়া যায়নি" else "No matching locations found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(searchResults) { location ->
                    val isFav = favorites.any { it.latitude == location.latitude && it.longitude == location.longitude }
                    LocationResultCard(
                        location = location,
                        isFavorite = isFav,
                        onSelect = { onSelectLocation(location) },
                        onToggleFavorite = { onToggleFavorite(location) }
                    )
                }
            }
        }

        // 5. Favorite Locations List
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.BANGLA) "সংরক্ষিত প্রিয় স্থানসমূহ" else "Saved Favorite Locations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${BanglaHelper.formatNumber(favorites.size, language)}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (favorites.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) {
                                "এখনো কোনো প্রিয় স্থান যুক্ত করা হয়নি। উপরে শহর অনুসন্ধান করে ⭐ আইকনে চাপ দিন।"
                            } else {
                                "No favorite locations added yet. Search above and tap the heart icon to save."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(favorites) { fav ->
                FavoriteLocationCard(
                    location = fav,
                    settings = settings,
                    onSelect = { onSelectLocation(fav) },
                    onDelete = { onRemoveFavorite(fav.id) }
                )
            }
        }
    }
}

@Composable
private fun LocationResultCard(
    location: LocationItem,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val sub = listOfNotNull(location.admin1, location.country).joinToString(", ")
                    if (sub.isNotBlank()) {
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FavoriteLocationCard(
    location: LocationItem,
    settings: AppSettings,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val language = settings.language

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationCity,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = location.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (location.cachedTempC != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${BanglaHelper.formatTemp(location.cachedTempC, settings.tempUnit, language)} • ${location.cachedCondition ?: ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}
