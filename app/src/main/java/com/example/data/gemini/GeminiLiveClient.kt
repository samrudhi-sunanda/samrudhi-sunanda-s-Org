package com.example.data.gemini

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class LiveSessionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    LISTENING,
    SPEAKING,
    ERROR
}

data class LiveTranscriptTurn(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String,
    val text: String,
    val isComplete: Boolean = true
)

class GeminiLiveClient(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())
) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // Keep-alive for WebSocket
        .build()

    private var webSocket: WebSocket? = null

    private val _sessionState = MutableStateFlow(LiveSessionState.DISCONNECTED)
    val sessionState: StateFlow<LiveSessionState> = _sessionState.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready to connect")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _transcripts = MutableStateFlow<List<LiveTranscriptTurn>>(emptyList())
    val transcripts: StateFlow<List<LiveTranscriptTurn>> = _transcripts.asStateFlow()

    private val _audioWaveformLevels = MutableStateFlow(listOf(0.2f, 0.4f, 0.7f, 0.5f, 0.3f))
    val audioWaveformLevels: StateFlow<List<Float>> = _audioWaveformLevels.asStateFlow()

    private var audioTrack: AudioTrack? = null

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            val sampleRate = 24000
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            audioTrack = AudioTrack(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
                bufferSize.coerceAtLeast(4096),
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            audioTrack?.play()
        } catch (e: Exception) {
            Log.e("GeminiLiveClient", "Error initializing audio track", e)
        }
    }

    fun startLiveSession() {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            _sessionState.value = LiveSessionState.ERROR
            _statusMessage.value = "Gemini API key is not configured in Secrets."
            return
        }

        _sessionState.value = LiveSessionState.CONNECTING
        _statusMessage.value = "Connecting to Gemini 3.8 Live API..."

        val url = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent?key=$apiKey"
        val request = Request.Builder().url(url).build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("GeminiLiveClient", "WebSocket connection opened")
                sendInitialSetup(webSocket)
                _sessionState.value = LiveSessionState.CONNECTED
                _statusMessage.value = "Live with Gemini 3.8 Live (Listening...)"
                addSystemMessage("Connected to Gemini 3.8 Live session.")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleServerMessage(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("GeminiLiveClient", "WebSocket closing: $code / $reason")
                _sessionState.value = LiveSessionState.DISCONNECTED
                _statusMessage.value = "Session ended ($reason)"
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("GeminiLiveClient", "WebSocket failure", t)
                _sessionState.value = LiveSessionState.ERROR
                _statusMessage.value = "Connection issue: ${t.localizedMessage ?: "Network error"}"
            }
        })
    }

    private fun sendInitialSetup(ws: WebSocket) {
        try {
            val setupJson = JSONObject().apply {
                val setupObj = JSONObject().apply {
                    put("model", "models/${GeminiModels.GEMINI_3_8_LIVE}")
                    val genConfig = JSONObject().apply {
                        val modalities = JSONArray().apply {
                            put("AUDIO")
                            put("TEXT")
                        }
                        put("responseModalities", modalities)
                        val speechConfig = JSONObject().apply {
                            val voiceConfig = JSONObject().apply {
                                val prebuilt = JSONObject().apply {
                                    put("voiceName", "Aoede")
                                }
                                put("prebuiltVoiceConfig", prebuilt)
                            }
                            put("voiceConfig", voiceConfig)
                        }
                        put("speechConfig", speechConfig)
                    }
                    put("generationConfig", genConfig)

                    val sysInstruction = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "You are Mausam Live AeroCopilot, a real-time conversational atmospheric and telemetry advisor. Speak naturally, conversationally, concisely, and accurately about wind velocity, aerodynamic drag, routes, and weather risks.")
                            })
                        }
                        put("parts", parts)
                    }
                    put("systemInstruction", sysInstruction)
                }
                put("setup", setupObj)
            }

            ws.send(setupJson.toString())
            Log.d("GeminiLiveClient", "Sent setup payload for gemini-3.8-live")
        } catch (e: Exception) {
            Log.e("GeminiLiveClient", "Failed to send initial setup", e)
        }
    }

    private fun handleServerMessage(text: String) {
        try {
            val root = JSONObject(text)

            val serverContent = root.optJSONObject("serverContent")
            if (serverContent != null) {
                val modelTurn = serverContent.optJSONObject("modelTurn")
                if (modelTurn != null) {
                    val parts = modelTurn.optJSONArray("parts")
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            val textPart = part.optString("text")
                            if (textPart.isNotEmpty()) {
                                appendModelText(textPart)
                            }
                            val inlineData = part.optJSONObject("inlineData")
                            if (inlineData != null) {
                                val base64Data = inlineData.optString("data")
                                if (base64Data.isNotEmpty()) {
                                    playAudioChunk(base64Data)
                                }
                            }
                        }
                    }
                }
                val turnComplete = serverContent.optBoolean("turnComplete", false)
                if (turnComplete) {
                    _sessionState.value = LiveSessionState.LISTENING
                } else {
                    _sessionState.value = LiveSessionState.SPEAKING
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiLiveClient", "Failed to parse server message: $text", e)
        }
    }

    fun sendTextMessage(text: String) {
        if (_sessionState.value != LiveSessionState.CONNECTED && _sessionState.value != LiveSessionState.LISTENING) {
            startLiveSession()
        }

        addUserMessage(text)

        try {
            val clientContent = JSONObject().apply {
                val ccObj = JSONObject().apply {
                    val turns = JSONArray().apply {
                        val turn = JSONObject().apply {
                            put("role", "user")
                            val parts = JSONArray().apply {
                                put(JSONObject().apply { put("text", text) })
                            }
                            put("parts", parts)
                        }
                        put(turn)
                    }
                    put("turns", turns)
                    put("turnComplete", true)
                }
                put("clientContent", ccObj)
            }

            webSocket?.send(clientContent.toString())
            _sessionState.value = LiveSessionState.SPEAKING
        } catch (e: Exception) {
            Log.e("GeminiLiveClient", "Failed to send clientContent", e)
        }
    }

    fun sendPcmAudioChunk(pcmBytes: ByteArray) {
        if (_sessionState.value != LiveSessionState.CONNECTED && _sessionState.value != LiveSessionState.LISTENING) {
            return
        }

        try {
            val base64Data = Base64.encodeToString(pcmBytes, Base64.NO_WRAP)
            val realtimeInput = JSONObject().apply {
                val inputObj = JSONObject().apply {
                    val mediaChunks = JSONArray().apply {
                        val chunk = JSONObject().apply {
                            put("mimeType", "audio/pcm;rate=16000")
                            put("data", base64Data)
                        }
                        put(chunk)
                    }
                    put("mediaChunks", mediaChunks)
                }
                put("realtimeInput", inputObj)
            }
            webSocket?.send(realtimeInput.toString())
        } catch (e: Exception) {
            Log.e("GeminiLiveClient", "Failed to send audio chunk", e)
        }
    }

    private fun playAudioChunk(base64Data: String) {
        try {
            val audioBytes = Base64.decode(base64Data, Base64.NO_WRAP)
            audioTrack?.write(audioBytes, 0, audioBytes.size)
        } catch (e: Exception) {
            Log.e("GeminiLiveClient", "AudioTrack write failed", e)
        }
    }

    private fun addUserMessage(text: String) {
        val current = _transcripts.value.toMutableList()
        current.add(LiveTranscriptTurn(sender = "You", text = text))
        _transcripts.value = current
    }

    private fun appendModelText(text: String) {
        val current = _transcripts.value.toMutableList()
        val last = current.lastOrNull()
        if (last != null && last.sender == "Gemini Live") {
            current[current.size - 1] = last.copy(text = last.text + text)
        } else {
            current.add(LiveTranscriptTurn(sender = "Gemini Live", text = text))
        }
        _transcripts.value = current
    }

    private fun addSystemMessage(text: String) {
        val current = _transcripts.value.toMutableList()
        current.add(LiveTranscriptTurn(sender = "System", text = text))
        _transcripts.value = current
    }

    fun stopLiveSession() {
        try {
            webSocket?.close(1000, "User closed session")
            webSocket = null
            _sessionState.value = LiveSessionState.DISCONNECTED
            _statusMessage.value = "Session stopped"
        } catch (e: Exception) {
            Log.e("GeminiLiveClient", "Error stopping session", e)
        }
    }

    fun cleanup() {
        stopLiveSession()
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.e("GeminiLiveClient", "Error releasing AudioTrack", e)
        }
    }
}
