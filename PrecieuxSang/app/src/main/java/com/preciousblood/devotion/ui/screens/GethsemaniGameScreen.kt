package com.preciousblood.devotion.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.preciousblood.devotion.R
import com.preciousblood.devotion.data.CompletionResult
import com.preciousblood.devotion.data.GethsemaniProgress
import com.preciousblood.devotion.data.ProgressStore
import com.preciousblood.devotion.ui.theme.BloodRed
import com.preciousblood.devotion.ui.theme.Gold
import java.time.LocalDate

private const val HOLY_HOUR_SECONDS = 60 * 60

@Composable
fun GethsemaniGameScreen(
    contentPadding: PaddingValues,
    onOpenPrayer: (String) -> Unit
) {
    val context = LocalContext.current
    val store = remember { ProgressStore(context) }
    var progress by remember { mutableStateOf(store.load()) }

    var remaining by rememberSaveable { mutableIntStateOf(HOLY_HOUR_SECONDS) }
    var running by rememberSaveable { mutableStateOf(false) }
    var dialog by remember { mutableStateOf<CompletionResult?>(null) }
    var showReset by remember { mutableStateOf(false) }

    fun record() {
        val friday = store.fridayFor(LocalDate.now())
        val res = store.recordCompletion(friday)
        progress = res.progress
        dialog = res
    }

    // Décompte
    LaunchedEffect(running) {
        while (running && remaining > 0) {
            kotlinx.coroutines.delay(1000)
            remaining -= 1
        }
        if (remaining == 0 && running) {
            running = false
            record()
        }
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gethsemani),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 16.dp, end = 16.dp,
                    top = contentPadding.calculateTopPadding() + 12.dp,
                    bottom = contentPadding.calculateBottomPadding() + 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LevelCard(progress)
            TimerCard(
                remaining = remaining,
                running = running,
                onToggle = { running = !running },
                onReset = { running = false; remaining = HOLY_HOUR_SECONDS }
            )
            Button(
                onClick = { record() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BloodRed)
            ) {
                Text("Marquer cette heure comme accomplie")
            }
            OutlinedButton(
                onClick = { onOpenPrayer("consolation") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ouvrir les prières de la veillée")
            }
            RulesCard()
            TextButton(
                onClick = { showReset = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Réinitialiser la progression",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    dialog?.let { res ->
        CompletionDialog(res) { dialog = null }
    }
    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text("Réinitialiser ?") },
            text = { Text("Cela effacera votre niveau et votre série. Action irréversible.") },
            confirmButton = {
                TextButton(onClick = {
                    store.reset(); progress = store.load(); showReset = false
                }) { Text("Réinitialiser") }
            },
            dismissButton = {
                TextButton(onClick = { showReset = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
private fun LevelCard(p: GethsemaniProgress) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Veillée de Gethsémani",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(64.dp).clip(CircleShape).background(Gold),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${p.level}",
                        fontSize = 30.sp, fontWeight = FontWeight.Bold,
                        color = BloodRed)
                }
                Column(Modifier.padding(start = 16.dp)) {
                    Text("Niveau ${p.level}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Text("${p.progressInLevel}/${GethsemaniProgress.FRIDAYS_PER_LEVEL} vendredis vers le niveau ${p.level + 1}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(GethsemaniProgress.FRIDAYS_PER_LEVEL) { i ->
                    val filled = i < p.progressInLevel
                    Box(
                        Modifier.size(18.dp).clip(CircleShape)
                            .background(if (filled) Gold else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Stat("Série", "${p.currentStreak}")
                Stat("Record", "${p.bestStreak}")
                Stat("Heures", "${p.totalHolyHours}")
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun TimerCard(
    remaining: Int,
    running: Boolean,
    onToggle: () -> Unit,
    onReset: () -> Unit
) {
    val fraction = 1f - remaining.toFloat() / HOLY_HOUR_SECONDS
    val animated by animateFloatAsState(targetValue = fraction, label = "ring")
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(190.dp)) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.size(190.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 12.dp
                )
                CircularProgressIndicator(
                    progress = animated,
                    modifier = Modifier.size(190.dp),
                    color = BloodRed,
                    strokeWidth = 12.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(formatTime(remaining),
                        fontSize = 44.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Une heure sainte",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                IconButton(onClick = onToggle, modifier = Modifier.size(64.dp)) {
                    Icon(
                        if (running) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                        contentDescription = if (running) "Pause" else "Commencer",
                        tint = BloodRed,
                        modifier = Modifier.size(64.dp)
                    )
                }
                IconButton(onClick = onReset, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Filled.Replay, contentDescription = "Réinitialiser",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
private fun RulesCard() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("La veillée", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(
                "Chaque jeudi soir (23 h → 3 h du vendredi), veillez une heure avec " +
                "Notre Seigneur au Jardin de Gethsémani. Lancez le minuteur d'une heure, " +
                "ou marquez l'heure comme accomplie.\n\n" +
                "Quatre vendredis consécutifs vous font passer au niveau suivant — et " +
                "ainsi de suite, à l'infini. Manquer un vendredi remet à zéro la " +
                "progression du niveau en cours, mais vous gardez vos niveaux acquis.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompletionDialog(res: CompletionResult, onClose: () -> Unit) {
    val title: String
    val msg: String
    when {
        res.alreadyDoneThisWeek -> {
            title = "Déjà accomplie"
            msg = "Vous avez déjà veillé ce vendredi. Revenez vendredi prochain pour " +
                  "continuer votre série."
        }
        res.leveledUp -> {
            title = "Niveau ${res.progress.level} atteint ✝"
            msg = "Quatre vendredis consécutifs ! Vous passez au niveau ${res.progress.level}. " +
                  "Que le Précieux Sang vous bénisse."
        }
        res.streakBroken -> {
            title = "Heure accomplie"
            msg = "Une nouvelle série commence (1 vendredi). Restez fidèle : 4 vendredis " +
                  "de suite pour le prochain niveau."
        }
        else -> {
            title = "Heure accomplie ✝"
            msg = "Vendredi ${res.progress.progressInLevel}/4 vers le niveau " +
                  "${res.progress.level + 1}. Série : ${res.progress.currentStreak}."
        }
    }
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(title) },
        text = { Text(msg) },
        confirmButton = { TextButton(onClick = onClose) { Text("Amen") } }
    )
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}
