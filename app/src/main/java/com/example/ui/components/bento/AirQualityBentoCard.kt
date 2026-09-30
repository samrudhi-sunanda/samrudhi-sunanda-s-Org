package com.example.ui.components.bento

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorder
import java.util.Locale

/**
 * Adaptive Bento-style Air Quality Index (AQI) Telemetry Card.
 * Displays dynamic animated AQI score, EPA color-coded radial arc,
 * particulate matter bars (PM2.5 and PM10), and pulmonary activity guidance.
 */
@Composable
fun AirQualityBentoCard(
    telemetry: EnvironmentalTelemetry,
    modifier: Modifier = Modifier
) {
    val aqiLevel = telemetry.aqiLevel
    val animatedAqi by animateFloatAsState(
        targetValue = telemetry.aqi.toFloat(),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "aqi_anim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.90f),
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f)
                    )
                )
            )
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("air_quality_bento_card")
            .semantics {
                contentDescription = "Air Quality card displaying AQI ${telemetry.aqi}, rating ${aqiLevel.label}"
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Icon + Title + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(aqiLevel.color.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = "Air Quality",
                            tint = aqiLevel.color,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "AIR QUALITY",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "EPA AQI Standard",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                    }
                }

                // Dynamic Live Pulse Pill
                Row(
                    modifier = Modifier
                        .background(aqiLevel.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(aqiLevel.color.copy(alpha = pulseAlpha), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = aqiLevel.label,
                        color = aqiLevel.color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body: Circular Arc Dial + Particulate Matter (PM2.5 & PM10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AQI Arc Gauge
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(64.dp)) {
                        val strokeWidth = 5.dp.toPx()
                        // Track
                        drawCircle(
                            color = Color(0xFF19374F).copy(alpha = 0.5f),
                            radius = (size.width - strokeWidth) / 2,
                            style = Stroke(width = strokeWidth)
                        )
                        // Sweeping progress arc (normalized to max 300 AQI)
                        val sweep = (animatedAqi / 300f * 360f).coerceIn(25f, 340f)
                        drawArc(
                            color = aqiLevel.color,
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${telemetry.aqi}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AQI",
                            color = aqiLevel.color,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Particulate Matter Breakdown
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // PM2.5 Micro-track
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "PM2.5", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            Text(
                                text = String.format(Locale.US, "%.0f µg/m³", telemetry.pm25),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        ) {
                            val pm25Progress = (telemetry.pm25 / 50f).coerceIn(0.05f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pm25Progress)
                                    .fillMaxHeight()
                                    .background(aqiLevel.color, RoundedCornerShape(2.dp))
                            )
                        }
                    }

                    // PM10 Micro-track
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "PM10", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            Text(
                                text = String.format(Locale.US, "%.0f µg/m³", telemetry.pm10),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        ) {
                            val pm10Progress = (telemetry.pm10 / 100f).coerceIn(0.05f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pm10Progress)
                                    .fillMaxHeight()
                                    .background(Color(0xFF00E5FF), RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Strip: Pulmonary Strain & Advice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lungs Exertion",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
                Text(
                    text = when {
                        telemetry.aqi <= 50 -> "Optimal for Cardio"
                        telemetry.aqi <= 100 -> "Normal Strain"
                        else -> "Mask Recommended"
                    },
                    color = aqiLevel.color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
