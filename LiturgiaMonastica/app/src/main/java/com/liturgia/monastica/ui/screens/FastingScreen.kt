package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
    "universel" -> if (it) "Universale" else "Universel"
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
        "saint_michael" -> FastingCalendar.saintMichaelFast(year).let { it.contains(today) to rangeLabel(it, italian) }
        "friday_weekly" -> (FastingCalendar.isFridayAbstinence(today) to (if (italian) "ogni venerdì" else "chaque vendredi"))
        "wedfri_weekly" -> (FastingCalendar.isWedFriFast(today) to (if (italian) "ogni mercoledì e venerdì" else "chaque mercredi et vendredi"))
        else -> null
    }
}

private fun rangeLabel(p: FastingCalendar.FastPeriod, italian: Boolean): String =
    "${FastingCalendar.formatDate(p.start, italian)} – ${FastingCalendar.formatDate(p.end, italian)}"

@Composable
fun FastingScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    var mode by remember { mutableStateOf("calendrier") }

    AppScaffold(
        title = if (it) "Digiuno" else "Le jeûne",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "calendrier" to (if (it) "Calendario" else "Calendrier"),
                    "suivi" to (if (it) "Il mio digiuno" else "Mon jeûne")
                ).forEach { (m, label) ->
                    val sel = mode == m
                    Text(
                        label,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .clickable { mode = m }
                            .padding(vertical = 12.dp),
                        color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center
                    )
                }
            }
            if (mode == "calendrier") FastCalendar(repo, it) else FastTracker(repo, store, it)
        }
    }
}

/* ============================================================================
   CALENDRIER — lecture seule des jeûnes traditionnels
   ============================================================================ */
