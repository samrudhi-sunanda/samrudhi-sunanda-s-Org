package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.CachedWeatherEntity
import com.example.data.local.UserEntity
import com.example.data.model.PersonaCatalog
import com.example.ui.components.ElevationSparkline
import com.example.ui.components.RadialWindDial
import com.example.ui.navigation.AakaashScreen
import com.example.ui.theme.AmberHazard
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import com.example.ui.components.bento.AdaptiveBentoTelemetryGrid
import com.example.ui.components.bento.toEnvironmentalTelemetry
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryContainer

@Composable
fun DashboardScreen(
    user: UserEntity?,
    weather: CachedWeatherEntity?,
    soundscapeActive: Boolean,
    isAdvisoryDismissed: Boolean,
    isAlarmShiftApplied: Boolean,
    onToggleSoundscape: () -> Unit,
    onDismissAdvisory: () -> Unit,
    onApplyAlarmShift: () -> Unit,
    onSelectPersona: (String) -> Unit,
    onNavigate: (AakaashScreen) -> Unit
) {
    val activePersonaId = user?.activePersonaId ?: "cyclist"
    val activeProfile = remember(activePersonaId) { PersonaCatalog.findPersona(activePersonaId) }
    var showPersonaModal by remember { mutableStateOf(false) }

    val safeWeather = weather ?: CachedWeatherEntity()

    val infiniteTransition = rememberInfiniteTransition(label = "audioEq")
    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 4f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar1"
    )
    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 12f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar2"
    )
    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar3"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp)
            .testTag("screen_dashboard"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Header: Mausam + Live Indicator + User Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_aakaash_logo),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Mausam",
                                color = Primary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(SecondaryContainer, CircleShape)
                            )
                        }
                        Text(
                            text = "DASHBOARD",
                            color = OnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier
                            .background(SurfaceContainerHigh.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            color = Secondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Primary, CircleShape)
                            .clickable { onNavigate(AakaashScreen.SETTINGS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = OnPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Micro-Status Header Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Floating Persona Switcher Pill
                Row(
                    modifier = Modifier
                        .background(SurfaceContainerHigh.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0x2200D2FF), RoundedCornerShape(20.dp))
                        .clickable { showPersonaModal = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("persona_switcher_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = activeProfile.emoji, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${activeProfile.title.uppercase()} MODE",
                        color = OnSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Switch Persona",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // BLR Loop route status
                    Row(
                        modifier = Modifier
                            .background(SurfaceContainerHigh.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .clickable { onNavigate(AakaashScreen.ROUTE_MAP) }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "BLR Loop • 8.4 km/h",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Notification Alarm Bell
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(SurfaceContainerHigh.copy(alpha = 0.7f), CircleShape)
                            .clickable { onNavigate(AakaashScreen.BIOSYNC_ALARMS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alerts",
                            tint = OnSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(6.dp)
                                .background(TertiaryContainer, CircleShape)
                        )
                    }
                }
            }
        }

        // Primary Bento Widget: Kinetic Wind Telemetry
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SurfaceContainer.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, Color(0x3300D2FF), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = PrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KINETIC WIND TELEMETRY",
                                color = OnSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // Aero Soundscape Pill with Audio Equalizer Animation
                        Row(
                            modifier = Modifier
                                .background(
                                    SurfaceContainerHighest.copy(alpha = 0.8f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable(onClick = onToggleSoundscape)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (soundscapeActive) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.height(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(bar1Height.dp)
                                            .background(Primary, RoundedCornerShape(1.dp))
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(bar2Height.dp)
                                            .background(Primary, RoundedCornerShape(1.dp))
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(bar3Height.dp)
                                            .background(Primary, RoundedCornerShape(1.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Aero Focus ♫",
                                    color = OnSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = "Soundscape Off",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    RadialWindDial(weather = safeWeather)
                }
            }
        }

        // AI Tactical Copilot Advisory Card
        item {
            AnimatedVisibility(
                visible = !isAdvisoryDismissed,
                enter = fadeIn(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = SurfaceContainerHigh.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(1.dp, AmberHazard.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TertiaryContainer.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = TertiaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TACTICAL ROUTE COPILOT",
                                    color = Tertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Just Now",
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Strong crosswinds (32 km/h) forecasted on orbital Miles 3 to 6. Shifting departure unlocks a continuous +14% tailwind vector.",
                                color = OnSurface,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (isAlarmShiftApplied) {
                                    Row(
                                        modifier = Modifier
                                            .background(Secondary, RoundedCornerShape(16.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = OnPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Alarm set for 06:20 AM",
                                            color = OnPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = onApplyAlarmShift,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryContainer,
                                            contentColor = OnPrimaryContainer
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AlarmAdd,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Shift Alarm -25 min",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onNavigate(AakaashScreen.AERO_COPILOT) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Ask Copilot",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                TextButton(
                                    onClick = onDismissAdvisory,
                                    colors = ButtonDefaults.textButtonColors(contentColor = OnSurfaceVariant),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(text = "Dismiss", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Adaptive Bento Environmental Telemetry Suite (Temperature, Humidity, AQI, Comfort Index)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ENVIRONMENTAL TELEMETRY",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "ADAPTIVE BENTO SUITE",
                        color = Primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                AdaptiveBentoTelemetryGrid(
                    telemetry = safeWeather.toEnvironmentalTelemetry(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Secondary Micro-Metrics: Solar Rad & Barometer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Solar Rad
                    BentoCard(
                        title = "SOLAR RAD",
                        icon = Icons.Default.WbSunny,
                        iconTint = TertiaryContainer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${safeWeather.uvIndex}",
                                    color = OnSurface,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "UV",
                                    color = Tertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                            Text(
                                text = safeWeather.uvRating,
                                color = Tertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerHighest.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Tinted lenses", color = OnSurfaceVariant, fontSize = 10.sp)
                            Text(text = "UV-A/B", color = Tertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Barometer
                    BentoCard(
                        title = "BAROMETER",
                        icon = Icons.Default.Speed,
                        iconTint = Primary,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${safeWeather.pressureHpa}",
                                    color = OnSurface,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "hPa",
                                    color = Primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingFlat,
                                    contentDescription = null,
                                    tint = Secondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = safeWeather.pressureTendency,
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerHighest.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Storm cell risk", color = OnSurfaceVariant, fontSize = 10.sp)
                            Text(text = safeWeather.stormCellRisk, color = Secondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active Elevation & Headwind Map Card
        item {
            ElevationSparkline(
                currentKm = 8.4f,
                altitudeM = 920,
                totalKm = 22.4f,
                modifier = Modifier.clickable { onNavigate(AakaashScreen.ROUTE_MAP) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(84.dp)) // padding for bottom floating bar
        }
    }

    // Persona Selection Dialog
    if (showPersonaModal) {
        PersonaSwitcherDialog(
            activePersonaId = activePersonaId,
            onSelect = {
                onSelectPersona(it)
                showPersonaModal = false
            },
            onDismiss = { showPersonaModal = false }
        )
    }
}

@Composable
private fun BentoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(SurfaceContainer.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0x1F00D2FF), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = OnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            content()
        }
    }
}

@Composable
private fun PersonaSwitcherDialog(
    activePersonaId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Switch Telemetry Persona",
                color = OnSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PersonaCatalog.categories.forEach { category ->
                    item {
                        Text(
                            text = category.title.uppercase(),
                            color = Primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(category.personas.size) { idx ->
                        val p = category.personas[idx]
                        val isSelected = p.id == activePersonaId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryContainer.copy(alpha = 0.2f) else SurfaceContainer)
                                .clickable { onSelect(p.id) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = p.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = p.title,
                                    color = if (isSelected) Primary else OnSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = p.subtitle,
                                    color = OnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close", color = Primary)
            }
        },
        containerColor = SurfaceContainerHigh,
        shape = RoundedCornerShape(16.dp)
    )
}
