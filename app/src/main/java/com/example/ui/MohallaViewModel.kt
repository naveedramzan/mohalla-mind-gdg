package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.network.GeminiService
import com.example.data.repository.MohallaRepository
import com.example.data.repository.PresetScenarios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiAnalysisState(
    val isLoading: Boolean = false,
    val output: MohallaMindOutput? = null,
    val isGemini: Boolean = false,
    val durationMs: Long = 0,
    val errorMessage: String? = null
)

class MohallaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MohallaRepository(AppDatabase.getDatabase(application).analysisDao())

    val history = repository.getHistory()

    private val _selectedCity = MutableStateFlow("Islamabad")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedMohalla = MutableStateFlow("F-10")
    val selectedMohalla: StateFlow<String> = _selectedMohalla.asStateFlow()

    private val _activeScenarioName = MutableStateFlow("Scenario B — Heavy Rain")
    val activeScenarioName: StateFlow<String> = _activeScenarioName.asStateFlow()

    private val _activeSignals = MutableStateFlow(PresetScenarios.scenarioB("Islamabad", "F-10"))
    val activeSignals: StateFlow<CommunitySignalSet> = _activeSignals.asStateFlow()

    private val _analysisState = MutableStateFlow(UiAnalysisState())
    val analysisState: StateFlow<UiAnalysisState> = _analysisState.asStateFlow()

    private val _isApiKeyAvailable = MutableStateFlow(GeminiService.isApiKeyAvailable())
    val isApiKeyAvailable: StateFlow<Boolean> = _isApiKeyAvailable.asStateFlow()

    init {
        // Run initial analysis on app boot
        runAnalysis()
    }

    fun selectLocation(city: String, area: String) {
        _selectedCity.value = city
        _selectedMohalla.value = area
        val updatedLoc = _activeSignals.value.location.copy(city = city, area = area)
        _activeSignals.value = _activeSignals.value.copy(location = updatedLoc)
        runAnalysis()
    }

    fun selectPreset(scenario: String) {
        _activeScenarioName.value = scenario
        val city = _selectedCity.value
        val area = _selectedMohalla.value
        val newSet = when (scenario) {
            "Scenario A — Normal" -> PresetScenarios.scenarioA(city, area)
            "Scenario B — Heavy Rain" -> PresetScenarios.scenarioB(city, area)
            "Scenario C — Multiple Problems" -> PresetScenarios.scenarioC(city, area)
            "Scenario D — Water Crisis" -> PresetScenarios.scenarioD(city, area)
            else -> PresetScenarios.scenarioB(city, area)
        }
        _activeSignals.value = newSet
        runAnalysis()
    }

    fun updateWeatherSignal(weather: WeatherSignal) {
        _activeScenarioName.value = "Custom Live Scenario"
        _activeSignals.value = _activeSignals.value.copy(weather = weather)
        runAnalysis()
    }

    fun updateFloodSignal(flood: FloodSignal) {
        _activeScenarioName.value = "Custom Live Scenario"
        _activeSignals.value = _activeSignals.value.copy(flood = flood)
        runAnalysis()
    }

    fun updateWaterSignal(water: WaterSignal) {
        _activeScenarioName.value = "Custom Live Scenario"
        _activeSignals.value = _activeSignals.value.copy(water = water)
        runAnalysis()
    }

    fun updateRoadSignal(roads: RoadSignal) {
        _activeScenarioName.value = "Custom Live Scenario"
        _activeSignals.value = _activeSignals.value.copy(roads = roads)
        runAnalysis()
    }

    fun updateSanitationSignal(sanitation: SanitationSignal) {
        _activeScenarioName.value = "Custom Live Scenario"
        _activeSignals.value = _activeSignals.value.copy(sanitation = sanitation)
        runAnalysis()
    }

    fun runAnalysis() {
        viewModelScope.launch {
            _analysisState.value = _analysisState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.analyzeSignals(
                signals = _activeSignals.value,
                scenarioName = _activeScenarioName.value
            )
            _analysisState.value = UiAnalysisState(
                isLoading = false,
                output = result.output,
                isGemini = result.isGemini,
                durationMs = result.durationMs,
                errorMessage = result.errorMessage
            )
        }
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
