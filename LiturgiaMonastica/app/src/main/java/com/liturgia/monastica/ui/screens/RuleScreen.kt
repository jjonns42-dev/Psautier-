package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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

/** Étapes réelles de formation religieuse, communes aux trois familles (voir recherche : Shalom a
 *  Postulantado → Discipulado → Promessas Temporárias → Definitivas ; les ordres catholiques et les
 *  monastères orthodoxes suivent le même schéma postulat/noviciat/profession). */
private fun fullUnlockLevel(tradition: RuleTradition, diff: RuleDifficulty): Int {
    val stepsNeeded = (maxDevotionsFor(tradition, diff) - diff.startCount).coerceAtLeast(0)
    return 1 + diff.paceLevels * stepsNeeded
}

private data class Stage(val label: String, val sub: String)

private fun stageFor(level: Int, tradition: RuleTradition, diff: RuleDifficulty, italian: Boolean): Stage {
    val fullLevel = fullUnlockLevel(tradition, diff)
    return when {
        level < fullLevel && level <= diff.paceLevels ->
            Stage(if (italian) "Postulato" else "Postulat", if (italian) "Découverte de la règle" else "Découverte de la règle")
        level < fullLevel ->
            Stage(if (italian) "Noviziato" else "Noviciat", if (italian) "La règle s'apprend, dévotion après dévotion" else "La règle s'apprend, dévotion après dévotion")
        level == fullLevel -> {
            val sub = if (italian)
                "La regola intera dell'ordine, senza riduzione: sei pienamente nel carisma."
            else
                "La règle intégrale de l'ordre, sans réduction : tu es pleinement dans le charisme."
            Stage(if (italian) "Professione" else "Profession", sub)
        }
        else -> {
            val weeksSince = level - fullLevel
            val label = when {
                weeksSince < 4 -> if (italian) "Fedeltà — settimana $weeksSince" else "Fidélité — semaine $weeksSince"
                weeksSince < 12 -> if (italian) "~1 mese di fedeltà" else "~1 mois de fidélité"
                weeksSince < 26 -> if (italian) "~3 mesi di fedeltà" else "~3 mois de fidélité"
                weeksSince < 52 -> if (italian) "~6 mesi di fedeltà" else "~6 mois de fidélité"
                else -> {
                    val years = weeksSince / 52
                    val jubilee = years in setOf(5, 10, 15, 20, 25, 30, 40, 50)
                    if (jubilee) (if (italian) "Giubileo — $years anni di fedeltà" else "Jubilé — $years ans de fidélité")
                    else (if (italian) "$years anni di fedeltà" else "$years ans de fidélité")
                }
            }
            Stage(label, if (italian) "Vivere la regola, senza aggiungere, senza cedere" else "Vivre la règle, sans en ajouter, sans en céder")
        }
    }
}

/** Nombre de dévotions accessibles au maximum : toutes les difficultés atteignent 100 % de la Règle,
 *  seul le rythme pour y parvenir diffère (paceLevels). */
private fun maxDevotionsFor(tradition: RuleTradition, diff: RuleDifficulty): Int {
    val cap = kotlin.math.ceil(tradition.devotions.size * diff.capFraction).toInt()
    return cap.coerceIn(diff.startCount, tradition.devotions.size)
}

private fun activeDevotionCount(tradition: RuleTradition, diff: RuleDifficulty, level: Int): Int {
    val steps = (level - 1) / diff.paceLevels
    return (diff.startCount + steps).coerceIn(0, maxDevotionsFor(tradition, diff))
}

/** Résout les dévotions actives en retirant celles qu'une dévotion plus récente remplace
 *  (ex. "Trois Heures de l'Office" retire "Une Heure" et "Deux Heures" de l'affichage). */
