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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.R
import com.liturgia.monastica.data.*
import com.liturgia.monastica.ui.components.AppScaffold
import com.liturgia.monastica.ui.components.Backdrop

private fun ruleAccent(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) { Color(0xFFC8A24B) }

private val familyOrder = listOf("orthodoxe", "catholique", "charismatique")
private fun familyLabel(f: String, it: Boolean) = when (f) {
    "orthodoxe" -> if (it) "Ortodossa" else "Orthodoxe"
    "catholique" -> if (it) "Cattolica" else "Catholique"
    else -> if (it) "Carismatica cattolica" else "Charismatique catholique"
}

/** Extra, procedurally generated intensifications once every named devotion is unlocked — the Rule never truly ends. */
private val EXTRA_INTENSIFIERS = listOf(
    "Prolonge ton temps d'oraison silencieuse de cinq minutes de plus.",
    "Ajoute un jour de jeûne supplémentaire ce mois-ci.",
    "Reprends une veille ou un temps d'adoration nocturne supplémentaire.",
    "Consacre une journée entière au silence complet ce mois-ci.",
    "Double, une semaine sur deux, l'une des dévotions déjà acquises.",
    "Ajoute un acte de charité cachée chaque jour de cette semaine."
)

private fun activeDevotionCount(tradition: RuleTradition, diff: RuleDifficulty, level: Int): Int {
    val steps = (level - 1) / diff.paceLevels
    return (diff.startCount + steps).coerceIn(0, tradition.devotions.size)
}

private fun extraIntensifierSteps(tradition: RuleTradition, diff: RuleDifficulty, level: Int): Int {
    val unlockStepsTotal = (tradition.devotions.size - diff.startCount).coerceAtLeast(0)
    val currentStep = (level - 1) / diff.paceLevels
    return (currentStep - unlockStepsTotal).coerceAtLeast(0)
}

@Composable
fun RuleScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    LaunchedEffect(Unit) { store.ruleRefresh() }
    AppScaffold(
        title = if (it) "La Regola" else "La Règle",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        if (!store.ruleProgress.started) {
            RuleSetup(repo, store, Modifier.padding(pad), italian = it)
        } else {
            RuleRunning(repo, store, Modifier.padding(pad), italian = it)
        }
    }
}

/* ============================================================================
   CHOIX — famille, tradition/ordre, difficulté
   ============================================================================ */
