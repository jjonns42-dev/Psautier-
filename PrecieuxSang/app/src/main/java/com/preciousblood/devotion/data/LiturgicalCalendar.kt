package com.preciousblood.devotion.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.temporal.ChronoUnit

/** Type d'événement, pour la couleur/icône. */
enum class FeastKind { GETHSEMANI, VENDREDI, JUILLET, FETE }

data class SpecialDay(
    val title: String,
    val description: String,
    val date: LocalDate,
    val endDate: LocalDate = date,
    val kind: FeastKind,
    /** Prière associée à ouvrir, si pertinent. */
    val prayerId: String? = null
) {
    val isRange: Boolean get() = endDate != date
    fun contains(day: LocalDate): Boolean =
        !day.isBefore(date) && !day.isAfter(endDate)
}

/**
 * Calcule les jours spéciaux de la dévotion :
 *  - Heures de Gethsémani : chaque jeudi 23h → vendredi 3h
 *  - Troisième vendredi de chaque mois : réparation
 *  - Grand mois de Juillet et ses trois neuvaines
 *  - 1er juillet : fête du Précieux Sang
 */
object LiturgicalCalendar {

    /** Le prochain jeudi des Heures de Gethsémani, à partir de maintenant. */
    fun nextGethsemani(now: LocalDateTime): LocalDateTime {
        var date = now.toLocalDate()
        // Si on est jeudi avant 23h, c'est ce soir.
        if (now.dayOfWeek == DayOfWeek.THURSDAY && now.toLocalTime().isBefore(LocalTime.of(23, 0))) {
            return LocalDateTime.of(date, LocalTime.of(23, 0))
        }
        // Sinon, prochain jeudi.
        do {
            date = date.plusDays(1)
        } while (date.dayOfWeek != DayOfWeek.THURSDAY)
        return LocalDateTime.of(date, LocalTime.of(23, 0))
    }

    /** Sommes-nous actuellement dans la fenêtre de Gethsémani (jeu 23h → ven 3h) ? */
    fun isGethsemaniNow(now: LocalDateTime): Boolean {
        val t = now.toLocalTime()
        return when (now.dayOfWeek) {
            DayOfWeek.THURSDAY -> !t.isBefore(LocalTime.of(23, 0))
            DayOfWeek.FRIDAY -> t.isBefore(LocalTime.of(3, 0))
            else -> false
        }
    }

    /** Le troisième vendredi d'un mois donné. */
    fun thirdFriday(year: Int, month: Int): LocalDate {
        var d = LocalDate.of(year, month, 1)
        var fridays = 0
        while (true) {
            if (d.dayOfWeek == DayOfWeek.FRIDAY) {
                fridays++
                if (fridays == 3) return d
            }
            d = d.plusDays(1)
        }
    }

    /** Le prochain troisième vendredi à partir d'une date. */
    fun nextThirdFriday(from: LocalDate): LocalDate {
        val thisMonth = thirdFriday(from.year, from.monthValue)
        if (!thisMonth.isBefore(from)) return thisMonth
        val next = from.plusMonths(1)
        return thirdFriday(next.year, next.monthValue)
    }

    /** Nombre de jours entiers entre deux dates (≥ 0). */
    fun daysUntil(from: LocalDate, target: LocalDate): Long =
        ChronoUnit.DAYS.between(from, target)

    /**
     * Tous les jours spéciaux d'une année donnée : les trois neuvaines de
     * juillet, la fête du Précieux Sang, et les douze troisièmes vendredis.
     */
    fun specialDaysForYear(year: Int): List<SpecialDay> {
        val list = mutableListOf<SpecialDay>()

        list += SpecialDay(
            title = "Fête du Précieux Sang",
            description = "Ouverture du Grand mois de Juillet, mois du Précieux Sang.",
            date = LocalDate.of(year, Month.JULY, 1),
            kind = FeastKind.FETE,
            prayerId = "chapelet"
        )
        list += SpecialDay(
            title = "Neuvaine du Précieux Sang",
            description = "Neuf jours en l'honneur des neuf Chœurs des Anges.",
            date = LocalDate.of(year, Month.JULY, 1),
            endDate = LocalDate.of(year, Month.JULY, 9),
            kind = FeastKind.JUILLET,
            prayerId = "chapelet"
        )
        list += SpecialDay(
            title = "Trois jours de la Très Sainte Trinité",
            description = "Prière en l'honneur de la Très Sainte Trinité.",
            date = LocalDate.of(year, Month.JULY, 13),
            endDate = LocalDate.of(year, Month.JULY, 15),
            kind = FeastKind.JUILLET
        )
        list += SpecialDay(
            title = "Douze jours pour le Nouvel Israël",
            description = "Douze jours de prière pour le Nouvel Israël.",
            date = LocalDate.of(year, Month.JULY, 20),
            endDate = LocalDate.of(year, Month.JULY, 31),
            kind = FeastKind.JUILLET,
            prayerId = "nouvel-israel"
        )

        for (m in 1..12) {
            val d = thirdFriday(year, m)
            list += SpecialDay(
                title = "Réparation du 3ᵉ vendredi",
                description = "Troisième vendredi du mois : prières de réparation.",
                date = d,
                kind = FeastKind.VENDREDI,
                prayerId = "troisiemes-vendredis"
            )
        }

        return list.sortedBy { it.date }
    }

    /** Les prochains événements à venir (jusqu'à [limit]). */
    fun upcoming(now: LocalDateTime, limit: Int = 6): List<SpecialDay> {
        val today = now.toLocalDate()
        val pool = specialDaysForYear(today.year) + specialDaysForYear(today.year + 1)
        return pool
            .filter { !it.endDate.isBefore(today) }
            .sortedBy { it.date }
            .take(limit)
    }

    /** Les jours spéciaux actifs aujourd'hui. */
    fun activeToday(today: LocalDate): List<SpecialDay> =
        specialDaysForYear(today.year).filter { it.contains(today) }
}
