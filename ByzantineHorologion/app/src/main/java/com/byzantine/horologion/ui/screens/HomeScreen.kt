package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.byzantine.horologion.R
import com.byzantine.horologion.ui.LocalEngine
import com.byzantine.horologion.ui.LocalSettings
import com.byzantine.horologion.ui.ScrollColumn
import com.byzantine.horologion.ui.components.OrthodoxCross
import com.byzantine.horologion.data.Lang
import java.util.Calendar

@Composable
fun HomeScreen(nav: NavHostController) {
    val settings = LocalSettings.current
    val engine = LocalEngine.current
    val repo = com.byzantine.horologion.ui.LocalRepo.current
    val lang = settings.lang

    val now = Calendar.getInstance()
    val info = engine.dayInfo(
        now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH)
    )
    val saint = repo.saintOfDay(now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH))

    ScrollColumn {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OrthodoxCross(Modifier.size(40.dp), MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    if (lang == Lang.FR) "Liturgie des heures byzantine"
                    else "Liturgia delle ore bizantina",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    if (info.tone > 0) "${if (lang == Lang.FR) "Ton" else "Tono"} ${info.tone} · ${info.theme(lang)}"
                    else (if (lang == Lang.FR) "Temps pascal · " else "Tempo pasquale · ") + info.theme(lang),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        info.feast(lang)?.let {
            Spacer(Modifier.height(12.dp))
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        if (lang == Lang.FR) "Aujourd'hui" else "Oggi",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(it, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            if (lang == Lang.FR) "« Acquiers l'esprit de paix, et des milliers autour de toi seront sauvés. »"
            else "« Acquista lo spirito di pace e migliaia intorno a te saranno salvati. »",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        saint?.let { s ->
            Spacer(Modifier.height(16.dp))
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        if (lang == Lang.FR) "Saint du jour" else "Santo del giorno",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(s.title(lang), style = MaterialTheme.typography.titleLarge)
                    androidx.compose.foundation.layout.Box(
                        Modifier
                            .padding(vertical = 6.dp)
                            .height(3.dp)
                            .width(48.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Text(s.bio(lang), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        val cards = listOf(
            Triple("office", R.string.nav_office, "office"),
            Triple("jesus", R.string.nav_jesus, "jesus"),
            Triple("akathist", R.string.nav_akathist, "akathist"),
            Triple("calendar", R.string.nav_calendar, "calendar")
        )
        cards.forEach { (route, label, _) ->
            Card(
                onClick = { nav.navigate(route) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    OrthodoxCross(Modifier.size(22.dp), MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        androidx.compose.ui.res.stringResource(label),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Image(
            painter = painterResource(R.drawable.img_dttw),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
