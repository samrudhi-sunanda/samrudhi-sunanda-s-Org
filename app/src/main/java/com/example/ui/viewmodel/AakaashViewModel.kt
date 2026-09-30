package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.AudioRecorderHelper
import com.example.data.gemini.ChatMessage
import com.example.data.gemini.GeminiApiService
import com.example.data.gemini.GeminiChatRequest
import com.example.data.gemini.GeminiContent
import com.example.data.gemini.GeminiLiveClient
import com.example.data.gemini.GeminiModels
import com.example.data.gemini.GeminiPart
import com.example.data.gemini.LiveSessionState
import com.example.data.gemini.LiveTranscriptTurn
import com.example.data.gemini.MessageSender
import com.example.data.local.AlertThresholdEntity
import com.example.data.local.BioSyncAlarmEntity
import com.example.data.local.CachedWeatherEntity
import com.example.data.local.RouteEntity
import com.example.data.local.UserEntity
import com.example.data.model.PersonaCatalog
import com.example.data.repository.AakaashRepository
import com.example.ui.navigation.AakaashScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AakaashViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AakaashRepository.getInstance(application)
    private val geminiApiService = GeminiApiService.getInstance()
    private val liveClient = GeminiLiveClient(viewModelScope)
    val audioRecorder = AudioRecorderHelper()

    private val _currentScreen = MutableStateFlow(AakaashScreen.DASHBOARD)
    val currentScreen: StateFlow<AakaashScreen> = _currentScreen.asStateFlow()

    val user: StateFlow<UserEntity?> = repository.getUserFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weather: StateFlow<CachedWeatherEntity?> = repository.getWeatherFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val routes: StateFlow<List<RouteEntity>> = repository.getRoutesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alarms: StateFlow<List<BioSyncAlarmEntity>> = repository.getAlarmsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timeMachineHour = repository.timeMachineHourOffset
    val isRouteBypassActive = repository.isRouteBypassActive
    val activeMapLayer = repository.activeMapLayer

    private val _isAdvisoryDismissed = MutableStateFlow(false)
    val isAdvisoryDismissed = _isAdvisoryDismissed.asStateFlow()

    private val _isAlarmShiftApplied = MutableStateFlow(false)
    val isAlarmShiftApplied = _isAlarmShiftApplied.asStateFlow()

    private val _selectedOnboardingDomain = MutableStateFlow("fitness")
    val selectedOnboardingDomain = _selectedOnboardingDomain.asStateFlow()

    private val _selectedOnboardingPersona = MutableStateFlow("cyclist")
    val selectedOnboardingPersona = _selectedOnboardingPersona.asStateFlow()

    // Thresholds state
    private val _customWindLimit = MutableStateFlow(28f)
    val customWindLimit = _customWindLimit.asStateFlow()

    private val _customAqiLimit = MutableStateFlow(75f)
    val customAqiLimit = _customAqiLimit.asStateFlow()

    private val _customRainLimit = MutableStateFlow(20f)
    val customRainLimit = _customRainLimit.asStateFlow()

    // ==========================================
    // Gemini Chatbot & Grounding States
    // ==========================================
    private val _selectedChatModel = MutableStateFlow(GeminiModels.GEMINI_3_5_FLASH)
    val selectedChatModel: StateFlow<String> = _selectedChatModel.asStateFlow()

    private val _useGoogleMaps = MutableStateFlow(true)
    val useGoogleMaps: StateFlow<Boolean> = _useGoogleMaps.asStateFlow()

    private val _useGoogleSearch = MutableStateFlow(true)
    val useGoogleSearch: StateFlow<Boolean> = _useGoogleSearch.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing.asStateFlow()

    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.ASSISTANT,
                text = "Welcome to **AeroCopilot Intelligence**. I can analyze real-time crosswinds, evaluate road bottlenecks with Google Maps, ground forecasts via Google Search, and help optimize your departure windows.",
                modelUsed = GeminiModels.GEMINI_3_5_FLASH
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Live API states
    val liveSessionState: StateFlow<LiveSessionState> = liveClient.sessionState
    val liveStatusMessage: StateFlow<String> = liveClient.statusMessage
    val liveTranscripts: StateFlow<List<LiveTranscriptTurn>> = liveClient.transcripts
    val liveAudioWaveform: StateFlow<List<Float>> = liveClient.audioWaveformLevels

    private val _isLiveDialogOpen = MutableStateFlow(false)
    val isLiveDialogOpen: StateFlow<Boolean> = _isLiveDialogOpen.asStateFlow()

    fun navigateTo(screen: AakaashScreen) {
        _currentScreen.value = screen
    }

    fun selectOnboardingDomain(domainId: String) {
        _selectedOnboardingDomain.value = domainId
        val category = PersonaCatalog.categories.find { it.id == domainId }
        category?.personas?.firstOrNull()?.let {
            _selectedOnboardingPersona.value = it.id
        }
    }

    fun selectOnboardingPersona(personaId: String) {
        _selectedOnboardingPersona.value = personaId
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.selectPersona(_selectedOnboardingPersona.value)
            repository.setOnboarded(true)
            _currentScreen.value = AakaashScreen.DASHBOARD
        }
    }

    fun selectPersonaDirectly(personaId: String) {
        viewModelScope.launch {
            repository.selectPersona(personaId)
            _selectedOnboardingPersona.value = personaId
        }
    }

    fun toggleSoundscape() {
        viewModelScope.launch {
            val current = user.value?.soundscapeEnabled ?: true
            repository.toggleSoundscape(!current)
        }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            repository.updateTheme(isDark)
        }
    }

    fun dismissAdvisory() {
        _isAdvisoryDismissed.value = true
    }

    fun applyDepartureShift() {
        viewModelScope.launch {
            _isAlarmShiftApplied.value = true
            val firstAlarm = alarms.value.firstOrNull()
            if (firstAlarm != null) {
                repository.applyDepartureShift(firstAlarm.id, -25)
            }
        }
    }

    fun setTimeMachineHour(hours: Int) {
        repository.setTimeMachineHour(hours)
    }

    fun setMapLayer(layer: String) {
        repository.setMapLayer(layer)
    }

    fun autoRecalculateSafeRoute() {
        viewModelScope.launch {
            repository.recalculateSafeRoute()
        }
    }

    fun updateThresholds(wind: Float, aqi: Float, rain: Float) {
        _customWindLimit.value = wind
        _customAqiLimit.value = aqi
        _customRainLimit.value = rain

        viewModelScope.launch {
            val currentPersona = user.value?.activePersonaId ?: "cyclist"
            repository.saveThreshold(
                AlertThresholdEntity(
                    personaId = currentPersona,
                    maxWindKmh = wind,
                    maxGustKmh = wind + 10f,
                    maxAqi = aqi.toInt(),
                    maxRainProbability = rain.toInt()
                )
            )
        }
    }

    // ==========================================
    // Gemini Chatbot Actions
    // ==========================================
    fun selectChatModel(model: String) {
        _selectedChatModel.value = model
    }

    fun toggleGoogleMaps(enabled: Boolean) {
        _useGoogleMaps.value = enabled
    }

    fun toggleGoogleSearch(enabled: Boolean) {
        _useGoogleSearch.value = enabled
    }

    fun clearChatHistory() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = MessageSender.ASSISTANT,
                text = "Chat history cleared. How can I assist with your atmospheric mission or route safety?",
                modelUsed = _selectedChatModel.value
            )
        )
    }

    fun sendChatMessage(userText: String, isVoiceInput: Boolean = false) {
        if (userText.isBlank()) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = userText.trim(),
            isAudioTranscribed = isVoiceInput
        )

        val updatedList = _chatMessages.value.toMutableList().apply { add(userMessage) }
        _chatMessages.value = updatedList
        _isGenerating.value = true

        viewModelScope.launch {
            // Build conversation history for multi-turn chat
            val contentsList = mutableListOf<GeminiContent>()
            for (msg in updatedList.filter { it.sender != MessageSender.SYSTEM }) {
                val role = if (msg.sender == MessageSender.USER) "user" else "model"
                contentsList.add(
                    GeminiContent(
                        role = role,
                        parts = listOf(GeminiPart(text = msg.text))
                    )
                )
            }

            val currentWeather = weather.value ?: CachedWeatherEntity()
            val activePersona = user.value?.activePersonaId ?: "cyclist"

            val systemInstruction = """
                You are Mausam AeroCopilot, an elite spatial meteorology AI specialized in kinetic wind telemetry, headwind aerodynamic drag, micro-climate hazard prevention, and optimal departure routing for athletes, outdoor workers, and transport missions.
                Active Persona: $activePersona
                Current Telemetry: Wind ${currentWeather.windSpeedKmh} km/h ${currentWeather.windDirectionCardinal} (${currentWeather.windDirectionDeg}°), Gusts ${currentWeather.gustSpeedKmh} km/h, AQI ${currentWeather.aqi} (${currentWeather.aqiRating}), Tire Traction ${currentWeather.tireTractionPct}%, Precip ${currentWeather.precipWindowPct}%.
                When Google Maps or Google Search is enabled, incorporate real geography, roads, landmarks, and live weather bulletins. Keep advice actionable, precise, and encouraging.
            """.trimIndent()

            val modelToUse = _selectedChatModel.value

            val request = GeminiChatRequest(
                contents = contentsList,
                systemInstruction = systemInstruction,
                useGoogleMaps = _useGoogleMaps.value,
                useGoogleSearch = _useGoogleSearch.value
            )

            val result = geminiApiService.generateChatTurn(modelToUse, request)
            _isGenerating.value = false

            result.onSuccess { response ->
                val assistantMessage = ChatMessage(
                    sender = MessageSender.ASSISTANT,
                    text = response.text,
                    modelUsed = modelToUse,
                    groundingSources = response.groundingCitations
                )
                _chatMessages.value = _chatMessages.value + assistantMessage
            }.onFailure { err ->
                val errorMessage = ChatMessage(
                    sender = MessageSender.ASSISTANT,
                    text = "⚠️ **Error (${modelToUse})**: ${err.localizedMessage}\n\n*Tip: Ensure your GEMINI_API_KEY is configured in the AI Studio Secrets panel.*",
                    modelUsed = modelToUse
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            }
        }
    }

    // Audio recording & Transcription via gemini-3.5-transcribe
    fun startAudioRecording() {
        val started = audioRecorder.startRecording()
        _isRecordingAudio.value = started
    }

    fun stopAudioRecordingAndTranscribe(onTranscribed: (String) -> Unit) {
        if (!_isRecordingAudio.value) return
        _isRecordingAudio.value = false
        _isTranscribing.value = true

        viewModelScope.launch {
            val wavBytes = audioRecorder.stopAndGetWavBytes()
            if (wavBytes != null && wavBytes.isNotEmpty()) {
                val transcribeResult = geminiApiService.transcribeAudio(wavBytes, "audio/wav")
                _isTranscribing.value = false
                transcribeResult.onSuccess { transcript ->
                    if (transcript.isNotBlank()) {
                        onTranscribed(transcript)
                    }
                }.onFailure { err ->
                    val errorMessage = ChatMessage(
                        sender = MessageSender.SYSTEM,
                        text = "🎙️ **Audio Transcription Failed (gemini-3.5-transcribe)**: ${err.localizedMessage}"
                    )
                    _chatMessages.value = _chatMessages.value + errorMessage
                }
            } else {
                _isTranscribing.value = false
            }
        }
    }

    // ==========================================
    // Gemini Live API (gemini-3.8-live) Actions
    // ==========================================
    fun openLiveDialog() {
        _isLiveDialogOpen.value = true
        liveClient.startLiveSession()
    }

    fun closeLiveDialog() {
        _isLiveDialogOpen.value = false
        liveClient.stopLiveSession()
    }

    fun sendLiveTextMessage(text: String) {
        liveClient.sendTextMessage(text)
    }

    override fun onCleared() {
        super.onCleared()
        liveClient.cleanup()
    }
}
