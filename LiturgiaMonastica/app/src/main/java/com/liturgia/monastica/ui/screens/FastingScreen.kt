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

private fun familyLabel(f: String, it: Boolean) = when (f) {
    "orthodoxe" -> if (it) "Ortodossa" else "Orthodoxe"
    else -> if (it) "Cattolica" else "Catholique"
}

/** Résout si l'entrée est active aujourd'hui et la période correspondante à afficher, quand elle est calculable. */
private fun activeInfo(entry: FastEntry, today: DateYMD, italian: Boolean): Pair<Boolean, String>? {
    val year = today.year
    return when (entry.computed) {
        "great_lent" -> FastingCalendar.greatLent(year).let { it.contains(today) to rangeLabel(it, italian) }
        "apostles_fast" -> FastingCalendar.apostlesFast(year).let { it.contains(today) to rangeLabel(it, italian) }
        "dormition_fast" -> FastingCalendar.dormitionFast(year).let { it.contains(today) to rangeLabel(it, italian) }
        "nativity_fast" -> FastingCalendar.nativityFast(year).let { it.contains(today) to rangeLabel(it, italian) }
        "lent_catholic" -> FastingCalendar.lentCatholic(year).let { it.contains(today) to rangeLabel(it, italian) }
        "advent_catholic" -> FastingCalendar.adventCatholic(year).let { it.contains(today) to rangeLabel(it, italian) }
        "friday_weekly" -> (FastingCalendar.isFridayAbstinence(today) to (if (italian) "ogni venerdì" else "chaque vendredi"))
        "wedfri_weekly" -> (FastingCalendar.isWedFriFast(today) to (if (italian) "ogni mercoledì e venerdì" else "chaque mercredi et vendredi"))
        "michael_lent" -> FastingCalendar.michaelLent(year).let { it.contains(today) to rangeLabel(it, italian) }
        else -> null
    }
}

private fun rangeLabel(p: FastingCalendar.FastPeriod, italian: Boolean): String =
    "${FastingCalendar.formatDate(p.start, italian)} – ${FastingCalendar.formatDate(p.end, italian)}"

private fun fastMilestone(level: Int, italian: Boolean): String = when {
    level <= 0 -> if (italian) "Inizio" else "Commencement"
    level < 10 -> if (italian) "Primi passi" else "Premiers pas"
    level < 40 -> if (italian) "Fedeltà" else "Fidélité"
    level < 100 -> if (italian) "Una quarantena compiuta" else "Une quarantaine accomplie"
    level < 365 -> if (italian) "Cento giorni di magro" else "Cent jours maigres"
    else -> if (italian) "Un anno di digiuno" else "Une année de jeûne"
}

@Composable
fun FastingScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    val today = remember { FastingCalendar.todayYmd() }
    val todayKey = "${today.year}-${today.month}-${today.day}"
    var family by remember { mutableStateOf("orthodoxe") }
    val entries = remember(family) { repo.fastEntriesByFamily(family) }

    AppScaffold(
        title = if (it) "Calendario dei digiuni" else "Calendrier de jeûne",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("orthodoxe", "catholique").forEach { f ->
                    val sel = family == f
                    Text(
                        familyLabel(f, it),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .clickable { family = f }
                            .padding(vertical = 12.dp),
                        color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center
                    )
                }
            }
            Text(
                (if (it) "Aujourd'hui : " else "Aujourd'hui : ") + FastingCalendar.formatDate(today, it),
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
            ) {
                Text(
                    if (it) "Fonti universali" else "Sources universelles",
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                )
                entries.filter { e -> e.scope.contains("universelle") }.forEach { e -> FastCard(e, today, todayKey, store, it) }

                Text(
                    if (it) "Tradizioni e ordini" else "Traditions et ordres",
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                )
                entries.filterNot { e -> e.scope.contains("universelle") }.forEach { e -> FastCard(e, today, todayKey, store, it) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FastCard(entry: FastEntry, today: DateYMD, todayKey: String, store: GameStore, italian: Boolean) {
    val info = activeInfo(entry, today, italian)
    val active = info?.first == true
    Card(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (active) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(entry.name, style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f))
                if (active) {
                    Text(
                        if (italian) "● in corso" else "● en cours",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            Text(entry.scope, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            info?.second?.let { label ->
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 2.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(entry.rule, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            if (entry.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(entry.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            val prog = store.fastProgress
            val isChosen = prog.started && prog.fastId == entry.id
            Spacer(Modifier.height(10.dp))
            if (isChosen) {
                val isFastDay = active || entry.computed == null
                val doneToday = prog.lastDayKey == todayKey
                Text(
                    (if (italian) "Il mio digiuno · livello " else "Mon jeûne · niveau ") + "${prog.level}",
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    if (italian) "${prog.level} giorni di magro compiuti" else "${prog.level} jours maigres accomplis",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "— " + fastMilestone(prog.level, italian),
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(8.dp))
                when {
                    doneToday -> Text(
                        if (italian) "✓ Fatto oggi" else "✓ Fait aujourd'hui",
                        style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary
                    )
                    isFastDay -> Button(onClick = { store.fastConfirm(todayKey) }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (italian) "Sì, ho digiunato oggi" else "Oui, j'ai jeûné aujourd'hui")
                    }
                    else -> Text(
                        if (italian) "Oggi non è giorno di magro — riposo" else "Aujourd'hui n'est pas un jour maigre — repos",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { store.fastReset() }, contentPadding = PaddingValues(0.dp)) {
                    Text(if (italian) "Cambia digiuno" else "Changer de jeûne", style = MaterialTheme.typography.labelSmall)
                }
            } else {
                OutlinedButton(onClick = { store.fastStart(entry.id) }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (italian) "Scegli questo digiuno" else "Choisir ce jeûne")
                }
            }
        }
    }
}
