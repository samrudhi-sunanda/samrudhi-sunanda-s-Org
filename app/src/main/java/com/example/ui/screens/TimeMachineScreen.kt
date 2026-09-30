package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CachedWeatherEntity
import com.example.ui.components.RadialWindDial
import com.example.ui.navigation.AakaashScreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Secondary
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryContainer

@Composable
fun TimeMachineScreen(
    currentHourOffset: Int,
    weather: CachedWeatherEntity?,
    onScrubHour: (Int) -> Unit,
    onNavigate: (AakaashScreen) -> Unit
) {
    val safeWeather = weather ?: CachedWeatherEntity()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp)
            .testTag("screen_time_machine"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = PrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Persona Time-Machine",
                            color = Primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "48-HOUR ATMOSPHERIC TIMELINE SIMULATOR",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                // Reset to Live
                IconButton(
                    onClick = { onScrubHour(0) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(SurfaceContainerHigh, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset to Live",
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Timeline Scrubber HUD Panel
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainer.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0x3300D2FF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (currentHourOffset == 0) "LIVE NOW" else "FORECAST SIMULATION",
                                color = if (currentHourOffset == 0) Secondary else TertiaryContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (currentHourOffset == 0) "T + 0 Hours (Real-time)" else "T + $currentHourOffset Hours Ahead",
                                color = OnSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(SurfaceContainerHighest, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = when (currentHourOffset % 24) {
                                    in 6..18 -> "☀️ Daylight Phase"
                                    in 19..21 -> "🌅 Sunset Dusk"
                                    else -> "🌙 Tropospheric Night"
                                },
                                color = Primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 48-Hour Slider
                    Slider(
                        value = currentHourOffset.toFloat(),
                        onValueChange = { onScrubHour(it.toInt()) },
                        valueRange = 0f..48f,
                        steps = 47,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryContainer,
                            activeTrackColor = CyanGlow,
                            inactiveTrackColor = SurfaceContainerHighest
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("time_machine_slider")
                    )

                    // Scrubber Quick Jump Shortcuts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(0, 6, 12, 24, 36, 48).forEach { hour ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (currentHourOffset == hour) PrimaryContainer.copy(alpha = 0.25f)
                                        else SurfaceContainerLow
                                    )
                                    .clickable { onScrubHour(hour) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (hour == 0) "NOW" else "+${hour}h",
                                    color = if (currentHourOffset == hour) Primary else OnSurfaceVariant,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Morphing Bento Telemetry Preview
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainer.copy(alpha = 0.75f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0x2200D2FF), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MORPHED BENTO TELEMETRY",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = null,
                                tint = PrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Synthetic Doppler Model",
                                color = Primary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    RadialWindDial(weather = safeWeather)
                }
            }
        }

        // Morphing Weather Trend Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ForecastMetricCard(
                    title = "CROSSWIND RISK",
                    value = if (safeWeather.gustSpeedKmh > 36) "Severe Gusts" else "Calm Vector",
                    subtext = "${safeWeather.gustSpeedKmh} km/h max",
                    isHazard = safeWeather.gustSpeedKmh > 36,
                    modifier = Modifier.weight(1f)
                )
                ForecastMetricCard(
                    title = "PRECIPITATION",
                    value = "${safeWeather.precipWindowPct}% Influx",
                    subtext = safeWeather.precipSummary,
                    isHazard = safeWeather.precipWindowPct > 30,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Button(
                onClick = { onNavigate(AakaashScreen.DASHBOARD) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = OnPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Apply Simulated Forecast to Dashboard",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun ForecastMetricCard(
    title: String,
    value: String,
    subtext: String,
    isHazard: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceContainer.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isHazard) TertiaryContainer.copy(alpha = 0.4f) else Color(0x1F00D2FF),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                color = OnSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = if (isHazard) TertiaryContainer else Secondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = OnSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}
