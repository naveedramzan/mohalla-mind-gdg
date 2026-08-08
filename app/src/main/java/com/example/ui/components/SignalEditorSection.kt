package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalEditorSection(
    signals: CommunitySignalSet,
    onWeatherChange: (WeatherSignal) -> Unit,
    onFloodChange: (FloodSignal) -> Unit,
    onWaterChange: (WaterSignal) -> Unit,
    onRoadsChange: (RoadSignal) -> Unit,
    onSanitationChange: (SanitationSignal) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Weather", "Flood", "Water", "Roads", "Sanitation")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("signal_editor_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Signal Adjuster",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Interactive Community Signals Control",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    text = "Live Tweak",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Domain Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> WeatherEditor(weather = signals.weather, onChange = onWeatherChange)
                1 -> FloodEditor(flood = signals.flood, onChange = onFloodChange)
                2 -> WaterEditor(water = signals.water, onChange = onWaterChange)
                3 -> RoadsEditor(roads = signals.roads, onChange = onRoadsChange)
                4 -> SanitationEditor(sanitation = signals.sanitation, onChange = onSanitationChange)
            }
        }
    }
}

@Composable
fun WeatherEditor(weather: WeatherSignal, onChange: (WeatherSignal) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SignalSliderRow(
            label = "Rainfall (24h)",
            valueText = "${weather.rainfall_last_24h_mm} mm",
            value = weather.rainfall_last_24h_mm.toFloat(),
            range = 0f..100f,
            onValueChange = { onChange(weather.copy(rainfall_last_24h_mm = it.toInt())) }
        )

        SignalSliderRow(
            label = "Rain Probability",
            valueText = "${weather.rain_probability} %",
            value = weather.rain_probability.toFloat(),
            range = 0f..100f,
            onValueChange = { onChange(weather.copy(rain_probability = it.toInt())) }
        )

        SignalSliderRow(
            label = "Temperature",
            valueText = "${weather.temperature} °C",
            value = weather.temperature.toFloat(),
            range = 10f..48f,
            onValueChange = { onChange(weather.copy(temperature = it.toInt())) }
        )
    }
}

@Composable
fun FloodEditor(flood: FloodSignal, onChange: (FloodSignal) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SignalSliderRow(
            label = "Flood Reports",
            valueText = "${flood.flood_reports}",
            value = flood.flood_reports.toFloat(),
            range = 0f..10f,
            onValueChange = { onChange(flood.copy(flood_reports = it.toInt())) }
        )

        SignalSliderRow(
            label = "Water Accumulation Spots",
            valueText = "${flood.water_accumulation_reports}",
            value = flood.water_accumulation_reports.toFloat(),
            range = 0f..10f,
            onValueChange = { onChange(flood.copy(water_accumulation_reports = it.toInt())) }
        )

        SignalSliderRow(
            label = "Blocked Drainage Outlets",
            valueText = "${flood.blocked_drains}",
            value = flood.blocked_drains.toFloat(),
            range = 0f..8f,
            onValueChange = { onChange(flood.copy(blocked_drains = it.toInt())) }
        )
    }
}

@Composable
fun WaterEditor(water: WaterSignal, onChange: (WaterSignal) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SignalSliderRow(
            label = "Shortage Reports",
            valueText = "${water.shortage_reports}",
            value = water.shortage_reports.toFloat(),
            range = 0f..10f,
            onValueChange = { onChange(water.copy(shortage_reports = it.toInt())) }
        )

        SignalSliderRow(
            label = "Affected Streets",
            valueText = "${water.affected_streets}",
            value = water.affected_streets.toFloat(),
            range = 0f..8f,
            onValueChange = { onChange(water.copy(affected_streets = it.toInt())) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Supply Status:", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("normal", "intermittent", "severely_disrupted").forEach { status ->
                    FilterChip(
                        selected = water.supply_status == status,
                        onClick = { onChange(water.copy(supply_status = status)) },
                        label = { Text(status.replace("_", " "), fontSize = 10.sp) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoadsEditor(roads: RoadSignal, onChange: (RoadSignal) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SignalSliderRow(
            label = "Flooded Road Segments",
            valueText = "${roads.flooded_roads}",
            value = roads.flooded_roads.toFloat(),
            range = 0f..8f,
            onValueChange = { onChange(roads.copy(flooded_roads = it.toInt())) }
        )

        SignalSliderRow(
            label = "Severe Potholes",
            valueText = "${roads.potholes}",
            value = roads.potholes.toFloat(),
            range = 0f..12f,
            onValueChange = { onChange(roads.copy(potholes = it.toInt())) }
        )

        SignalSliderRow(
            label = "Blocked Routes",
            valueText = "${roads.blocked_roads}",
            value = roads.blocked_roads.toFloat(),
            range = 0f..5f,
            onValueChange = { onChange(roads.copy(blocked_roads = it.toInt())) }
        )
    }
}

@Composable
fun SanitationEditor(sanitation: SanitationSignal, onChange: (SanitationSignal) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SignalSliderRow(
            label = "Waste Complaints",
            valueText = "${sanitation.waste_reports}",
            value = sanitation.waste_reports.toFloat(),
            range = 0f..10f,
            onValueChange = { onChange(sanitation.copy(waste_reports = it.toInt())) }
        )

        SignalSliderRow(
            label = "Overflowing Bins",
            valueText = "${sanitation.overflowing_bins}",
            value = sanitation.overflowing_bins.toFloat(),
            range = 0f..8f,
            onValueChange = { onChange(sanitation.copy(overflowing_bins = it.toInt())) }
        )

        SignalSliderRow(
            label = "Collection Coverage",
            valueText = "${sanitation.collection_coverage} %",
            value = sanitation.collection_coverage.toFloat(),
            range = 30f..100f,
            onValueChange = { onChange(sanitation.copy(collection_coverage = it.toInt())) }
        )
    }
}

@Composable
fun SignalSliderRow(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Text(valueText, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldPrimary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = EmeraldPrimary,
                activeTrackColor = EmeraldPrimary
            )
        )
    }
}
