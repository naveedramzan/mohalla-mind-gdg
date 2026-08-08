package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary

data class ScenarioPresetItem(
    val name: String,
    val description: String,
    val icon: ImageVector
)

val PRESET_ITEMS = listOf(
    ScenarioPresetItem("Scenario A — Normal", "Clear weather, baseline services", Icons.Default.WbSunny),
    ScenarioPresetItem("Scenario B — Heavy Rain", "High rainfall & flood risk", Icons.Default.Thunderstorm),
    ScenarioPresetItem("Scenario C — Multiple Problems", "Heavy rain, flooded roads, waste", Icons.Default.Warning),
    ScenarioPresetItem("Scenario D — Water Crisis", "Pipe shortage & supply drop", Icons.Default.WaterDrop)
)

@Composable
fun ScenarioPresetSelector(
    selectedScenario: String,
    onScenarioSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Demo Scenarios & Signal Simulation",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Mohalla Mind Core Cycle",
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
            modifier = Modifier.testTag("scenario_preset_selector_row")
        ) {
            items(PRESET_ITEMS) { item ->
                val isSelected = selectedScenario == item.name

                FilterChip(
                    selected = isSelected,
                    onClick = { onScenarioSelected(item.name) },
                    label = {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = item.name.split(" — ").getOrElse(1) { item.name },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}
