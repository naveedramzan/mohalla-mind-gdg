package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationInfo(
    val city: String = "Islamabad",
    val area: String = "F-10",
    val latitude: Double? = 33.6938,
    val longitude: Double? = 73.0142
)

@JsonClass(generateAdapter = true)
data class WeatherSignal(
    val temperature: Int = 32,
    val humidity: Int = 68,
    val rain_probability: Int = 75,
    val rainfall_last_24h_mm: Int = 48,
    val forecast: String = "Heavy rain expected"
)

@JsonClass(generateAdapter = true)
data class FloodSignal(
    val flood_reports: Int = 4,
    val water_accumulation_reports: Int = 3,
    val historical_flood_risk: String = "medium", // "low", "medium", "high"
    val blocked_drains: Int = 2
)

@JsonClass(generateAdapter = true)
data class WaterSignal(
    val shortage_reports: Int = 3,
    val supply_status: String = "intermittent", // "normal", "intermittent", "severely_disrupted"
    val affected_streets: Int = 2
)

@JsonClass(generateAdapter = true)
data class RoadSignal(
    val potholes: Int = 5,
    val blocked_roads: Int = 1,
    val flooded_roads: Int = 2,
    val traffic_disruptions: Int = 1
)

@JsonClass(generateAdapter = true)
data class SanitationSignal(
    val waste_reports: Int = 4,
    val collection_coverage: Int = 82,
    val overflowing_bins: Int = 3
)

data class CommunitySignalSet(
    val location: LocationInfo = LocationInfo(),
    val weather: WeatherSignal = WeatherSignal(),
    val flood: FloodSignal = FloodSignal(),
    val water: WaterSignal = WaterSignal(),
    val roads: RoadSignal = RoadSignal(),
    val sanitation: SanitationSignal = SanitationSignal()
)
