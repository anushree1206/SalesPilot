package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Robust Gemini AI client supporting:
 * 1. Secure merchant backend proxy (recommended for production - keeps keys off Android devices).
 * 2. On-device Bring-Your-Own-Key (BYOK) for private merchant testing.
 * 3. Graceful null return on failure so the local deterministic engine takes over seamlessly.
 */
object GeminiClient {
    private const val TAG = "GeminiClient"
    // Latest supported model according to current Google AI Studio documentation (2026)
    const val DEFAULT_MODEL = "gemini-3.8-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun isServiceConfigured(customApiKey: String? = null, proxyUrl: String? = null): Boolean {
        if (!proxyUrl.isNullOrBlank()) return true
        if (!customApiKey.isNullOrBlank()) return true
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return buildKey.isNotBlank() && !buildKey.contains("MY_GEMINI_API_KEY") && !buildKey.contains("YOUR_GEMINI_API_KEY")
    }

    private fun logD(tag: String, msg: String) {
        try {
            android.util.Log.d(tag, msg)
        } catch (e: Throwable) {
            println("[$tag] $msg")
        }
    }

    private fun logE(tag: String, msg: String, tr: Throwable? = null) {
        try {
            android.util.Log.e(tag, msg, tr)
        } catch (e: Throwable) {
            println("[$tag] ERROR: $msg")
            tr?.printStackTrace()
        }
    }

    /**
     * Executes grounded AI reply generation.
     * Tries:
     * 1. Secure backend proxy (if configured)
     * 2. Direct Google AI Studio REST endpoint using merchant BYOK or build key
     * Returns null if unavailable, prompting the local fallback engine.
     */
    suspend fun generateGroundedReply(
        systemInstructionText: String,
        promptText: String,
        proxyUrl: String? = null,
        merchantApiKey: String? = null
    ): String? = withContext(Dispatchers.IO) {
        // Priority 1: Secure Backend Proxy
        if (!proxyUrl.isNullOrBlank()) {
            val proxyReply = callBackendProxy(proxyUrl, systemInstructionText, promptText)
            if (!proxyReply.isNullOrBlank()) {
                return@withContext proxyReply
            }
            logD(TAG, "Proxy returned null/failed. Checking direct credentials.")
        }

        // Priority 2: Direct REST with BYOK or BuildConfig Key
        val directKey = when {
            !merchantApiKey.isNullOrBlank() -> merchantApiKey
            else -> {
                try {
                    val key = BuildConfig.GEMINI_API_KEY
                    if (key.isNotBlank() && !key.contains("MY_GEMINI_API_KEY") && !key.contains("YOUR_GEMINI_API_KEY")) key else ""
                } catch (e: Exception) {
                    ""
                }
            }
        }

        if (directKey.isBlank()) {
            logD(TAG, "No valid AI service/proxy configured. Seamlessly activating local grounded engine.")
            return@withContext null
        }

        callDirectGeminiRest(directKey, systemInstructionText, promptText)
    }

    private fun callBackendProxy(
        proxyUrl: String,
        systemInstructionText: String,
        promptText: String
    ): String? {
        return try {
            val json = JSONObject().apply {
                put("model", DEFAULT_MODEL)
                put("systemInstruction", systemInstructionText)
                put("prompt", promptText)
            }
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(proxyUrl)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                logE(TAG, "Proxy request failed with code: ${response.code}")
                return null
            }

            val body = response.body?.string() ?: return null
            val obj = JSONObject(body)
            // Support common proxy formats: { "reply": "..." } or { "text": "..." }
            val reply = obj.optString("reply").ifBlank { obj.optString("text") }
            reply.ifBlank { null }
        } catch (e: Exception) {
            logE(TAG, "Exception contacting backend proxy: ${e.message}", e)
            null
        }
    }

    private fun callDirectGeminiRest(
        apiKey: String,
        systemInstructionText: String,
        promptText: String
    ): String? {
        return try {
            val url = "$BASE_URL/$DEFAULT_MODEL:generateContent?key=$apiKey"

            val root = JSONObject()

            // System instruction
            val sysContent = JSONObject()
            val sysParts = JSONArray().apply {
                put(JSONObject().apply { put("text", systemInstructionText) })
            }
            sysContent.put("parts", sysParts)
            root.put("systemInstruction", sysContent)

            // User Content
            val contents = JSONArray()
            val userContent = JSONObject()
            val userParts = JSONArray().apply {
                put(JSONObject().apply { put("text", promptText) })
            }
            userContent.put("parts", userParts)
            contents.put(userContent)
            root.put("contents", contents)

            // Generation config with low temperature for factual correctness
            val genConfig = JSONObject().apply {
                put("temperature", 0.3)
                put("topP", 0.95)
            }
            root.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = root.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string()
                logE(TAG, "Gemini API call failed: code=${response.code}, body=$errBody")
                return null
            }

            val responseBody = response.body?.string() ?: return null
            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null

            val text = parts.getJSONObject(0).optString("text")
            text.ifBlank { null }
        } catch (e: Exception) {
            logE(TAG, "Exception calling direct Gemini REST API: ${e.message}", e)
            null
        }
    }
}
