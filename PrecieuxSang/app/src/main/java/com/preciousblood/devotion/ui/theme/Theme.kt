package com.preciousblood.devotion.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = BloodRed,
    onPrimary = Color.White,
    primaryContainer = BloodRedLight,
    onPrimaryContainer = Color.White,
    secondary = Gold,
    onSecondary = InkBrown,
    secondaryContainer = GoldLight,
    onSecondaryContainer = InkBrown,
    tertiary = CrimsonAccent,
    background = Parchment,
    onBackground = InkBrown,
    surface = Parchment,
    onSurface = InkBrown,
    surfaceVariant = ParchmentDim,
    onSurfaceVariant = InkBrown,
    outline = Gold
)

private val DarkColors = darkColorScheme(
    primary = CrimsonAccent,
    onPrimary = Color.White,
    primaryContainer = BloodRedDark,
    onPrimaryContainer = OnDarkCream,
    secondary = GoldLight,
    onSecondary = NightBackground,
    secondaryContainer = Gold,
    onSecondaryContainer = NightBackground,
    tertiary = BloodRedLight,
    background = NightBackground,
    onBackground = OnDarkCream,
    surface = NightSurface,
    onSurface = OnDarkCream,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = OnDarkCream,
    outline = Gold
)

@Composable
fun PrecieuxSangTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
