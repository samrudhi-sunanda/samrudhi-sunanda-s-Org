package com.example.ui.components.bento

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PrimaryContainer
import java.util.Locale

/**
 * Adaptive Bento-style Temperature Telemetry Card.
 * Displays dynamic animated temperature, unit conversion (°C/°F), feels-like delta,
 * comfort status badge, and an integrated canvas sparkline trajectory.
 */
@Composable
fun TemperatureBentoCard(
    telemetry: EnvironmentalTelemetry,
    modifier: Modifier = Modifier,
    unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    onToggleUnit: (() -> Unit)? = null
) {
    var internalUnit by remember { mutableStateOf(unit) }
    val activeUnit = onToggleUnit?.let { unit } ?: internalUnit

    val displayTemp = activeUnit.fromCelsius(telemetry.temperatureCelsius)
    val displayFeelsLike = activeUnit.fromCelsius(telemetry.feelsLikeCelsius)
    val displayMin = activeUnit.fromCelsius(telemetry.tempMinCelsius)
    val displayMax = activeUnit.fromCelsius(telemetry.tempMaxCelsius)

    val animatedTemp by animateFloatAsState(
        targetValue = displayTemp,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "temp_anim"
    )

    val deltaFeelsLike = displayFeelsLike - displayTemp
    val tempAccentColor by animateColorAsState(
        targetValue = when {
            telemetry.temperatureCelsius > 32f -> Color(0xFFFF5252)
            telemetry.temperatureCelsius > 24f -> Color(0xFFFFAB40)
            telemetry.temperatureCelsius in 18f..24f -> Color(0xFF00E5FF)
            else -> Color(0xFF80D8FF)
        },
        label = "temp_color"
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
            .testTag("temperature_bento_card")
            .semantics {
                contentDescription = "Temperature card displaying ${String.format(Locale.US, "%.1f", displayTemp)}${activeUnit.symbol}"
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Metric Title + Unit Selector Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(tempAccentColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeviceThermostat,
                            contentDescription = "Thermostat",
                            tint = tempAccentColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "TEMPERATURE",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Live Ambient Sensor",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                    }
                }

                // Interactive Unit Switcher Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8f))
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            if (onToggleUnit != null) onToggleUnit() else {
                                internalUnit = if (internalUnit == TemperatureUnit.CELSIUS) {
                                    TemperatureUnit.FAHRENHEIT
                                } else {
                                    TemperatureUnit.CELSIUS
                                }
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "°C",
                        color = if (activeUnit == TemperatureUnit.CELSIUS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = if (activeUnit == TemperatureUnit.CELSIUS) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = " / ",
                        color = MaterialTheme.colorScheme.outlineVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "°F",
                        color = if (activeUnit == TemperatureUnit.FAHRENHEIT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = if (activeUnit == TemperatureUnit.FAHRENHEIT) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Metric Body: Large Value + Sparkline Trajectory
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    AnimatedContent(
                        targetState = activeUnit,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                        label = "unit_text_anim"
                    ) { targetUnit ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = String.format(Locale.US, "%.1f", animatedTemp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = targetUnit.symbol,
                                color = tempAccentColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp, start = 2.dp)
                            )
                        }
                    }

                    // Feels-like difference badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Feels like ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f%s", displayFeelsLike, activeUnit.symbol),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (kotlin.math.abs(deltaFeelsLike) >= 0.5f) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (deltaFeelsLike > 0) "(+${String.format(Locale.US, "%.1f", deltaFeelsLike)}°)" else "(${String.format(Locale.US, "%.1f", deltaFeelsLike)}°)",
                                color = if (deltaFeelsLike > 0) Color(0xFFFF9E1B) else Color(0xFF00D2FF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Mini Canvas Temperature Trajectory Sparkline
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(44.dp)
                        .padding(bottom = 4.dp)
                ) {
                    TemperatureSparklineCanvas(
                        points = telemetry.hourlyTemperaturesCelsius,
                        accentColor = tempAccentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Strip: Range Pill + Comfort Assessment
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Range: ${String.format(Locale.US, "%.0f", displayMin)}° - ${String.format(Locale.US, "%.0f", displayMax)}°",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = when {
                        telemetry.temperatureCelsius in 19f..26f -> "Optimal Comfort"
                        telemetry.temperatureCelsius > 26f -> "Warm Isobar"
                        else -> "Cool Exertion"
                    },
                    color = tempAccentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TemperatureSparklineCanvas(
    points: List<Float>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(110.dp, 44.dp)) {
        if (points.isEmpty()) return@Canvas
        val min = points.minOrNull() ?: 0f
        val max = points.maxOrNull() ?: 1f
        val range = (max - min).coerceAtLeast(1f)

        val w = size.width
        val h = size.height
        val stepX = w / (points.size - 1).coerceAtLeast(1)

        val path = Path()
        val fillPath = Path()

        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = 1f - ((value - min) / range)
            val y = normalizedY * (h - 12f) + 6f

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(w, h)
        fillPath.close()

        // Gradient fill under curve
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = h
            )
        )

        // Stroke line
        drawPath(
            path = path,
            color = accentColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Pulsing end dot on the latest reading
        val lastX = (points.size - 1) * stepX
        val lastNormalizedY = 1f - ((points.last() - min) / range)
        val lastY = lastNormalizedY * (h - 12f) + 6f

        drawCircle(
            color = accentColor.copy(alpha = 0.35f),
            radius = 5.dp.toPx(),
            center = Offset(lastX, lastY)
        )
        drawCircle(
            color = Color.White,
            radius = 2.5.dp.toPx(),
            center = Offset(lastX, lastY)
        )
    }
}
