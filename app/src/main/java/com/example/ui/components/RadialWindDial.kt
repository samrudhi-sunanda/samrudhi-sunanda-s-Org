package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CachedWeatherEntity
import com.example.ui.theme.AmberHazard
import com.example.ui.theme.TertiaryContainer

@Composable
fun RadialWindDial(
    weather: CachedWeatherEntity,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val trackBgColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val dottedRingColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryTeal = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Radial Compass Vector Gauge
        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(210.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val outerRadius = size.width / 2 - 8.dp.toPx()
                val mainRadius = size.width / 2 - 20.dp.toPx()
                val gustRadius = size.width / 2 - 12.dp.toPx()

                // Outer atmospheric dotted tick track
                drawCircle(
                    color = dottedRingColor,
                    radius = outerRadius,
                    center = center,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 10f), 0f)
                    )
                )

                // Track background
                drawCircle(
                    color = trackBgColor,
                    radius = mainRadius,
                    center = center,
                    style = Stroke(width = 8.dp.toPx())
                )

                // Steady wind arc
                val windFraction = (weather.windSpeedKmh / 50f).coerceIn(0.15f, 0.95f)
                val sweepAngle = 260f * windFraction
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(primaryCyan, secondaryTeal)
                    ),
                    startAngle = 140f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - mainRadius, center.y - mainRadius),
                    size = Size(mainRadius * 2, mainRadius * 2),
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )

                // Gust arc (amber)
                val gustFraction = (weather.gustSpeedKmh / 60f).coerceIn(0.2f, 0.9f)
                val gustSweep = 160f * gustFraction
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(AmberHazard, AmberHazard.copy(alpha = 0.7f))
                    ),
                    startAngle = 200f,
                    sweepAngle = gustSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - gustRadius, center.y - gustRadius),
                    size = Size(gustRadius * 2, gustRadius * 2),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Cardinal markings
            Box(modifier = Modifier.size(200.dp)) {
                Text(
                    text = "N",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                Text(
                    text = "S",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                Text(
                    text = "W",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "E",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Center HUD Telemetry Value
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Wind Direction",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(weather.windDirectionDeg.toFloat() - 45f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${weather.windDirectionCardinal} ${weather.windDirectionDeg}°",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${weather.windSpeedKmh}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 44.sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "KM/H",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Headwind Drag pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Aero Drag",
                        tint = tertiaryColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${weather.aeroDragPct}% Aero Drag",
                        color = tertiaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Kinetic Gust Indicator Pill Alert
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = AmberHazard.copy(alpha = pulseAlpha),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AmberHazard,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Max Gusts to ",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${weather.gustSpeedKmh} km/h",
                color = AmberHazard,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dual Sub-Meters (Precip Window & Sight Range)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Precip Window
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = secondaryTeal,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PRECIP WINDOW",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "${weather.precipWindowPct}%",
                        color = secondaryTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((weather.precipWindowPct / 100f).coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(secondaryTeal, RoundedCornerShape(2.dp))
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = weather.precipSummary,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Road Visibility
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = primaryCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SIGHT RANGE",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "${weather.sightRangeKm} km",
                        color = primaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((weather.sightRangeKm / 10f).coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(primaryCyan, RoundedCornerShape(2.dp))
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (weather.sightRangeKm > 7f) "Optimal solar clarity" else "Reduced atmospheric range",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}