@Composable
private fun RuleSetup(repo: ContentRepository, store: GameStore, modifier: Modifier, italian: Boolean) {
    val allTraditions = remember { repo.ruleTraditions() }
    var family by remember { mutableStateOf<String?>(null) }
    var selected by remember { mutableStateOf<RuleTradition?>(null) }
    var difficulty by remember { mutableStateOf<RuleDifficulty?>(null) }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (italian) "Una regola di preghiera per crescere in santità" else "Une règle de prière pour grandir en sainteté",
            style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            if (italian)
                "Scegli una famiglia spirituale, poi una tradizione o un ordine, poi una difficoltà. Questa scelta sarà fissa: per cambiarla, bisognerà ricominciare da zero."
            else
                "Choisis une famille spirituelle, puis une tradition ou un ordre, puis une difficulté. Ce choix restera fixé : pour en changer, il faudra tout recommencer à zéro.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 10.dp)
        )

        Text(
            (if (italian) "1. Famiglia spirituale" else "1. Famille spirituelle").uppercase(),
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 6.dp)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            familyOrder.forEach { f ->
                val sel = family == f
                Text(
                    familyLabel(f, italian),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .clickable { family = f; selected = null }
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center
                )
            }
        }

        if (family != null) {
            Text(
                (if (italian) "2. Tradizione o ordine" else "2. Tradition ou ordre").uppercase(),
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 6.dp)
            )
            allTraditions.filter { t -> t.family == family }.forEach { t ->
                val isSel = selected?.id == t.id
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selected = t },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSel) ruleAccent(t.accent).copy(alpha = 0.28f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(t.name, style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        Text(t.subtitle, style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (isSel) {
                            Spacer(Modifier.height(8.dp))
                            Text(t.desc, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                (if (italian) "Ispirazione: " else "Inspiration : ") + t.patron,
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }

        if (selected != null) {
            Text(
                (if (italian) "3. Difficoltà" else "3. Difficulté").uppercase(),
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 6.dp)
            )
            Text(
                if (italian) "La difficoltà determina il ritmo e l'intensità delle preghiere aggiunte ogni mese."
                else "La difficulté détermine le rythme et l'intensité des prières ajoutées chaque mois.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            RuleDifficulty.entries.forEach { d ->
                val sel = difficulty == d
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 3.dp).clickable { difficulty = d },
                    colors = CardDefaults.cardColors(
                        containerColor = if (sel) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(Modifier.padding(14.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(d.label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            (if (italian) "nuova devozione ogni " else "nouvelle dévotion tous les ") +
                                "${d.paceLevels} " + (if (italian) "settimane" else "semaines"),
                            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { val t = selected; val d = difficulty; if (t != null && d != null) store.ruleStart(t.id, d) },
            enabled = selected != null && difficulty != null,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1F1B))
        ) {
            Text(if (italian) "Inizio questa regola" else "Commencer cette règle", color = Color.White)
        }
        Spacer(Modifier.height(24.dp))
    }
}

/* ============================================================================
   EN COURS — la règle du niveau actuel
   ============================================================================ */
@Composable
private fun RuleRunning(repo: ContentRepository, store: GameStore, modifier: Modifier, italian: Boolean) {
    val p = store.ruleProgress
    val tradition = remember(p.traditionId) { repo.ruleTradition(p.traditionId) }
    var showReset by remember { mutableStateOf(false) }

    if (tradition == null) {
        Column(modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (italian) "Tradizione non trovata." else "Tradition introuvable.")
            Button(onClick = { store.ruleReset() }) { Text(if (italian) "Ricomincia" else "Recommencer") }
        }
        return
    }

    val diff = RuleDifficulty.fromKey(p.difficulty)
    val level = p.level
    val month = ((level - 1) / 4) + 1
    val activeCount = activeDevotionCount(tradition, diff, level)
    val unlocked = tradition.devotions.take(activeCount)
    val extraSteps = extraIntensifierSteps(tradition, diff, level)
    val accent = ruleAccent(tradition.accent)

    Backdrop(R.drawable.madonna_silenzio, modifier, dim = 0.7f) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(tradition.name, style = MaterialTheme.typography.headlineSmall, color = Color(0xFFF1E4BD), textAlign = TextAlign.Center)
            Text(tradition.subtitle, style = MaterialTheme.typography.labelLarge, color = Color(0xFFD8B768), textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text(
                (if (italian) "Livello " else "Niveau ") + "$level · " + (if (italian) "Mese " else "Mois ") + "$month · ${diff.label}",
                style = MaterialTheme.typography.titleMedium, color = Color.White
            )

            if (store.ruleJustDemoted) {
                Spacer(Modifier.height(10.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xCC5A1512)), shape = RoundedCornerShape(6.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            if (italian) "Un giorno mancato ti ha fatto scendere al livello $level, da rifare interamente."
                            else "Un jour manqué t'a fait redescendre au niveau $level, à revalider entièrement.",
                            style = MaterialTheme.typography.bodySmall, color = Color(0xFFF1E4BD)
                        )
                        TextButton(onClick = { store.ruleConsumeDemoted() }) { Text("OK", color = Color(0xFFD8B768)) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row {
                repeat(7) { i ->
                    val on = i < p.daysConfirmed
                    Box(
                        Modifier.padding(4.dp).size(22.dp).clip(CircleShape)
                            .background(if (on) Color(0xFFD8B768) else Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (on) "✓" else "${i + 1}", color = if (on) Color(0xFF14110D) else Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))

            SectionHeader(if (italian) "Il fondamento quotidiano" else "Le socle quotidien", accent)
            tradition.dailyCore.forEach { core -> RuleLine(core, bullet = "✛") }

            Spacer(Modifier.height(14.dp))
            SectionHeader(if (italian) "La tua regola attuale" else "Ta règle actuelle", accent)
            unlocked.forEachIndexed { idx, dev ->
                val isNewest = idx == unlocked.lastIndex
                RuleDevotionLine(dev, highlighted = isNewest, italian = italian)
            }

            if (extraSteps > 0) {
                Spacer(Modifier.height(14.dp))
                SectionHeader(if (italian) "Intensificazioni" else "Intensifications", accent)
                for (i in 0 until extraSteps) {
                    val base = EXTRA_INTENSIFIERS[i % EXTRA_INTENSIFIERS.size]
                    val cycle = i / EXTRA_INTENSIFIERS.size
                    val text = if (cycle > 0) "$base (${if (italian) "rinnovato" else "renouvelé"} ×${cycle + 1})" else base
                    RuleLine(text, bullet = "↑")
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    if (italian)
                        "Un padre spirituale ricorderebbe qui la discrezione: la perseveranza conta più dell'eccesso. Non esitare a consultare un accompagnatore reale."
                    else
                        "Un père spirituel rappellerait ici la discrétion : la persévérance importe plus que la démesure. N'hésite pas à demander conseil à un accompagnateur réel.",
                    style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A87A),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(20.dp))

            if (store.ruleDoneToday()) {
                Text(
                    if (italian) "✓ Oggi è fatto. A domani." else "✓ Aujourd'hui est fait. À demain.",
                    style = MaterialTheme.typography.titleMedium, color = Color(0xFFD8B768), textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (italian) "Se salti un giorno, si ridiscende di un livello."
                    else "Si un jour est manqué, on redescend d'un niveau.",
                    style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A87A), textAlign = TextAlign.Center
                )
            } else {
                Button(
                    onClick = { store.ruleConfirmToday() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1F1B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (italian) "Ho compiuto oggi questa regola di preghiera" else "J'ai accompli aujourd'hui cette règle de prière",
                        color = Color.White, textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    if (italian) "⚠ Un giorno mancato ti farà scendere di un livello."
                    else "⚠ Un jour manqué te fera redescendre d'un niveau.",
                    style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A87A), textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(22.dp))
            TextButton(onClick = { showReset = true }) {
                Text(if (italian) "Cambiare regola (ricomincia da zero)" else "Changer de règle (recommencer à zéro)", color = Color(0xFFB9A87A))
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    if (showReset) AlertDialog(
        onDismissRequest = { showReset = false },
        confirmButton = {
            TextButton(onClick = { store.ruleReset(); showReset = false }) {
                Text(if (italian) "Sì, ricomincia" else "Oui, recommencer")
            }
        },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text(if (italian) "Annulla" else "Annuler") } },
        title = { Text(if (italian) "Cambiare regola?" else "Changer de règle ?") },
        text = {
            Text(
                if (italian) "Perderai la progressione attuale e potrai scegliere una nuova famiglia, tradizione e difficoltà."
                else "Tu perdras la progression actuelle et pourras choisir une nouvelle famille, tradition et difficulté."
            )
        }
    )
}

@Composable
private fun SectionHeader(title: String, accent: Color) {
    Text(
        title.uppercase(), style = MaterialTheme.typography.labelLarge, color = accent,
        fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    )
}

@Composable
private fun RuleLine(text: String, bullet: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(bullet, color = Color(0xFFD8B768), modifier = Modifier.padding(end = 8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFF1E4BD))
    }
}

@Composable
private fun RuleDevotionLine(dev: RuleDevotion, highlighted: Boolean, italian: Boolean) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) Color(0x33D8B768) else Color(0x22FFFFFF)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (dev.signature) Text("★ ", color = Color(0xFFD8B768))
                Text(dev.name, style = MaterialTheme.typography.titleSmall, color = Color(0xFFF1E4BD), fontWeight = FontWeight.Medium)
                if (highlighted) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "· " + (if (italian) "nuova" else "nouvelle"),
                        style = MaterialTheme.typography.labelSmall, color = Color(0xFFD8B768), fontStyle = FontStyle.Italic
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(dev.note, style = MaterialTheme.typography.bodySmall, color = Color(0xFFE9D9A8))
        }
    }
}
