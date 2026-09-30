package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.RouteEntity
import com.example.ui.components.RouteHazardCanvas
import com.example.ui.navigation.AakaashScreen
import com.example.ui.theme.AmberHazard
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.Error
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceBright
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryContainer

@Composable
fun RouteMapScreen(
    routes: List<RouteEntity>,
    activeLayer: String,
    isEcoBypassActive: Boolean,
    onLayerSelected: (String) -> Unit,
    onAutoRecalculateRoute: () -> Unit,
    onApplyDepartureShift: () -> Unit,
    onNavigate: (AakaashScreen) -> Unit
) {
    var selectedRouteId by remember(isEcoBypassActive) {
        mutableStateOf(if (isEcoBypassActive) "eco_bypass" else "alpha_direct")
    }

    var isRecalculating by remember { mutableStateOf(false) }
    var departureShiftApplied by remember { mutableStateOf(false) }

    val syncRotation by animateFloatAsState(
        targetValue = if (isRecalculating) 360f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "syncRotation"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .testTag("screen_route_map")
    ) {
        item {
            // App Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_aakaash_logo),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
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
                            text = "ROUTE MAP",
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

            // Sub-header Bar: Route Active Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AltRoute,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Route Hazard Tracker",
                            color = Primary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(SecondaryContainer, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SECTOR 4 → TECH CORRIDOR (14.2 KM)",
                            color = OnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                IconButton(
                    onClick = {
                        selectedRouteId = if (selectedRouteId == "alpha_direct") "eco_bypass" else "alpha_direct"
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(SurfaceContainerHigh, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch Route",
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Map HUD Canvas
            RouteHazardCanvas(
                activeLayer = activeLayer,
                onLayerSelected = onLayerSelected,
                isEcoBypassActive = isEcoBypassActive || selectedRouteId == "eco_bypass",
                onRecenterClicked = { /* recentered */ }
            )

            // Tactile Bottom Sheet Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SurfaceContainer.copy(alpha = 0.96f),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Grab Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(width = 36.dp, height = 4.dp)
                            .background(OnSurfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    )

                    // Aero-Intelligence Dispatch Alert Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerHigh.copy(alpha = 0.9f), RoundedCornerShape(14.dp))
                            .border(1.dp, TertiaryContainer.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(TertiaryContainer.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = TertiaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "AERO-INTELLIGENCE DISPATCH",
                                        color = TertiaryContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(TertiaryContainer, CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Hazard window detected at Mile 3 bottleneck. Shift departure by 25 mins to allow crosswinds to settle to safe levels (14 km/h).",
                                    color = OnSurface,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .background(Primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .clickable { onNavigate(AakaashScreen.AERO_COPILOT) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Query Maps Grounding & Copilot",
                                        color = Primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Analyzed Paths Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ANALYZED FLIGHT & GROUND PATHS",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "2 Options Found",
                            color = Primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Option A: Path Alpha (Direct)
                    val isAlphaSelected = selectedRouteId == "alpha_direct"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isAlphaSelected) SurfaceContainerHighest.copy(alpha = 0.7f)
                                else SurfaceContainerHigh.copy(alpha = 0.4f)
                            )
                            .border(
                                1.dp,
                                if (isAlphaSelected) AmberHazard.copy(alpha = 0.4f) else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedRouteId = "alpha_direct" }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(TertiaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PriorityHigh,
                                    contentDescription = null,
                                    tint = SurfaceDark,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Path Alpha (Direct)",
                                        color = OnSurface,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(ErrorContainer, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "1 HAZARD",
                                            color = OnSurface,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "14.2 km • 38 min • Wind shear alert at Mile 3",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "38",
                                    color = OnSurface,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "m",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "CURRENT",
                                color = Tertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Option B: Eco-Bypass Vector
                    val isEcoSelected = selectedRouteId == "eco_bypass"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isEcoSelected) SurfaceBright.copy(alpha = 0.8f)
                                else SurfaceContainerHigh.copy(alpha = 0.4f)
                            )
                            .border(
                                1.dp,
                                if (isEcoSelected) Secondary else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedRouteId = "eco_bypass" }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(SecondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SurfaceDark,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Eco-Bypass Vector",
                                        color = Primary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(SecondaryContainer.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "100% GREEN",
                                            color = Secondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "15.1 km • 41 min • Zero exposure (+3 mins)",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "41",
                                    color = Secondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "m",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "RECOMMENDED",
                                color = Secondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Micro-Climate Telemetry Cluster
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricClusterCard(
                            label = "ROUTE AQI",
                            value = "38 AVG",
                            subtext = "Good Zone",
                            valueColor = Secondary,
                            subtextColor = OnSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        MetricClusterCard(
                            label = "PEAK CROSS",
                            value = "34 km/h",
                            subtext = "Mile 3 Spike",
                            valueColor = TertiaryContainer,
                            subtextColor = Error,
                            modifier = Modifier.weight(1f)
                        )
                        MetricClusterCard(
                            label = "CONFIDENCE",
                            value = "96.4%",
                            subtext = "Sensors Sync",
                            valueColor = Primary,
                            subtextColor = Secondary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Execution Actions
                    Button(
                        onClick = {
                            isRecalculating = true
                            onAutoRecalculateRoute()
                            selectedRouteId = "eco_bypass"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = OnPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                Brush.horizontalGradient(listOf(PrimaryContainer, SecondaryContainer)),
                                RoundedCornerShape(12.dp)
                            )
                            .testTag("recalculate_safe_route_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(syncRotation)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEcoBypassActive || selectedRouteId == "eco_bypass")
                                    "Route Optimized: Eco-Bypass Live"
                                else
                                    "Auto-Recalculate Safe Route",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            departureShiftApplied = true
                            onApplyDepartureShift()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceContainerHigh,
                            contentColor = if (departureShiftApplied) Secondary else Primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("apply_departure_shift_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (departureShiftApplied) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (departureShiftApplied) Secondary else TertiaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (departureShiftApplied)
                                    "Departure Adjusted to 08:45 AM (-25 min)"
                                else
                                    "Apply 25-Min Departure Shift",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricClusterCard(
    label: String,
    value: String,
    subtext: String,
    valueColor: Color,
    subtextColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceContainerHigh.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = OnSurfaceVariant,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtext,
            color = subtextColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
