package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byzantine.horologion.R
import com.byzantine.horologion.data.GameState
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.data.Quote
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings
import com.byzantine.horologion.ui.components.CandleView
import kotlinx.coroutines.delay
import java.time.LocalDate

private fun bgRes(name: String): Int = when (name) {
    "bg_dttw_cross" -> R.drawable.bg_dttw_cross
    "bg_dttw_skull" -> R.drawable.bg_dttw_skull
    "bg_madonna" -> R.drawable.bg_madonna
    else -> R.drawable.icon_virgin_silence
}

@Composable
fun JesusPrayerScreen() {
    val repo = LocalRepo.current
    val settings = LocalSettings.current
    val lang = settings.lang
    val gameOn = settings.gameEnabled
    val (el, fr, it) = repo.jesusPrayer()

    val context = LocalContext.current
    val game = remember { GameState(context) }
    val today = remember { LocalDate.now().toEpochDay() }
    LaunchedEffect(Unit) { if (gameOn) game.reconcile(today) }

    var running by remember { mutableStateOf(!gameOn) } // free mode auto-runs
    var restartKey by remember { mutableIntStateOf(0) }
    var showConfirm by remember { mutableStateOf(false) }

    // weekly quote
    var quote by remember { mutableStateOf<Quote?>(null) }
    LaunchedEffect(running) {
        if (gameOn && running && game.shouldShowQuoteToday(today)) {
            val pool = repo.quotePool(game.quotePool())
            if (pool.isNotEmpty()) {
                quote = pool[(today % pool.size).toInt()]
                delay(90_000L)
                quote = null
                game.markQuoteShown(today)
            }
        }
    }

    val bgName = if (gameOn) game.backgroundDrawable(today) else "icon_virgin_silence"
    val burnMin = if (gameOn) game.requiredMinutes() else 20

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(bgRes(bgName)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.32f),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xCC0E0B0B), Color(0x880E0B0B), Color(0xDD0E0B0B)))
            )
        )

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Κύριε ἐλέησον", color = Color(0xFFE7D3A0),
                fontStyle = FontStyle.Italic, style = MaterialTheme.typography.titleMedium)

            if (gameOn) {
                Spacer(Modifier.height(6.dp))
                Text(
                    (if (lang == Lang.FR) "Niveau " else "Livello ") + game.level +
                        " · " + game.requiredMinutes() + " min",
                    color = Color(0xFFF3ECE0), style = MaterialTheme.typography.titleLarge
                )
                Text(
                    (if (lang == Lang.FR) "Jours cette semaine : " else "Giorni questa settimana: ") +
                        game.daysThisWeek + "/7",
                    color = Color(0xFFC9A24B), style = MaterialTheme.typography.bodyMedium
                )
            }

            // weekly quote, fades after 90 s
            quote?.let { q ->
                Spacer(Modifier.height(10.dp))
                Surface(color = Color(0x66000000), shape = RoundedCornerShape(10.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("« ${q.text} »", color = Color(0xFFF3ECE0),
                            textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                        Text(q.src, color = Color(0xFFC9A24B),
                            modifier = Modifier.align(Alignment.End),
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            CandleView(
                modifier = Modifier.fillMaxWidth().height(290.dp).padding(top = 8.dp),
                burnMillis = burnMin * 60_000L,
                running = running,
                restartKey = restartKey,
                onConsumed = { if (gameOn) { running = false; showConfirm = true } }
            )

            if (gameOn && !running && !showConfirm) {
                Button(onClick = { restartKey++; running = true }) {
                    Text(if (lang == Lang.FR) "Commencer la prière" else "Inizia la preghiera")
                }
                if (game.canFreeze()) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)) {
                        Switch(checked = game.frozen, onCheckedChange = { game.setFrozen(it) })
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (lang == Lang.FR) "Rester à ce niveau" else "Resta a questo livello",
                            color = Color(0xFFF3ECE0), style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            PrayerLine(el, 20.sp)
            Spacer(Modifier.height(14.dp))
            PrayerLine(fr, 18.sp)
            Spacer(Modifier.height(14.dp))
            PrayerLine(it, 18.sp)
            Spacer(Modifier.height(30.dp))
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(if (lang == Lang.FR) "Prière accomplie ?" else "Preghiera compiuta?") },
            text = {
                Text(
                    if (lang == Lang.FR)
                        "Avez-vous prié la prière de Jésus pendant ${game.requiredMinutes()} minutes ?"
                    else
                        "Hai recitato la preghiera di Gesù per ${game.requiredMinutes()} minuti?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    game.confirm(true, today); showConfirm = false; restartKey++
                }) { Text(if (lang == Lang.FR) "Oui" else "Sì") }
            },
            dismissButton = {
                TextButton(onClick = {
                    game.confirm(false, today); showConfirm = false; restartKey++
                }) { Text(if (lang == Lang.FR) "Non" else "No") }
            }
        )
    }
}

@Composable
private fun PrayerLine(text: String, size: TextUnit) {
    Text(
        text, color = Color(0xFFF3ECE0), textAlign = TextAlign.Center,
        fontSize = size, lineHeight = size * 1.4f, style = MaterialTheme.typography.bodyLarge
    )
}
