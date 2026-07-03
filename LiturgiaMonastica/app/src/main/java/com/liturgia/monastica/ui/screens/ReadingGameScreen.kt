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
import kotlinx.coroutines.delay

private fun categoryLabel(cat: String, it: Boolean): String = when (cat) {
    ReadingCategory.BIBLE -> if (it) "Bibbia" else "Bible"
    ReadingCategory.THEOLOGIE -> if (it) "Teologia" else "Théologie"
    ReadingCategory.MYSTIQUE -> if (it) "Spiritualità e mistica" else "Spiritualité & mystique"
    else -> if (it) "Padri della Chiesa" else "Pères de l'Église"
}

private fun categorySubtitle(cat: String, it: Boolean): String = when (cat) {
    ReadingCategory.BIBLE -> if (it) "I 73 libri del canone" else "Les 73 livres du canon"
    else -> if (it) "Le tue opere, aggiunte da te" else "Tes œuvres, ajoutées par toi"
}

@Composable
fun ReadingGameScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    var category by remember { mutableStateOf<String?>(null) }

    AppScaffold(
        title = if (it) "Gioco di lettura" else "Jeu de lecture",
        onBack = { if (category != null) category = null else onBack() },
        night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        if (category == null) {
            ReadingHub(store, Modifier.padding(pad), italian = it, onOpen = { category = it })
        } else {
            ReadingCategoryList(repo, store, category!!, Modifier.padding(pad), italian = it)
        }
    }
}

