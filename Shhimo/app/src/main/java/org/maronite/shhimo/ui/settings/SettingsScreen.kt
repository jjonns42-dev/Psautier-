package org.maronite.shhimo.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.maronite.shhimo.data.model.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    fontScale: Float,
    onLanguageChange: (AppLanguage) -> Unit,
    onFontScaleChange: (Float) -> Unit
) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            when (currentLanguage) {
                AppLanguage.ARABIC -> "اللغة"
                AppLanguage.ITALIAN -> "Lingua"
                AppLanguage.SYRIAC -> "Language"
            },
            style = MaterialTheme.typography.titleMedium
        )
        listOf(
            AppLanguage.ARABIC to "العربية اللبنانية",
            AppLanguage.ITALIAN to "Italiano",
            AppLanguage.SYRIAC to "ܣܘܪܝܝܐ / Syriaque"
        ).forEach { (lang, label) ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .selectable(selected = lang == currentLanguage) { onLanguageChange(lang) }
            ) {
                androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = lang == currentLanguage, onClick = { onLanguageChange(lang) })
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Text(
            when (currentLanguage) {
                AppLanguage.ARABIC -> "حجم الخط"
                AppLanguage.ITALIAN -> "Dimensione del testo"
                AppLanguage.SYRIAC -> "Font size"
            },
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = fontScale,
            onValueChange = onFontScaleChange,
            valueRange = 0.8f..1.6f,
            steps = 7
        )
    }
}
