package com.example.data.gemini

import android.util.Base64
import android.util.Log
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

class GeminiApiService private constructor() {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Executes a multi-turn chat generation with Gemini, with optional Google Maps or Google Search grounding.
     */
    suspend fun generateChatTurn(
        modelName: String,
        request: GeminiChatRequest
    ): Result<GeminiChatResponse> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please enter your API key in the Secrets panel in AI Studio.")
            )
        }

        try {
            val rootJson = JSONObject()

            // 1. Build contents list (multi-turn conversation)
            val contentsArray = JSONArray()
            for (c in request.contents) {
                val contentObj = JSONObject()
                contentObj.put("role", c.role)
                val partsArray = JSONArray()
                for (p in c.parts) {
                    val partObj = JSONObject()
                    if (p.text != null) {
                        partObj.put("text", p.text)
                    }
                    if (p.inlineData != null) {
                        val inlineObj = JSONObject()
                        inlineObj.put("mimeType", p.inlineData.mimeType)
                        inlineObj.put("data", p.inlineData.base64Data)
                        partObj.put("inlineData", inlineObj)
                    }
                    partsArray.put(partObj)
                }
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }
            rootJson.put("contents", contentsArray)

            // 2. System Instruction
            if (!request.systemInstruction.isNullOrBlank()) {
                val sysObj = JSONObject()
                val partsArr = JSONArray().apply {
                    put(JSONObject().apply { put("text", request.systemInstruction) })
                }
                sysObj.put("parts", partsArr)
                rootJson.put("systemInstruction", sysObj)
            }

            // 3. Grounding Tools (Google Maps and Google Search)
            val toolsArray = JSONArray()
            if (request.useGoogleMaps) {
                toolsArray.put(JSONObject().apply {
                    put("googleMaps", JSONObject())
                })
            }
            if (request.useGoogleSearch) {
                toolsArray.put(JSONObject().apply {
                    put("googleSearch", JSONObject())
                })
            }
            if (toolsArray.length() > 0) {
                rootJson.put("tools", toolsArray)
            }

            // 4. Generation Config
            val genConfig = JSONObject().apply {
                put("temperature", request.temperature)
            }
            rootJson.put("generationConfig", genConfig)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)

            val httpRequest = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(httpRequest).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = parseErrorMessage(responseBody)
                return@withContext Result.failure(
                    Exception("Gemini API Error ($modelName - ${response.code}): $errorMsg")
                )
            }

            val parsedResponse = parseGenerateContentResponse(responseBody)
            Result.success(parsedResponse)
        } catch (e: Exception) {
            Log.e("GeminiApiService", "Failed to generate chat turn", e)
            Result.failure(e)
        }
    }

    /**
     * Transcribes audio using model gemini-3.5-transcribe.
     */
    suspend fun transcribeAudio(
        audioBytes: ByteArray,
        mimeType: String = "audio/wav"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your key in the AI Studio Secrets panel.")
            )
        }

        val base64Audio = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
        
        // Attempt first with gemini-3.5-transcribe as requested by user
        val result = callTranscribeEndpoint(
            model = GeminiModels.GEMINI_3_5_TRANSCRIBE,
            base64Audio = base64Audio,
            mimeType = mimeType,
            apiKey = apiKey
        )

        if (result.isSuccess) {
            return@withContext result
        }

        // Fallback to gemini-3.5-flash for audio transcription if transcribe-specific alias endpoint has differing region availability
        Log.w("GeminiApiService", "gemini-3.5-transcribe failed (${result.exceptionOrNull()?.message}), falling back to gemini-3.5-flash audio input")
        callTranscribeEndpoint(
            model = GeminiModels.GEMINI_3_5_FLASH,
            base64Audio = base64Audio,
            mimeType = mimeType,
            apiKey = apiKey
        )
    }

    private fun callTranscribeEndpoint(
        model: String,
        base64Audio: String,
        mimeType: String,
        apiKey: String
    ): Result<String> {
        try {
            val rootJson = JSONObject()
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            contentObj.put("role", "user")

            val partsArray = JSONArray()
            partsArray.put(JSONObject().apply {
                put("text", "Transcribe the spoken audio verbatim into clean text. Output ONLY the transcription, without any markdown formatting, preamble, or conversational commentary.")
            })
            partsArray.put(JSONObject().apply {
                val inlineData = JSONObject().apply {
                    put("mimeType", mimeType)
                    put("data", base64Audio)
                }
                put("inlineData", inlineData)
            })
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            rootJson.put("contents", contentsArray)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)

            val httpRequest = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(httpRequest).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return Result.failure(Exception("Transcription error ($model - ${response.code}): ${parseErrorMessage(responseBody)}"))
            }

            val parsed = parseGenerateContentResponse(responseBody)
            return Result.success(parsed.text.trim())
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun parseGenerateContentResponse(jsonString: String): GeminiChatResponse {
        val root = JSONObject(jsonString)
        val candidates = root.optJSONArray("candidates") ?: return GeminiChatResponse("No response generated")
        if (candidates.length() == 0) return GeminiChatResponse("No response generated")

        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")

        val textBuilder = StringBuilder()
        if (parts != null) {
            for (i in 0 until parts.length()) {
                val part = parts.getJSONObject(i)
                val text = part.optString("text")
                if (text.isNotEmpty()) {
                    textBuilder.append(text)
                }
            }
        }

        // Parse Grounding Metadata (Google Maps & Google Search)
        val citations = mutableListOf<GroundingCitation>()
        val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
        if (groundingMetadata != null) {
            // Google Search queries / chunks
            val webSearchQueries = groundingMetadata.optJSONArray("webSearchQueries")
            if (webSearchQueries != null) {
                for (i in 0 until webSearchQueries.length()) {
                    val query = webSearchQueries.getString(i)
                    citations.add(GroundingCitation(title = query, sourceType = GroundingType.GOOGLE_SEARCH))
                }
            }

            val groundingChunks = groundingMetadata.optJSONArray("groundingChunks")
            if (groundingChunks != null) {
                for (i in 0 until groundingChunks.length()) {
                    val chunk = groundingChunks.getJSONObject(i)
                    val web = chunk.optJSONObject("web")
                    if (web != null) {
                        val title = web.optString("title", "Google Search Result")
                        val uri = web.optString("uri")
                        citations.add(GroundingCitation(title = title, uri = uri, sourceType = GroundingType.GOOGLE_SEARCH))
                    }
                    val maps = chunk.optJSONObject("maps")
                    if (maps != null) {
                        val title = maps.optString("title", "Google Maps Location")
                        val uri = maps.optString("uri")
                        citations.add(GroundingCitation(title = title, uri = uri, sourceType = GroundingType.GOOGLE_MAPS))
                    }
                }
            }
        }

        val finishReason = firstCandidate.optString("finishReason")
        return GeminiChatResponse(
            text = textBuilder.toString(),
            groundingCitations = citations.distinctBy { it.title },
            finishReason = finishReason
        )
    }

    private fun parseErrorMessage(jsonString: String): String {
        return try {
            val root = JSONObject(jsonString)
            val error = root.optJSONObject("error")
            error?.optString("message") ?: jsonString
        } catch (e: Exception) {
            jsonString
        }
    }

    companion object {
        @Volatile
        private var instance: GeminiApiService? = null

        fun getInstance(): GeminiApiService {
            return instance ?: synchronized(this) {
                instance ?: GeminiApiService().also { instance = it }
            }
        }
    }
}
