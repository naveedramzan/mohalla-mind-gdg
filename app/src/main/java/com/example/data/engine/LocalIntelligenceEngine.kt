package com.example.data.engine

import com.example.data.model.*

object LocalIntelligenceEngine {

    fun analyze(signals: CommunitySignalSet): MohallaMindOutput {
        val weather = signals.weather
        val flood = signals.flood
        val water = signals.water
        val roads = signals.roads
        val sanitation = signals.sanitation
        val loc = signals.location

        // Calculate severity metrics across domains
        val isRainHigh = weather.rainfall_last_24h_mm > 35 || weather.rain_probability > 70
        val isFloodSevere = flood.flood_reports >= 3 || flood.water_accumulation_reports >= 3 || flood.blocked_drains >= 2
        val isWaterCritical = water.shortage_reports >= 3 || water.supply_status == "severely_disrupted" || water.affected_streets >= 3
        val isRoadsDisrupted = roads.flooded_roads >= 2 || roads.blocked_roads >= 1 || roads.potholes >= 5
        val isSanitationBad = sanitation.waste_reports >= 3 || sanitation.overflowing_bins >= 3 || sanitation.collection_coverage < 70

        // Compound pattern detection
        val floodRoadCompound = (isRainHigh || isFloodSevere) && isRoadsDisrupted
        val waterSanitationCompound = isWaterCritical && isSanitationBad
        val severeStormCompound = isRainHigh && isFloodSevere && (isRoadsDisrupted || isSanitationBad)

        // Health Score calculation (100 = perfect, 0 = crisis)
        var penalty = 0
        penalty += (weather.rainfall_last_24h_mm / 5).coerceAtMost(20)
        penalty += (flood.flood_reports * 6 + flood.water_accumulation_reports * 5 + flood.blocked_drains * 7).coerceAtMost(30)
        penalty += (water.shortage_reports * 8 + water.affected_streets * 6).coerceAtMost(25)
        penalty += (roads.flooded_roads * 8 + roads.blocked_roads * 10 + roads.potholes * 2).coerceAtMost(25)
        penalty += (sanitation.waste_reports * 6 + sanitation.overflowing_bins * 7 + (100 - sanitation.collection_coverage) / 3).coerceAtMost(25)

        val healthScore = (100 - penalty).coerceIn(10, 100)
        val overallStatusText = when {
            healthScore >= 75 -> "Good"
            healthScore >= 45 -> "Needs Attention"
            else -> "Critical"
        }

        // Domain condition assessments
        val weatherCond = ConditionDetail(
            status = if (isRainHigh) "Severe Precipitation" else "Stable",
            summary = "${weather.temperature}°C, ${weather.rainfall_last_24h_mm}mm rain (24h). ${weather.forecast}",
            confidence = "High"
        )

        val floodCond = ConditionDetail(
            status = if (isFloodSevere) "Elevated Risk" else "Low Risk",
            summary = "${flood.flood_reports} flood reports, ${flood.water_accumulation_reports} accumulation spots, ${flood.blocked_drains} blocked drains.",
            confidence = if (flood.flood_reports + flood.water_accumulation_reports >= 3) "High" else "Medium"
        )

        val waterCond = ConditionDetail(
            status = if (isWaterCritical) "Shortage Alert" else "Supply Normal",
            summary = "Status: ${water.supply_status.replace("_", " ")}, ${water.shortage_reports} reports across ${water.affected_streets} streets.",
            confidence = if (water.shortage_reports >= 2) "High" else "Medium"
        )

        val roadCond = ConditionDetail(
            status = if (isRoadsDisrupted) "Impaired Mobility" else "Passable",
            summary = "${roads.flooded_roads} flooded roads, ${roads.potholes} potholes, ${roads.blocked_roads} blocked routes.",
            confidence = "High"
        )

        val sanitationCond = ConditionDetail(
            status = if (isSanitationBad) "Overburdened" else "Satisfactory",
            summary = "${sanitation.collection_coverage}% coverage, ${sanitation.overflowing_bins} overflowing bins, ${sanitation.waste_reports} complaints.",
            confidence = "High"
        )

        // Reason over priorities & build top insight + recommended action + explanation + simulation
        val (topInsight, recAction, explanation, simulation, alerts) = when {
            severeStormCompound || (isFloodSevere && isRoadsDisrupted) -> {
                val insight = TopInsight(
                    title = "Flash Flood & Drainage Obstruction in ${loc.area}",
                    description = "Heavy 24h rainfall (${weather.rainfall_last_24h_mm}mm) coupled with ${flood.blocked_drains} blocked main drains and ${flood.water_accumulation_reports} water accumulation reports has caused ${roads.flooded_roads} flooded road sections.",
                    severity = if (severeStormCompound) "Critical" else "High",
                    confidence = "High"
                )
                val action = RecommendedAction(
                    action = "Dispatch Emergency Municipal Drainage Inspection & Clearing Team",
                    priority = "High",
                    location = "Main Arterials & Low-Lying Drains, ${loc.area}, ${loc.city}",
                    reason = "Elevated surface water build-up threatening local mobility and property safety.",
                    signals_used = listOf("Weather (Rainfall)", "Flood Reports", "Blocked Drains", "Flooded Roads")
                )
                val expl = ActionExplanation(
                    why = "Heavy rainfall combined with three recent water-accumulation reports and two blocked drains directly caused street flooding and mobility hazards.",
                    evidence = listOf(
                        "${weather.rainfall_last_24h_mm}mm rainfall recorded in last 24 hours",
                        "${flood.water_accumulation_reports} independent community water accumulation reports",
                        "${flood.blocked_drains} confirmed blocked drainage outlets",
                        "${roads.flooded_roads} flooded road segments disrupting local traffic"
                    ),
                    reasoning = "When rainfall intensity exceeds natural runoff and drainage outlets are clogged, water quickly accumulates on roads, exponentially increasing urban flood risk."
                )
                val sim = ActionSimulation(
                    status = "SIMULATED",
                    before = "Flood Risk: High | Flooded Road Segments: ${roads.flooded_roads} | Water Accumulation: Critical",
                    action_taken = "Deployment of heavy suction pumps and municipal drain-clearing crews to ${loc.area}",
                    after = "Flood Risk: Moderate to Low | Flooded Road Segments: 0-1 | Water Drain Rate: +180%",
                    impact = "Restores road accessibility for ~4,500 local residents within 3 hours and prevents indoor property seepage."
                )
                val alertList = listOf(
                    "⚠️ Severe drainage alert in ${loc.area}. Avoid low-lying underpasses.",
                    "📢 Municipal emergency team dispatched for stormwater clearance."
                )
                Tuple5(insight, action, expl, sim, alertList)
            }

            waterSanitationCompound -> {
                val insight = TopInsight(
                    title = "Compound Health & Water Shortage Crisis in ${loc.area}",
                    description = "Intermittent water supply affecting ${water.affected_streets} streets combined with ${sanitation.overflowing_bins} overflowing waste bins presents severe public hygiene and health risks.",
                    severity = "High",
                    confidence = "High"
                )
                val action = RecommendedAction(
                    action = "Escalate Water Tanker Supply & Prioritize Municipal Sanitation Clearance",
                    priority = "High",
                    location = "Affected Sectors in ${loc.area}, ${loc.city}",
                    reason = "Concurrent water scarcity and waste build-up increase contamination and illness risks.",
                    signals_used = listOf("Water Supply Status", "Shortage Reports", "Overflowing Bins", "Waste Reports")
                )
                val expl = ActionExplanation(
                    why = "Simultaneous water shortages and uncollected waste create a compound public health risk requiring immediate joint municipal response.",
                    evidence = listOf(
                        "${water.shortage_reports} water shortage reports across ${water.affected_streets} streets",
                        "Water supply status marked as '${water.supply_status}'",
                        "${sanitation.overflowing_bins} overflowing bins with ${sanitation.waste_reports} community complaints",
                        "Collection coverage down at ${sanitation.collection_coverage}%"
                    ),
                    reasoning = "Uncleaned waste dumps near areas experiencing clean water shortages significantly elevate bacterial contamination risks for vulnerable households."
                )
                val sim = ActionSimulation(
                    status = "SIMULATED",
                    before = "Public Health Hazard: High | Water Supply: ${water.supply_status} | Uncollected Waste Bins: ${sanitation.overflowing_bins}",
                    action_taken = "Emergency delivery of 4 water tankers and priority waste collection truck deployment",
                    after = "Public Health Hazard: Low | Water Shortage Reports: 3 → 0 | Waste Clearance: 100%",
                    impact = "Protects ~1,200 households from hygiene degradation and provides 50,000L emergency potable water."
                )
                val alertList = listOf(
                    "💧 Emergency water tanker fleet scheduled for ${loc.area}.",
                    "🧹 Priority sanitation cleanup active."
                )
                Tuple5(insight, action, expl, sim, alertList)
            }

            isWaterCritical -> {
                val insight = TopInsight(
                    title = "Localized Water Scarcity in ${loc.area}",
                    description = "Multiple reports (${water.shortage_reports}) indicate intermittent or disrupted pipeline supply across ${water.affected_streets} streets.",
                    severity = "Medium",
                    confidence = "High"
                )
                val action = RecommendedAction(
                    action = "Initiate Municipal Water Supply Rerouting & Tanker Dispatch",
                    priority = "Medium",
                    location = "${loc.area} Water Utility Sub-Station",
                    reason = "Pressure drop and main valve distribution bottleneck in Sector 2.",
                    signals_used = listOf("Water Shortage Reports", "Affected Streets", "Supply Status")
                )
                val expl = ActionExplanation(
                    why = "Clustered complaints across adjacent streets indicate a localized pipeline valve issue rather than a city-wide depletion.",
                    evidence = listOf(
                        "${water.shortage_reports} community reports logged",
                        "${water.affected_streets} adjacent streets impacted",
                        "Supply pipeline status: ${water.supply_status}"
                    ),
                    reasoning = "Spatially concentrated shortage reports point to mechanical line throttling; immediate valve inspection restores baseline supply."
                )
                val sim = ActionSimulation(
                    status = "SIMULATED",
                    before = "Water Availability: Critical | Affected Streets: ${water.affected_streets}",
                    action_taken = "Pressure valve adjustments and supplemental tanker deployment",
                    after = "Water Availability: Normal | Affected Streets: 0",
                    impact = "Restores pipeline water pressure to standard baseline within 2 hours."
                )
                val alertList = listOf("💧 Water supply restoration in progress for ${loc.area}.")
                Tuple5(insight, action, expl, sim, alertList)
            }

            isRoadsDisrupted -> {
                val insight = TopInsight(
                    title = "Road Infrastructure & Mobility Impairment in ${loc.area}",
                    description = "${roads.potholes} potholes, ${roads.flooded_roads} flooded road sections, and ${roads.blocked_roads} blocked routes causing traffic congestion.",
                    severity = "Medium",
                    confidence = "High"
                )
                val action = RecommendedAction(
                    action = "Issue Local Traffic Advisory & Schedule Immediate Pothole Patching",
                    priority = "Medium",
                    location = "Commercial Corridor, ${loc.area}",
                    reason = "Hazardous road surface condition poses commuter safety risk.",
                    signals_used = listOf("Pothole Reports", "Flooded Roads", "Traffic Disruptions")
                )
                val expl = ActionExplanation(
                    why = "Surface potholes combined with standing water lead to commuter vehicle damage and bottleneck delays.",
                    evidence = listOf(
                        "${roads.potholes} reported severe potholes",
                        "${roads.flooded_roads} flooded roadway patches",
                        "${roads.traffic_disruptions} traffic disruption alerts"
                    ),
                    reasoning = "Unresolved potholes underwater become invisible hazards for motorists, compounding slowdowns."
                )
                val sim = ActionSimulation(
                    status = "SIMULATED",
                    before = "Traffic Speed: Reduced by 40% | Potholes: ${roads.potholes}",
                    action_taken = "Cold-mix asphalt patching and temporary traffic rerouting signage",
                    after = "Traffic Speed: Normal | Potholes Patched: 100%",
                    impact = "Prevents vehicular damage and eliminates commute bottlenecks for ~3,000 daily commuters."
                )
                val alertList = listOf("🚗 Drive carefully in ${loc.area} due to road surface repairs.")
                Tuple5(insight, action, expl, sim, alertList)
            }

            isSanitationBad -> {
                val insight = TopInsight(
                    title = "Municipal Sanitation Accumulation in ${loc.area}",
                    description = "Collection coverage dropped to ${sanitation.collection_coverage}% with ${sanitation.overflowing_bins} overflowing bins reported.",
                    severity = "Medium",
                    confidence = "High"
                )
                val action = RecommendedAction(
                    action = "Prioritize Emergency Municipal Waste Collection Route",
                    priority = "Medium",
                    location = "Central Waste Depots, ${loc.area}",
                    reason = "Collection lag creating visual clutter and pest breeding risk.",
                    signals_used = listOf("Overflowing Bins", "Collection Coverage", "Waste Reports")
                )
                val expl = ActionExplanation(
                    why = "Community reports match a drop in collection coverage, indicating missed municipal pickup cycles.",
                    evidence = listOf(
                        "${sanitation.overflowing_bins} overflowing public bins",
                        "Collection coverage at ${sanitation.collection_coverage}%",
                        "${sanitation.waste_reports} community complaints logged"
                    ),
                    reasoning = "Direct correlation between missed truck routes and overflowing bins confirms logistical delay."
                )
                val sim = ActionSimulation(
                    status = "SIMULATED",
                    before = "Sanitation Score: Below Average | Overfilled Bins: ${sanitation.overflowing_bins}",
                    action_taken = "Rerouting 2 extra compaction trucks to ${loc.area}",
                    after = "Sanitation Score: Satisfactory | Overfilled Bins: 0",
                    impact = "Clears 12 tons of accumulated solid waste within 4 hours."
                )
                val alertList = listOf("🧹 Extra waste compaction trucks dispatched to ${loc.area}.")
                Tuple5(insight, action, expl, sim, alertList)
            }

            else -> {
                val insight = TopInsight(
                    title = "Community Operations Stable in ${loc.area}",
                    description = "All signals across weather, drainage, water supply, roads, and sanitation are operating within normal baseline parameters.",
                    severity = "Low",
                    confidence = "High"
                )
                val action = RecommendedAction(
                    action = "Maintain Standard Municipal Monitoring & Preventive Maintenance",
                    priority = "Low",
                    location = "${loc.area}, ${loc.city}",
                    reason = "Baseline community indicators are positive.",
                    signals_used = listOf("Weather Baseline", "Road Conditions", "Water Supply", "Sanitation Coverage")
                )
                val expl = ActionExplanation(
                    why = "Signal fusion confirms low disturbance levels and good municipal service coverage across all monitored domains.",
                    evidence = listOf(
                        "Moderate temperature (${weather.temperature}°C) with low active rainfall",
                        "No major flood or road obstruction reports",
                        "Water supply and sanitation operating at standard performance levels"
                    ),
                    reasoning = "Agreement across all 5 independent data channels establishes high confidence in overall neighborhood health."
                )
                val sim = ActionSimulation(
                    status = "SIMULATED",
                    before = "Neighborhood Status: Good (Score: $healthScore/100)",
                    action_taken = "Routine automated sensor telemetry sweep & preventive drain inspection",
                    after = "Neighborhood Status: Good (Score: $healthScore/100)",
                    impact = "Ensures uninterrupted urban services for all residents in ${loc.area}."
                )
                val alertList = emptyList<String>()
                Tuple5(insight, action, expl, sim, alertList)
            }
        }

        val summaryText = "Mohalla Mind fusion engine analyzed 5 neighborhood signals for ${loc.area}, ${loc.city}. Overall neighborhood health index is $healthScore/100 ($overallStatusText). Top priority: ${topInsight.title}."

        return MohallaMindOutput(
            location = OutputLocation(city = loc.city, area = loc.area),
            overall_status = OverallStatus(
                status = overallStatusText,
                score = healthScore,
                summary = summaryText
            ),
            conditions = DomainConditions(
                weather = weatherCond,
                flood = floodCond,
                water = waterCond,
                roads = roadCond,
                sanitation = sanitationCond
            ),
            top_insight = topInsight,
            recommended_action = recAction,
            explanation = explanation,
            simulation = simulation,
            alerts = alerts,
            data_gaps = if (weather.rainfall_last_24h_mm == 0 && flood.flood_reports > 0) listOf("Recent precipitation sensor telemetry incomplete") else emptyList()
        )
    }

    private data class Tuple5<A, B, C, D, E>(
        val a: A, val b: B, val c: C, val d: D, val e: E
    )
}
