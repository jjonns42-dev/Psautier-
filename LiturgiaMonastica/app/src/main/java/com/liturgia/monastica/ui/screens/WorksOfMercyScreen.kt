package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.data.WorkOfMercy
import com.liturgia.monastica.ui.components.AppScaffold

@Composable
fun WorksOfMercyScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val italian = store.lang == "it"
    val works = remember { repo.worksOfMercy() }
    var category by remember { mutableStateOf("corporelle") }

    AppScaffold(
        title = if (italian) "Opere di misericordia" else "Œuvres de miséricorde",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "corporelle" to (if (italian) "Corporali" else "Corporelles"),
                    "spirituelle" to (if (italian) "Spirituali" else "Spirituelles")
                ).forEach { (c, label) ->
                    val sel = category == c
                    Text(
                        label,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .clickable { category = c }
                            .padding(vertical = 12.dp),
                        color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center
                    )
                }
            }

            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
                Text(
                    if (italian)
                        "« Beati i misericordiosi, perché troveranno misericordia » (Mt 5, 7). Segna i giorni in cui vivi un'opera di misericordia."
                    else
                        "« Heureux les miséricordieux, car ils obtiendront miséricorde » (Mt 5, 7). Coche les jours où tu vis une œuvre de miséricorde.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                works.filter { it.category == category }.forEach { w -> MercyCard(w, store, italian) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MercyCard(w: WorkOfMercy, store: GameStore, italian: Boolean) {
    val done = store.mercyDoneToday(w.id)
    val count = store.mercyDoneCount(w.id)
    var expanded by remember { mutableStateOf(false) }

    Card(
        Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = if (done) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (italian) w.nameIt else w.nameFr,
                    style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f)
                )
                if (count > 0) Text(
                    if (italian) "$count volte" else "$count fois",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary
                )
            }
            Text(
                w.scripture, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary, fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(w.desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(
                    if (italian) "Gesti concreti" else "Gestes concrets",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
                w.examples.forEach { ex ->
                    Row(Modifier.fillMaxWidth().padding(top = 3.dp)) {
                        Text("• ", color = MaterialTheme.colorScheme.tertiary)
                        Text(ex, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            } else {
                Text(
                    if (italian) "Tocca per vedere i gesti concreti" else "Toucher pour voir les gestes concrets",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { store.mercyToggleToday(w.id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (done) MaterialTheme.colorScheme.tertiary else Color(0xFF3A5A3A)
                )
            ) {
                Text(
                    if (done) (if (italian) "✓ Vissuta oggi" else "✓ Vécue aujourd'hui")
                    else (if (italian) "Segna oggi" else "Marquer aujourd'hui"),
                    color = Color.White
                )
            }
        }
    }
}
