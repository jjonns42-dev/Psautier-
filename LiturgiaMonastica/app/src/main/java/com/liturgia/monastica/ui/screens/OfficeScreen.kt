package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.ui.components.AppScaffold
import com.liturgia.monastica.ui.components.GoldRule
import com.liturgia.monastica.ui.components.SectionCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun OfficeListScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit, onHour: (String) -> Unit) {
    val it = store.lang == "it"
    val hours = remember { repo.hours() }
    val today = remember {
        val loc = if (it) Locale.ITALIAN else Locale.FRENCH
        SimpleDateFormat("EEEE d MMMM", loc).format(Calendar.getInstance().time)
            .replaceFirstChar { c -> c.uppercase() }
    }
    AppScaffold(
        title = if (it) "Liturgia delle Ore" else "Liturgie des Heures",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(today, style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary)
                    GoldRule()
                    Text(
                        if (it) "Testi salmici dalla Bibbia Crampon · Ordinario dal Breviarium Monasticum"
                        else "Psaumes d'après la Bible Crampon · Ordinaire du Breviarium Monasticum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(hours) { h ->
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp)
                        .clickable { onHour(h.id) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${h.order}", style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.width(34.dp))
                        Column {
                            Text(if (it) h.nameIt else h.nameFr,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                            Text(h.nameLa, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun OfficeHourScreen(repo: ContentRepository, store: GameStore, hourId: String, onBack: () -> Unit) {
    val it = store.lang == "it"
    val meta = remember(hourId) { repo.hours().firstOrNull { it2 -> it2.id == hourId } }
    val weekday = remember { ContentRepository.todayWeekday() }
    val sections = remember(hourId, store.lang) { repo.buildHour(hourId, weekday, store.lang) }
    AppScaffold(
        title = meta?.let { m -> if (it) m.nameIt else m.nameFr } ?: "",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize().padding(vertical = 8.dp)) {
            items(sections) { s ->
                SectionCard(
                    title = s.title, subtitle = s.subtitle, body = s.body,
                    accent = when (s.kind) {
                        "gospel_canticle" -> MaterialTheme.colorScheme.secondary
                        "hymn" -> MaterialTheme.colorScheme.tertiary
                        "psalm" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
