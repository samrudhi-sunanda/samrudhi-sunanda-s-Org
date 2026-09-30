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
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.data.model.PersonaCatalog
import com.example.ui.navigation.AakaashScreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.TertiaryContainer

@Composable
fun SettingsScreen(
    user: UserEntity?,
    windLimit: Float,
    aqiLimit: Float,
    rainLimit: Float,
    soundscapeActive: Boolean,
    isDarkTheme: Boolean = false,
    onToggleTheme: (Boolean) -> Unit,
    onToggleSoundscape: () -> Unit,
    onUpdateThresholds: (Float, Float, Float) -> Unit,
    onNavigate: (AakaashScreen) -> Unit
) {
    var windVal by remember(windLimit) { mutableStateOf(windLimit) }
    var aqiVal by remember(aqiLimit) { mutableStateOf(aqiLimit) }
    var rainVal by remember(rainLimit) { mutableStateOf(rainLimit) }
    var savedSuccess by remember { mutableStateOf(false) }

    val activePersona = remember(user?.activePersonaId) {
        PersonaCatalog.findPersona(user?.activePersonaId ?: "cyclist")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("screen_settings"),
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
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Settings & Thresholds Hub",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "SENSOR CALIBRATION & TELEMETRY RULES",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        // Appearance Theme Mode Card (Light Theme default & toggle)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
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
                                imageVector = if (isDarkTheme) Icons.Default.Nightlight else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isDarkTheme) TertiaryContainer else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Theme Appearance",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isDarkTheme) "Dark Nocturnal HUD" else "Light Oceanic Sky (Active)",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PrimaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("theme_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Light Mode Pill
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (!isDarkTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceContainerLow
                                )
                                .border(
                                    1.dp,
                                    if (!isDarkTheme) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onToggleTheme(false) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = if (!isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Light Theme",
                                    color = if (!isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Dark Mode Pill
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isDarkTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceContainerLow
                                )
                                .border(
                                    1.dp,
                                    if (isDarkTheme) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onToggleTheme(true) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Nightlight,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Dark Theme",
                                    color = if (isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Persona Calibration Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = activePersona.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = activePersona.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Calibrated for: ${activePersona.subtitle}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .clickable { onNavigate(AakaashScreen.ONBOARDING) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Change",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Custom Trigger Threshold Sliders
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CUSTOM SENSOR THRESHOLDS",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    // 1. Wind Speed Trigger Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Wind Speed Hazard Limit", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                            Text(
                                text = "${windVal.toInt()} km/h",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = windVal,
                            onValueChange = {
                                windVal = it
                                savedSuccess = false
                            },
                            valueRange = 10f..60f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // 2. AQI Hazard Limit Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "AQI Alert Trigger", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                            Text(
                                text = "${aqiVal.toInt()} AQI",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = aqiVal,
                            onValueChange = {
                                aqiVal = it
                                savedSuccess = false
                            },
                            valueRange = 30f..200f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.secondary,
                                activeTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }

                    // 3. Rain Probability Trigger Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Rain Probability Warning", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                            Text(
                                text = "${rainVal.toInt()}%",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = rainVal,
                            onValueChange = {
                                rainVal = it
                                savedSuccess = false
                            },
                            valueRange = 5f..80f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.tertiary,
                                activeTrackColor = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }

                    // Save Thresholds Button
                    Button(
                        onClick = {
                            onUpdateThresholds(windVal, aqiVal, rainVal)
                            savedSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (savedSuccess) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_thresholds_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (savedSuccess) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Thresholds Synced with Mesonet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text(text = "Save Custom Thresholds", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Saved Locations
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SAVED TELEMETRY NODES",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    SavedLocationItem(
                        title = "BLR Loop Sector 4",
                        subtitle = "Active Doppler GPS Feed • 8.4 km/h",
                        isActive = true
                    )
                    SavedLocationItem(
                        title = "Tech Corridor Terminal",
                        subtitle = "Stationary Tower Sensor 09",
                        isActive = false
                    )
                    SavedLocationItem(
                        title = "Alpine Ridge Basecamp",
                        subtitle = "Barometric Isobar Node",
                        isActive = false
                    )
                }
            }
        }

        // Soundscape & Audio Settings
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Ambient Aero Soundscape", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Generative wind & atmospheric drone audio", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = soundscapeActive,
                        onCheckedChange = { onToggleSoundscape() },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun SavedLocationItem(
    title: String,
    subtitle: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (isActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            }
        }
        if (isActive) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = "ACTIVE", color = MaterialTheme.colorScheme.secondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