private fun resolveActive(tradition: RuleTradition, activeCount: Int): List<RuleDevotion> {
    val raw = tradition.devotions.take(activeCount)
    val supersededIndices = raw.mapNotNull { if (it.supersedes >= 0) it.supersedes else null }.toSet()
    return raw.filterIndexed { idx, _ -> idx !in supersededIndices }
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
    var building by remember { mutableStateOf(false) }

    if (building) {
        RuleBuilder(store = store, modifier = modifier, italian = italian, onCancel = { building = false })
        return
    }

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

        OutlinedButton(
            onClick = {
                val f = familyOrder.random()
                val t = allTraditions.filter { tr -> tr.family == f }.random()
                family = f
                selected = t
                difficulty = RuleDifficulty.entries.toTypedArray().random()
            },
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Text("🎲 " + (if (italian) "Scegli a caso la mia regola" else "Choisir ma règle au hasard"))
        }

        OutlinedButton(
            onClick = { building = true },
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text("✍️ " + (if (italian) "Scrivi la tua regola" else "Écris ta propre règle"))
        }

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
                        Column {
                            Text(d.label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                when (d) {
                                    RuleDifficulty.DEBUTANT ->
                                        if (italian) "Regola integrale · cammino lento" else "Règle intégrale · chemin lent"
                                    RuleDifficulty.NORMAL ->
                                        if (italian) "Regola integrale · cammino ordinario" else "Règle intégrale · chemin ordinaire"
                                    RuleDifficulty.DIFFICILE ->
                                        if (italian) "Regola integrale · cammino esigente" else "Règle intégrale · chemin exigeant"
                                },
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                (if (italian) "nuova devozione ogni " else "nouvelle dévotion tous les ") +
                                    "${d.paceLevels} " + (if (italian) "settimane" else "semaines"),
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                (if (italian) "durées ×" else "durées ×") + "${d.timeMultiplier}",
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
    val tradition = remember(p.traditionId, store.customRule) {
        if (p.traditionId == store.customRuleId) store.customRule else repo.ruleTradition(p.traditionId)
    }
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
    val activeCount = activeDevotionCount(tradition, diff, level)
    val unlocked = resolveActive(tradition, activeCount)
    val stage = stageFor(level, tradition, diff, italian)
    val accent = ruleAccent(tradition.accent)
    val fullLevel = fullUnlockLevel(tradition, diff)
    val weeksSince = (level - fullLevel).coerceAtLeast(0)
    val currentYear = if (level > fullLevel) weeksSince / 52 else 0
    var showHistory by remember { mutableStateOf(false) }
    val showRenewal = currentYear >= 1 && currentYear > p.lastRenewalYear && weeksSince % 52 == 0

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
                stage.label, style = MaterialTheme.typography.titleLarge, color = Color(0xFFD8B768),
                textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold
            )
            Text(
                stage.sub, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB9A87A),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                (if (italian) "Livello " else "Niveau ") + "$level · ${diff.label}",
                style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.6f)
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
            val totalMinutes = unlocked.sumOf { scaledMinutes(it.minutes, diff) }
            if (totalMinutes > 0) {
                Text(
                    (if (italian) "Tempo di preghiera stimato oggi: ~" else "Temps de prière estimé aujourd'hui : ~") +
                        "$totalMinutes " + (if (italian) "min" else "min"),
                    style = MaterialTheme.typography.labelMedium, color = Color(0xFFD8B768),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            unlocked.forEachIndexed { idx, dev ->
                val isNewest = idx == unlocked.lastIndex
                RuleDevotionLine(dev, highlighted = isNewest, italian = italian, minutes = scaledMinutes(dev.minutes, diff))
            }

            if (level > fullUnlockLevel(tradition, diff)) {
                Spacer(Modifier.height(14.dp))
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x22D8B768)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        if (italian)
                            "La regola non chiede più nulla di nuovo: dal giorno della Professione, il combattimento è vivere questa stessa regola, fedelmente, settimana dopo settimana."
                        else
                            "La règle ne demande plus rien de nouveau : depuis le jour de la Profession, le combat consiste à vivre cette même règle, fidèlement, semaine après semaine.",
                        style = MaterialTheme.typography.bodySmall, color = Color(0xFFE9D9A8),
                        modifier = Modifier.padding(14.dp), textAlign = TextAlign.Center
                    )
                }
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
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                TextButton(onClick = { showHistory = true }) {
                    Text(if (italian) "Storico dei livelli" else "Historique des niveaux", color = Color(0xFFB9A87A))
                }
                TextButton(onClick = { showReset = true }) {
                    Text(
                        if (italian) "Cambiare regola (ricomincia da zero)" else "Changer de règle (recommencer à zéro)",
                        color = Color(0xFFB9A87A)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    if (showRenewal) AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = { store.ruleAcknowledgeRenewal(currentYear) }) {
                Text(if (italian) "Rinnovo il mio impegno" else "Je renouvelle mon engagement")
            }
        },
        title = {
            Text(
                if (italian) "Rinnovamento dei voti — $currentYear ${if (currentYear>1) "anni" else "anno"} di fedeltà"
                else "Renouvellement des vœux — $currentYear ${if (currentYear>1) "ans" else "an"} de fidélité"
            )
        },
        text = {
            Text(
                if (italian)
                    "Come i religiosi rinnovano i loro voti temporanei, prenditi un momento per rinnovare interiormente il tuo impegno in questa regola, prima di continuare."
                else
                    "Comme les religieux renouvellent leurs vœux temporaires, prends un instant pour renouveler intérieurement ton engagement dans cette règle, avant de continuer."
            )
        }
    )

    if (showHistory) {
        val entries = p.history.sortedByDescending { it.epochDay }
        AlertDialog(
            onDismissRequest = { showHistory = false },
            confirmButton = { TextButton(onClick = { showHistory = false }) { Text("OK") } },
            title = { Text(if (italian) "Storico dei livelli" else "Historique des niveaux") },
            text = {
                Column(Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                    if (entries.isEmpty()) {
                        Text(if (italian) "Nessuna cronologia ancora." else "Aucun historique pour l'instant.")
                    }
                    entries.forEach { e ->
                        val entryStage = stageFor(e.level, tradition, diff, italian)
                        val eventLabel = when (e.event) {
                            "start" -> if (italian) "Inizio" else "Début"
                            "levelup" -> if (italian) "Progresso" else "Progression"
                            "demotion" -> if (italian) "Ricaduta" else "Rétrogradation"
                            "renewal" -> if (italian) "Rinnovamento dei voti" else "Renouvellement des vœux"
                            else -> e.event
                        }
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Text("$eventLabel — ${if (italian) "livello" else "niveau"} ${e.level}",
                                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(entryStage.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                        }
                        Divider()
                    }
                }
            }
        )
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

private fun scaledMinutes(base: Int, diff: RuleDifficulty): Int {
    if (base <= 0) return 0
    return (base * diff.timeMultiplier).toInt().coerceAtLeast(1)
}

@Composable
private fun RuleDevotionLine(dev: RuleDevotion, highlighted: Boolean, italian: Boolean, minutes: Int) {
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
                if (minutes > 0) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        "~$minutes min",
                        style = MaterialTheme.typography.labelSmall, color = Color(0xFFD8B768), fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(dev.note, style = MaterialTheme.typography.bodySmall, color = Color(0xFFE9D9A8))
        }
    }
}

