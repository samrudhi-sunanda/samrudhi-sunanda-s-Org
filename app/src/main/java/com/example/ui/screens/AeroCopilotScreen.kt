package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.gemini.ChatMessage
import com.example.data.gemini.GeminiModels
import com.example.data.gemini.GroundingCitation
import com.example.data.gemini.GroundingType
import com.example.data.gemini.MessageSender
import com.example.ui.theme.AmberHazard
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.Secondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AeroCopilotScreen(
    messages: List<ChatMessage>,
    selectedModel: String,
    useGoogleMaps: Boolean,
    useGoogleSearch: Boolean,
    isGenerating: Boolean,
    isTranscribing: Boolean,
    isRecordingAudio: Boolean,
    onSelectModel: (String) -> Unit,
    onToggleGoogleMaps: (Boolean) -> Unit,
    onToggleGoogleSearch: (Boolean) -> Unit,
    onSendMessage: (String, Boolean) -> Unit,
    onStartRecordingAudio: () -> Unit,
    onStopRecordingAndTranscribe: ((String) -> Unit) -> Unit,
    onOpenLiveVoiceDialog: () -> Unit,
    onClearChatHistory: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartRecordingAudio()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val micPulse by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "micPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("screen_aero_copilot")
    ) {
        // Screen Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AeroCopilot AI",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "GEMINI MULTIMODAL",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Spatial Weather & Route Copilot",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Live voice button (gemini-3.8-live)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .clickable(onClick = onOpenLiveVoiceDialog)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("launch_live_api_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Live Voice",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live 3.8",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(onClick = onClearChatHistory) {
                    Icon(
                        imageVector = Icons.Default.ClearAll,
                        contentDescription = "Clear History",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Model Selector Ribbon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            GeminiModels.CHAT_MODELS.forEach { model ->
                val isSelected = selectedModel == model.id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceContainer
                        )
                        .border(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectModel(model.id) }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = model.badge,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = model.name.replace("Gemini ", ""),
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Grounding Controls Strip (Google Maps Grounding & Google Search Grounding)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REAL-TIME GROUNDING:",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Google Maps Grounding Chip
                FilterChip(
                    selected = useGoogleMaps,
                    onClick = { onToggleGoogleMaps(!useGoogleMaps) },
                    label = { Text("Google Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(30.dp).testTag("toggle_maps_grounding")
                )

                // Google Search Grounding Chip
                FilterChip(
                    selected = useGoogleSearch,
                    onClick = { onToggleGoogleSearch(!useGoogleSearch) },
                    label = { Text("Google Search", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Secondary.copy(alpha = 0.2f),
                        selectedLabelColor = Secondary,
                        selectedLeadingIconColor = Secondary
                    ),
                    modifier = Modifier.height(30.dp).testTag("toggle_search_grounding")
                )
            }
        }

        // Scrollable Chat Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(message = msg)
            }

            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyzing atmospheric telemetry via $selectedModel...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (isTranscribing) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Secondary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Transcribing spoken voice via ${GeminiModels.GEMINI_3_5_TRANSCRIBE}...",
                            color = Secondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Quick Suggestion Chips
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val suggestions = listOf(
                "Evaluate headwind on Sector 4",
                "Google Maps road bottleneck check",
                "Should I delay departure by 25 mins?",
                "Search live Doppler radar bulletin"
            )
            suggestions.forEach { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clickable { onSendMessage(prompt, false) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = prompt,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Input & Audio Transcription Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Microphone Button (gemini-3.5-transcribe)
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRecordingAudio) AmberHazard
                        else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    .clickable {
                        if (isRecordingAudio) {
                            onStopRecordingAndTranscribe { transcribedText ->
                                inputText = transcribedText
                                onSendMessage(transcribedText, true)
                            }
                        } else {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                onStartRecordingAudio()
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    }
                    .testTag("mic_transcribe_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isRecordingAudio) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = if (isRecordingAudio) "Listening... tap stop to transcribe" else "Ask AeroCopilot or tap Mic...",
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_text_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                maxLines = 3,
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText, false)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .testTag("send_chat_message_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(72.dp)) // padding for bottom dock bar
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER
    val isSystem = message.sender == MessageSender.SYSTEM

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser && !isSystem) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                        else if (isSystem) MaterialTheme.colorScheme.surfaceContainerHigh
                        else MaterialTheme.colorScheme.surfaceContainer
                    )
                    .border(
                        1.dp,
                        if (isUser) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    if (message.isAudioTranscribed) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "TRANSCRIBED AUDIO (${GeminiModels.GEMINI_3_5_TRANSCRIBE})",
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = message.text,
                        color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    // Grounding Citations (Google Maps & Google Search)
                    if (message.groundingSources.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "GROUNDING SOURCES:",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            message.groundingSources.forEach { source ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = if (source.sourceType == GroundingType.GOOGLE_MAPS) Icons.Default.Place else Icons.Default.Language,
                                        contentDescription = null,
                                        tint = if (source.sourceType == GroundingType.GOOGLE_MAPS) MaterialTheme.colorScheme.primary else Secondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = source.title,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Model footer label
            if (message.modelUsed != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message.modelUsed,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
            }
        }
    }
}
