package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.data.BibleBook
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.ui.components.AppScaffold
import com.liturgia.monastica.ui.components.SectionCard

@Composable
fun BibleScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val it = store.lang == "it"
    val books = remember { repo.bibleBooks() }
    var book by remember { mutableStateOf<BibleBook?>(null) }
    var chapter by remember { mutableStateOf<Int?>(null) }

    val title = when {
        book != null && chapter != null -> "${book!!.name.substringAfterLast(' ')} $chapter"
        book != null -> book!!.name.removePrefix("Évangile selon ").removePrefix("saint ")
        else -> if (it) "Bibbia" else "La Bible"
    }

    AppScaffold(
        title = title,
        onBack = {
            when {
                chapter != null -> chapter = null
                book != null -> book = null
                else -> onBack()
            }
        },
        night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            if (book == null) {
                BookListView(books, it) { selected -> book = selected }
            } else if (chapter == null) {
                ChapterGrid(book!!, repo) { c -> chapter = c }
            } else {
                ChapterReader(repo, book!!, chapter!!)
            }
        }
    }
}

@Composable
private fun BookListView(books: List<BibleBook>, it: Boolean, onPick: (BibleBook) -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Text(
                if (it) "Antico e Nuovo Testamento — testo Crampon 1923"
                else "Ancien et Nouveau Testament — texte Crampon 1923",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(books) { b ->
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp)
                    .clickable { onPick(b) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(b.name, style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        Text("${b.chapters} ${if (it) "capitoli" else "chapitres"} · ${b.testament}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun ChapterGrid(book: BibleBook, repo: ContentRepository, onPick: (Int) -> Unit) {
    val count = remember(book.id) { repo.bibleChapterCount(book.file) }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 56.dp),
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        items((1..count).toList()) { c ->
            Card(
                Modifier.padding(6.dp).clickable { onPick(c) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                    Text("$c", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun ChapterReader(repo: ContentRepository, book: BibleBook, chapter: Int) {
    val text = remember(book.id, chapter) { repo.bibleChapter(book.file, chapter) }
    LazyColumn(Modifier.fillMaxSize().padding(vertical = 8.dp)) {
        item {
            SectionCard(
                title = book.name.removePrefix("Évangile selon ").removePrefix("saint "),
                subtitle = "${if (book.id == "psaumes") "Psaume" else "Chapitre"} $chapter",
                body = text
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
