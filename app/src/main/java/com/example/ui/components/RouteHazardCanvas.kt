package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberHazard
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.Error
import com.example.ui.theme.MintFlow
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
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryContainer

@Composable
fun RouteHazardCanvas(
    activeLayer: String,
    onLayerSelected: (String) -> Unit,
    isEcoBypassActive: Boolean,
    onRecenterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHazardDetail by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "hazardPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(390.dp)
            .background(SurfaceContainerLowest)
    ) {
        val surfaceBg = MaterialTheme.colorScheme.surface
        val contourColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Synthetic Grid
            val gridSize = 36.dp.toPx()
            var x = 0f
            while (x < width) {
                drawLine(
                    color = CyanGlow.copy(alpha = 0.04f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridSize
            }
            var y = 0f
            while (y < height) {
                drawLine(
                    color = CyanGlow.copy(alpha = 0.04f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridSize
            }

            // 2. Spatial Isobars / Background terrain contours
            val contourPath1 = Path().apply {
                moveTo(-20f, 120.dp.toPx())
                quadraticTo(width * 0.25f, 80.dp.toPx(), width * 0.5f, 140.dp.toPx())
                quadraticTo(width * 0.8f, 180.dp.toPx(), width + 30f, 100.dp.toPx())
            }
            drawPath(
                path = contourPath1,
                color = contourColor,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // Wind Streamline Vectors (if WIND active)
            if (activeLayer == "WIND") {
                val stream1 = Path().apply {
                    moveTo(30.dp.toPx(), 70.dp.toPx())
                    quadraticTo(width * 0.35f, 40.dp.toPx(), width * 0.65f, 80.dp.toPx())
                }
                drawPath(
                    path = stream1,
                    color = MintFlow.copy(alpha = 0.25f),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 25f), 0f),
                        cap = StrokeCap.Round
                    )
                )

                val stream2 = Path().apply {
                    moveTo(50.dp.toPx(), 200.dp.toPx())
                    quadraticTo(width * 0.45f, 170.dp.toPx(), width * 0.75f, 210.dp.toPx())
                }
                drawPath(
                    path = stream2,
                    color = MintFlow.copy(alpha = 0.22f),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 25f), 0f),
                        cap = StrokeCap.Round
                    )
                )
            }

            // 3. Route Coordinates
            val pStart = Offset(width * 0.16f, height * 0.82f) // Mile 0
            val pHazardStart = Offset(width * 0.45f, height * 0.48f) // Mile 2.5
            val pHazardCenter = Offset(width * 0.58f, height * 0.37f) // Mile 3.2 Hazard
            val pHazardEnd = Offset(width * 0.65f, height * 0.28f) // Mile 3.8
            val pEnd = Offset(width * 0.88f, height * 0.15f) // Tech Corridor

            // Alternative Eco-Bypass Path
            val bypassPath = Path().apply {
                moveTo(pHazardStart.x, pHazardStart.y)
                cubicTo(
                    width * 0.38f, height * 0.32f,
                    width * 0.50f, height * 0.16f,
                    pEnd.x, pEnd.y
                )
            }

            if (isEcoBypassActive) {
                // Eco-Bypass is active and illuminated
                drawPath(
                    path = bypassPath,
                    color = Secondary,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
            } else {
                // Eco-Bypass ghosted
                drawPath(
                    path = bypassPath,
                    color = CyanGlow.copy(alpha = 0.35f),
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f),
                        cap = StrokeCap.Round
                    )
                )
            }

            // Path Sector A: Mile 0 -> 2.5 (Clean Teal)
            val leg1 = Path().apply {
                moveTo(pStart.x, pStart.y)
                cubicTo(
                    width * 0.22f, height * 0.64f,
                    width * 0.32f, height * 0.54f,
                    pHazardStart.x, pHazardStart.y
                )
            }
            drawPath(
                path = leg1,
                color = Secondary,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Path Sector B: Hazard Segment (Amber Warning dashed)
            val hazardLeg = Path().apply {
                moveTo(pHazardStart.x, pHazardStart.y)
                cubicTo(
                    width * 0.52f, height * 0.44f,
                    width * 0.58f, height * 0.38f,
                    pHazardEnd.x, pHazardEnd.y
                )
            }
            val hazardColor = if (isEcoBypassActive) AmberHazard.copy(alpha = 0.45f) else AmberHazard
            drawPath(
                path = hazardLeg,
                color = hazardColor,
                style = Stroke(
                    width = 5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                    cap = StrokeCap.Round
                )
            )

            // Path Sector C: Resuming Clean Path
            val leg2 = Path().apply {
                moveTo(pHazardEnd.x, pHazardEnd.y)
                cubicTo(
                    width * 0.70f, height * 0.22f,
                    width * 0.78f, height * 0.18f,
                    pEnd.x, pEnd.y
                )
            }
            drawPath(
                path = leg2,
                color = Secondary,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Departure Pin (Mile 0)
            drawCircle(
                color = surfaceBg,
                radius = 7.dp.toPx(),
                center = pStart
            )
            drawCircle(
                color = Secondary,
                radius = 7.dp.toPx(),
                center = pStart,
                style = Stroke(width = 3.dp.toPx())
            )

            // Destination Pin (Tech Corridor)
            drawCircle(
                color = Secondary,
                radius = 7.dp.toPx(),
                center = pEnd
            )
            drawCircle(
                color = surfaceBg,
                radius = 3.dp.toPx(),
                center = pEnd
            )

            // Hazard Node Concentric Animated Radar (Mile 3.2)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AmberHazard.copy(alpha = pulseAlpha), Color.Transparent),
                    center = pHazardCenter,
                    radius = pulseRadius.dp.toPx()
                ),
                radius = pulseRadius.dp.toPx(),
                center = pHazardCenter
            )
            drawCircle(
                color = surfaceBg,
                radius = 12.dp.toPx(),
                center = pHazardCenter
            )
            drawCircle(
                color = AmberHazard,
                radius = 12.dp.toPx(),
                center = pHazardCenter,
                style = Stroke(width = 2.5.dp.toPx())
            )
            drawCircle(
                color = AmberHazard,
                radius = 5.dp.toPx(),
                center = pHazardCenter
            )
        }

        // Floating Top HUD: Layer Selector & Recenter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Layer Pills
            Row(
                modifier = Modifier
                    .background(
                        color = SurfaceContainerHigh.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LayerButton(
                    title = "WIND",
                    icon = Icons.Default.Air,
                    isSelected = activeLayer == "WIND",
                    onClick = { onLayerSelected("WIND") }
                )
                LayerButton(
                    title = "AQI",
                    icon = Icons.Default.Cloud,
                    isSelected = activeLayer == "AQI",
                    onClick = { onLayerSelected("AQI") }
                )
                LayerButton(
                    title = "TOPO",
                    icon = Icons.Default.Layers,
                    isSelected = activeLayer == "TOPO",
                    onClick = { onLayerSelected("TOPO") }
                )
            }

            // Recenter
            IconButton(
                onClick = onRecenterClicked,
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = SurfaceContainerHigh.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Recenter",
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Mile 0 - 2.0 Serene Marker Tooltip (Bottom Left)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = 44.dp)
                .background(
                    color = SurfaceContainer.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(1.dp, Color(0x2200D2FF), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(SecondaryContainer, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "MILE 0 - 2.0 • OPTIMAL",
                        color = Secondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "AQI 38 • Calm 12 km/h WNW",
                        color = OnSurface,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Pinned Telemetry Popover for Hazard (Mile 3.2 Bottleneck)
        if (showHazardDetail) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp, start = 30.dp)
                    .width(225.dp)
                    .background(
                        color = SurfaceContainerHigh.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(1.dp, AmberHazard.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .clickable { showHazardDetail = !showHazardDetail }
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = TertiaryContainer,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MILE 3.2 BOTTLENECK",
                                color = TertiaryContainer,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    TertiaryContainer.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "HIGH RISK",
                                color = TertiaryContainer,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Crosswind",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "34 km/h ESE",
                            color = Primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "PM2.5 Spike",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "148 • Unhealthy",
                            color = Tertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Micro-Shear",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "72% Prob.",
                            color = Error,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Bottom Map Legend Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendChip(color = Secondary, label = "Clean Flow")
            LegendChip(color = TertiaryContainer, label = "Hazard Node")
            LegendChip(color = PrimaryContainer, label = "AI Eco-Bypass")
        }
    }
}

@Composable
private fun LayerButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = if (isSelected) Primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) OnPrimary else OnSurfaceVariant,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = title,
            color = if (isSelected) OnPrimary else OnSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LegendChip(color: Color, label: String) {
    Row(
        modifier = Modifier
            .background(
                color = SurfaceContainer.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = OnSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
