package com.byzantine.horologion.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * A wax candle that visibly consumes itself over [burnMillis] while [running]
 * is true, with a softly flickering flame. No timer, counter or number is shown:
 * only the candle getting shorter marks the passage of prayer. [onConsumed] fires
 * once when the candle is fully spent. Changing [restartKey] resets the wax.
 */
@Composable
fun CandleView(
    modifier: Modifier = Modifier,
    burnMillis: Long = 20 * 60 * 1000L,
    running: Boolean = true,
    restartKey: Int = 0,
    onConsumed: () -> Unit = {}
) {
    var burn by remember(restartKey) { mutableFloatStateOf(0f) }
    val consumedHandler by rememberUpdatedState(onConsumed)

    LaunchedEffect(running, restartKey, burnMillis) {
        if (!running) return@LaunchedEffect
        val step = 80L
        while (burn < 1f) {
            delay(step)
            burn = (burn + step.toFloat() / burnMillis).coerceAtMost(1f)
        }
        consumedHandler()
    }

    val flicker = rememberInfiniteTransition(label = "flame")
    val phase by flicker.animateFloat(
        initialValue = 0f, targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val candleWidth = w * 0.20f
        val cx = w / 2f
        val baseY = h * 0.92f
        val fullCandleHeight = h * 0.62f
        val candleHeight = fullCandleHeight * (1f - burn)
        val topY = baseY - candleHeight

        val waxBrush = Brush.horizontalGradient(
            colors = listOf(Color(0xFFFFF4DA), Color(0xFFF3E2B8), Color(0xFFD9C28A)),
            startX = cx - candleWidth / 2, endX = cx + candleWidth / 2
        )
        drawRect(
            brush = waxBrush,
            topLeft = Offset(cx - candleWidth / 2, topY),
            size = Size(candleWidth, baseY - topY)
        )
        drawOval(
            color = Color(0xFFE7D3A0),
            topLeft = Offset(cx - candleWidth / 2, topY - candleWidth * 0.12f),
            size = Size(candleWidth, candleWidth * 0.26f)
        )

        // wick + flame only while there is wax left
        if (candleHeight > h * 0.02f) {
            val wickH = candleWidth * 0.22f
            drawLine(
                color = Color(0xFF2A2018),
                start = Offset(cx, topY),
                end = Offset(cx, topY - wickH),
                strokeWidth = candleWidth * 0.05f
            )
            val flickerAmt = 1f + 0.10f * sin(phase) + 0.05f * sin(phase * 3.1f)
            val flameH = candleWidth * 1.25f * flickerAmt
            val flameW = candleWidth * 0.55f * (0.96f + 0.06f * sin(phase * 2f))
            val flameCenterY = topY - wickH - flameH * 0.45f
            val sway = candleWidth * 0.06f * sin(phase * 1.7f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55FFC766), Color(0x00FFC766)),
                    center = Offset(cx + sway, flameCenterY),
                    radius = flameH * 2.2f
                ),
                radius = flameH * 2.2f,
                center = Offset(cx + sway, flameCenterY)
            )
            drawOval(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFE08A), Color(0xFFFF8A1E)),
                    startY = flameCenterY - flameH / 2, endY = flameCenterY + flameH / 2
                ),
                topLeft = Offset(cx + sway - flameW / 2, flameCenterY - flameH / 2),
                size = Size(flameW, flameH)
            )
            drawOval(
                color = Color(0xCC6FA8FF),
                topLeft = Offset(cx + sway - flameW * 0.18f, flameCenterY + flameH * 0.05f),
                size = Size(flameW * 0.36f, flameH * 0.34f)
            )
        }
    }
}
