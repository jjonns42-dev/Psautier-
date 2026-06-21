package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.byzantine.horologion.R
import com.byzantine.horologion.data.Accent
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.data.Role
import com.byzantine.horologion.data.ThemeMode
import com.byzantine.horologion.ui.LocalSettings

@Composable
fun SettingsScreen(onLanguageChange: (Lang) -> Unit) {
    val settings = LocalSettings.current

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {

        SectionTitle(stringResource(R.string.settings_theme))
        RadioRow(stringResource(R.string.settings_day), settings.themeMode == ThemeMode.DAY) { settings.setTheme(ThemeMode.DAY) }
        RadioRow(stringResource(R.string.settings_night), settings.themeMode == ThemeMode.NIGHT) { settings.setTheme(ThemeMode.NIGHT) }
        RadioRow(stringResource(R.string.settings_system), settings.themeMode == ThemeMode.SYSTEM) { settings.setTheme(ThemeMode.SYSTEM) }

        HorizontalDivider()
        SectionTitle(stringResource(R.string.settings_accent))
        RadioRow(stringResource(R.string.accent_red), settings.accent == Accent.RED) { settings.applyAccent(Accent.RED) }
        RadioRow(stringResource(R.string.accent_pink), settings.accent == Accent.PINK) { settings.applyAccent(Accent.PINK) }

        HorizontalDivider()
        SectionTitle(stringResource(R.string.settings_language))
        RadioRow("Français", settings.lang == Lang.FR) { if (settings.lang != Lang.FR) onLanguageChange(Lang.FR) }
        RadioRow("Italiano", settings.lang == Lang.IT) { if (settings.lang != Lang.IT) onLanguageChange(Lang.IT) }

        HorizontalDivider()
        SectionTitle(stringResource(R.string.settings_role))
        RadioRow(stringResource(R.string.role_layperson), settings.role == Role.LAYPERSON) { settings.applyRole(Role.LAYPERSON) }
        RadioRow(stringResource(R.string.role_priest), settings.role == Role.PRIEST) { settings.applyRole(Role.PRIEST) }

        HorizontalDivider()
        SectionTitle(stringResource(R.string.nav_chants))
        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = settings.showScores, onCheckedChange = { settings.applyShowScores(it) })
            Spacer(Modifier.width(12.dp))
            Text(stringResource(R.string.settings_show_scores), style = MaterialTheme.typography.bodyLarge)
        }

        HorizontalDivider()
        SectionTitle(if (settings.lang == Lang.FR) "Prière de Jésus" else "Preghiera di Gesù")
        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = settings.gameEnabled, onCheckedChange = { settings.applyGameEnabled(it) })
            Spacer(Modifier.width(12.dp))
            Text(
                if (settings.lang == Lang.FR)
                    "Activer le jeu de la bougie (niveaux hebdomadaires)"
                else
                    "Attiva il gioco della candela (livelli settimanali)",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(24.dp))
        Text(
            if (settings.lang == Lang.FR)
                "Horologion · Liturgie des heures byzantine. Calendrier selon le Paschalion orthodoxe."
            else
                "Horologion · Liturgia delle ore bizantina. Calendario secondo il Paschalion ortodosso.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().selectable(selected, onClick = onClick).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}
