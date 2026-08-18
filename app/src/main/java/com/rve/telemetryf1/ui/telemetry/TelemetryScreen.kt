package com.rve.telemetryf1.ui.telemetry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.telemetryf1.data.PlayerTelemetry
import kotlin.math.roundToInt

@Composable
fun TelemetryScreen(modifier: Modifier = Modifier, viewModel: TelemetryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier) { paddingValues ->
        TelemetryDashboard(
            telemetry = uiState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun TelemetryDashboard(telemetry: PlayerTelemetry, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "F1 23 Telemetry",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardItem("Speed", "${telemetry.speedKmh} km/h")
            
            val gearText = when (telemetry.gear) {
                0 -> "N"
                -1 -> "R"
                else -> telemetry.gear.toString()
            }
            DashboardItem("Gear", gearText)
            
            DashboardItem("RPM", "${telemetry.engineRPM}")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardItem("Throttle", "${(telemetry.throttle * 100).roundToInt()}%")
            DashboardItem("Brake", "${(telemetry.brake * 100).roundToInt()}%")
            DashboardItem("Steer", "${(telemetry.steer * 100).roundToInt()}%")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardItem("Engine Temp", "${telemetry.engineTemperature} °C")
            DashboardItem("DRS", if (telemetry.drs) "ON" else "OFF")
            DashboardItem("Rev Lights", "${telemetry.revLightsPercent}%")
        }
    }
}

@Composable
fun DashboardItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
