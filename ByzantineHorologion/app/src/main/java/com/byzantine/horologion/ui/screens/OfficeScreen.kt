package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.data.Office
import com.byzantine.horologion.data.OfficeStep
import com.byzantine.horologion.data.Role
import com.byzantine.horologion.ui.LocalEngine
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OfficeScreen() {
    val settings = LocalSettings.current
    val repo = LocalRepo.current
    val engine = LocalEngine.current
    val lang = settings.lang

    val now = Calendar.getInstance()
    val info = engine.dayInfo(
        now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH)
    )

    val offices = repo.allOffices()
    var selected by remember { mutableStateOf(offices.firstOrNull { it.id == "orthros" } ?: offices.first()) }
    var role by remember { mutableStateOf(settings.role) }

    Column(Modifier.fillMaxSize()) {
        // --- day header ---
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    if (info.tone > 0) "${if (lang == Lang.FR) "Ton" else "Tono"} ${info.tone} · ${info.theme(lang)}"
                    else info.theme(lang),
                    style = MaterialTheme.typography.titleMedium
                )
                info.feast(lang)?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary)
                }
                if (info.heothinon > 0) {
                    Text(
                        (if (lang == Lang.FR) "Évangile dominical (Heothinon) n° " else "Vangelo domenicale (Heothinon) n. ") +
                            info.heothinon,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    repo.eothinonText(info.heothinon, lang)?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                    repo.sundayExapostilarionText(info.heothinon, lang)?.let {
                        Text(
                            (if (lang == Lang.FR) "Exapostilaire : " else "Essaposteilarion: ") + it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                info.triodePrep?.let { which ->
                    val label = when (which) {
                        "publicain" -> if (lang == Lang.FR) "Dimanche du Publicain et du Pharisien" else "Domenica del Pubblicano e del Fariseo"
                        "prodigue" -> if (lang == Lang.FR) "Dimanche de l'Enfant prodigue" else "Domenica del Figlio prodigo"
                        "apokreo" -> if (lang == Lang.FR) "Dimanche du Jugement dernier (Carnaval)" else "Domenica del Giudizio finale (Carnevale)"
                        "tyrini" -> if (lang == Lang.FR) "Dimanche du Pardon (Tyrophagie)" else "Domenica del Perdono (Settimana dei latticini)"
                        else -> ""
                    }
                    Text(
                        (if (lang == Lang.FR) "Triode : " else "Triodion: ") + label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    repo.triodePreparatoryText(which, lang)?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                }
                val kath = (info.kathMatines + info.kathVepres).joinToString(", ")
                if (kath.isNotBlank()) {
                    Text(
                        (if (lang == Lang.FR) "Kathismes : " else "Kathismata: ") + kath +
                            "  ·  " + info.seasonLabel(lang),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- role switch (priest / laity) ---
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = role == Role.LAYPERSON,
                    onClick = { role = Role.LAYPERSON },
                    shape = SegmentedButtonDefaults.itemShape(0, 2)
                ) { Text(if (lang == Lang.FR) "Laïc" else "Laico") }
                SegmentedButton(
                    selected = role == Role.PRIEST,
                    onClick = { role = Role.PRIEST },
                    shape = SegmentedButtonDefaults.itemShape(1, 2)
                ) { Text(if (lang == Lang.FR) "Prêtre" else "Sacerdote") }
            }
        }

        // --- office selector ---
        FlowRow(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            offices.forEach { o ->
                FilterChip(
                    selected = selected.id == o.id,
                    onClick = { selected = o },
                    label = { Text(o.title(lang)) }
                )
            }
        }

        HorizontalDivider(Modifier.padding(top = 8.dp))

        // --- ordo ---
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                Text(
                    selected.title(lang),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            items(visibleSteps(selected, role)) { step ->
                StepRow(step, lang, info.tone, info.weekdayKey, repo)
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

private fun visibleSteps(office: Office, role: Role): List<OfficeStep> =
    if (role == Role.PRIEST) office.steps
    else office.steps // laity sees all, priest-only steps are tagged below

@Composable
private fun StepRow(step: OfficeStep, lang: Lang, tone: Int, weekday: String, repo: com.byzantine.horologion.data.Repository) {
    val isPriest = step.role == "pretre"
    val container = if (isPriest) MaterialTheme.colorScheme.surfaceVariant
    else MaterialTheme.colorScheme.surface

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(container)
            .padding(12.dp)
    ) {
        if (isPriest) {
            Text(
                if (lang == Lang.FR) "✠ Partie du prêtre" else "✠ Parte del sacerdote",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        step.rubric(lang)?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        val body = resolve(step, lang, tone, weekday, repo)
        if (!body.isNullOrBlank()) {
            Text(body, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** Resolve a step to its display text, following references. */
private fun resolve(step: OfficeStep, lang: Lang, tone: Int, weekday: String, repo: com.byzantine.horologion.data.Repository): String? {
    val ref = step.ref
    if (ref == "apolytikion" && tone in 1..8) {
        val t = repo.resurrectionTroparion(tone, lang)
        if (!t.isNullOrBlank()) return t
    }
    if (ref == "kontakion" && tone in 1..8) {
        val k = repo.resurrectionKontakion(tone, lang)
        if (!k.isNullOrBlank()) return k
    }
    if (ref == "hymne_trinitaire_ton" && tone in 1..8) {
        repo.trinitarianHymnByTone(tone, lang)?.let { return it }
    }
    if (ref == "trinika_dimanche_mesonyktikon") {
        val canonText = repo.trinitarianCanonText(tone, lang)
        val trinika = repo.sundayMesonyktikonTrinika(lang)
        return when {
            canonText != null && trinika != null -> canonText + "\n\n" +
                (if (lang == Lang.FR) "— Tropaires trinitaires fixes —\n" else "— Tropari trinitari fissi —\n") + trinika
            trinika != null -> trinika
            else -> null
        }
    }
    if (ref == "evlogitaires_resurrection") {
        repo.resurrectionEvlogitaria(lang)?.let { return it }
    }
    if (ref == "tropaire_epoux") {
        repo.bridegroomTroparion(lang)?.let { return it }
    }
    if (ref == "priere_eustrate") {
        repo.eustratiusPrayer(lang)?.let { return it }
    }
    if (ref == "canon" && tone == 0) {
        // Bright Week / Paschaltide: show the actual Easter Canon (Hypakoe + Odes 1,3,4,5)
        // instead of the generic ordinary-day description.
        repo.paschaCanonText(lang)?.let { return it }
        repo.commonPrayer("canon_paques_ode1", lang)?.let { return it }
    }
    if (ref == "canon" && weekday == "DIM") {
        repo.resurrectionCanonText(tone, lang)?.let { return it }
    }
    if (ref == "canon" && weekday == "LUN") {
        repo.mondayAngelsCanonText(tone, lang)?.let { return it }
    }
    if (ref == "exapostilaire" && weekday == "LUN") {
        repo.mondayAngelsExapostilarion(tone, lang)?.let { return it }
    }
    if (ref == "stichera_lucernaire" && weekday == "MAR") {
        repo.tuesdayForerunnerSticheron(tone, lang)?.let { return it }
    }
    if (ref == "stichera_lucernaire" && weekday == "SAM") {
        repo.saturdayFuneralTroparion(tone, lang)?.let { return it }
    }
    if (ref == "stichera_lucernaire" && weekday == "MER") {
        repo.wednesdayCrossSticheron(tone, lang)?.let { return it }
    }
    if (ref == "stichera_lucernaire" && weekday == "JEU") {
        repo.thursdayApostlesSticheron(tone, lang)?.let { return it }
    }
    if (ref == "stichera_lucernaire" && weekday == "VEN") {
        repo.fridayCrossSticheron(tone, lang)?.let { return it }
    }
    if (ref != null) {
        repo.commonPrayer(ref, lang)?.let { return it }
    }
    return step.text(lang)
}
