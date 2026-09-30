package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PersonaCatalog
import com.example.data.model.PersonaCategory
import com.example.data.model.PersonaProfile
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
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TertiaryContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    selectedDomainId: String,
    selectedPersonaId: String,
    onDomainSelected: (String) -> Unit,
    onPersonaSelected: (String) -> Unit,
    onConfirmLaunch: () -> Unit
) {
    val activeCategory = PersonaCatalog.categories.find { it.id == selectedDomainId }
        ?: PersonaCatalog.categories.first()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp)
            .testTag("screen_onboarding"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Branding Logo Emblem
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        color = SurfaceContainerHigh.copy(alpha = 0.85f),
                        shape = CircleShape
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(CyanGlow.copy(alpha = 0.5f), Secondary.copy(alpha = 0.3f))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_aakaash_logo),
                    contentDescription = "Mausam Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stepper Indicator Pill
            Row(
                modifier = Modifier
                    .background(
                        color = SurfaceContainerHigh.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(PrimaryContainer, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "STEP 1 OF 2: INTELLIGENT PROFILING",
                    color = Primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "What is your primary focus with Mausam?",
                color = OnSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select a domain to tailor your real-time environmental intelligence and sensor thresholds.",
                color = OnSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 5 Domain Cards (2x2 + 1 Full Width)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DomainCard(
                    category = PersonaCatalog.categories[0], // Fitness & Sports
                    isSelected = selectedDomainId == "fitness",
                    icon = Icons.Default.DirectionsRun,
                    accentColor = PrimaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onDomainSelected("fitness") }
                )
                DomainCard(
                    category = PersonaCatalog.categories[1], // Work & Agri
                    isSelected = selectedDomainId == "agri",
                    icon = Icons.Default.Agriculture,
                    accentColor = Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = { onDomainSelected("agri") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DomainCard(
                    category = PersonaCatalog.categories[2], // Travel & Transit
                    isSelected = selectedDomainId == "travel",
                    icon = Icons.Default.FlightTakeoff,
                    accentColor = Primary,
                    modifier = Modifier.weight(1f),
                    onClick = { onDomainSelected("travel") }
                )
                DomainCard(
                    category = PersonaCatalog.categories[3], // Family & Life
                    isSelected = selectedDomainId == "family",
                    icon = Icons.Default.FamilyRestroom,
                    accentColor = TertiaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onDomainSelected("family") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card 5: Specialized Missions (Full Width)
            SpecializedDomainCard(
                category = PersonaCatalog.categories[4],
                isSelected = selectedDomainId == "specialized",
                onClick = { onDomainSelected("specialized") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step 2 Drawer Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = SurfaceContainerHigh.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(1.dp, Color(0x2200D2FF), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Grab Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(width = 38.dp, height = 4.dp)
                            .background(OnSurfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sheet Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "STEP 2 SPECIFICATION",
                                    color = Secondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(Secondary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Live Vectors",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeCategory.title,
                                color = OnSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(SurfaceContainerLowest, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${activeCategory.personas.size} Personas",
                                color = Primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Personas List
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        activeCategory.personas.forEach { persona ->
                            PersonaSelectionCard(
                                persona = persona,
                                isSelected = persona.id == selectedPersonaId,
                                onClick = { onPersonaSelected(persona.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Confirm & Launch Button
                    Button(
                        onClick = onConfirmLaunch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_launch_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Confirm & Launch Mausam",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Telemetry models synchronize with local Doppler & mesonet nodes.",
                        color = OnSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun DomainCard(
    category: PersonaCategory,
    isSelected: Boolean,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (isSelected) SurfaceContainerHigh else SurfaceContainerLow
    val borderBrush = if (isSelected) {
        Brush.linearGradient(listOf(CyanGlow.copy(alpha = 0.5f), Color.Transparent))
    } else {
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.05f), Color.Transparent))
    }

    Box(
        modifier = modifier
            .height(148.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, borderBrush, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(SurfaceContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) PrimaryContainer.copy(alpha = 0.2f) else SurfaceContainerHighest,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category.badge,
                        color = if (isSelected) Primary else OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column {
                Text(
                    text = category.title,
                    color = OnSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.description,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 2,
                    lineHeight = 14.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isSelected) "TELEMETRY PRIMED" else "${category.personaCount} PROFILES",
                    color = if (isSelected) PrimaryContainer else OnSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PrimaryContainer,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecializedDomainCard(
    category: PersonaCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) SurfaceContainerHigh else SurfaceContainerLow
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(
                1.dp,
                if (isSelected) CyanGlow.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SatelliteAlt,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.title,
                        color = OnSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(SurfaceContainerHighest, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = category.badge,
                            color = Primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.description,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = OnSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PersonaSelectionCard(
    persona: PersonaProfile,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) SurfaceContainerLowest else SurfaceContainer.copy(alpha = 0.8f)
    val borderModifier = if (isSelected) {
        Modifier.border(1.dp, CyanGlow.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = persona.emoji,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = persona.title,
                            color = OnSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = persona.subtitle,
                            color = if (isSelected) Primary else OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // Radio Check indicator
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            if (isSelected) PrimaryContainer else SurfaceContainerHighest,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = OnPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Telemetry vector chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                persona.keyVectors.forEach { vector ->
                    val isWind = vector.contains("WIND")
                    val isRain = vector.contains("RAIN") || vector.contains("WATER")
                    val chipBg = if (isSelected) SurfaceContainer else SurfaceContainerHighest
                    val chipText = if (isSelected) {
                        if (isWind) Primary else if (isRain) Secondary else OnSurfaceVariant
                    } else OnSurfaceVariant

                    Row(
                        modifier = Modifier
                            .background(chipBg, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isWind) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = null,
                                tint = chipText,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        } else if (isRain) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = chipText,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = vector,
                            color = chipText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
