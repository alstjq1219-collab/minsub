package com.teslacam.viewer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teslacam.viewer.model.TelemetryData

@Composable
fun TelemetryHudOverlay(
    telemetry: TelemetryData,
    modifier: Modifier = Modifier
) {
    val animatedSteering by animateFloatAsState(targetValue = telemetry.steeringAngle, label = "steering")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xCC121212))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Speedometer
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = "Speed",
                tint = Color(0xFF3E82F7),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${telemetry.speedKmh}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " km/h",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }

        // Steering Wheel
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "⎈",
                color = Color(0xFFE0E0E0),
                fontSize = 22.sp,
                modifier = Modifier.rotate(animatedSteering)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${telemetry.steeringAngle.toInt()}°",
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Throttle & Brake
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("엑셀 ", color = Color.LightGray, fontSize = 12.sp)
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.DarkGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(telemetry.throttlePercent)
                        .background(Color(0xFF4CAF50))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (telemetry.brakeActive) Color.Red else Color(0xFF333333))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("BRK", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Timestamp
        Text(
            text = telemetry.timestampText,
            color = Color(0xFFFFD700),
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
