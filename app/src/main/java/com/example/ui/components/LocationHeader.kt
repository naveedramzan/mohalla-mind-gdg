package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldPrimary

val CITIES_MOHALLAS = mapOf(
    "Islamabad" to listOf("F-10", "G-9", "I-8", "F-6", "E-11"),
    "Lahore" to listOf("Gulberg III", "DHA Phase 5", "Johar Town", "Model Town"),
    "Karachi" to listOf("Clifton Block 2", "DHA Phase 6", "PECHS", "Gulshan-e-Iqbal"),
    "Peshawar" to listOf("University Town", "Hayatabad Phase 3", "Saddar"),
    "Rawalpindi" to listOf("Saddar", "Bahria Town Phase 4", "Satellite Town")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationHeader(
    selectedCity: String,
    selectedMohalla: String,
    isGemini: Boolean,
    executionTimeMs: Long,
    onLocationChanged: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLocationDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("location_header_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Hero Image Background Banner
            Image(
                painter = painterResource(id = R.drawable.mohalla_hero_banner_1786190125233),
                contentDescription = "Mohalla Mind Neighborhood Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )

            // Content Header Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Mind Logo",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Mohalla Mind",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "AI Community Intelligence Engine",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Engine Mode Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isGemini) Color(0xFF00BFA5).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isGemini) Icons.Default.AutoAwesome else Icons.Default.Memory,
                                contentDescription = null,
                                tint = if (isGemini) Color(0xFF00BFA5) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isGemini) "Gemini 3.5 Flash" else "Local AI Engine",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (isGemini) Color(0xFF00BFA5) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Location Picker Trigger
                Surface(
                    onClick = { showLocationDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("location_picker_trigger")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "$selectedMohalla, $selectedCity",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tap to change neighborhood scope",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand Location",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showLocationDialog) {
        LocationSelectionDialog(
            currentCity = selectedCity,
            currentMohalla = selectedMohalla,
            onDismiss = { showLocationDialog = false },
            onSelect = { city, area ->
                onLocationChanged(city, area)
                showLocationDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LocationSelectionDialog(
    currentCity: String,
    currentMohalla: String,
    onDismiss: () -> Unit,
    onSelect: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var tempCity by remember { mutableStateOf(currentCity) }
    var tempMohalla by remember { mutableStateOf(currentMohalla) }

    val filteredCities = remember(searchQuery) {
        CITIES_MOHALLAS.keys.filter {
            it.contains(searchQuery, ignoreCase = true)
        }
    }

    val mohallaList = CITIES_MOHALLAS[tempCity] ?: listOf("Central Area", "Sector 1", "Phase 1", "Main Bazaar")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Map, contentDescription = null, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Neighborhood Location")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Search City Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        if (query.isNotBlank() && !CITIES_MOHALLAS.containsKey(query)) {
                            tempCity = query.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("city_search_input"),
                    placeholder = { Text("Search or type city name...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search City", tint = EmeraldPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Cities",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (searchQuery.isNotBlank() && CITIES_MOHALLAS.keys.none { it.equals(searchQuery.trim(), ignoreCase = true) }) {
                        val customCityName = searchQuery.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        FilterChip(
                            selected = tempCity == customCityName,
                            onClick = {
                                tempCity = customCityName
                                tempMohalla = CITIES_MOHALLAS[customCityName]?.firstOrNull() ?: "Central Area"
                            },
                            label = { Text("+ Use \"$customCityName\"", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.AddLocation, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    filteredCities.forEach { city ->
                        FilterChip(
                            selected = tempCity == city,
                            onClick = {
                                tempCity = city
                                tempMohalla = CITIES_MOHALLAS[city]?.firstOrNull() ?: "Central Area"
                            },
                            label = { Text(city, fontSize = 12.sp) }
                        )
                    }
                }

                if (filteredCities.isEmpty() && searchQuery.isNotBlank() && CITIES_MOHALLAS.keys.none { it.equals(searchQuery.trim(), ignoreCase = true) }) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Custom city typed. Tap '+ Use' or apply.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Mohalla / Area in $tempCity",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    mohallaList.forEach { area ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { tempMohalla = area }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tempMohalla == area,
                                onClick = { tempMohalla = area }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(area, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalCity = if (tempCity.isBlank()) "Islamabad" else tempCity
                    val finalMohalla = if (tempMohalla.isBlank()) "F-10" else tempMohalla
                    onSelect(finalCity, finalMohalla)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Apply Location Scope")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
