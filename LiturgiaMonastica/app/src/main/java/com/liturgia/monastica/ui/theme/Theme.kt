package com.liturgia.monastica.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DayColors = lightColorScheme(
    primary = Forest,
    onPrimary = ParchmentHi,
    secondary = Oxblood,
    onSecondary = ParchmentHi,
    tertiary = Gold,
    onTertiary = Ink,
    background = Parchment,
    onBackground = Ink,
    surface = ParchmentHi,
    onSurface = Ink,
    surfaceVariant = Parchment,
    onSurfaceVariant = InkSoft,
    outline = Gold
)

private val NightColors = darkColorScheme(
    primary = NightGold,
    onPrimary = NightBg,
    secondary = Oxblood,
    onSecondary = NightText,
    tertiary = NightGold,
    onTertiary = NightBg,
    background = NightBg,
    onBackground = NightText,
    surface = NightSurf,
    onSurface = NightText,
    surfaceVariant = NightSurf,
    onSurfaceVariant = NightText,
    outline = NightGold
)

@Composable
fun LiturgiaTheme(
    night: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (night) NightColors else DayColors,
        typography = AppTypography,
        content = content
    )
}
