package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.data.FastingCalendar
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.ui.components.AppScaffold

private data class PenanceWork(
    val id: String,
    val nameFr: String, val nameIt: String,
    val descFr: String, val descIt: String
)

private val PENANCES = listOf(
    PenanceWork("pain-eau", "Jour au pain et à l'eau", "Giorno a pane e acqua",
        "Un jour de jeûne rigoureux, au pain et à l'eau.", "Un giorno di digiuno rigoroso, a pane e acqua."),
    PenanceWork("abstinence", "Abstinence de viande", "Astinenza dalla carne",
        "S'abstenir de viande en esprit de pénitence.", "Astenersi dalla carne in spirito di penitenza."),
    PenanceWork("aumone", "Aumône aux pauvres", "Elemosina ai poveri",
        "Donner de son nécessaire à un plus pauvre.", "Donare del proprio necessario a un più povero."),
    PenanceWork("renoncement", "Renoncement volontaire", "Rinuncia volontaria",
        "Se priver d'un plaisir légitime, offert à Dieu.", "Privarsi di un piacere lecito, offerto a Dio."),
    PenanceWork("silence", "Silence pénitentiel", "Silenzio penitenziale",
        "Garder un long silence, mortifier la langue.", "Custodire un lungo silenzio, mortificare la lingua."),
    PenanceWork("psaumes", "Sept psaumes pénitentiels", "Sette salmi penitenziali",
        "Prier les sept psaumes de la pénitence.", "Pregare i sette salmi penitenziali."),
    PenanceWork("veille", "Veille de prière", "Veglia di preghiera",
        "Veiller une partie de la nuit en prière.", "Vegliare una parte della notte in preghiera."),
    PenanceWork("misericorde", "Œuvre de miséricorde", "Opera di misericordia",
        "Accomplir une œuvre de miséricorde, corporelle ou spirituelle.", "Compiere un'opera di misericordia, corporale o spirituale."),
    PenanceWork("pardon", "Pardon offert", "Perdono offerto",
        "Pardonner une offense de tout cœur.", "Perdonare di cuore un'offesa."),
    PenanceWork("confession", "Confession sacramentelle", "Confessione sacramentale",
        "Recevoir le sacrement de la réconciliation.", "Ricevere il sacramento della riconciliazione."),
    PenanceWork("prostrations", "Prosternations", "Prostrazioni",
        "Faire des métanies, corps et âme dans la supplication.", "Fare metanie, corpo e anima nella supplica."),
    PenanceWork("patience", "Épreuve acceptée", "Prova accettata",
        "Supporter avec patience une contrariété du jour, offerte.", "Sopportare con pazienza una contrarietà del giorno, offerta.")
)

private fun penanceStage(total: Int, italian: Boolean): String = when {
    total <= 0 -> if (italian) "Nessuna penitenza ancora" else "Aucune pénitence encore"
    total < 10 -> if (italian) "Primi passi nella penitenza" else "Premiers pas dans la pénitence"
    total < 40 -> if (italian) "Cammino penitenziale" else "Chemin pénitentiel"
    total < 100 -> if (italian) "Una quarantena di penitenza" else "Une quarantaine de pénitence"
    total < 365 -> if (italian) "Cuore contrito" else "Cœur contrit"
    else -> if (italian) "Penitente perseverante" else "Pénitent persévérant"
}

