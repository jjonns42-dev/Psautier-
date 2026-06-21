package com.byzantine.horologion.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

/** The three-bar Russian/Byzantine cross, drawn as a small ornament. */
@Composable
fun OrthodoxCross(modifier: Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val stroke = (w * 0.07f).coerceAtLeast(2f)

        // vertical post
        drawLine(color, Offset(cx, h * 0.06f), Offset(cx, h * 0.94f), stroke, StrokeCap.Round)
        // top title bar
        drawLine(color, Offset(cx - w * 0.16f, h * 0.20f), Offset(cx + w * 0.16f, h * 0.20f), stroke, StrokeCap.Round)
        // main transverse bar
        drawLine(color, Offset(cx - w * 0.34f, h * 0.36f), Offset(cx + w * 0.34f, h * 0.36f), stroke, StrokeCap.Round)
        // slanted footrest (raised left, lowered right)
        drawLine(color, Offset(cx - w * 0.22f, h * 0.74f), Offset(cx + w * 0.22f, h * 0.62f), stroke, StrokeCap.Round)
    }
}
