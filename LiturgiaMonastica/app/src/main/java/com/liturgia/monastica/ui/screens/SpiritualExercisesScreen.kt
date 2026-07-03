package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.data.*
import com.liturgia.monastica.ui.components.AppScaffold

@Composable
fun SpiritualExercisesScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    var family by remember { mutableStateOf("orthodoxe") }
    val exercises = remember(family) { repo.spiritualExercisesByFamily(family) }
    var opened by remember { mutableStateOf<SpiritualExercise?>(null) }

    AppScaffold(
        title = if (it) "Esercizi spirituali" else "Exercices spirituels",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            Text(
                if (it) "Pratiche fondate sulla Scrittura e sull'insegnamento dei Padri della Chiesa"
                else "Pratiques fondées sur l'Écriture et l'enseignement des Pères de l'Église",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("orthodoxe" to (if (it) "Ortodossa" else "Orthodoxe"),
                    "catholique" to (if (it) "Cattolica" else "Catholique")).forEach { (f, label) ->
                    val sel = family == f
                    Text(
                        label,
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
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                exercises.forEach { ex ->
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { opened = ex },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(ex.name, style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                            Text(ex.source, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary, fontStyle = FontStyle.Italic)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    opened?.let { ex ->
        AlertDialog(
            onDismissRequest = { opened = null },
            confirmButton = { TextButton(onClick = { opened = null }) { Text("OK") } },
            title = { Text(ex.name) },
            text = {
                Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                    Text(ex.source, style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary, fontStyle = FontStyle.Italic)
                    Spacer(Modifier.height(8.dp))
                    Text(ex.desc, style = MaterialTheme.typography.bodyMedium)
                    if (ex.steps.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            if (store.lang == "it") "Come si pratica" else "Comment le pratiquer",
                            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary
                        )
                        ex.steps.forEachIndexed { i, s ->
                            Text("${i + 1}. $s", style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                    if (ex.scripture.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            if (store.lang == "it") "Fondamento biblico" else "Fondement biblique",
                            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(ex.scripture.joinToString(" · "), style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        )
    }
}
