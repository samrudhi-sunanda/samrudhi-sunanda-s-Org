package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AakaashBottomBar
import com.example.ui.components.GeminiLiveDialog
import com.example.ui.navigation.AakaashScreen
import com.example.ui.screens.AeroCopilotScreen
import com.example.ui.screens.BioSyncAlarmScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.RouteMapScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimeMachineScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AakaashViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AakaashViewModel = viewModel()
            val user by viewModel.user.collectAsStateWithLifecycle()
            val isDarkTheme = user?.isDarkTheme ?: false
            MyApplicationTheme(darkTheme = isDarkTheme) {
                AakaashApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AakaashApp(viewModel: AakaashViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val routes by viewModel.routes.collectAsStateWithLifecycle()
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val timeMachineHour by viewModel.timeMachineHour.collectAsStateWithLifecycle()
    val isRouteBypassActive by viewModel.isRouteBypassActive.collectAsStateWithLifecycle()
    val activeMapLayer by viewModel.activeMapLayer.collectAsStateWithLifecycle()
    val isAdvisoryDismissed by viewModel.isAdvisoryDismissed.collectAsStateWithLifecycle()
    val isAlarmShiftApplied by viewModel.isAlarmShiftApplied.collectAsStateWithLifecycle()
    val selectedDomain by viewModel.selectedOnboardingDomain.collectAsStateWithLifecycle()
    val selectedPersona by viewModel.selectedOnboardingPersona.collectAsStateWithLifecycle()
    val windLimit by viewModel.customWindLimit.collectAsStateWithLifecycle()
    val aqiLimit by viewModel.customAqiLimit.collectAsStateWithLifecycle()
    val rainLimit by viewModel.customRainLimit.collectAsStateWithLifecycle()

    // Gemini Chat & Live states
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val selectedChatModel by viewModel.selectedChatModel.collectAsStateWithLifecycle()
    val useGoogleMaps by viewModel.useGoogleMaps.collectAsStateWithLifecycle()
    val useGoogleSearch by viewModel.useGoogleSearch.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isTranscribing by viewModel.isTranscribing.collectAsStateWithLifecycle()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val isLiveDialogOpen by viewModel.isLiveDialogOpen.collectAsStateWithLifecycle()
    val liveSessionState by viewModel.liveSessionState.collectAsStateWithLifecycle()
    val liveStatusMessage by viewModel.liveStatusMessage.collectAsStateWithLifecycle()
    val liveTranscripts by viewModel.liveTranscripts.collectAsStateWithLifecycle()

    // Handle back button on sub-screens
    BackHandler(enabled = currentScreen != AakaashScreen.DASHBOARD && currentScreen != AakaashScreen.ONBOARDING) {
        viewModel.navigateTo(AakaashScreen.DASHBOARD)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AakaashScreen.ONBOARDING -> {
                        OnboardingScreen(
                            selectedDomainId = selectedDomain,
                            selectedPersonaId = selectedPersona,
                            onDomainSelected = { viewModel.selectOnboardingDomain(it) },
                            onPersonaSelected = { viewModel.selectOnboardingPersona(it) },
                            onConfirmLaunch = { viewModel.completeOnboarding() }
                        )
                    }

                    AakaashScreen.DASHBOARD -> {
                        DashboardScreen(
                            user = user,
                            weather = weather,
                            soundscapeActive = user?.soundscapeEnabled ?: true,
                            isAdvisoryDismissed = isAdvisoryDismissed,
                            isAlarmShiftApplied = isAlarmShiftApplied,
                            onToggleSoundscape = { viewModel.toggleSoundscape() },
                            onDismissAdvisory = { viewModel.dismissAdvisory() },
                            onApplyAlarmShift = { viewModel.applyDepartureShift() },
                            onSelectPersona = { viewModel.selectPersonaDirectly(it) },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AakaashScreen.ROUTE_MAP -> {
                        RouteMapScreen(
                            routes = routes,
                            activeLayer = activeMapLayer,
                            isEcoBypassActive = isRouteBypassActive,
                            onLayerSelected = { viewModel.setMapLayer(it) },
                            onAutoRecalculateRoute = { viewModel.autoRecalculateSafeRoute() },
                            onApplyDepartureShift = { viewModel.applyDepartureShift() },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AakaashScreen.AERO_COPILOT -> {
                        AeroCopilotScreen(
                            messages = chatMessages,
                            selectedModel = selectedChatModel,
                            useGoogleMaps = useGoogleMaps,
                            useGoogleSearch = useGoogleSearch,
                            isGenerating = isGenerating,
                            isTranscribing = isTranscribing,
                            isRecordingAudio = isRecordingAudio,
                            onSelectModel = { viewModel.selectChatModel(it) },
                            onToggleGoogleMaps = { viewModel.toggleGoogleMaps(it) },
                            onToggleGoogleSearch = { viewModel.toggleGoogleSearch(it) },
                            onSendMessage = { text, isVoice -> viewModel.sendChatMessage(text, isVoice) },
                            onStartRecordingAudio = { viewModel.startAudioRecording() },
                            onStopRecordingAndTranscribe = { onDone -> viewModel.stopAudioRecordingAndTranscribe(onDone) },
                            onOpenLiveVoiceDialog = { viewModel.openLiveDialog() },
                            onClearChatHistory = { viewModel.clearChatHistory() }
                        )
                    }

                    AakaashScreen.TIME_MACHINE -> {
                        TimeMachineScreen(
                            currentHourOffset = timeMachineHour,
                            weather = weather,
                            onScrubHour = { viewModel.setTimeMachineHour(it) },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AakaashScreen.BIOSYNC_ALARMS -> {
                        BioSyncAlarmScreen(
                            user = user,
                            alarms = alarms,
                            onApplyShift = { viewModel.applyDepartureShift() },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AakaashScreen.SETTINGS -> {
                        SettingsScreen(
                            user = user,
                            windLimit = windLimit,
                            aqiLimit = aqiLimit,
                            rainLimit = rainLimit,
                            soundscapeActive = user?.soundscapeEnabled ?: true,
                            isDarkTheme = user?.isDarkTheme ?: false,
                            onToggleTheme = { viewModel.toggleTheme(it) },
                            onToggleSoundscape = { viewModel.toggleSoundscape() },
                            onUpdateThresholds = { wind, aqi, rain ->
                                viewModel.updateThresholds(wind, aqi, rain)
                            },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                }

                // Show floating dock bar on all screens except onboarding
                if (currentScreen != AakaashScreen.ONBOARDING) {
                    AakaashBottomBar(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Real-time Live Voice Dialog (gemini-3.8-live)
                if (isLiveDialogOpen) {
                    GeminiLiveDialog(
                        sessionState = liveSessionState,
                        statusMessage = liveStatusMessage,
                        transcripts = liveTranscripts,
                        onSendMessage = { viewModel.sendLiveTextMessage(it) },
                        onDismiss = { viewModel.closeLiveDialog() }
                    )
                }
            }
        }
    }
}
