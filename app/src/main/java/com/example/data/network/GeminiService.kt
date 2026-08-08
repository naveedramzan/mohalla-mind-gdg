package com.example.data.network

import com.example.BuildConfig
import com.example.data.model.CommunitySignalSet
import com.example.data.model.MohallaMindOutput
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val outputAdapter = moshi.adapter(MohallaMindOutput::class.java)

    fun isApiKeyAvailable(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun analyzeWithGemini(signals: CommunitySignalSet): Result<MohallaMindOutput> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (!isApiKeyAvailable()) {
            return@withContext Result.failure(IllegalStateException("Gemini API key is not configured in Secrets panel."))
        }

        val systemPrompt = """
            You are Mohalla Mind, an AI-powered community intelligence system designed for Pakistani neighborhoods.
            Your job is NOT simply to summarize data.
            Your job is to: Fuse multiple signals -> Detect patterns -> Reason about community conditions -> Identify priorities -> Recommend an action -> Explain why -> Simulate the impact.
            
            Synthesize all provided signals together: Weather, Flood/Drainage, Water, Road Conditions, Sanitation.
            Return strictly valid JSON using this EXACT JSON schema structure:
            {
              "location": { "city": "", "area": "" },
              "overall_status": { "status": "Good | Needs Attention | Critical", "score": 80, "summary": "" },
              "conditions": {
                "weather": { "status": "", "summary": "", "confidence": "High | Medium | Low" },
                "flood": { "status": "", "summary": "", "confidence": "High | Medium | Low" },
                "water": { "status": "", "summary": "", "confidence": "High | Medium | Low" },
                "roads": { "status": "", "summary": "", "confidence": "High | Medium | Low" },
                "sanitation": { "status": "", "summary": "", "confidence": "High | Medium | Low" }
              },
              "top_insight": { "title": "", "description": "", "severity": "High | Medium | Low", "confidence": "High | Medium | Low" },
              "recommended_action": { "action": "", "priority": "High | Medium | Low", "location": "", "reason": "", "signals_used": [] },
              "explanation": { "why": "", "evidence": [], "reasoning": "" },
              "simulation": { "status": "SIMULATED", "before": "", "action_taken": "", "after": "", "impact": "" },
              "alerts": [],
              "data_gaps": []
            }
        """.trimIndent()

        val signalJson = """
            {
              "location": { "city": "${signals.location.city}", "area": "${signals.location.area}" },
              "weather": { "temperature": ${signals.weather.temperature}, "humidity": ${signals.weather.humidity}, "rain_probability": ${signals.weather.rain_probability}, "rainfall_last_24h_mm": ${signals.weather.rainfall_last_24h_mm}, "forecast": "${signals.weather.forecast}" },
              "flood": { "flood_reports": ${signals.flood.flood_reports}, "water_accumulation_reports": ${signals.flood.water_accumulation_reports}, "historical_flood_risk": "${signals.flood.historical_flood_risk}", "blocked_drains": ${signals.flood.blocked_drains} },
              "water": { "shortage_reports": ${signals.water.shortage_reports}, "supply_status": "${signals.water.supply_status}", "affected_streets": ${signals.water.affected_streets} },
              "roads": { "potholes": ${signals.roads.potholes}, "blocked_roads": ${signals.roads.blocked_roads}, "flooded_roads": ${signals.roads.flooded_roads}, "traffic_disruptions": ${signals.roads.traffic_disruptions} },
              "sanitation": { "waste_reports": ${signals.sanitation.waste_reports}, "collection_coverage": ${signals.sanitation.collection_coverage}, "overflowing_bins": ${signals.sanitation.overflowing_bins} }
            }
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "Perform full multi-signal intelligence analysis for this Pakistani neighborhood:\n$signalJson"))
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemPrompt))
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val url = "$BASE_URL?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val rawBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gemini HTTP ${response.code}: $rawBody"))
            }

            val jsonResp = JSONObject(rawBody)
            val candidates = jsonResp.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (text.isBlank()) {
                return@withContext Result.failure(Exception("Empty text returned from Gemini API."))
            }

            // Clean markdown code blocks if any
            val cleanJson = text.replace("```json", "").replace("```", "").trim()
            val parsedOutput = outputAdapter.fromJson(cleanJson)
                ?: return@withContext Result.failure(Exception("Failed to parse JSON response into MohallaMindOutput"))

            Result.success(parsedOutput)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
