package com.example.ui.components.bento

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorder
import java.util.Locale

/**
 * Adaptive Bento-style Environmental Summary Card.
 * Combines Temperature, Humidity, and Air Quality into an overall
 * Atmospheric Comfort Index (0-100) with quick micro-indicators.
 */
@Composable
fun EnvironmentalSummaryBentoCard(
    telemetry: EnvironmentalTelemetry,
    modifier: Modifier = Modifier,
    onSimulateTelemetry: (() -> Unit)? = null
) {
    val comfortScore = telemetry.comfortIndexScore
    val animatedScore by animateFloatAsState(
        targetValue = comfortScore.toFloat(),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "comfort_anim"
    )

    val scoreColor = when {
        comfortScore >= 80 -> Color(0xFF00E676)
        comfortScore >= 60 -> Color(0xFF26C6DA)
        comfortScore >= 40 -> Color(0xFFFFB74D)
        else -> Color(0xFFFF5252)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.90f),
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f)
                    )
                )
            )
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .clickable(enabled = onSimulateTelemetry != null) { onSimulateTelemetry?.invoke() }
            .padding(16.dp)
            .testTag("environmental_summary_bento_card")
            .semantics {
                contentDescription = "Atmospheric Comfort Index score $comfortScore out of 100"
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(scoreColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Sensor",
                            tint = scoreColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ATMOSPHERIC COMFORT INDEX",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Composite Telemetry Analysis",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .background(scoreColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = scoreColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when {
                            comfortScore >= 80 -> "Prime Conditions"
                            comfortScore >= 60 -> "Favorable"
                            comfortScore >= 40 -> "Moderate Stress"
                            else -> "Adverse"
                        },
                        color = scoreColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body: Score Display + 3-Pill Matrix
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format(Locale.US, "%.0f", animatedScore),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "/100",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                    )
                }

                // 3 Live Metric Micro-Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricChip(
                        label = "TEMP",
                        value = "${telemetry.temperatureCelsius.toInt()}°C",
                        dotColor = Color(0xFF00E5FF)
                    )
                    MetricChip(
                        label = "HUMID",
                        value = "${telemetry.humidityPct}%",
                        dotColor = Color(0xFF26C6DA)
                    )
                    MetricChip(
                        label = "AQI",
                        value = "${telemetry.aqi}",
                        dotColor = telemetry.aqiLevel.color
                    )
                }
            }

            if (onSimulateTelemetry != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tap to cycle dynamic micro-climate telemetry simulation",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    dotColor: Color
) {
    Column(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(5.dp).background(dotColor, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