/* ============================================================================
   ÉCRIRE SA PROPRE RÈGLE — à la manière de l'ermite diocésain (canon 603)
   ============================================================================ */
private class DevDraft(
    name: String = "",
    minutes: String = "",
    signature: Boolean = false
) {
    var name by mutableStateOf(name)
    var minutes by mutableStateOf(minutes)
    var signature by mutableStateOf(signature)
}

private fun customAccentFor(family: String): String = when (family) {
    "orthodoxe" -> "#1f4e79"
    "charismatique" -> "#c0562e"
    else -> "#6b5230"
}

@Composable
private fun RuleBuilder(store: GameStore, modifier: Modifier, italian: Boolean, onCancel: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var family by remember { mutableStateOf("catholique") }
    var difficulty by remember { mutableStateOf<RuleDifficulty?>(null) }
    val drafts = remember { mutableStateListOf(DevDraft(), DevDraft(), DevDraft()) }

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text(
            if (italian) "Scrivi la tua regola" else "Écris ta propre règle",
            style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            if (italian)
                "Come l'eremita diocesano, componi la tua regola di vita. Crescerà anch'essa poco a poco, fino alla piena osservanza."
            else
                "Comme l'ermite diocésain, compose ta propre règle de vie. Elle grandira elle aussi peu à peu, jusqu'à la pleine observance.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text(if (italian) "Nome della regola" else "Nom de la règle") },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )

        Text(
            (if (italian) "Famiglia" else "Famille").uppercase(),
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 4.dp)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("orthodoxe", "catholique", "charismatique").forEach { f ->
                val sel = family == f
                OutlinedButton(
                    onClick = { family = f },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (sel) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) else Color.Transparent
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) { Text(familyLabel(f, italian), style = MaterialTheme.typography.labelSmall) }
            }
        }

        Text(
            (if (italian) "Devozioni (almeno 3)" else "Dévotions (au moins 3)").uppercase(),
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp)
        )
        Text(
            if (italian)
                "Si sbloccheranno una dopo l'altra, nell'ordine. Segna con ★ le devozioni irrinunciabili; i minuti sono la durata suggerita (0 = senza durata)."
            else
                "Elles se débloqueront l'une après l'autre, dans l'ordre. Marque d'un ★ les dévotions essentielles ; les minutes sont la durée suggérée (0 = sans durée).",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        drafts.forEachIndexed { i, dr ->
            Card(
                Modifier.fillMaxWidth().padding(vertical = 3.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(10.dp)) {
                    OutlinedTextField(
                        value = dr.name, onValueChange = { dr.name = it },
                        label = { Text((if (italian) "Devozione " else "Dévotion ") + "${i + 1}") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = dr.minutes,
                            onValueChange = { v -> dr.minutes = v.filter { c -> c.isDigit() }.take(3) },
                            label = { Text("min") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(90.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Checkbox(checked = dr.signature, onCheckedChange = { dr.signature = it })
                        Text("★", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.weight(1f))
                        if (drafts.size > 3) {
                            TextButton(onClick = { drafts.removeAt(i) }) {
                                Text(if (italian) "Togli" else "Retirer")
                            }
                        }
                    }
                }
            }
        }

        OutlinedButton(onClick = { drafts.add(DevDraft()) }, modifier = Modifier.padding(top = 6.dp)) {
            Text("＋ " + (if (italian) "Aggiungi una devozione" else "Ajouter une dévotion"))
        }

        Text(
            (if (italian) "Ritmo" else "Rythme").uppercase(),
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            RuleDifficulty.entries.forEach { d ->
                val sel = difficulty == d
                OutlinedButton(
                    onClick = { difficulty = d },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (sel) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) else Color.Transparent
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) { Text(d.label, style = MaterialTheme.typography.labelSmall) }
            }
        }

        val filled = drafts.count { it.name.isNotBlank() }
        val valid = name.isNotBlank() && difficulty != null && filled >= 3

        Spacer(Modifier.height(18.dp))
        Button(
            onClick = {
                val devs = drafts.filter { it.name.isNotBlank() }.map {
                    RuleDevotion(
                        name = it.name.trim(), note = "", signature = it.signature,
                        minutes = it.minutes.toIntOrNull()?.coerceIn(0, 240) ?: 0, supersedes = -1
                    )
                }
                val rule = RuleTradition(
                    id = store.customRuleId, family = family, name = name.trim(),
                    subtitle = if (italian) "« La mia regola di vita »" else "« Ma règle de vie »",
                    patron = "", accent = customAccentFor(family),
                    desc = if (italian) "Una regola di vita che hai scritto tu." else "Une règle de vie que tu as écrite toi-même.",
                    dailyCore = devs.take(3).map { it.name }, devotions = devs
                )
                store.ruleStartCustom(rule, difficulty!!)
            },
            enabled = valid, modifier = Modifier.fillMaxWidth()
        ) { Text(if (italian) "Comincia questa regola" else "Commencer cette règle") }

        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Text(if (italian) "Annulla" else "Annuler")
        }
        Spacer(Modifier.height(24.dp))
    }
}
