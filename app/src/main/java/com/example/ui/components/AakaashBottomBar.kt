package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.AakaashScreen

@Composable
fun AakaashBottomBar(
    currentScreen: AakaashScreen,
    onNavigate: (AakaashScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockItem(
                label = "DASH",
                icon = Icons.Default.Dashboard,
                isSelected = currentScreen == AakaashScreen.DASHBOARD,
                testTag = "nav_dash",
                onClick = { onNavigate(AakaashScreen.DASHBOARD) }
            )
            DockItem(
                label = "MAP",
                icon = Icons.Default.Explore,
                isSelected = currentScreen == AakaashScreen.ROUTE_MAP,
                testTag = "nav_map",
                onClick = { onNavigate(AakaashScreen.ROUTE_MAP) }
            )
            DockItem(
                label = "COPILOT",
                icon = Icons.Default.AutoAwesome,
                isSelected = currentScreen == AakaashScreen.AERO_COPILOT,
                testTag = "nav_copilot",
                onClick = { onNavigate(AakaashScreen.AERO_COPILOT) }
            )
            DockItem(
                label = "TIME",
                icon = Icons.Default.History,
                isSelected = currentScreen == AakaashScreen.TIME_MACHINE,
                testTag = "nav_time",
                onClick = { onNavigate(AakaashScreen.TIME_MACHINE) }
            )
            DockItem(
                label = "ALARMS",
                icon = Icons.Default.Alarm,
                isSelected = currentScreen == AakaashScreen.BIOSYNC_ALARMS,
                testTag = "nav_alarms",
                onClick = { onNavigate(AakaashScreen.BIOSYNC_ALARMS) }
            )
            DockItem(
                label = "CONFIG",
                icon = Icons.Default.Settings,
                isSelected = currentScreen == AakaashScreen.SETTINGS,
                testTag = "nav_config",
                onClick = { onNavigate(AakaashScreen.SETTINGS) }
            )
        }
    }
}

@Composable
private fun DockItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val bgModifier = if (isSelected) {
        Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                shape = CircleShape
            )
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), CircleShape)
    } else {
        Modifier
    }

    Column(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .then(bgModifier)
            .clickable(onClick = onClick)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}
