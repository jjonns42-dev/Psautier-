package com.liturgia.monastica.ui.screens

import androidx.compose.animation.core.*
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
import com.liturgia.monastica.R
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.ui.components.AppScaffold
import com.liturgia.monastica.ui.components.Backdrop
import kotlinx.coroutines.delay

@Composable
fun CandleGameScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    LaunchedEffect(Unit) { store.candleRefresh() }

    val totalSeconds = store.candleMinutes * 60
    var remaining by remember { mutableStateOf(totalSeconds) }
    var running by remember { mutableStateOf(false) }
    var finishedNow by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }

    val quote = remember(store.candleLevel) {
        if (store.candleMinutes >= 30) repo.randomBenedictQuote() else null
    }

    LaunchedEffect(running) {
        if (running) {
            while (remaining > 0 && running) {
                delay(1000)
                remaining -= 1
            }
            if (remaining <= 0) {
                running = false
                finishedNow = true
                store.candleCompleteToday()
            }
        }
    }

    AppScaffold(
        title = if (it) "La Candela" else "La Bougie",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Backdrop(R.drawable.madonna_silenzio, Modifier.padding(pad), dim = 0.58f) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    (if (it) "Livello" else "Niveau") + " ${store.candleLevel}",
                    style = MaterialTheme.typography.displaySmall, color = Color(0xFFF1E4BD)
                )
                Text(
                    "${store.candleMinutes} " + (if (it) "minuti al giorno" else "minutes par jour"),
                    style = MaterialTheme.typography.titleMedium, color = Color(0xFFE9D9A8)
                )
                Spacer(Modifier.height(18.dp))

                Candle(lit = running || store.candleDoneToday())
                Spacer(Modifier.height(14.dp))

                // Weekly streak pips (x / 7)
                Row {
                    repeat(7) { i ->
                        Box(
                            Modifier.padding(4.dp).size(16.dp).clip(CircleShape)
                                .background(
                                    if (i < store.candleStreak) Color(0xFFD8B768)
                                    else Color.White.copy(alpha = 0.30f)
                                )
                        )
                    }
                }
                Text(
                    "${store.candleStreak}/7 " + (if (it) "giorni" else "jours"),
                    style = MaterialTheme.typography.labelLarge, color = Color(0xFFE9D9A8)
                )
                Spacer(Modifier.height(18.dp))

                // Timer / actions
                when {
                    store.candleAwaitingChoice -> WeekCompleteChoice(store, it)
                    store.candleDoneToday() -> DoneToday(it)
                    else -> {
                        Text(
                            formatTime(remaining),
                            style = MaterialTheme.typography.displaySmall, color = Color.White
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { running = !running },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1F1B))
                        ) {
                            Text(
                                if (running) (if (it) "Pausa" else "Pause")
                                else (if (it) "Inizia la preghiera" else "Commencer la prière"),
                                color = Color.White
                            )
                        }
                        TextButton(onClick = {
                            store.candleCompleteToday(); finishedNow = true
                        }) {
                            Text(
                                if (it) "Ho già pregato oggi" else "J'ai déjà prié aujourd'hui",
                                color = Color(0xFFE9D9A8)
                            )
                        }
                    }
                }

                if (quote != null) {
                    Spacer(Modifier.height(18.dp))
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xCC201C16)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                if (it) "San Benedetto" else "Saint Benoît",
                                style = MaterialTheme.typography.labelLarge, color = Color(0xFFD8B768)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "« ${if (it) quote.it else quote.fr} »",
                                style = MaterialTheme.typography.bodyLarge, color = Color(0xFFF1E4BD)
                            )
                            Text(
                                "— Regula, ${quote.src}",
                                style = MaterialTheme.typography.labelSmall, color = Color(0xFFB9A87A)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                TextButton(onClick = { showReset = true }) {
                    Text(if (it) "Ricominciare" else "Réinitialiser", color = Color(0xFFB9A87A))
                }
            }
        }
    }

    if (showReset) AlertDialog(
        onDismissRequest = { showReset = false },
        confirmButton = {
            TextButton(onClick = { store.candleReset(); remaining = store.candleMinutes * 60; showReset = false }) {
                Text(if (it) "Sì, ricomincia" else "Oui, recommencer")
            }
        },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text(if (it) "Annulla" else "Annuler") } },
        title = { Text(if (it) "Ricominciare da capo?" else "Tout recommencer ?") },
        text = {
            Text(
                if (it) "Tornerai al livello 1 (5 minuti)."
                else "Vous reviendrez au niveau 1 (5 minutes)."
            )
        }
    )
}

@Composable
private fun WeekCompleteChoice(store: GameStore, it: Boolean) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xE6201C16)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (it) "Settimana compiuta!" else "Semaine accomplie !",
                style = MaterialTheme.typography.headlineSmall, color = Color(0xFFD8B768)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (it) "Salire al livello successivo (+5 minuti) o restare a questo livello?"
                else "Monter au niveau supérieur (+5 minutes) ou rester à ce niveau ?",
                style = MaterialTheme.typography.bodyMedium, color = Color(0xFFF1E4BD),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            Button(onClick = { store.candleAdvance() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5135))) {
                Text(if (it) "Salgo di livello" else "Monter d'un niveau", color = Color.White)
            }
            Spacer(Modifier.height(6.dp))
            OutlinedButton(onClick = { store.candleStay() }) {
                Text(if (it) "Resto a questo livello" else "Rester à ce niveau", color = Color(0xFFE9D9A8))
            }
        }
    }
}

@Composable
private fun DoneToday(it: Boolean) {
    Text(
        if (it) "Preghiera compiuta oggi. A domani." else "Prière accomplie aujourd'hui. À demain.",
        style = MaterialTheme.typography.titleMedium, color = Color(0xFFD8B768),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Candle(lit: Boolean) {
    val infinite = rememberInfiniteTransition(label = "flame")
    val flicker by infinite.animateFloat(
        initialValue = 0.85f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(620), RepeatMode.Reverse), label = "flicker"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(width = 26.dp, height = if (lit) (30 * flicker).dp else 6.dp)
                .clip(RoundedCornerShape(50))
                .background(if (lit) Color(0xFFFFC85B) else Color(0x55FFFFFF))
        )
        Box(
            Modifier.padding(top = 2.dp).size(width = 38.dp, height = 120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3ECD8))
        )
    }
}

private fun formatTime(total: Int): String {
    val m = total / 60; val s = total % 60
    return "%02d:%02d".format(m, s)
}
