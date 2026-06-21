package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.byzantine.horologion.data.BibleBookFull
import com.byzantine.horologion.data.BibleBookMeta
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BibleScreen() {
    val repo = LocalRepo.current
    val lang = LocalSettings.current.lang
    val index = remember { repo.bibleIndex }

    var testament by remember { mutableStateOf("AT") }
    var bookMeta by remember { mutableStateOf<BibleBookMeta?>(null) }
    var chapter by remember { mutableStateOf<Int?>(null) }

    // LEVEL 1 — book list
    if (bookMeta == null) {
        Column(Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = if (testament == "AT") 0 else 1) {
                Tab(selected = testament == "AT", onClick = { testament = "AT" },
                    text = { Text(if (lang == Lang.FR) "Ancien Testament" else "Antico Testamento") })
                Tab(selected = testament == "NT", onClick = { testament = "NT" },
                    text = { Text(if (lang == Lang.FR) "Nouveau Testament" else "Nuovo Testamento") })
            }
            Text(
                repo.bibleVersion, Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
                items(index.filter { it.testament == testament }) { b ->
                    Column(
                        Modifier.fillMaxWidth().clickable { bookMeta = b; chapter = null }
                            .padding(horizontal = 4.dp, vertical = 12.dp)
                    ) {
                        Text(b.name(lang), style = MaterialTheme.typography.titleMedium)
                        Text((if (lang == Lang.FR) "chapitres : " else "capitoli: ") + b.chapters,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider()
                }
            }
        }
        return
    }

    val meta = bookMeta!!
    // load the book off the main thread
    val full by produceState<BibleBookFull?>(initialValue = null, key1 = meta.id) {
        value = withContext(Dispatchers.IO) { repo.bibleBook(meta.id) }
    }

    // LEVEL 2 — chapter grid
    if (chapter == null) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            BackRow(lang) { bookMeta = null }
            Text(meta.name(lang), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (c in 1..meta.chapters) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(48.dp).clickable { chapter = c }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(c.toString(), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        return
    }

    // LEVEL 3 — verses
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        BackRow(lang) { chapter = null }
        Text(
            meta.name(lang) + " " + chapter,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(10.dp))
        val f = full
        if (f == null) {
            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val verses = f.chapters.getOrNull(chapter!! - 1).orEmpty()
            verses.forEach { v ->
                Text(v, style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 3.dp))
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun BackRow(lang: Lang, onBack: () -> Unit) {
    TextButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        Spacer(Modifier.width(6.dp))
        Text(if (lang == Lang.FR) "Retour" else "Indietro")
    }
}
