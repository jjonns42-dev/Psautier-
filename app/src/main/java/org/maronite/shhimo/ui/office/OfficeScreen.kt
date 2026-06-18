package org.maronite.shhimo.ui.office

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.maronite.shhimo.data.model.AppLanguage
import org.maronite.shhimo.data.model.CanonicalHour
import org.maronite.shhimo.data.model.OfficeElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficeScreen(
    viewModel: OfficeViewModel,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lang = state.language

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.day?.title?.forLanguage(lang)
                                ?: hourTitle(state.selectedHour, lang),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = state.date.toString(),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::previousDay) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backLabel(lang))
                    }
                    IconButton(onClick = viewModel::goToToday) {
                        Icon(Icons.Filled.Today, contentDescription = todayLabel(lang))
                    }
                    IconButton(onClick = viewModel::nextDay) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = forwardLabel(lang))
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            HourSelector(
                selected = state.selectedHour,
                language = lang,
                onSelect = viewModel::selectHour
            )
            val office = state.office
            val hour = office?.hour(state.selectedHour)
            if (hour == null || hour.elements.isEmpty()) {
                EmptyOffice(lang)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(hour.elements) { element ->
                        OfficeElementCard(element, lang)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HourSelector(
    selected: CanonicalHour,
    language: AppLanguage,
    onSelect: (CanonicalHour) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CanonicalHour.principal.forEach { hour ->
            FilterChip(
                selected = hour == selected,
                onClick = { onSelect(hour) },
                label = { Text(hourTitle(hour, language)) }
            )
        }
    }
}

@Composable
private fun OfficeElementCard(element: OfficeElement, language: AppLanguage) {
    val isRubric = element.type == org.maronite.shhimo.data.model.OfficeElementType.RUBRIC
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            if (element.reference.isNotBlank()) {
                Text(
                    element.reference,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            val title = element.title.forLanguage(language)
            if (title.isNotBlank()) {
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            val body = element.body.forLanguage(language)
            if (body.isNotBlank()) {
                Text(
                    body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isRubric) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun EmptyOffice(language: AppLanguage) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (language) {
                AppLanguage.ARABIC -> "لم تُدخَل نصوص هذا المكتب بعد."
                AppLanguage.ITALIAN -> "I testi di questo ufficio non sono ancora stati inseriti."
                AppLanguage.SYRIAC -> "..."
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun hourTitle(hour: CanonicalHour, lang: AppLanguage): String = when (hour) {
    CanonicalHour.RAMCHO -> mapOf(AppLanguage.ARABIC to "رمشو (المساء)", AppLanguage.ITALIAN to "Ramsho (Vespro)", AppLanguage.SYRIAC to "ܪܡܫܐ")[lang]!!
    CanonicalHour.SOUTORO -> mapOf(AppLanguage.ARABIC to "سوتورو (النوم)", AppLanguage.ITALIAN to "Sutoro (Compieta)", AppLanguage.SYRIAC to "ܣܘܬܪܐ")[lang]!!
    CanonicalHour.LILIO -> mapOf(AppLanguage.ARABIC to "ليليو (الليل)", AppLanguage.ITALIAN to "Lilyo (Veglia)", AppLanguage.SYRIAC to "ܠܠܝܐ")[lang]!!
    CanonicalHour.SAFRO -> mapOf(AppLanguage.ARABIC to "صفرو (الصباح)", AppLanguage.ITALIAN to "Safro (Lodi)", AppLanguage.SYRIAC to "ܨܦܪܐ")[lang]!!
    else -> hour.syriacKey
}

private fun todayLabel(l: AppLanguage) = when (l) { AppLanguage.ARABIC -> "اليوم"; AppLanguage.ITALIAN -> "Oggi"; AppLanguage.SYRIAC -> "Today" }
private fun backLabel(l: AppLanguage) = when (l) { AppLanguage.ARABIC -> "السابق"; AppLanguage.ITALIAN -> "Precedente"; AppLanguage.SYRIAC -> "Prev" }
private fun forwardLabel(l: AppLanguage) = when (l) { AppLanguage.ARABIC -> "التالي"; AppLanguage.ITALIAN -> "Successivo"; AppLanguage.SYRIAC -> "Next" }
