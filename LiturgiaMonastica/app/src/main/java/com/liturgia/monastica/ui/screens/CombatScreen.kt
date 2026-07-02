package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
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
import com.liturgia.monastica.R
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.data.Sin
import com.liturgia.monastica.ui.components.AppScaffold
import com.liturgia.monastica.ui.components.Backdrop
import com.liturgia.monastica.ui.components.GoldRule

private val OrthoGreen = Color(0xFF7A8A5A)
private val CathoBlue = Color(0xFF5A6E8A)

private fun accentColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) { Color(0xFF7A1F1B) }

/* ============================================================================
   1. LISTE DES NEUF COMBATS
   ============================================================================ */
@Composable
fun CombatListScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit, onSin: (String) -> Unit) {
    val it = store.lang == "it"
    LaunchedEffect(Unit) { store.combatRefresh() }
    val sins = remember { repo.sins() }

    AppScaffold(
        title = if (it) "Il Combattimento" else "Le Combat",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text(
                        if (it) "Le otto passioni, secondo Evagrio e Cassiano, più l'invidia"
                        else "Les huit pensées, selon Évagre et Cassien, augmentées de l'envie",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    GoldRule()
                    Text(
                        if (it)
                            "Dieci livelli, una settimana ciascuno. Ogni giorno va confermato: un solo giorno mancato riporta il livello a zero."
                        else
                            "Dix niveaux, une semaine chacun. Chaque jour doit être confirmé : une seule journée manquée remet le niveau à zéro.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(sins) { s ->
                val level = store.combatLevel(s.id)
                val days = store.combatDaysConfirmed(s.id)
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp)
                        .clickable { onSin(s.id) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(8.dp).clip(CircleShape).background(accentColor(s.accent))
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(s.name, style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                            Text(s.latin, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                (if (it) "Livello " else "Niveau ") + "$level/10",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                "$days/7",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

/* ============================================================================
   2. DÉTAIL D'UN COMBAT — hero + règle de prière + échelle des 10 niveaux
   ============================================================================ */
@Composable
fun CombatSinScreen(repo: ContentRepository, store: GameStore, sinId: String, onBack: () -> Unit, onLevel: (Int) -> Unit) {
    val it = store.lang == "it"
    LaunchedEffect(sinId) { store.combatRefresh() }
    val sin = remember(sinId) { repo.sin(sinId) } ?: return
    val currentLevel = store.combatLevel(sinId)
    var prayerOpen by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    val wasReset = sinId in store.combatJustReset

    AppScaffold(
        title = sin.name, onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text(
                        (if (it) "Virtù opposta — " else "Vertu opposée — ") + sin.virtue,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(sin.desc, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (wasReset) {
                item {
                    Card(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                if (it) "Un giorno senza conferma ha riportato questo combattimento a zero per il livello $currentLevel. Riprendi, senza scoraggiarti: è il combattimento stesso."
                                else "Une journée sans confirmation a remis ce combat à zéro pour le niveau $currentLevel. Reprends, sans te décourager : c'est le combat lui-même.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = { store.combatConsumeReset(sinId) }) {
                                Text("OK", color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
            item { PrayerRuleCard(sin, expanded = prayerOpen, onToggle = { prayerOpen = !prayerOpen }, italian = it) }
            item { Spacer(Modifier.height(10.dp)) }
            items(sin.levels.size) { idx ->
                val n = idx + 1
                val state = when {
                    n < currentLevel -> LevelState.DONE
                    n == currentLevel -> LevelState.CURRENT
                    else -> LevelState.LOCKED
                }
                LevelRow(
                    number = n, title = sin.levels[idx].title, state = state,
                    daysConfirmed = if (n == currentLevel) store.combatDaysConfirmed(sinId) else 0,
                    italian = it,
                    onClick = { if (state != LevelState.LOCKED) onLevel(n) }
                )
            }
            item {
                Spacer(Modifier.height(20.dp))
                TextButton(onClick = { showReset = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        if (it) "Ricominciare questo combattimento" else "Recommencer ce combat",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showReset) AlertDialog(
        onDismissRequest = { showReset = false },
        confirmButton = {
            TextButton(onClick = { store.combatResetSin(sinId); showReset = false }) {
                Text(if (it) "Sì, ricomincia" else "Oui, recommencer")
            }
        },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text(if (it) "Annulla" else "Annuler") } },
        title = { Text(if (it) "Ricominciare da capo?" else "Tout recommencer ?") },
        text = {
            Text(
                if (it) "Tornerai al livello 1, giorno 0, per questo combattimento."
                else "Vous reviendrez au niveau 1, jour 0, pour ce combat."
            )
        }
    )
}

private enum class LevelState { DONE, CURRENT, LOCKED }

@Composable
private fun LevelRow(number: Int, title: String, state: LevelState, daysConfirmed: Int, italian: Boolean, onClick: () -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(enabled = state != LevelState.LOCKED, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(34.dp).clip(CircleShape)
                    .background(
                        when (state) {
                            LevelState.DONE -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                            LevelState.CURRENT -> MaterialTheme.colorScheme.secondary
                            LevelState.LOCKED -> Color.Transparent
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    LevelState.DONE -> Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.size(18.dp))
                    LevelState.LOCKED -> Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    LevelState.CURRENT -> Text("$number", color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title, style = MaterialTheme.typography.bodyLarge,
                    color = if (state == LevelState.LOCKED) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    (if (italian) "Settimana " else "Semaine ") + "$number/10" +
                        if (state == LevelState.CURRENT) " · $daysConfirmed/7" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    }
}

@Composable
private fun PrayerRuleCard(sin: Sin, expanded: Boolean, onToggle: () -> Unit, italian: Boolean) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onToggle),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    (if (italian) "Regola di preghiera" else "Règle de prière").uppercase(),
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(if (expanded) "▴" else "▾", color = MaterialTheme.colorScheme.tertiary)
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                PrayerItem("☦ " + (if (italian) "Preghiera ortodossa" else "Prière orthodoxe"), OrthoGreen, sin.prayer.orthoPrayer.text, sin.prayer.orthoPrayer.source, quote = true)
                Spacer(Modifier.height(12.dp))
                PrayerItem("✛ " + (if (italian) "Preghiera cattolica" else "Prière catholique"), CathoBlue, sin.prayer.cathoPrayer.text, sin.prayer.cathoPrayer.source, quote = true)
                Spacer(Modifier.height(12.dp))
                PrayerItem("☦ " + (if (italian) "Consiglio di un Padre" else "Conseil d'un Père"), OrthoGreen, sin.prayer.orthoCounsel.text, sin.prayer.orthoCounsel.source, quote = false)
                Spacer(Modifier.height(12.dp))
                PrayerItem("✛ " + (if (italian) "Consiglio di un santo" else "Conseil d'un saint"), CathoBlue, sin.prayer.cathoCounsel.text, sin.prayer.cathoCounsel.source, quote = false)
            }
        }
    }
}

@Composable
private fun PrayerItem(tag: String, tagColor: Color, text: String, source: String, quote: Boolean) {
    Column {
        Text(
            tag, style = MaterialTheme.typography.labelSmall, color = tagColor,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(tagColor.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (quote) "« $text »" else text,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = if (quote) FontStyle.Italic else FontStyle.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(3.dp))
        Text(source, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/* ============================================================================
   3. ÉCRAN D'UN NIVEAU — pratiques, citation, confirmation quotidienne
   ============================================================================ */
@Composable
fun CombatLevelScreen(repo: ContentRepository, store: GameStore, sinId: String, levelNum: Int, onBack: () -> Unit) {
    val it = store.lang == "it"
    val sin = remember(sinId) { repo.sin(sinId) } ?: return
    val level = sin.levels.getOrNull(levelNum - 1) ?: return
    val currentLevel = store.combatLevel(sinId)
    val isCurrent = levelNum == currentLevel
    val daysConfirmed = if (isCurrent) store.combatDaysConfirmed(sinId) else 7
    val verse = sin.verses[daysConfirmed % sin.verses.size]
    val doneToday = store.combatDoneToday(sinId)
    val fullyAccomplished = currentLevel >= 10 && store.combatDaysConfirmed(sinId) >= 7 && isCurrent

    AppScaffold(
        title = (if (it) "Livello " else "Niveau ") + "$levelNum/10 — ${sin.name}",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Backdrop(R.drawable.seraphim_sarov, Modifier.padding(pad), dim = 0.72f) {
            Column(
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    level.title, style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFF1E4BD), textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xCC201C16)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        level.practice, style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFF1E4BD), modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                DuoBox("☦ " + (if (it) "Pratica ortodossa" else "Pratique orthodoxe"), OrthoGreen, level.ortho)
                Spacer(Modifier.height(8.dp))
                DuoBox("✛ " + (if (it) "Pratica cattolica" else "Pratique catholique"), CathoBlue, level.catho)
                Spacer(Modifier.height(14.dp))

                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33C8A24B)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            (if (it) "Citazione del giorno" else "Citation du jour").uppercase(),
                            style = MaterialTheme.typography.labelSmall, color = Color(0xFFD8B768)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "« ${verse.text} »", style = MaterialTheme.typography.bodyLarge,
                            fontStyle = FontStyle.Italic, color = Color(0xFFF1E4BD)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            verse.ref, style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB9A87A), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                // 7 streak dots
                Row {
                    repeat(7) { i ->
                        val on = i < daysConfirmed
                        Box(
                            Modifier.padding(4.dp).size(24.dp).clip(CircleShape)
                                .background(if (on) Color(0xFFD8B768) else Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (on) "✓" else "${i + 1}",
                                color = if (on) Color(0xFF14110D) else Color.White.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                when {
                    !isCurrent -> {
                        Text(
                            if (it) "Livello compiuto" else "Niveau accompli",
                            style = MaterialTheme.typography.titleMedium, color = Color(0xFFD8B768)
                        )
                    }
                    fullyAccomplished -> {
                        Text(
                            if (it) "✦ Questo combattimento è compiuto. Che continui nel cuore. ✦"
                            else "✦ Ce combat est achevé. Qu'il se poursuive dans le cœur. ✦",
                            style = MaterialTheme.typography.titleMedium, color = Color(0xFFD8B768),
                            textAlign = TextAlign.Center
                        )
                    }
                    doneToday -> {
                        Text(
                            if (it) "✓ Oggi: pratica compiuta e confermata." else "✓ Aujourd'hui : pratique accomplie et confirmée.",
                            style = MaterialTheme.typography.titleMedium, color = Color(0xFFD8B768),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (it) "Torna domani per confermare il giorno successivo. Un giorno senza conferma riporta questo livello a zero."
                            else "Reviens demain pour confirmer le jour suivant. Un jour sans confirmation remet ce niveau à zéro.",
                            style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A87A),
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        Button(
                            onClick = { store.combatConfirmToday(sinId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1F1B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (it) "Ho compiuto oggi la pratica ortodossa e/o cattolica"
                                else "J'ai accompli aujourd'hui la pratique orthodoxe et/ou catholique",
                                color = Color.White, textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (it) "⚠ Se non confermi domani, questo livello ricomincerà interamente da zero."
                            else "⚠ Si tu ne confirmes pas demain, ce niveau recommencera entièrement à zéro.",
                            style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A87A),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DuoBox(label: String, color: Color, text: String) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFF1E4BD))
        }
    }
}
