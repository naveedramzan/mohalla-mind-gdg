package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_history")
data class AnalysisHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val city: String,
    val area: String,
    val scenarioName: String,
    val healthScore: Int,
    val overallStatus: String,
    val topInsightTitle: String,
    val recommendedAction: String,
    val simulationSummary: String,
    val isGeminiGenerated: Boolean
)
