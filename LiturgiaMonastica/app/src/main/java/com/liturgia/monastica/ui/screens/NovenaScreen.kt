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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.data.*
import com.liturgia.monastica.ui.components.AppScaffold

@Composable
fun NovenaScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    LaunchedEffect(Unit) { store.novenaRefresh() }
    val types = remember { repo.novenaTypes() }

    AppScaffold(
        title = if (it) "Neuvaine" else "Neuvaine",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        if (!store.novenaProgress.started) {
            NovenaSetup(types, store, Modifier.padding(pad), italian = it)
        } else {
            val type = remember(store.novenaProgress.typeId) { repo.novenaType(store.novenaProgress.typeId) }
            if (type == null) {
                Column(Modifier.padding(pad).fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (it) "Neuvaine introvabile." else "Neuvaine introuvable.")
                    Button(onClick = { store.novenaAbandon() }) { Text(if (it) "Ricomincia" else "Recommencer") }
                }
            } else {
                NovenaRunning(type, store, Modifier.padding(pad), italian = it)
            }
        }
    }
}

@Composable
private fun NovenaSetup(types: List<NovenaType>, store: GameStore, modifier: Modifier, italian: Boolean) {
    var selected by remember { mutableStateOf<NovenaType?>(null) }
    var showHistory by remember { mutableStateOf(false) }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (italian) "Un angolo di novena" else "Un coin de neuvaine",
            style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            if (italian)
                "Scegli la durata della tua novena. Ogni giorno pregato conta; un giorno saltato fa ricominciare il conteggio da zero."
            else
                "Choisis la durée de ta neuvaine. Chaque jour prié compte ; un jour manqué fait recommencer le compteur à zéro.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 10.dp)
        )

        types.forEach { t ->
            val isSel = selected?.id == t.id
            Card(
                Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selected = t },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (italian) t.nameIt else t.nameFr,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f))
                        Text("${t.days} ${if (italian) "gg" else "j"}",
                            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
                    }
                    if (t.fasting) {
                        Text(if (italian) "· con digiuno" else "· avec jeûne",
                            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                    }
                    if (isSel) {
                        Spacer(Modifier.height(8.dp))
                        Text(if (italian) t.descIt else t.descFr,
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        if (t.phases.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            t.phases.forEach { ph ->
                                Text(
                                    "• ${if (italian) ph.labelIt else ph.labelFr} (${if (italian) "gg" else "j"} ${ph.fromDay}-${ph.toDay})",
                                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { selected?.let { store.novenaStart(it.id) } },
            enabled = selected != null,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1F1B))
        ) {
            Text(if (italian) "Inizia questa novena" else "Commencer cette neuvaine", color = Color.White)
        }

        if (store.novenaProgress.completed.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = { showHistory = true }) {
                Text(
                    if (italian) "Repertorio delle novene compiute (${store.novenaProgress.completed.size})"
                    else "Répertoire des neuvaines accomplies (${store.novenaProgress.completed.size})"
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            confirmButton = { TextButton(onClick = { showHistory = false }) { Text("OK") } },
            title = { Text(if (italian) "Novene compiute" else "Neuvaines accomplies") },
            text = {
                Column(Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                    store.novenaProgress.completed.sortedByDescending { it.endEpochDay }.forEach { e ->
                        val t = types.firstOrNull { it.id == e.typeId }
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Text(
                                (if (italian) t?.nameIt else t?.nameFr) ?: e.typeId,
                                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium
                            )
                        }
                        Divider()
                    }
                }
            }
        )
    }
}

@Composable
private fun NovenaRunning(type: NovenaType, store: GameStore, modifier: Modifier, italian: Boolean) {
    val p = store.novenaProgress
    var showAbandon by remember { mutableStateOf(false) }
    val currentPhase = type.phases.firstOrNull { p.day + 1 in it.fromDay..it.toDay }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (italian) type.nameIt else type.nameFr,
            style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text("${p.day} / ${type.days}",
            style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onBackground)
        Text(if (italian) "giorni" else "jours",
            style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        LinearProgressIndicator(
            progress = p.day.toFloat() / type.days,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(6.dp),
            color = MaterialTheme.colorScheme.tertiary, trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        currentPhase?.let { ph ->
            Text(
                if (italian) ph.labelIt else ph.labelFr,
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        if (store.novenaJustReset) {
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(6.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        if (italian) "Un giorno mancato: la novena riparte dal giorno 0."
                        else "Un jour manqué : la neuvaine repart du jour 0.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    TextButton(onClick = { store.novenaConsumeReset() }) { Text("OK") }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(if (italian) type.descIt else type.descFr,
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center)

        Spacer(Modifier.height(20.dp))
        if (store.novenaDoneToday()) {
            Text(
                if (italian) "✓ Oggi è fatto. A domani." else "✓ Aujourd'hui est fait. À demain.",
                style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center
            )
        } else {
            Button(
                onClick = { store.novenaConfirmToday(type.days) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5135))
            ) {
                Text(
                    if (italian) "✓ Ho pregato questo giorno di novena" else "✓ J'ai prié ce jour de neuvaine",
                    color = Color.White
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                if (italian) "Se salti un giorno, si ricomincia dal giorno 0."
                else "Si un jour est manqué, on recommence au jour 0.",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))
        TextButton(onClick = { showAbandon = true }) {
            Text(if (italian) "Interrompere questa novena" else "Interrompre cette neuvaine")
        }
        Spacer(Modifier.height(20.dp))
    }

    if (showAbandon) AlertDialog(
        onDismissRequest = { showAbandon = false },
        confirmButton = {
            TextButton(onClick = { store.novenaAbandon(); showAbandon = false }) {
                Text(if (italian) "Sì, interrompi" else "Oui, interrompre")
            }
        },
        dismissButton = { TextButton(onClick = { showAbandon = false }) { Text(if (italian) "Annulla" else "Annuler") } },
        title = { Text(if (italian) "Interrompere la novena?" else "Interrompre la neuvaine ?") },
        text = {
            Text(
                if (italian) "La progressione andrà persa e potrai scegliere una nuova novena."
                else "La progression sera perdue et tu pourras choisir une nouvelle neuvaine."
            )
        }
    )
}