@Composable
fun PenanceScreen(store: GameStore, onBack: () -> Unit) {
    val italian = store.lang == "it"
    val today = remember { FastingCalendar.todayYmd() }
    val todayKey = "${today.year}-${today.month}-${today.day}"
    LaunchedEffect(Unit) { store.penancePeriodRefresh() }

    AppScaffold(
        title = if (italian) "Penitenza" else "Pénitence",
        onBack = onBack, night = store.night, onToggleNight = { store.toggleNight() }
    ) { pad ->
        val total = PENANCES.sumOf { store.penanceState(it.id).level }
        Column(
            Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text(
                if (italian) "Opere di penitenza" else "Œuvres de pénitence",
                style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                if (italian)
                    "Ogni volta che compi un'opera di penitenza, segnala: il suo livello sale. Una volta al giorno per opera."
                else
                    "Chaque fois que tu accomplis une œuvre de pénitence, marque-la : son niveau monte. Une fois par jour par œuvre.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                (if (italian) "Totale atti · " else "Total d'actes · ") + "$total — " + penanceStage(total, italian),
                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp)
            )

            // --- Temps de pénitence : une durée choisie, parcourue jour après jour ---
            val period = store.penancePeriod
            Card(
                Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        if (italian) "Tempo di penitenza" else "Temps de pénitence",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (store.penancePeriodJustReset) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                if (italian) "Un giorno saltato ha azzerato il tempo." else "Un jour manqué a remis le temps à zéro.",
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { store.penancePeriodConsumeReset() }, contentPadding = PaddingValues(0.dp)) {
                                Text("Ok", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    if (!period.started) {
                        Text(
                            if (italian) "Scegli una durata da percorrere giorno dopo giorno."
                            else "Choisis une durée à parcourir jour après jour.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )
                        val options = listOf(
                            3 to (if (italian) "3 giorni" else "3 jours"),
                            7 to (if (italian) "1 settimana" else "1 semaine"),
                            14 to (if (italian) "2 settimane" else "2 semaines"),
                            21 to (if (italian) "3 settimane" else "3 semaines"),
                            40 to (if (italian) "40 giorni" else "40 jours")
                        )
                        options.chunked(2).forEach { row ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                row.forEach { pair ->
                                    OutlinedButton(
                                        onClick = { store.penancePeriodStart(pair.first) },
                                        modifier = Modifier.weight(1f)
                                    ) { Text(pair.second, style = MaterialTheme.typography.labelSmall) }
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    } else {
                        val done = period.daysDone
                        val target = period.targetDays
                        val complete = done >= target
                        val remaining = (target - done).coerceAtLeast(0)
                        val pct = if (target > 0) (done * 100 / target).coerceIn(0, 100) else 0
                        Text(
                            (if (italian) "Giorno " else "Jour ") + "${done.coerceAtMost(target)} " +
                                (if (italian) "di " else "sur ") + "$target · $pct%",
                            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        if (!complete) {
                            Text(
                                (if (italian) "mancano " else "il reste ") + "$remaining " +
                                    (if (italian) "giorni" else "jours"),
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        when {
                            complete -> {
                                Text(
                                    if (italian) "✓ Tempo di penitenza compiuto!" else "✓ Temps de pénitence accompli !",
                                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary
                                )
                                Button(
                                    onClick = { store.penancePeriodReset() },
                                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                                ) { Text(if (italian) "Ricomincia" else "Recommencer") }
                            }
                            store.penancePeriodDoneToday() -> Text(
                                if (italian) "✓ Fatto oggi — torna domani" else "✓ Fait aujourd'hui — reviens demain",
                                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary
                            )
                            else -> Button(
                                onClick = { store.penancePeriodConfirm() },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(if (italian) "Ho fatto penitenza oggi" else "J'ai fait pénitence aujourd'hui") }
                        }
                        if (!complete) {
                            TextButton(onClick = { store.penancePeriodReset() }, contentPadding = PaddingValues(0.dp)) {
                                Text(if (italian) "Annulla il tempo" else "Annuler le temps", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            PENANCES.forEach { p ->
                val st = store.penanceState(p.id)
                val doneToday = st.lastDayKey == todayKey
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (italian) p.nameIt else p.nameFr,
                                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f)
                            )
                            Text(
                                (if (italian) "Livello " else "Niveau ") + "${st.level}",
                                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Text(
                            if (italian) p.descIt else p.descFr,
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )
                        if (doneToday) {
                            Text(
                                if (italian) "✓ Fatto oggi" else "✓ Fait aujourd'hui",
                                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary
                            )
                        } else {
                            Button(onClick = { store.penanceDo(p.id, todayKey) }, modifier = Modifier.fillMaxWidth()) {
                                Text(if (italian) "L'ho fatto oggi" else "Je l'ai fait aujourd'hui")
                            }
                        }
                    }
                }
            }
            val hist = store.penancePeriodHistory
            if (hist.isNotEmpty()) {
                Text(
                    if (italian) "Tempi compiuti" else "Temps accomplis",
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
                hist.reversed().forEach { h ->
                    val date = FastingCalendar.formatDate(FastingCalendar.epochDayToYmd(h.endEpochDay), italian)
                    Text(
                        "• ${h.targetDays} " + (if (italian) "giorni — compiuto il " else "jours — accompli le ") + date,
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
