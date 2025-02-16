package com.example.myapp.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
    var isImageCaptured by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
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
                    .size(50.dp)
            ) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val scale = 20f

                drawLine(Color.Gray, Offset(0f, centerY), Offset(size.width, centerY), strokeWidth = 2f)
                drawLine(Color.Gray, Offset(centerX, 0f), Offset(centerX, size.height), strokeWidth = 2f)

                val circleX = centerX + accelerometerValues.first * scale
                val circleY = centerY - accelerometerValues.second * scale

                drawCircle(Color.Blue, radius = 10f, center = Offset(circleX, circleY))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Luminosity: $luminosityValue lux")
            Canvas(
                modifier = Modifier.fillMaxWidth().height(30.dp)
            ) {
                val normalizedLuminosity = (luminosityValue / 500f).coerceIn(0f, 1f)
                val barWidth = size.width * normalizedLuminosity

                drawRoundRect(
                    brush = Brush.horizontalGradient(colors = listOf(Color.Blue, Color.Green, Color.Yellow)),
                    size = androidx.compose.ui.geometry.Size(barWidth, size.height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!entitySummary.isNullOrEmpty() && isImageCaptured) {
            Text(
                text = entitySummary,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                style = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                cameraPreview()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!isImageCaptured) {
            Button(
                onClick = {
                    isImageCaptured = true
                    onCaptureClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Capture", color = Color.White)
            }
        } else {
            Button(
                onClick = {
                    isImageCaptured = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Show Camera Preview", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAuthenticateClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Authenticate", color = Color.White)
        }
    }
}
