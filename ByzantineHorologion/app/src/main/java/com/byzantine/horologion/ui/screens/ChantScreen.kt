package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings

@Composable
fun ChantScreen() {
    val repo = LocalRepo.current
    val settings = LocalSettings.current
    val lang = settings.lang
    val showScores = settings.showScores

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(
            if (lang == Lang.FR)
                "Section musicale. Les partitions n'apparaissent que si l'option « Je veux les participations musicales » est cochée dans les Réglages."
            else
                "Sezione musicale. Gli spartiti compaiono solo se l'opzione « Voglio le parti musicali » è attivata nelle Impostazioni.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        repo.chants.forEach { c ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.Filled.MusicNote, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(10.dp))
                        Text(c.title(lang), style = MaterialTheme.typography.titleMedium)
                    }
                    c.textRef?.let { ref ->
                        repo.commonPrayer(ref, lang)?.let {
                            Spacer(Modifier.height(6.dp))
                            Text(it, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    if (showScores) {
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (lang == Lang.FR)
                                    "♪ Partition — déposez votre fichier de partition (image/PDF) dans assets/chants/ et référencez-le ici."
                                else
                                    "♪ Spartito — inserite il vostro file (immagine/PDF) in assets/chants/ e collegatelo qui.",
                                Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}
