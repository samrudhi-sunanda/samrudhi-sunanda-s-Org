package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark

@Composable
fun ElevationSparkline(
    currentKm: Float = 8.4f,
    altitudeM: Int = 920,
    totalKm: Float = 22.4f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SurfaceContainer.copy(alpha = 0.55f),
                shape = RoundedCornerShape(14.dp)
            )
            .border(1.dp, Color(0x1F00D2FF), RoundedCornerShape(14.dp))
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
                        imageVector = Icons.Default.AltRoute,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTIVE ELEVATION & HEADWIND MAP",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
                Text(
                    text = "${totalKm} km loop",
                    color = Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val surfaceColor = MaterialTheme.colorScheme.surface

            // Canvas terrain
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                val w = size.width
                val h = size.height

                val path = Path().apply {
                    moveTo(0f, h * 0.75f)
                    quadraticTo(w * 0.12f, h * 0.45f, w * 0.25f, h * 0.6f)
                    quadraticTo(w * 0.38f, h * 0.72f, w * 0.5f, h * 0.35f)
                    quadraticTo(w * 0.65f, h * 0.15f, w * 0.75f, h * 0.65f)
                    quadraticTo(w * 0.88f, h * 0.95f, w, h * 0.25f)
                }

                // Fill area below
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CyanGlow.copy(alpha = 0.32f),
                            surfaceColor.copy(alpha = 0.0f)
                        )
                    )
                )

                // Stroke outline
                drawPath(
                    path = path,
                    color = CyanGlow,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Beacon pin at currentKm position
                val pinRatio = (currentKm / totalKm).coerceIn(0.1f, 0.9f)
                val pinX = w * pinRatio
                val pinY = h * 0.46f

                // Drop dashed vertical line
                drawLine(
                    color = Secondary.copy(alpha = 0.8f),
                    start = Offset(pinX, pinY),
                    end = Offset(pinX, h),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // Outer glow circle
                drawCircle(
                    color = Secondary.copy(alpha = 0.35f),
                    radius = 9.dp.toPx(),
                    center = Offset(pinX, pinY)
                )

                // Core beacon
                drawCircle(
                    color = Secondary,
                    radius = 4.5.dp.toPx(),
                    center = Offset(pinX, pinY)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sub-ticks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "START (0 KM)",
                    color = OnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "KM ${currentKm} • ${altitudeM}m ALT",
                    color = Secondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "FINISH (${totalKm} KM)",
                    color = OnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
