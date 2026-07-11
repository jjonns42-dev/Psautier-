package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.data.HoursPrayers
import com.liturgia.monastica.data.todayEpochDay
import java.util.Calendar
import kotlin.random.Random

/* ---------------------------------------------------------------------------
   Définition des Heures : nombre de psaumes, hymne, cantique, place de
   l'Évangile (moitié pour Matines, fin ailleurs), thème de lecture, longueur
   maximale, intercessions.
   --------------------------------------------------------------------------- */
private data class HourDef(
    val id: String, val fr: String, val it: String,
    val psalms: Int, val hymn: String, val canticle: String?,
    val gospel: String, val gospelMax: Int, val theme: String, val intercessions: Boolean
)

// Ordre canonique (sert aussi à répartir les psaumes sans répétition).
private val HOURS = listOf(
    HourDef("matines", "Matines (Vigiles)", "Mattutino (Vigilie)", 12, "st_patrick", null, "half", 50, "random", false),
    HourDef("laudes", "Laudes", "Lodi", 6, "vexilla_crux", "benedictus", "end", 50, "random", true),
    HourDef("tierce", "Tierce", "Terza", 3, "veni_creator", null, "end", 15, "holy_spirit", false),
    HourDef("sexte", "Sexte", "Sesta", 3, "vexilla_crux", null, "end", 15, "random", false),
    HourDef("none", "None", "Nona", 3, "ephrem_passion", null, "end", 15, "passion", false),
    HourDef("vepres", "Vêpres", "Vespri", 4, "phos_hilaron", "magnificat", "end", 50, "random", true),
    HourDef("complies", "Complies", "Compieta", 1, "vexilla_crux", "nunc", "end", 15, "random", false),
    HourDef("minuit", "Minuit", "Mezzanotte", 2, "christos_anesti", null, "end", 15, "resurrection", false)
)
private val TOTAL_SLOTS = HOURS.sumOf { it.psalms }          // 34
private fun offsetOf(hour: HourDef): Int =
    HOURS.takeWhile { it.id != hour.id }.sumOf { it.psalms }

private data class GRef(val file: String, val book: String, val chap: Int, val start: Int, val end: Int)
private val GOSPELS = listOf(
    Triple("bible/matthieu_fr.json", "Matthieu", 28),
    Triple("bible/marc_fr.json", "Marc", 16),
    Triple("bible/luc_fr.json", "Luc", 24),
    Triple("bible/jean_fr.json", "Jean", 21)
)
private val HOLY_SPIRIT = listOf(
    GRef("bible/jean_fr.json", "Jean", 14, 15, 26), GRef("bible/jean_fr.json", "Jean", 16, 5, 15),
    GRef("bible/jean_fr.json", "Jean", 7, 37, 39), GRef("bible/jean_fr.json", "Jean", 20, 19, 23),
    GRef("bible/luc_fr.json", "Luc", 11, 9, 13), GRef("bible/luc_fr.json", "Luc", 4, 14, 21),
    GRef("bible/jean_fr.json", "Jean", 3, 1, 8)
)
private val PASSION = listOf(
    GRef("bible/matthieu_fr.json", "Matthieu", 26, 36, 46), GRef("bible/matthieu_fr.json", "Matthieu", 27, 27, 44),
    GRef("bible/marc_fr.json", "Marc", 15, 16, 32), GRef("bible/luc_fr.json", "Luc", 22, 39, 53),
    GRef("bible/luc_fr.json", "Luc", 23, 33, 46), GRef("bible/jean_fr.json", "Jean", 18, 1, 14),
    GRef("bible/jean_fr.json", "Jean", 19, 16, 30)
)
private val RESURRECTION = listOf(
    GRef("bible/matthieu_fr.json", "Matthieu", 28, 1, 10), GRef("bible/marc_fr.json", "Marc", 16, 1, 8),
    GRef("bible/luc_fr.json", "Luc", 24, 1, 12), GRef("bible/luc_fr.json", "Luc", 24, 13, 27),
    GRef("bible/jean_fr.json", "Jean", 20, 1, 18), GRef("bible/jean_fr.json", "Jean", 20, 19, 29),
    GRef("bible/jean_fr.json", "Jean", 21, 1, 14)
)

