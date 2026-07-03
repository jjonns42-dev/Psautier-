package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.R
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.Devotion
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.ui.components.AppScaffold
import com.liturgia.monastica.ui.components.Backdrop
import kotlinx.coroutines.delay

@Composable
fun ThousandDaysScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    LaunchedEffect(Unit) { store.thRefresh() }
    val devotions = remember { repo.devotions() }

    AppScaffold(
        title = if (it) "1000 Giorni" else "1000 Jours",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Backdrop(R.drawable.seraphim_sarov, Modifier.padding(pad), dim = 0.62f) {
            if (!store.thStarted) {
                SetupView(devotions, it) { devId, minutes -> store.thStart(devId, minutes) }
            } else {
                RunningView(repo, store, it)
            }
        }
    }
}

@Composable
private fun SetupView(devotions: List<Devotion>, it: Boolean, onStart: (String, Int) -> Unit) {
    var selected by remember { mutableStateOf<Devotion?>(null) }
    var minutes by remember { mutableStateOf(30) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (it) "Diventare santo" else "Devenir un saint",
            style = MaterialTheme.typography.displaySmall, color = Color(0xFFF1E4BD),
            textAlign = TextAlign.Center
        )
        Text(
            if (it) "Ogni giorno, di notte, pregare con l'intenzione di farsi santo. Scegli una sola devozione: resterà fissa fino a un nuovo inizio."
            else "Chaque jour, la nuit, prier avec l'intention de devenir un saint. Choisis une seule dévotion : elle restera fixée jusqu'à un nouveau départ.",
            style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE9D9A8),
            textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 10.dp)
        )

        // Duration
        Row(Modifier.padding(vertical = 8.dp)) {
            listOf(30, 60).forEach { m ->
                val sel = minutes == m
                Text(
                    if (m == 60) (if (it) "1 ora" else "1 heure") else "30 min",
                    modifier = Modifier.padding(6.dp).clip(RoundedCornerShape(18.dp))
                        .background(if (sel) Color(0xFF2E5135) else Color(0x55000000))
                        .clickable { minutes = m }.padding(horizontal = 22.dp, vertical = 10.dp),
                    color = Color.White, style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Text(
            if (it) "Scegli la devozione" else "Choisis la dévotion",
            style = MaterialTheme.typography.titleMedium, color = Color(0xFFD8B768),
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        devotions.forEach { d ->
            val isSel = selected?.id == d.id
            Card(
                Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selected = d },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSel) Color(0xF22E5135) else Color(0xCC201C16)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(if (it) d.nameIt else d.nameFr,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSel) Color.White else Color(0xFFF1E4BD),
                        fontWeight = FontWeight.Medium)
                    if (isSel) {
                        Spacer(Modifier.height(6.dp))
                        Text(d.desc, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE9D9A8))
                        if (d.promises.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(if (it) "Promesse:" else "Promesses :",
                                style = MaterialTheme.typography.labelLarge, color = Color(0xFFD8B768))
                            d.promises.forEach { p ->
                                Text("• $p", style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFF1E4BD), modifier = Modifier.padding(top = 3.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { selected?.let { onStart(it.id, minutes) } },
            enabled = selected != null,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1F1B))
        ) {
            Text(if (it) "Inizio i 1000 giorni" else "Commencer les 1000 jours", color = Color.White)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun RunningView(repo: ContentRepository, store: GameStore, it: Boolean) {
    val dev = remember(store.thDevotion) { repo.devotion(store.thDevotion) }
    val totalSeconds = store.thMinutes * 60
    var remaining by remember { mutableStateOf(totalSeconds) }
    var running by remember { mutableStateOf(false) }
    var showRestart by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        if (running) {
            while (remaining > 0 && running) { delay(1000); remaining -= 1 }
            if (remaining <= 0) { running = false; store.thCheckToday() }
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${store.thDay} / ${store.THOUSAND}",
            style = MaterialTheme.typography.displaySmall, color = Color(0xFFF1E4BD))
        Text(if (it) "giorni" else "jours",
            style = MaterialTheme.typography.titleMedium, color = Color(0xFFE9D9A8))

        LinearProgressIndicator(
            progress = store.thDay.toFloat() / store.THOUSAND,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(6.dp),
            color = Color(0xFFD8B768), trackColor = Color(0x55FFFFFF)
        )

        dev?.let {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xCC201C16)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(if (store.lang == "it") dev.nameIt else dev.nameFr,
                        style = MaterialTheme.typography.titleLarge, color = Color(0xFFD8B768))
                    Text("${store.thMinutes} min · " +
                            (if (store.lang == "it") "ogni notte" else "chaque nuit"),
                        style = MaterialTheme.typography.labelLarge, color = Color(0xFFB9A87A))
                    Spacer(Modifier.height(6.dp))
                    Text(dev.desc, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFF1E4BD))
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        if (store.thDoneToday()) {
            Text(
                if (store.lang == "it") "Oggi è fatto. Persevera." else "Aujourd'hui est fait. Persévère.",
                style = MaterialTheme.typography.titleMedium, color = Color(0xFFD8B768),
                textAlign = TextAlign.Center
            )
        } else {
            Text(formatTime(remaining), style = MaterialTheme.typography.displaySmall, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { running = !running },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5135))) {
                Text(
                    if (running) (if (store.lang == "it") "Pausa" else "Pause")
                    else (if (store.lang == "it") "Inizia la preghiera" else "Commencer la prière"),
                    color = Color.White
                )
            }
            Spacer(Modifier.height(6.dp))
            // The essential daily action: check the box "I did this day".
            OutlinedButton(onClick = { store.thCheckToday() }) {
                Text(
                    if (store.lang == "it") "✓ Ho pregato questo giorno" else "✓ Je fais ce jour",
                    color = Color(0xFFE9D9A8)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            if (store.lang == "it")
                "Se salti un giorno, si ricomincia dal giorno 0."
            else "Si un jour est manqué, on recommence au jour 0.",
            style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A87A),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(18.dp))
        TextButton(onClick = { showRestart = true }) {
            Text(
                if (store.lang == "it") "Ricominciare il gioco (cambia devozione)"
                else "Recommencer le jeu (changer de dévotion)",
                color = Color(0xFFB9A87A)
            )
        }
        Spacer(Modifier.height(20.dp))
    }

    if (showRestart) AlertDialog(
        onDismissRequest = { showRestart = false },
        confirmButton = {
            TextButton(onClick = { store.thRestart(); showRestart = false }) {
                Text(if (it) "Sì" else "Oui")
            }
        },
        dismissButton = { TextButton(onClick = { showRestart = false }) { Text(if (it) "Annulla" else "Annuler") } },
        title = { Text(if (it) "Ricominciare il gioco?" else "Recommencer le jeu ?") },
        text = {
            Text(
                if (it) "Il conteggio torna a 0 e potrai scegliere una nuova devozione."
                else "Le compteur revient à 0 et vous pourrez choisir une nouvelle dévotion."
            )
        }
    )
}

private fun formatTime(total: Int): String {
    val m = total / 60; val s = total % 60
    return "%02d:%02d".format(m, s)
}
