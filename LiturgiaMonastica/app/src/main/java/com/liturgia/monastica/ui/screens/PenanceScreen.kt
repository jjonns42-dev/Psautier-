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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.data.*
import com.liturgia.monastica.ui.components.AppScaffold
import java.util.Calendar

private fun levelLabel(level: Int, it: Boolean): String = when (level) {
    1 -> if (it) "Dolce" else "Douce"
    2 -> if (it) "Moderata" else "Modérée"
    3 -> if (it) "Esigente" else "Exigeante"
    4 -> if (it) "Severa" else "Sévère"
    else -> if (it) "Eroica" else "Héroïque"
}

private fun weekdayShort(wd: Int, it: Boolean): String {
    val fr = listOf("dim.", "lun.", "mar.", "mer.", "jeu.", "ven.", "sam.")
    val ita = listOf("dom.", "lun.", "mar.", "mer.", "gio.", "ven.", "sab.")
    return (if (it) ita else fr)[(wd - 1).coerceIn(0, 6)]
}

private fun computedLabel(key: String?, it: Boolean): String? = when (key) {
    "friday_weekly" -> if (it) "ogni venerdì" else "chaque vendredi"
    "wedfri_weekly" -> if (it) "mercoledì e venerdì" else "mercredi et vendredi"
    "lent_catholic" -> if (it) "Quaresima" else "Carême"
    "great_lent" -> if (it) "Grande Quaresima" else "Grand Carême"
    "advent_catholic" -> if (it) "Avvento" else "Avent"
    "saint_michael" -> if (it) "Quaresima di san Michele" else "Carême de saint Michel"
    else -> null
}

private fun todayWeekday(): Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

private fun assignedToday(p: Penance, today: DateYMD): Boolean {
    val byWeekday = p.weekdays.isNotEmpty() && p.weekdays.contains(todayWeekday())
    val byPeriod = p.computed != null && FastingCalendar.isComputedActiveToday(p.computed, today)
    return byWeekday || byPeriod
}

private fun isAnytime(p: Penance): Boolean = p.weekdays.isEmpty() && p.computed == null

private fun assignedLabel(p: Penance, it: Boolean): String {
    val parts = mutableListOf<String>()
    computedLabel(p.computed, it)?.let { parts.add(it) }
    if (p.weekdays.isNotEmpty()) parts.add(p.weekdays.sorted().joinToString(", ") { weekdayShort(it, false) })
    if (parts.isEmpty()) parts.add(if (it) "a volontà" else "à volonté")
    return parts.joinToString(" · ")
}

@Composable
fun PenanceScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    val today = remember { FastingCalendar.todayYmd() }
    val builtIn = remember { repo.penances() }
    val all = builtIn + store.penanceProgress.custom
    var showAdd by remember { mutableStateOf(false) }

    AppScaffold(
        title = if (it) "La penitenza" else "La pénitence",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text(
                if (it) "Penitenze classificate per intensità, ciascuna nei suoi giorni assegnati. Segna i giorni in cui la vivi."
                else "Des pénitences classées par intensité, chacune sur ses jours assignés. Coche les jours où tu la vis.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                if (it) "Sempre con discernimento, secondo la salute e con l'accordo di un accompagnatore per le penitenze severe."
                else "Toujours avec discernement, selon la santé, et avec l'accord d'un accompagnateur pour les pénitences sévères.",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
            OutlinedButton(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (it) "＋ Aggiungi una penitenza personale" else "＋ Ajouter une pénitence personnelle")
            }
            Spacer(Modifier.height(8.dp))

            (1..5).forEach { lvl ->
                val group = all.filter { p -> p.level == lvl }
                if (group.isNotEmpty()) {
                    Text(
                        (if (it) "Livello $lvl · " else "Niveau $lvl · ") + levelLabel(lvl, it),
                        style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    group.forEach { p -> PenanceCard(p, today, store, it) }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showAdd) AddPenanceDialog(store, it, onClose = { showAdd = false })
}

@Composable
private fun PenanceCard(p: Penance, today: DateYMD, store: GameStore, it: Boolean) {
    val active = assignedToday(p, today)
    val anytime = isAnytime(p)
    val canMark = active || anytime
    val done = store.penanceDoneToday(p.id)
    val count = store.penanceDoneCount(p.id)

    Card(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (active) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(p.name, style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f))
                if (active) Text(if (it) "● oggi" else "● aujourd'hui",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
            }
            Text(assignedLabel(p, it), style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(p.desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            if (p.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(p.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (canMark) {
                    Button(
                        onClick = { store.penanceToggleToday(p.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (done) MaterialTheme.colorScheme.tertiary else Color(0xFF5A3A3A)
                        )
                    ) {
                        Text(
                            if (done) (if (it) "✓ Vissuta oggi" else "✓ Vécue aujourd'hui")
                            else (if (it) "Segna oggi" else "Marquer aujourd'hui"),
                            color = Color.White
                        )
                    }
                } else {
                    Text(if (it) "Non è un giorno assegnato" else "Pas un jour assigné",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.weight(1f))
                if (count > 0) Text(
                    (if (it) "$count giorni" else "$count jours"),
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary
                )
            }
            if (p.custom) {
                TextButton(onClick = { store.removeCustomPenance(p.id) }) {
                    Text(if (it) "Elimina" else "Supprimer", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun AddPenanceDialog(store: GameStore, it: Boolean, onClose: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(1) }
    val weekdays = remember { mutableStateListOf<Int>() }
    var computed by remember { mutableStateOf<String?>(null) }

    val periodChoices = listOf(
        null to (if (it) "aucune" else "aucune"),
        "friday_weekly" to (if (it) "venerdì" else "vendredis"),
        "wedfri_weekly" to (if (it) "mer. e ven." else "mer. et ven."),
        "lent_catholic" to (if (it) "Quaresima" else "Carême"),
        "advent_catholic" to (if (it) "Avvento" else "Avent"),
        "saint_michael" to (if (it) "san Michele" else "saint Michel")
    )

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    store.addCustomPenance(level, name, desc, weekdays.toList(), computed)
                    onClose()
                }
            ) { Text(if (it) "Aggiungi" else "Ajouter") }
        },
        dismissButton = { TextButton(onClick = onClose) { Text(if (it) "Annulla" else "Annuler") } },
        title = { Text(if (it) "Nuova penitenza" else "Nouvelle pénitence") },
        text = {
            Column(Modifier.heightIn(max = 460.dp).verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text(if (it) "Nome" else "Nom") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it },
                    label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Text(if (it) "Intensità" else "Intensité", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { lvl ->
                        val sel = level == lvl
                        Text("$lvl",
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .clickable { level = lvl }.padding(vertical = 10.dp),
                            color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(if (it) "Giorni della settimana" else "Jours de la semaine",
                    style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    (1..7).forEach { wd ->
                        val sel = weekdays.contains(wd)
                        Text(weekdayShort(wd, it).take(2),
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp))
                                .background(if (sel) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
                                .clickable { if (sel) weekdays.remove(wd) else weekdays.add(wd) }
                                .padding(vertical = 8.dp),
                            color = if (sel) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(if (it) "Periodo liturgico" else "Période liturgique",
                    style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                Column {
                    periodChoices.forEach { (key, label) ->
                        val sel = computed == key
                        Text(label,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                                .background(if (sel) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.Transparent)
                                .clickable { computed = key }.padding(vertical = 8.dp, horizontal = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    )
}
