package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MohallaMindOutput(
    val location: OutputLocation = OutputLocation(),
    val overall_status: OverallStatus = OverallStatus(),
    val conditions: DomainConditions = DomainConditions(),
    val top_insight: TopInsight = TopInsight(),
    val recommended_action: RecommendedAction = RecommendedAction(),
    val explanation: ActionExplanation = ActionExplanation(),
    val simulation: ActionSimulation = ActionSimulation(),
    val alerts: List<String> = emptyList(),
    val data_gaps: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class OutputLocation(
    val city: String = "",
    val area: String = ""
)

@JsonClass(generateAdapter = true)
data class OverallStatus(
    val status: String = "Good", // "Good", "Needs Attention", "Critical"
    val score: Int = 85, // 0 - 100 health index
    val summary: String = ""
)

@JsonClass(generateAdapter = true)
data class DomainConditions(
    val weather: ConditionDetail = ConditionDetail(),
    val flood: ConditionDetail = ConditionDetail(),
    val water: ConditionDetail = ConditionDetail(),
    val roads: ConditionDetail = ConditionDetail(),
    val sanitation: ConditionDetail = ConditionDetail()
)

@JsonClass(generateAdapter = true)
data class ConditionDetail(
    val status: String = "Normal", // e.g. "Normal", "Elevated Risk", "Critical"
    val summary: String = "",
    val confidence: String = "High" // "High", "Medium", "Low"
)

@JsonClass(generateAdapter = true)
data class TopInsight(
    val title: String = "",
    val description: String = "",
    val severity: String = "Low", // "Low", "Medium", "High", "Critical"
    val confidence: String = "High"
)

@JsonClass(generateAdapter = true)
data class RecommendedAction(
    val action: String = "",
    val priority: String = "Medium", // "High", "Medium", "Low"
    val location: String = "",
    val reason: String = "",
    val signals_used: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ActionExplanation(
    val why: String = "",
    val evidence: List<String> = emptyList(),
    val reasoning: String = ""
)

@JsonClass(generateAdapter = true)
data class ActionSimulation(
    val status: String = "SIMULATED",
    val before: String = "",
    val action_taken: String = "",
    val after: String = "",
    val impact: String = ""
)
