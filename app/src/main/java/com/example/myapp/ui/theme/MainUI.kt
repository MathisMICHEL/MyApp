package com.example.myapp.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainUI(
    accelerometerValues: Triple<Float, Float, Float>,
    luminosityValue: Float,
    cameraPreview: @Composable () -> Unit,
    onCaptureClick: () -> Unit,
    onAuthenticateClick: () -> Unit,
    entitySummary: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Accelerometer and Graph
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text("X: ${accelerometerValues.first}", modifier = Modifier.padding(bottom = 8.dp))
                Text("Y: ${accelerometerValues.second}", modifier = Modifier.padding(bottom = 8.dp))
                Text("Z: ${accelerometerValues.third}", modifier = Modifier.padding(bottom = 8.dp))
            }

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .size(150.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerX = canvasWidth / 2
                val centerY = canvasHeight / 2
                val scale = 20f

                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, centerY),
                    end = Offset(canvasWidth, centerY),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, canvasHeight),
                    strokeWidth = 2f
                )

                val circleX = centerX + accelerometerValues.first * scale
                val circleY = centerY - accelerometerValues.second * scale

                drawCircle(
                    color = Color.Blue,
                    radius = 10f,
                    center = Offset(circleX, circleY)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Luminosity: $luminosityValue lux")
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                val normalizedLuminosity = (luminosityValue / 500f).coerceIn(0f, 1f)
                val barWidth = size.width * normalizedLuminosity

                val gradient = Brush.horizontalGradient(
                    colors = listOf(Color.Blue, Color.Green, Color.Yellow),
                    startX = 0f,
                    endX = size.width
                )

                drawRoundRect(
                    brush = gradient,
                    size = androidx.compose.ui.geometry.Size(barWidth, size.height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )

                drawRoundRect(
                    color = Color.Black,
                    size = size,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display summary or camera preview
        if (!entitySummary.isNullOrEmpty()) {
            // Show Wikipedia summary
            Text(
                text = entitySummary,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                style = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
            )
        } else {
            // Show camera preview when no summary is available
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                cameraPreview()
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onCaptureClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Capture", color = Color.White)
            }

            Button(
                onClick = onAuthenticateClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Authenticate", color = Color.White)
            }
        }
    }
}
