package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.ui.LocalEngine
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings
import java.util.Calendar

@Composable
fun CalendarScreen() {
    val engine = LocalEngine.current
    val repo = LocalRepo.current
    val lang = LocalSettings.current.lang
    val today = Calendar.getInstance()

    var viewYear by remember { mutableIntStateOf(today.get(Calendar.YEAR)) }
    var viewMonth by remember { mutableIntStateOf(today.get(Calendar.MONTH) + 1) } // 1..12
    var selDay by remember { mutableIntStateOf(today.get(Calendar.DAY_OF_MONTH)) }
    var selYear by remember { mutableIntStateOf(today.get(Calendar.YEAR)) }
    var selMonth by remember { mutableIntStateOf(today.get(Calendar.MONTH) + 1) }

    val monthsFr = listOf("Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Août","Septembre","Octobre","Novembre","Décembre")
    val monthsIt = listOf("Gennaio","Febbraio","Marzo","Aprile","Maggio","Giugno","Luglio","Agosto","Settembre","Ottobre","Novembre","Dicembre")
    val daysFr = listOf("dim","lun","mar","mer","jeu","ven","sam")
    val daysIt = listOf("dom","lun","mar","mer","gio","ven","sab")
    val months = if (lang == Lang.FR) monthsFr else monthsIt
    val dayHdr = if (lang == Lang.FR) daysFr else daysIt

    fun prev() { if (viewMonth == 1) { viewMonth = 12; viewYear-- } else viewMonth-- }
    fun next() { if (viewMonth == 12) { viewMonth = 1; viewYear++ } else viewMonth++ }

    val firstCal = Calendar.getInstance().apply { clear(); set(viewYear, viewMonth - 1, 1) }
    val firstDow = firstCal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun
    val daysInMonth = firstCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        // header with arrows
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { prev() }) { Icon(Icons.Filled.ChevronLeft, null) }
            Text("${months[viewMonth - 1]} $viewYear",
                Modifier.weight(1f), textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { next() }) { Icon(Icons.Filled.ChevronRight, null) }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            dayHdr.forEach {
                Text(it, Modifier.weight(1f), textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        // grid
        val cells = firstDow + daysInMonth
        val rows = (cells + 6) / 7
        var dayNum = 1
        for (r in 0 until rows) {
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                for (c in 0 until 7) {
                    val idx = r * 7 + c
                    if (idx < firstDow || dayNum > daysInMonth) {
                        Box(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val d = dayNum
                        val isSel = d == selDay && viewMonth == selMonth && viewYear == selYear
                        val isToday = d == today.get(Calendar.DAY_OF_MONTH) &&
                            viewMonth == today.get(Calendar.MONTH) + 1 &&
                            viewYear == today.get(Calendar.YEAR)
                        Box(
                            Modifier.weight(1f).aspectRatio(1f).padding(3.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSel) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selDay = d; selMonth = viewMonth; selYear = viewYear },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                d.toString(),
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        dayNum++
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        val info = engine.dayInfo(selYear, selMonth, selDay)
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("$selDay ${months[selMonth - 1]} $selYear",
                    style = MaterialTheme.typography.titleMedium)
                Text(
                    if (info.tone > 0) "${if (lang == Lang.FR) "Ton" else "Tono"} ${info.tone} · ${info.theme(lang)}"
                    else info.theme(lang),
                    color = MaterialTheme.colorScheme.primary)
                info.feast(lang)?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
                if (info.heothinon > 0) {
                    Text(
                        (if (lang == Lang.FR) "Évangile dominical n° " else "Vangelo domenicale n. ") + info.heothinon,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val k = (info.kathMatines + info.kathVepres).joinToString(", ")
                if (k.isNotBlank()) Text(
                    (if (lang == Lang.FR) "Kathismes : " else "Kathismata: ") + k,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(info.seasonLabel(lang), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        val hwId = engine.holyWeekDayId(selYear, selMonth, selDay)
        if (hwId != null) {
            val hw = repo.holyWeek.firstOrNull { it.id == hwId }
            hw?.let {
                Spacer(Modifier.height(10.dp))
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(it.title(lang), style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(it.text(lang), style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                        it.textRef?.let { ref ->
                            repo.resolveAnyText(ref, lang)?.let { hymnText ->
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
                                Spacer(Modifier.height(8.dp))
                                Text(hymnText, style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }
        }

        val movable = engine.movableSundayName(selYear, selMonth, selDay)
        if (movable != null) {
            Spacer(Modifier.height(10.dp))
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        if (lang == Lang.FR) movable.first else movable.second,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    // Palm Sunday carries an actual hymn text; the others are date labels only.
                    val isPalmSunday = (if (lang == Lang.FR) movable.first else movable.second)
                        .contains(if (lang == Lang.FR) "Rameaux" else "Palme")
                    if (isPalmSunday) {
                        repo.palmSunday?.textRef?.let { ref ->
                            repo.resolveAnyText(ref, lang)?.let { hymnText ->
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
                                Spacer(Modifier.height(8.dp))
                                Text(hymnText, style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }
        }
        Text(
            if (lang == Lang.FR)
                "Ton, saison et kathismes calculés (indicatifs). Les fêtes mobiles suivent le Paschalion orthodoxe."
            else
                "Tono, stagione e kathismata calcolati (indicativi). Le feste mobili seguono il Paschalion ortodosso.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}
