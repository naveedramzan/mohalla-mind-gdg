package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MohallaMindScreen(
    viewModel: MohallaViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val selectedMohalla by viewModel.selectedMohalla.collectAsStateWithLifecycle()
    val activeScenarioName by viewModel.activeScenarioName.collectAsStateWithLifecycle()
    val activeSignals by viewModel.activeSignals.collectAsStateWithLifecycle()
    val analysisState by viewModel.analysisState.collectAsStateWithLifecycle()
    val historyList by viewModel.history.collectAsStateWithLifecycle(initialValue = emptyList())

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Mohalla Mind",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "PK Neighborhood AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.runAnalysis() },
                        modifier = Modifier.testTag("refresh_analysis_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Analysis",
                            tint = EmeraldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Location Picker and Engine Badge
            LocationHeader(
                selectedCity = selectedCity,
                selectedMohalla = selectedMohalla,
                isGemini = analysisState.isGemini,
                executionTimeMs = analysisState.durationMs,
                onLocationChanged = { city, area ->
                    viewModel.selectLocation(city, area)
                }
            )

            // Preset Scenario Pills
            ScenarioPresetSelector(
                selectedScenario = activeScenarioName,
                onScenarioSelected = { scenario ->
                    viewModel.selectPreset(scenario)
                }
            )

            // Primary Intelligence Engine Execution Bar
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = EmeraldPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trigger_analysis_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Multi-Signal Fusion Engine",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = if (analysisState.isGemini) "Gemini 3.5 Flash reasoning active" else "Local Pattern Fusion active (${analysisState.durationMs}ms)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }

                    Button(
                        onClick = { viewModel.runAnalysis() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = EmeraldPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("run_ai_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Analyze",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Loading state
            if (analysisState.isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = EmeraldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Mohalla Mind is fusing community signals & reasoning...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Analysis Output Cards
            analysisState.output?.let { output ->
                AnimatedVisibility(
                    visible = !analysisState.isLoading,
                    enter = fadeIn() + expandVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // 1. Overall Status Card
                        OverallStatusCard(output = output)

                        // 2. Top Fused Insight Card
                        TopInsightCard(insight = output.top_insight)

                        // 3. Recommended Intervention Card
                        RecommendedActionCard(action = output.recommended_action)

                        // 4. Simulated Impact Card
                        SimulationCard(simulation = output.simulation)

                        // 5. Explainability & Reasoning Card
                        ExplanationCard(explanation = output.explanation)
                    }
                }
            }

            // Error Message Banner if any
            analysisState.errorMessage?.let { err ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notice: $err",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Interactive Live Signal Tweaker Controls
            SignalEditorSection(
                signals = activeSignals,
                onWeatherChange = { viewModel.updateWeatherSignal(it) },
                onFloodChange = { viewModel.updateFloodSignal(it) },
                onWaterChange = { viewModel.updateWaterSignal(it) },
                onRoadsChange = { viewModel.updateRoadSignal(it) },
                onSanitationChange = { viewModel.updateSanitationSignal(it) }
            )

            // Saved Snapshot History Logs from Room DB
            HistoryLogSection(
                historyList = historyList,
                onDeleteItem = { id -> viewModel.deleteHistory(id) },
                onClearAll = { viewModel.clearHistory() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
