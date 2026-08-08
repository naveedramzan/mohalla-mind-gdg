package com.example.data.repository

import com.example.data.db.AnalysisDao
import com.example.data.db.AnalysisHistoryEntity
import com.example.data.engine.LocalIntelligenceEngine
import com.example.data.model.*
import com.example.data.network.GeminiService
import kotlinx.coroutines.flow.Flow

data class AnalysisResult(
    val output: MohallaMindOutput,
    val isGemini: Boolean,
    val durationMs: Long,
    val errorMessage: String? = null
)

object PresetScenarios {
    fun scenarioA(city: String = "Islamabad", area: String = "F-10"): CommunitySignalSet {
        return CommunitySignalSet(
            location = LocationInfo(city = city, area = area, latitude = 33.6938, longitude = 73.0142),
            weather = WeatherSignal(temperature = 28, humidity = 45, rain_probability = 10, rainfall_last_24h_mm = 0, forecast = "Clear skies"),
            flood = FloodSignal(flood_reports = 0, water_accumulation_reports = 0, historical_flood_risk = "low", blocked_drains = 0),
            water = WaterSignal(shortage_reports = 0, supply_status = "normal", affected_streets = 0),
            roads = RoadSignal(potholes = 1, blocked_roads = 0, flooded_roads = 0, traffic_disruptions = 0),
            sanitation = SanitationSignal(waste_reports = 0, collection_coverage = 95, overflowing_bins = 0)
        )
    }

    fun scenarioB(city: String = "Islamabad", area: String = "F-10"): CommunitySignalSet {
        return CommunitySignalSet(
            location = LocationInfo(city = city, area = area, latitude = 33.6938, longitude = 73.0142),
            weather = WeatherSignal(temperature = 32, humidity = 68, rain_probability = 75, rainfall_last_24h_mm = 48, forecast = "Heavy rain expected"),
            flood = FloodSignal(flood_reports = 4, water_accumulation_reports = 3, historical_flood_risk = "medium", blocked_drains = 2),
            water = WaterSignal(shortage_reports = 1, supply_status = "intermittent", affected_streets = 1),
            roads = RoadSignal(potholes = 3, blocked_roads = 0, flooded_roads = 1, traffic_disruptions = 1),
            sanitation = SanitationSignal(waste_reports = 1, collection_coverage = 88, overflowing_bins = 1)
        )
    }

    fun scenarioC(city: String = "Islamabad", area: String = "F-10"): CommunitySignalSet {
        return CommunitySignalSet(
            location = LocationInfo(city = city, area = area, latitude = 33.6938, longitude = 73.0142),
            weather = WeatherSignal(temperature = 30, humidity = 82, rain_probability = 90, rainfall_last_24h_mm = 65, forecast = "Continuous torrential downpour"),
            flood = FloodSignal(flood_reports = 8, water_accumulation_reports = 6, historical_flood_risk = "high", blocked_drains = 5),
            water = WaterSignal(shortage_reports = 4, supply_status = "severely_disrupted", affected_streets = 3),
            roads = RoadSignal(potholes = 8, blocked_roads = 2, flooded_roads = 4, traffic_disruptions = 3),
            sanitation = SanitationSignal(waste_reports = 6, collection_coverage = 60, overflowing_bins = 5)
        )
    }

    fun scenarioD(city: String = "Islamabad", area: String = "F-10"): CommunitySignalSet {
        return CommunitySignalSet(
            location = LocationInfo(city = city, area = area, latitude = 33.6938, longitude = 73.0142),
            weather = WeatherSignal(temperature = 36, humidity = 35, rain_probability = 5, rainfall_last_24h_mm = 0, forecast = "Dry & hot"),
            flood = FloodSignal(flood_reports = 0, water_accumulation_reports = 0, historical_flood_risk = "low", blocked_drains = 0),
            water = WaterSignal(shortage_reports = 7, supply_status = "severely_disrupted", affected_streets = 5),
            roads = RoadSignal(potholes = 2, blocked_roads = 0, flooded_roads = 0, traffic_disruptions = 0),
            sanitation = SanitationSignal(waste_reports = 2, collection_coverage = 85, overflowing_bins = 2)
        )
    }
}

class MohallaRepository(private val dao: AnalysisDao) {

    fun getHistory(): Flow<List<AnalysisHistoryEntity>> = dao.getAllHistory()

    suspend fun analyzeSignals(
        signals: CommunitySignalSet,
        scenarioName: String,
        useGeminiIfAvailable: Boolean = true
    ): AnalysisResult {
        val startTime = System.currentTimeMillis()

        if (useGeminiIfAvailable && GeminiService.isApiKeyAvailable()) {
            val geminiResult = GeminiService.analyzeWithGemini(signals)
            val duration = System.currentTimeMillis() - startTime
            if (geminiResult.isSuccess) {
                val output = geminiResult.getOrThrow()
                saveToHistory(signals, scenarioName, output, isGemini = true)
                return AnalysisResult(output = output, isGemini = true, durationMs = duration)
            }
        }

        // Fallback to local rule engine
        val output = LocalIntelligenceEngine.analyze(signals)
        val duration = System.currentTimeMillis() - startTime
        saveToHistory(signals, scenarioName, output, isGemini = false)

        return AnalysisResult(output = output, isGemini = false, durationMs = duration)
    }

    private suspend fun saveToHistory(
        signals: CommunitySignalSet,
        scenarioName: String,
        output: MohallaMindOutput,
        isGemini: Boolean
    ) {
        val entity = AnalysisHistoryEntity(
            city = signals.location.city,
            area = signals.location.area,
            scenarioName = scenarioName,
            healthScore = output.overall_status.score,
            overallStatus = output.overall_status.status,
            topInsightTitle = output.top_insight.title,
            recommendedAction = output.recommended_action.action,
            simulationSummary = output.simulation.impact,
            isGeminiGenerated = isGemini
        )
        dao.insertHistory(entity)
    }

    suspend fun deleteHistoryItem(id: Long) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
