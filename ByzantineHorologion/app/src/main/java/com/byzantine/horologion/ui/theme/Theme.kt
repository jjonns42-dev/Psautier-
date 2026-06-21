package com.byzantine.horologion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.byzantine.horologion.data.Accent
import com.byzantine.horologion.data.ThemeMode

@Composable
fun HorologionTheme(
    themeMode: ThemeMode,
    accent: Accent,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.DAY -> false
        ThemeMode.NIGHT -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val primary = when (accent) {
        Accent.RED -> if (dark) BordeauxLight else Bordeaux
        Accent.PINK -> if (dark) RoseLight else RosePrimary
    }
    val primaryContainer = when (accent) {
        Accent.RED -> if (dark) BordeauxDeep else BordeauxLight
        Accent.PINK -> if (dark) RoseDeep else RoseLight
    }

    val scheme = if (dark) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primaryContainer,
            onPrimaryContainer = Color.White,
            secondary = Gold,
            background = NightBackground,
            onBackground = NightOnBackground,
            surface = NightSurface,
            onSurface = NightOnBackground,
            onSurfaceVariant = NightOnSurfaceVariant,
            surfaceVariant = Color(0xFF2A2521)
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primaryContainer,
            onPrimaryContainer = Color.White,
            secondary = Gold,
            background = DayBackground,
            onBackground = DayOnBackground,
            surface = DaySurface,
            onSurface = DayOnBackground,
            onSurfaceVariant = DayOnSurfaceVariant,
            surfaceVariant = Color(0xFFEDE4D2)
        )
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = HorologionTypography,
        content = content
    )
}
