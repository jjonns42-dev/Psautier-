package org.maronite.shhimo.domain.calendar

import org.maronite.shhimo.data.model.CalendarEntry
import org.maronite.shhimo.data.model.LiturgicalColor
import org.maronite.shhimo.data.model.LiturgicalDay
import org.maronite.shhimo.data.model.LiturgicalRank
import org.maronite.shhimo.data.model.LocalizedText
import org.maronite.shhimo.data.model.MaroniteSeason
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Résout, pour une date civile, le jour liturgique maronite correspondant :
 * saison, semaine, rang, couleur et identifiant d'office à charger.
 *
 * Le moteur combine deux sources :
 *  1. Les fêtes fixes et mobiles fournies par [calendarEntries]
 *     (chargées depuis assets/calendar/calendar.json).
 *  2. Le calcul des saisons à partir des dates pivots (Pâques, début de
 *     l'Annonciation, etc.).
 *
 * IMPORTANT : la délimitation exacte des saisons maronites et l'attribution
 * des semaines doivent être calées sur le calendrier officiel de Bkerké.
 * La logique ci-dessous est volontairement explicite et commentée pour
 * pouvoir être ajustée sans toucher au reste de l'application.
 */
class MaroniteCalendar(
    private val calendarEntries: List<CalendarEntry>,
    private val useJulianEaster: Boolean = false
) {

    fun resolve(date: LocalDate): LiturgicalDay {
        val year = date.year
        val easter = if (useJulianEaster) {
            EasterCalculator.julianEaster(year)
        } else {
            EasterCalculator.gregorianEaster(year)
        }

        // 1. Fête mobile (relative à Pâques) ?
        val mobile = calendarEntries.firstOrNull { entry ->
            entry.offsetFromEaster != null &&
                easter.plusDays(entry.offsetFromEaster.toLong()) == date
        }

        // 2. Fête fixe (MM-DD) ?
        val fixedKey = "%02d-%02d".format(date.monthValue, date.dayOfMonth)
        val fixed = calendarEntries.firstOrNull { it.fixedDate == fixedKey }

        // 3. Détermination de la saison.
        val season = determineSeason(date, easter)
        val weekOfSeason = weekOfSeason(date, season, easter)

        // Priorité : fête mobile > fête fixe > férie.
        val chosen = mobile ?: fixed

        val weekday = toLiturgicalWeekday(date.dayOfWeek)
        val isSunday = weekday == 1

        return if (chosen != null) {
            LiturgicalDay(
                civilDateIso = date.toString(),
                weekday = weekday,
                season = chosen.season,
                weekOfSeason = weekOfSeason,
                rank = chosen.rank,
                color = chosen.color,
                title = chosen.title,
                officeId = chosen.officeId ?: ferialOfficeId(season, weekday, weekOfSeason),
                commemorations = listOfNotNull(if (chosen == fixed) mobile else null)
            )
        } else {
            LiturgicalDay(
                civilDateIso = date.toString(),
                weekday = weekday,
                season = season,
                weekOfSeason = weekOfSeason,
                rank = if (isSunday) LiturgicalRank.SUNDAY else LiturgicalRank.FERIA,
                color = defaultColor(season),
                title = ferialTitle(season, weekday),
                officeId = ferialOfficeId(season, weekday, weekOfSeason)
            )
        }
    }

    /**
     * Détermine la saison maronite. Les bornes sont approximatives et
     * doivent être affinées : ce sont des points d'ancrage typiques.
     */
    private fun determineSeason(date: LocalDate, easter: LocalDate): MaroniteSeason {
        val ashMonday = easter.minusDays(48)        // début du Grand Carême (lundi)
        val palmSunday = easter.minusDays(7)
        val pentecost = easter.plusDays(49)

        return when {
            date >= ashMonday && date < palmSunday -> MaroniteSeason.GREAT_LENT
            date >= palmSunday && date < easter -> MaroniteSeason.PASSION_WEEK
            date >= easter && date < pentecost -> MaroniteSeason.RESURRECTION
            date == pentecost -> MaroniteSeason.PENTECOST
            // L'Annonciation (Avent maronite) : 6 dimanches avant Noël.
            isInAnnunciation(date) -> MaroniteSeason.ANNUNCIATION
            isNativityRange(date) -> MaroniteSeason.NATIVITY
            isEpiphanyRange(date) -> MaroniteSeason.EPIPHANY
            isHolyCrossRange(date) -> MaroniteSeason.HOLY_CROSS
            else -> MaroniteSeason.ORDINARY
        }
    }

    private fun isInAnnunciation(date: LocalDate): Boolean {
        // Le temps de l'Annonciation commence le dimanche après le 9 novembre
        // (Consécration/Renouvellement de l'Église) jusqu'à la veille de Noël.
        val year = date.year
        val start = LocalDate.of(year, 11, 10)
        val christmasEve = LocalDate.of(year, 12, 24)
        return date >= start && date <= christmasEve
    }

    private fun isNativityRange(date: LocalDate): Boolean {
        val christmas = LocalDate.of(date.year, 12, 25)
        val end = LocalDate.of(date.year, 12, 31)
        return date >= christmas && date <= end
    }

    private fun isEpiphanyRange(date: LocalDate): Boolean {
        // De l'Épiphanie (6 janvier) jusqu'au début du Carême.
        return date.monthValue == 1 && date.dayOfMonth >= 6 ||
            (date.monthValue == 1 && date.dayOfMonth < 6)
    }

    private fun isHolyCrossRange(date: LocalDate): Boolean {
        // Fête de la Croix : 14 septembre. Bref temps autour.
        return date.monthValue == 9 && date.dayOfMonth in 14..30
    }

    private fun weekOfSeason(date: LocalDate, season: MaroniteSeason, easter: LocalDate): Int {
        val seasonStart = when (season) {
            MaroniteSeason.GREAT_LENT -> easter.minusDays(48)
            MaroniteSeason.RESURRECTION -> easter
            MaroniteSeason.ANNUNCIATION -> LocalDate.of(date.year, 11, 10)
            else -> LocalDate.of(date.year, 1, 1)
        }
        val days = java.time.temporal.ChronoUnit.DAYS.between(seasonStart, date)
        return (days / 7).toInt() + 1
    }

    private fun defaultColor(season: MaroniteSeason): LiturgicalColor = when (season) {
        MaroniteSeason.GREAT_LENT, MaroniteSeason.ANNUNCIATION -> LiturgicalColor.VIOLET
        MaroniteSeason.PASSION_WEEK -> LiturgicalColor.RED
        MaroniteSeason.RESURRECTION, MaroniteSeason.NATIVITY,
        MaroniteSeason.EPIPHANY, MaroniteSeason.PENTECOST -> LiturgicalColor.WHITE
        MaroniteSeason.HOLY_CROSS -> LiturgicalColor.RED
        else -> LiturgicalColor.GREEN
    }

    private fun toLiturgicalWeekday(dow: DayOfWeek): Int = when (dow) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
    }

    private val weekdayKeys = listOf(
        "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
    )

    /**
     * Construit l'identifiant de l'office férial. Le Šḥimo attribue à chaque
     * jour de la semaine un thème propre ; l'office est donc identifié par le
     * jour, et éventuellement modulé par la saison.
     */
    private fun ferialOfficeId(season: MaroniteSeason, weekday: Int, week: Int): String {
        val day = weekdayKeys[weekday - 1]
        return "feria_${day}"
    }

    private fun ferialTitle(season: MaroniteSeason, weekday: Int): LocalizedText {
        // Titres génériques par jour ; à enrichir depuis les données réelles.
        return LocalizedText(
            ar = "اليوم الطقسي",
            it = "Giorno liturgico",
            syr = ""
        )
    }
}