@Composable
private fun FastCalendar(repo: ContentRepository, italian: Boolean) {
    val today = remember { FastingCalendar.todayYmd() }
    var family by remember { mutableStateOf("orthodoxe") }
    val entries = remember(family) { repo.fastEntriesByFamily(family) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("orthodoxe", "catholique").forEach { f ->
                val sel = family == f
                Text(
                    familyLabel(f, italian),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (sel) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
                        .clickable { family = f }
                        .padding(vertical = 10.dp),
                    color = if (sel) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center
                )
            }
        }
        Text(
            (if (italian) "Oggi : " else "Aujourd'hui : ") + FastingCalendar.formatDate(today, italian),
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Text(
                if (italian) "Fonti universali" else "Sources universelles",
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
            )
            entries.filter { e -> e.scope.contains("universelle") }.forEach { e -> FastCard(e, today, italian) }
            Text(
                if (italian) "Tradizioni e ordini" else "Traditions et ordres",
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
            )
            entries.filterNot { e -> e.scope.contains("universelle") }.forEach { e -> FastCard(e, today, italian) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FastCard(entry: FastEntry, today: DateYMD, italian: Boolean) {
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
        }
    }
}

/* ============================================================================
   SUIVI — un jeûne suivi jour après jour (remise à zéro stricte si un jour est manqué)
   ============================================================================ */
@Composable
private fun FastTracker(repo: ContentRepository, store: GameStore, italian: Boolean) {
    LaunchedEffect(Unit) { store.fastTrackRefresh() }
    val p = store.fastTrackProgress
    if (!p.started) FastTrackerSetup(repo, store, italian)
    else FastTrackerRunning(repo, store, italian)
}

@Composable
private fun FastTrackerSetup(repo: ContentRepository, store: GameStore, italian: Boolean) {
    val tracks = remember { repo.fastTracks() }
    var selected by remember { mutableStateOf<FastTrackType?>(null) }
    var customDays by remember { mutableStateOf(40) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(
            if (italian) "Scegli un digiuno da vivere, giorno dopo giorno."
            else "Choisis un jeûne à vivre, jour après jour.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            if (italian) "Come una novena : se salti un giorno, il conteggio riparte da zero."
            else "Comme une neuvaine : si tu manques un jour, le compte repart à zéro.",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
        )
        tracks.forEach { t ->
            val isSel = selected?.id == t.id
            Card(
                Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selected = t },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSel) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (italian) t.nameIt else t.nameFr, style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f))
                        if (t.days > 0) Text(
                            "${t.days} " + (if (italian) "gg" else "j"),
                            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Text(familyLabel(t.family, italian), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (isSel) {
                        Spacer(Modifier.height(6.dp))
                        Text(if (italian) t.descIt else t.descFr, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(6.dp))
                        Text(t.rule, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (t.days == 0) {
                            Spacer(Modifier.height(10.dp))
                            Text(if (italian) "Durata (giorni)" else "Durée (jours)",
                                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Stepper(value = customDays, onDec = { if (customDays > 1) customDays-- },
                                    onInc = { if (customDays < 365) customDays++ })
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = {
                val t = selected ?: return@Button
                val target = if (t.days > 0) t.days else customDays
                val label = if (italian) t.nameIt else t.nameFr
                store.fastTrackStart(t.id, label, target)
            },
            enabled = selected != null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A4A2A))
        ) {
            Text(if (italian) "Inizia questo digiuno" else "Commencer ce jeûne", color = Color.White)
        }

        val completed = store.fastTrackProgress.completed
        if (completed.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            Text(if (italian) "Digiuni compiuti" else "Jeûnes accomplis",
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
            completed.sortedByDescending { it.endEpochDay }.forEach { e ->
                Text("• ${e.label} — ${e.days} " + (if (italian) "giorni" else "jours"),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp))
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FastTrackerRunning(repo: ContentRepository, store: GameStore, italian: Boolean) {
    val p = store.fastTrackProgress
    val track = remember(p.typeId) { repo.fastTrack(p.typeId) }
    var showAbandon by remember { mutableStateOf(false) }
    val pct = if (p.target > 0) (p.day.toFloat() / p.target).coerceIn(0f, 1f) else 0f

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(p.label, style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(
            (if (italian) "Giorno " else "Jour ") + "${p.day} / ${p.target}",
            style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = pct,
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp))
        )
        Spacer(Modifier.height(16.dp))

        if (store.fastTrackJustReset) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0x33B00020)), shape = RoundedCornerShape(8.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        if (italian) "Un giorno mancato : il conteggio è ripartito da zero."
                        else "Un jour manqué : le compte est reparti à zéro.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { store.fastTrackConsumeReset() }) { Text("OK") }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        track?.let {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(8.dp)) {
                Text(it.rule, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(14.dp))
            }
            Spacer(Modifier.height(16.dp))
        }

        if (store.fastTrackDoneToday()) {
            Text(
                if (italian) "✓ Oggi hai digiunato. A domani." else "✓ Aujourd'hui, tu as jeûné. À demain.",
                style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center
            )
        } else {
            Button(
                onClick = { store.fastTrackConfirmToday() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A4A2A))
            ) {
                Text(if (italian) "Ho digiunato oggi" else "J'ai jeûné aujourd'hui", color = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (italian) "⚠ Un giorno mancato riporta il conteggio a zero."
                else "⚠ Un jour manqué remet le compte à zéro.",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(22.dp))
        TextButton(onClick = { showAbandon = true }) {
            Text(if (italian) "Abbandona questo digiuno" else "Abandonner ce jeûne",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (showAbandon) AlertDialog(
        onDismissRequest = { showAbandon = false },
        confirmButton = { TextButton(onClick = { store.fastTrackAbandon(); showAbandon = false }) {
            Text(if (italian) "Sì, abbandona" else "Oui, abandonner") } },
        dismissButton = { TextButton(onClick = { showAbandon = false }) {
            Text(if (italian) "Annulla" else "Annuler") } },
        title = { Text(if (italian) "Abbandonare il digiuno ?" else "Abandonner le jeûne ?") },
        text = { Text(if (italian) "Perderai il conteggio attuale." else "Tu perdras le compte actuel.") }
    )
}

@Composable
private fun Stepper(value: Int, onDec: () -> Unit, onInc: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StepBtn("−", onDec)
        Text("$value", style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.widthIn(min = 48.dp).padding(horizontal = 8.dp), textAlign = TextAlign.Center)
        StepBtn("+", onInc)
    }
}

@Composable
private fun StepBtn(label: String, onClick: () -> Unit) {
    Box(
        Modifier.size(40.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Text(label, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) }
}
