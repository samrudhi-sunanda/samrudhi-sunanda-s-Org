package com.example.ui.components.bento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Adaptive Bento-style environmental telemetry layout container.
 * Automatically switches between compact vertical-stack bento layout (phones)
 * and expanded multi-column bento grid (tablets, foldables, and landscape mode).
 */
@Composable
fun AdaptiveBentoTelemetryGrid(
    telemetry: EnvironmentalTelemetry,
    modifier: Modifier = Modifier,
    enableSimulationToggle: Boolean = true
) {
    var unit by remember { mutableStateOf(TemperatureUnit.CELSIUS) }
    var simulationStep by remember { mutableIntStateOf(0) }

    // Dynamic simulated telemetry variations for interactive inspection
    val dynamicTelemetry = remember(telemetry, simulationStep) {
        when (simulationStep % 3) {
            1 -> telemetry.copy(
                temperatureCelsius = telemetry.temperatureCelsius + 3.2f,
                feelsLikeCelsius = telemetry.feelsLikeCelsius + 3.6f,
                humidityPct = (telemetry.humidityPct + 14).coerceAtMost(98),
                dewPointCelsius = telemetry.dewPointCelsius + 2.4f,
                aqi = (telemetry.aqi + 22).coerceAtMost(250),
                pm25 = telemetry.pm25 + 8f,
                pm10 = telemetry.pm10 + 15f
            )
            2 -> telemetry.copy(
                temperatureCelsius = (telemetry.temperatureCelsius - 2.8f).coerceAtLeast(10f),
                feelsLikeCelsius = (telemetry.feelsLikeCelsius - 3.1f).coerceAtLeast(8f),
                humidityPct = (telemetry.humidityPct - 18).coerceAtLeast(20),
                dewPointCelsius = (telemetry.dewPointCelsius - 3.2f).coerceAtLeast(5f),
                aqi = (telemetry.aqi - 15).coerceAtLeast(18),
                pm25 = (telemetry.pm25 - 4f).coerceAtLeast(4f),
                pm10 = (telemetry.pm10 - 8f).coerceAtLeast(10f)
            )
            else -> telemetry
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isWide = maxWidth >= 600.dp

        if (isWide) {
            // Tablet / Landscape / Foldable Expanded Bento Layout
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Row: Temperature Hero + Summary Index
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TemperatureBentoCard(
                        telemetry = dynamicTelemetry,
                        unit = unit,
                        onToggleUnit = {
                            unit = if (unit == TemperatureUnit.CELSIUS) TemperatureUnit.FAHRENHEIT else TemperatureUnit.CELSIUS
                        },
                        modifier = Modifier.weight(1.2f)
                    )

                    EnvironmentalSummaryBentoCard(
                        telemetry = dynamicTelemetry,
                        onSimulateTelemetry = if (enableSimulationToggle) {
                            { simulationStep++ }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bottom Row: Humidity + Air Quality
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HumidityBentoCard(
                        telemetry = dynamicTelemetry,
                        modifier = Modifier.weight(1f)
                    )

                    AirQualityBentoCard(
                        telemetry = dynamicTelemetry,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Compact Phone Portrait Bento Layout
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Atmospheric Summary Card
                EnvironmentalSummaryBentoCard(
                    telemetry = dynamicTelemetry,
                    onSimulateTelemetry = if (enableSimulationToggle) {
                        { simulationStep++ }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Temperature Hero Bento Card
                TemperatureBentoCard(
                    telemetry = dynamicTelemetry,
                    unit = unit,
                    onToggleUnit = {
                        unit = if (unit == TemperatureUnit.CELSIUS) TemperatureUnit.FAHRENHEIT else TemperatureUnit.CELSIUS
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Paired Row: Humidity & Air Quality
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HumidityBentoCard(
                        telemetry = dynamicTelemetry,
                        modifier = Modifier.weight(1f)
                    )

                    AirQualityBentoCard(
                        telemetry = dynamicTelemetry,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
