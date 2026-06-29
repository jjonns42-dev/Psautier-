package com.preciousblood.devotion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.preciousblood.devotion.data.FeastKind
import com.preciousblood.devotion.data.LiturgicalCalendar
import com.preciousblood.devotion.data.SpecialDay
import com.preciousblood.devotion.ui.theme.BloodRed
import com.preciousblood.devotion.ui.theme.CrimsonAccent
import com.preciousblood.devotion.ui.theme.Gold
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    contentPadding: PaddingValues,
    now: LocalDateTime = LocalDateTime.now(),
    onPrayerClick: (String) -> Unit
) {
    val today = now.toLocalDate()
    val active = LiturgicalCalendar.activeToday(today)
    val inGethsemani = LiturgicalCalendar.isGethsemaniNow(now)
    val nextGeth = LiturgicalCalendar.nextGethsemani(now)
    val nextFriday = LiturgicalCalendar.nextThirdFriday(today)
    val upcoming = LiturgicalCalendar.upcoming(now, limit = 8)

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TodayBanner(today, active, inGethsemani) }

        item {
            HighlightCard(
                icon = Icons.Filled.Brightness2,
                title = "Heures de Gethsémani",
                line1 = if (inGethsemani) "En cours maintenant — veillez et priez"
                        else "Prochain : ${frenchDate(nextGeth.toLocalDate())} à 23h00",
                line2 = "Chaque jeudi 23h → vendredi 3h",
                tint = BloodRed,
                onClick = { onPrayerClick("consolation") }
            )
        }
        item {
            HighlightCard(
                icon = Icons.Filled.Event,
                title = "Réparation du 3ᵉ vendredi",
                line1 = "Prochain : ${frenchDate(nextFriday)}",
                line2 = countdown(today, nextFriday),
                tint = CrimsonAccent,
                onClick = { onPrayerClick("troisiemes-vendredis") }
            )
        }

        item {
            Text(
                "À venir",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            )
        }
        items(upcoming) { day ->
            SpecialDayRow(day, today, onPrayerClick)
        }
    }
}

@Composable
private fun TodayBanner(today: LocalDate, active: List<SpecialDay>, geth: Boolean) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Aujourd'hui",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                frenchDate(today),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(8.dp))
            when {
                geth -> BannerLine("Heures de Gethsémani en cours — veillez avec Lui.")
                active.isNotEmpty() -> active.forEach { BannerLine("• ${it.title}") }
                else -> BannerLine("Dévotion quotidienne : Rosaire, Chapelet, Litanie, Consécration.")
            }
        }
    }
}

@Composable
private fun BannerLine(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
private fun HighlightCard(
    icon: ImageVector,
    title: String,
    line1: String,
    line2: String,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }
            Column(Modifier.padding(start = 14.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(line1, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(line2, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SpecialDayRow(day: SpecialDay, today: LocalDate, onPrayerClick: (String) -> Unit) {
    val tint = when (day.kind) {
        FeastKind.GETHSEMANI -> BloodRed
        FeastKind.VENDREDI -> CrimsonAccent
        FeastKind.JUILLET -> Gold
        FeastKind.FETE -> BloodRed
    }
    val icon = when (day.kind) {
        FeastKind.GETHSEMANI -> Icons.Filled.Brightness2
        FeastKind.VENDREDI -> Icons.Filled.Event
        FeastKind.JUILLET -> Icons.Filled.WbSunny
        FeastKind.FETE -> Icons.Filled.LocalFireDepartment
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = day.prayerId != null) {
                day.prayerId?.let(onPrayerClick)
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(14.dp)) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }
            Column(Modifier.padding(start = 14.dp).weight(1f)) {
                Text(day.title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    if (day.isRange)
                        "${frenchDate(day.date)} → ${frenchDate(day.endDate)}"
                    else frenchDate(day.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = tint
                )
                Text(day.description, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                countdown(today, day.date),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// --- Helpers de formatage en français ---

private fun frenchDate(d: LocalDate): String {
    val dow = d.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH)
        .replaceFirstChar { it.uppercase() }
    val month = d.month.getDisplayName(TextStyle.FULL, Locale.FRENCH)
    return "$dow ${d.dayOfMonth} $month ${d.year}"
}

private fun countdown(today: LocalDate, target: LocalDate): String {
    val days = LiturgicalCalendar.daysUntil(today, target)
    return when {
        days < 0L -> "en cours"
        days == 0L -> "aujourd'hui"
        days == 1L -> "demain"
        else -> "dans $days j"
    }
}