/** Psaume à la position globale, sans répétition à l'intérieur d'un cycle de 150. */
private fun psalmAt(globalSlot: Long): Int {
    val cycle = Math.floorDiv(globalSlot, 150L)
    val pos = Math.floorMod(globalSlot, 150L).toInt()
    val perm = (1..150).toList().shuffled(Random(cycle * 1000003L + 12345L))
    return perm[pos]
}

private fun defaultHourId(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (h) {
        23, 0, 1 -> "minuit"
        2, 3, 4 -> "matines"
        5, 6, 7, 8 -> "laudes"
        9, 10 -> "tierce"
        11, 12, 13 -> "sexte"
        14, 15, 16 -> "none"
        17, 18, 19 -> "vepres"
        else -> "complies"
    }
}

@Composable
fun HoursScreen(repo: ContentRepository, store: GameStore, onBack: () -> Unit) {
    val italian = store.lang == "it"
    val prayers = remember { repo.hoursPrayers() }
    val epochDay = remember { todayEpochDay() }
    var hourId by remember { mutableStateOf(defaultHourId()) }
    val hour = HOURS.first { it.id == hourId }

    // Import via l'écran existant AppScaffold
    com.liturgia.monastica.ui.components.AppScaffold(
        title = if (italian) "Liturgia delle Ore" else "Liturgie des Heures",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            // Sélecteur des 8 heures
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HOURS.forEach { h ->
                    val sel = h.id == hourId
                    Text(
                        if (italian) h.it else h.fr,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .clickable { hourId = h.id }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Divider()
            HourOffice(repo, prayers, hour, epochDay, italian)
        }
    }
}

@Composable
private fun HourOffice(
    repo: ContentRepository, prayers: HoursPrayers, hour: HourDef, epochDay: Long, italian: Boolean
) {
    // Psaumes du jour pour cette heure (tirés sans répétition)
    val base = epochDay * TOTAL_SLOTS + offsetOf(hour)
    val psalmNums = remember(hour.id, epochDay) { (0 until hour.psalms).map { psalmAt(base + it) } }

    // Évangile / lecture du jour (stable dans la journée, change chaque jour)
    val gseed = epochDay * 8 + HOURS.indexOfFirst { it.id == hour.id }
    val passage = remember(hour.id, epochDay) { pickPassage(repo, hour, Random(gseed)) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(if (italian) hour.it else hour.fr, style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)

        // Annotation des psaumes (numéros)
        Text(
            (if (italian) "Salmi : " else "Psaumes : ") + psalmNums.joinToString(", "),
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
        )
        Text(
            if (italian) "I 150 salmi in una settimana o meno, senza ripetizione."
            else "Les 150 psaumes en une semaine ou moins, sans répétition.",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Hymne
        prayers.hymns[hour.hymn]?.let { hy ->
            SectionTitle(if (italian) "Hymne — " + hy.titleIt else "Hymne — " + hy.titleFr)
            hy.lines.forEach { line ->
                Text(line, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface, fontStyle = FontStyle.Italic)
            }
        }

        // Psaumes (avec Évangile inséré à la moitié pour Matines)
        SectionTitle(if (italian) "Salmodia" else "Psalmodie")
        val half = if (hour.gospel == "half") hour.psalms / 2 else -1
        psalmNums.forEachIndexed { idx, n ->
            PsalmBlock(n, repo.psalmText(n))
            if (idx == half - 1) {
                GospelBlock(passage, italian)
            }
        }

        // Cantique évangélique (Benedictus / Magnificat / Nunc dimittis)
        canticleRef(hour.canticle)?.let { c ->
            val verses = repo.bibleVerses(c.file, c.chap).filter { it.first in c.start..c.end }
            SectionTitle(canticleTitle(hour.canticle!!, italian))
            verses.forEach { (vn, vt) ->
                Text("$vn  $vt", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Évangile / lecture à la fin (sauf Matines où il est à la moitié)
        if (hour.gospel == "end") GospelBlock(passage, italian)

        // Intercessions (Laudes et Vêpres)
        if (hour.intercessions) Intercessions(prayers, epochDay, italian)

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun PsalmBlock(n: Int, text: String) {
    Text("Psaume $n", style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp))
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
}

private data class Passage(val ref: String, val verses: List<Pair<Int, String>>)

@Composable
private fun GospelBlock(p: Passage, italian: Boolean) {
    SectionTitle((if (italian) "Vangelo — " else "Évangile — ") + p.ref)
    p.verses.forEach { (vn, vt) ->
        Text("$vn  $vt", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
    if (p.verses.isEmpty()) Text("—", color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun Intercessions(prayers: HoursPrayers, epochDay: Long, italian: Boolean) {
    SectionTitle(if (italian) prayers.intercessionTitleIt else prayers.intercessionTitleFr)
    prayers.intercession.forEach { para ->
        Text(para, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 2.dp))
    }
    SectionTitle(if (italian) "Padre nostro" else "Notre Père")
    prayers.notrePere.forEach { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface) }

    SectionTitle(if (italian) prayers.oraisonTitleIt else prayers.oraisonTitleFr)
    prayers.oraison.forEach { para ->
        Text(para, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 2.dp))
    }

    // Invocation finale (facultative), répétée 12 fois — au hasard entre les deux formules
    val inv = prayers.invocations[Random(epochDay).nextInt(prayers.invocations.size)]
    SectionTitle(if (italian) "Invocazione (× 12)" else "Invocation (× 12)")
    for (i in 1..12) {
        Text("$i.  $inv", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SectionTitle(t: String) {
    Text(t, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
        fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
}

/* ---- Sélection des passages ------------------------------------------------ */
private fun pickPassage(repo: ContentRepository, hour: HourDef, rng: Random): Passage = when (hour.theme) {
    "holy_spirit" -> themed(repo, HOLY_SPIRIT.random(rng), hour.gospelMax)
    "passion" -> themed(repo, PASSION.random(rng), hour.gospelMax)
    "resurrection" -> themed(repo, RESURRECTION.random(rng), hour.gospelMax)
    else -> randomGospel(repo, hour.gospelMax, rng)
}

private fun themed(repo: ContentRepository, g: GRef, max: Int): Passage {
    val all = repo.bibleVerses(g.file, g.chap).filter { it.first in g.start..g.end }
    val slice = if (all.size > max) all.subList(0, max) else all
    val a = slice.firstOrNull()?.first ?: g.start
    val b = slice.lastOrNull()?.first ?: g.end
    return Passage("${g.book} ${g.chap}, $a-$b", slice)
}

private fun randomGospel(repo: ContentRepository, max: Int, rng: Random): Passage {
    repeat(6) {
        val (file, book, chapters) = GOSPELS.random(rng)
        val chap = 1 + rng.nextInt(chapters)
        val verses = repo.bibleVerses(file, chap)
        if (verses.isNotEmpty()) {
            val total = verses.size
            val count = minOf(max, total)
            val startIdx = if (total <= count) 0 else rng.nextInt(total - count + 1)
            val slice = verses.subList(startIdx, startIdx + count)
            val a = slice.first().first; val b = slice.last().first
            return Passage("$book $chap, $a-$b", slice)
        }
    }
    return Passage("—", emptyList())
}

/* ---- Cantiques évangéliques (Crampon, dans saint Luc) ---------------------- */
private fun canticleRef(id: String?): GRef? = when (id) {
    "benedictus" -> GRef("bible/luc_fr.json", "Luc", 1, 68, 79)
    "magnificat" -> GRef("bible/luc_fr.json", "Luc", 1, 46, 55)
    "nunc" -> GRef("bible/luc_fr.json", "Luc", 2, 29, 32)
    else -> null
}
private fun canticleTitle(id: String, italian: Boolean): String = when (id) {
    "benedictus" -> if (italian) "Cantico di Zaccaria (Benedictus)" else "Cantique de Zacharie (Benedictus)"
    "magnificat" -> if (italian) "Cantico della Vergine (Magnificat)" else "Cantique de la Vierge (Magnificat)"
    else -> if (italian) "Cantico di Simeone (Nunc dimittis)" else "Cantique de Siméon (Nunc dimittis)"
}
