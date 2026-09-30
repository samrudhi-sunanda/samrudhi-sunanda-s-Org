package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.gemini.GeminiModels
import com.example.data.gemini.LiveSessionState
import com.example.data.gemini.LiveTranscriptTurn
import com.example.ui.theme.AmberHazard
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiLiveDialog(
    sessionState: LiveSessionState,
    statusMessage: String,
    transcripts: List<LiveTranscriptTurn>,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "liveWaveform")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 12f, targetValue = 48f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 40f, targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 16f, targetValue = 54f,
        animationSpec = infiniteRepeatable(tween(510, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 36f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(360, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w4"
    )

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .testTag("gemini_live_dialog"),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Gemini 3.8 Live API",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Secondary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        color = Secondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Real-time Voice Conversation",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pulsing Kinetic Soundwave Orb
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        val active = sessionState == LiveSessionState.SPEAKING || sessionState == LiveSessionState.LISTENING
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(if (active) wave1.dp else 12.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                        )
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(if (active) wave2.dp else 18.dp)
                                .background(CyanGlow, RoundedCornerShape(3.dp))
                        )
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(if (active) wave3.dp else 24.dp)
                                .background(Secondary, RoundedCornerShape(3.dp))
                        )
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(if (active) wave4.dp else 14.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status Pill
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                when (sessionState) {
                                    LiveSessionState.CONNECTED -> Secondary
                                    LiveSessionState.SPEAKING -> MaterialTheme.colorScheme.primary
                                    LiveSessionState.LISTENING -> Secondary
                                    LiveSessionState.CONNECTING -> AmberHazard
                                    else -> MaterialTheme.colorScheme.error
                                },
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusMessage,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time Transcript Scroll
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    if (transcripts.isEmpty()) {
                        Text(
                            text = "Live conversation stream will appear here in real-time. Speak into microphone or type below.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            reverseLayout = true
                        ) {
                            items(transcripts.reversed()) { turn ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (turn.sender == "You") Arrangement.End else Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (turn.sender == "You") MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.surfaceContainerHighest
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = turn.sender,
                                                color = if (turn.sender == "You") MaterialTheme.colorScheme.primary else Secondary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = turn.text,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chat text input for Live API session
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Speak or type to Live API...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("live_chat_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText.trim())
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .testTag("send_live_text_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
