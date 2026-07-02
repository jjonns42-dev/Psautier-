package org.maronite.shhimo.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Palette inspirée des manuscrits liturgiques syriaques : encre brun-noir,
 * rouge rubrical (les rubriques étaient écrites en rouge — d'où "rubrique"),
 * fond parchemin, et un bleu-nuit pour les vigiles (Lilio).
 */
private val InkBrown = Color(0xFF2B2118)
private val Parchment = Color(0xFFF3ECDD)
private val ParchmentDeep = Color(0xFFE7DCC4)
private val RubricRed = Color(0xFF9E3B2E)
private val NightBlue = Color(0xFF263247)
private val Gold = Color(0xFFB08442)

private val LightColors = lightColorScheme(
    primary = RubricRed,
    onPrimary = Parchment,
    secondary = Gold,
    onSecondary = InkBrown,
    background = Parchment,
    onBackground = InkBrown,
    surface = ParchmentDeep,
    onSurface = InkBrown,
    surfaceVariant = ParchmentDeep,
    tertiary = NightBlue
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFCD6E5F),
    onPrimary = Color(0xFF1A140E),
    secondary = Gold,
    background = Color(0xFF161310),
    onBackground = Parchment,
    surface = Color(0xFF221C16),
    onSurface = Parchment,
    tertiary = Color(0xFF8FA4C4)
)

/**
 * Typographie : un corps généreux et lisible pour la prière prolongée,
 * avec des titres affirmés. La famille reste celle du système afin de
 * couvrir correctement l'arabe ; un fichier de police syriaque pourra
 * être ajouté dans res/font/ pour l'affichage du syriaque.
 */
private fun typographyFor(scale: Float) = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = (24 * scale).sp, lineHeight = (30 * scale).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = (19 * scale).sp, lineHeight = (25 * scale).sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = (18 * scale).sp, lineHeight = (28 * scale).sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = (16 * scale).sp, lineHeight = (25 * scale).sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = (14 * scale).sp)
)

@Composable
fun ShhimoTheme(
    fontScale: Float = 1.0f,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = typographyFor(fontScale),
        content = content
    )
}