@Composable
private fun LevelBadge(level: Int, xp: Long, needed: Long) {
    Box(
        Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF7A1F1B)),
        contentAlignment = Alignment.Center
    ) {
        Text("$level", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ReadingHub(store: GameStore, modifier: Modifier, italian: Boolean, onOpen: (String) -> Unit) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (italian) "Un gioco di lettura a livelli infiniti" else "Un jeu de lecture à niveaux infinis",
            style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            if (italian)
                "Ogni sessione cronometrata (5-60 min) fa salire il livello del libro o dell'opera letta. Le sessioni più lunghe fanno salire la barra più rapidamente."
            else
                "Chaque séance chronométrée (5 à 60 min) fait monter le niveau du livre ou de l'œuvre lue. Les séances les plus longues font monter la jauge plus vite.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 10.dp)
        )

        ReadingCategory.ALL.forEach { cat ->
            val xp = if (cat == ReadingCategory.BIBLE) store.bibleCategoryXp() else store.categoryWorksXp(cat)
            val level = ReadingLeveling.levelFromXp(xp)
            val (into, needed) = ReadingLeveling.progressWithinLevel(xp)
            Card(
                Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onOpen(cat) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    LevelBadge(level, into, needed)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(categoryLabel(cat, italian), style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        Text(categorySubtitle(cat, italian), style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LinearProgressIndicator(
                            progress = if (needed > 0) into.toFloat() / needed.toFloat() else 0f,
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp).height(5.dp),
                            color = MaterialTheme.colorScheme.tertiary, trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ReadingCategoryList(repo: ContentRepository, store: GameStore, category: String, modifier: Modifier, italian: Boolean) {
    var sessionFor by remember { mutableStateOf<Pair<String, String>?>(null) } // id, label
    var showAdd by remember { mutableStateOf(false) }
    val bibleBooks = remember(category) { if (category == ReadingCategory.BIBLE) repo.bibleReadingBooks() else emptyList() }

    Column(modifier.fillMaxSize()) {
        Text(
            categoryLabel(category, italian), style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        if (category != ReadingCategory.BIBLE) {
            Button(
                onClick = { showAdd = true },
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5135))
            ) { Text("+ " + (if (italian) "Aggiungi un'opera" else "Ajouter une œuvre"), color = Color.White) }
        }

        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
            if (category == ReadingCategory.BIBLE) {
                val at = bibleBooks.filter { it.testament == "AT" }
                val nt = bibleBooks.filter { it.testament == "NT" }
                item { SectionLabel(if (italian) "Antico Testamento" else "Ancien Testament") }
                items(at) { b ->
                    val xp = store.bibleBookXp(b.id)
                    ReadingItemRow(
                        title = if (italian) b.nameIt else b.nameFr, xp = xp,
                        onClick = { sessionFor = b.id to (if (italian) b.nameIt else b.nameFr) }
                    )
                }
                item { SectionLabel(if (italian) "Nuovo Testamento" else "Nouveau Testament") }
                items(nt) { b ->
                    val xp = store.bibleBookXp(b.id)
                    ReadingItemRow(
                        title = if (italian) b.nameIt else b.nameFr, xp = xp,
                        onClick = { sessionFor = b.id to (if (italian) b.nameIt else b.nameFr) }
                    )
                }
            } else {
                val works = store.worksByCategory(category)
                if (works.isEmpty()) {
                    item {
                        Text(
                            if (italian) "Nessuna opera ancora. Aggiungi la prima." else "Aucune œuvre pour l'instant. Ajoute la première.",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(), textAlign = TextAlign.Center
                        )
                    }
                }
                items(works, key = { it.id }) { w ->
                    ReadingItemRow(
                        title = w.title, subtitle = w.author.ifBlank { null }, xp = w.xp,
                        onClick = { sessionFor = w.id to w.title },
                        onLongClick = { store.removeWork(w.id) }
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    sessionFor?.let { (id, label) ->
        ReadingSessionDialog(
            label = label, italian = italian,
            onDismiss = { sessionFor = null },
            onComplete = { minutes ->
                if (category == ReadingCategory.BIBLE) store.bibleReadSession(id, minutes) else store.workReadSession(id, minutes)
                sessionFor = null
            }
        )
    }

    if (showAdd) {
        AddWorkDialog(italian = italian, onDismiss = { showAdd = false }, onAdd = { title, author ->
            store.addWork(category, title, author)
            showAdd = false
        })
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
}

@Composable
private fun ReadingItemRow(
    title: String, xp: Long, onClick: () -> Unit,
    subtitle: String? = null, onLongClick: (() -> Unit)? = null
) {
    val level = ReadingLeveling.levelFromXp(xp)
    val (into, needed) = ReadingLeveling.progressWithinLevel(xp)
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                Text("$level", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                subtitle?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                LinearProgressIndicator(
                    progress = if (needed > 0) into.toFloat() / needed.toFloat() else 0f,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(4.dp),
                    color = MaterialTheme.colorScheme.tertiary, trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            if (onLongClick != null) {
                TextButton(onClick = onLongClick) { Text("✕") }
            }
        }
    }
}

@Composable
private fun AddWorkDialog(italian: Boolean, onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { if (title.isNotBlank()) onAdd(title, author) }, enabled = title.isNotBlank()) {
                Text(if (italian) "Aggiungi" else "Ajouter")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(if (italian) "Annulla" else "Annuler") } },
        title = { Text(if (italian) "Nuova opera" else "Nouvelle œuvre") },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text(if (italian) "Titolo" else "Titre") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = author, onValueChange = { author = it },
                    label = { Text(if (italian) "Autore (facoltativo)" else "Auteur (facultatif)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun ReadingSessionDialog(label: String, italian: Boolean, onDismiss: () -> Unit, onComplete: (Int) -> Unit) {
    var chosen by remember { mutableStateOf<Int?>(null) }
    var running by remember { mutableStateOf(false) }
    var remaining by remember { mutableStateOf(0) }

    LaunchedEffect(running, remaining) {
        if (running && remaining > 0) {
            delay(1000)
            remaining -= 1
        } else if (running && remaining <= 0) {
            onComplete(chosen ?: 5)
        }
    }

    AlertDialog(
        onDismissRequest = { if (!running) onDismiss() },
        confirmButton = {
            if (!running) {
                TextButton(onClick = { chosen?.let { running = true; remaining = it * 60 } }, enabled = chosen != null) {
                    Text(if (italian) "Inizia" else "Commencer")
                }
            } else {
                TextButton(onClick = { running = false; onDismiss() }) { Text(if (italian) "Interrompi" else "Interrompre") }
            }
        },
        dismissButton = { if (!running) TextButton(onClick = onDismiss) { Text(if (italian) "Annulla" else "Annuler") } },
        title = { Text(label) },
        text = {
            if (!running) {
                Column {
                    Text(
                        if (italian) "Scegli la durata di lettura:" else "Choisis la durée de lecture :",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowDurations(chosen, italian) { chosen = it }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    val mm = remaining / 60
                    val ss = remaining % 60
                    Text(
                        String.format("%02d:%02d", mm, ss),
                        style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        if (italian) "Lettura in corso..." else "Lecture en cours...",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@Composable
private fun FlowDurations(chosen: Int?, italian: Boolean, onChoose: (Int) -> Unit) {
    Column {
        READING_DURATIONS.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { m ->
                    val sel = chosen == m
                    Text(
                        "$m",
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onChoose(m) }
                            .padding(vertical = 10.dp),
                        color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        Text(
            if (italian) "minuti" else "minutes",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
