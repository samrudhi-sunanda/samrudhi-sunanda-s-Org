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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BioSyncAlarmEntity
import com.example.data.local.UserEntity
import com.example.ui.navigation.AakaashScreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.OnPrimary
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
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryContainer

@Composable
fun BioSyncAlarmScreen(
    user: UserEntity?,
    alarms: List<BioSyncAlarmEntity>,
    onApplyShift: () -> Unit,
    onNavigate: (AakaashScreen) -> Unit
) {
    var nightBeforeEnabled by remember { mutableStateOf(true) }
    var earlyMorningScanEnabled by remember { mutableStateOf(true) }
    var shiftApplied by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp)
            .testTag("screen_biosync_alarms"),
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
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = TertiaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bio-Sync Alarms Hub",
                            color = Primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "CIRCADIAN ROUTINE PREDICTIVE WEATHER ENGINE",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(SurfaceContainerHigh, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SYNCHRONIZED",
                        color = Secondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Circadian Sleep & Departure Rhythm
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainer.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0x3300D2FF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "CIRCADIAN SLEEP-WAKE MATRIX",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Bedtime Target
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bedtime,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Bedtime Routine", color = OnSurfaceVariant, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = user?.sleepTime ?: "22:30",
                                    color = OnSurface,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Night alert @ 21:00", color = Secondary, fontSize = 10.sp)
                            }
                        }

                        // Wakeup Target
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = TertiaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Target Wakeup", color = OnSurfaceVariant, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (shiftApplied) "06:20 AM" else "06:45 AM",
                                    color = if (shiftApplied) Secondary else OnSurface,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (shiftApplied) "Shifted -25m" else "Scheduled",
                                    color = if (shiftApplied) Secondary else OnSurfaceVariant,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Night-Before Predictive Advisory Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerHigh.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                    .border(1.dp, TertiaryContainer.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(TertiaryContainer.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = TertiaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NIGHT-BEFORE PREVENTATIVE ADVISORY",
                                color = TertiaryContainer,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(TertiaryContainer.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "HIGH IMPACT", color = TertiaryContainer, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Crosswinds peaking at 34 km/h are forecasted along Sector 4 at 07:00 tomorrow morning. Bio-sync models recommend shifting wake-up to 06:20 AM to capture calm tailwinds.",
                        color = OnSurface,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (shiftApplied) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Secondary.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Departure window shifted to 06:20 AM. Tailwind unlocked.",
                                color = Secondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                shiftApplied = true
                                onApplyShift()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryContainer,
                                contentColor = OnPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("apply_alarm_shift_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AlarmAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Authorize 25-Min Alarm Shift", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Preventative Settings Toggles
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainer.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Night-Before Predictive Alerts", color = OnSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Scans tomorrow's telemetry at 21:00", color = OnSurfaceVariant, fontSize = 11.sp)
                        }
                        Switch(
                            checked = nightBeforeEnabled,
                            onCheckedChange = { nightBeforeEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryContainer)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Early Morning Doppler Radar Scan", color = OnSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Pre-wakes 45m before alarm for sudden squalls", color = OnSurfaceVariant, fontSize = 11.sp)
                        }
                        Switch(
                            checked = earlyMorningScanEnabled,
                            onCheckedChange = { earlyMorningScanEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryContainer)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
