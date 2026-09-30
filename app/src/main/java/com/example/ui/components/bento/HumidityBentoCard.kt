package com.example.ui.components.bento

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
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
 * Adaptive Bento-style Humidity & Moisture Telemetry Card.
 * Displays dynamic animated humidity percentage, circular arc gauge,
 * calculated dew point, and vapor comfort indicators.
 */
@Composable
fun HumidityBentoCard(
    telemetry: EnvironmentalTelemetry,
    modifier: Modifier = Modifier
) {
    val animatedHumidity by animateFloatAsState(
        targetValue = telemetry.humidityPct.toFloat(),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "humidity_anim"
    )

    val humidityColor = when {
        telemetry.humidityPct < 35 -> Color(0xFFFFB74D) // dry
        telemetry.humidityPct in 35..65 -> Color(0xFF26C6DA) // ideal
        telemetry.humidityPct in 66..80 -> Color(0xFF29B6F6) // humid
        else -> Color(0xFF7E57C2) // heavy saturation
    }

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
            .testTag("humidity_bento_card")
            .semantics {
                contentDescription = "Humidity card displaying ${telemetry.humidityPct}% relative humidity"
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Metric Title + Sensor Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(humidityColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Humidity",
                            tint = humidityColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "HUMIDITY",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Relative Moisture",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                    }
                }

                // Status Chip
                Box(
                    modifier = Modifier
                        .background(humidityColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = telemetry.humidityStatus,
                        color = humidityColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body Row: Circular Gauge & Metric Value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dial Gauge with Center Icon
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(64.dp)) {
                        val strokeWidth = 5.dp.toPx()
                        // Background track
                        drawCircle(
                            color = Color(0xFF19374F).copy(alpha = 0.5f),
                            radius = (size.width - strokeWidth) / 2,
                            style = Stroke(width = strokeWidth)
                        )
                        // Sweeping progress arc
                        val sweep = (animatedHumidity / 100f * 280f).coerceIn(10f, 280f)
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFF00E5FF),
                                    humidityColor,
                                    humidityColor
                                )
                            ),
                            startAngle = 130f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${telemetry.humidityPct}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "%",
                            color = humidityColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Metric Breakdown (Dew Point + Vapor comfort)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dew Point",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f°C", telemetry.dewPointCelsius),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            telemetry.dewPointCelsius < 10f -> "Very dry & crisp"
                            telemetry.dewPointCelsius in 10f..16f -> "Comfortable breathing"
                            telemetry.dewPointCelsius in 16f..21f -> "Slightly muggy"
                            else -> "Oppressive moisture"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Strip: Condensation & Evaporation baseline
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
                    text = "Condensation Risk",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
                Text(
                    text = if (telemetry.humidityPct > 80) "Elevated" else "Minimal",
                    color = if (telemetry.humidityPct > 80) Color(0xFFFF9E1B) else Color(0xFF00E676),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
