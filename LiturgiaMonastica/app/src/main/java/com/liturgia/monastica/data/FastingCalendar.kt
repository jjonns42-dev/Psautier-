package com.liturgia.monastica.data

import java.util.Calendar

/**
 * Calcul des dates mobiles (Pâques catholique/grégorienne et Pâques orthodoxe/julienne)
 * et des grandes périodes de jeûne qui en dépendent, afin que le calendrier de jeûne
 * puisse indiquer, pour aujourd'hui, si l'on est en période de jeûne dans chaque tradition.
 *
 * La conversion julien -> grégorien utilise un décalage de 13 jours, valable pour les
 * années 1900-2099 (usage courant de l'app) ; au-delà, une note l'indique à l'utilisateur.
 */
data class DateYMD(val year: Int, val month: Int, val day: Int) // month: 1-12

object FastingCalendar {

    /** Jour d'aujourd'hui, comme [todayEpochDay] de GameStore.kt (même convention). */
    fun ymdToEpochDay(y: Int, m: Int, d: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(y, m - 1, d, 12, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val zoneOffsetMs = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)
        return (cal.timeInMillis + zoneOffsetMs) / 86_400_000L
    }

    fun epochDayToYmd(epoch: Long): DateYMD {
        val cal = Calendar.getInstance()
        cal.timeInMillis = epoch * 86_400_000L + 43_200_000L
        return DateYMD(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    fun todayYmd(): DateYMD = epochDayToYmd(todayEpochDay())

    /** Pâques selon le calendrier grégorien (Église catholique et la plupart des protestants).
     *  Algorithme anonyme grégorien (Meeus/Jones/Butcher). */
    fun gregorianEaster(year: Int): DateYMD {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 25
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return DateYMD(year, month, day)
    }

    /** Pâques selon le calendrier julien (date julienne), puis convertie en date grégorienne
     *  (+13 jours, valable 1900-2099) : c'est la Pâque suivie par la plupart des Églises
     *  orthodoxes pour établir leur cycle pascal, même celles qui utilisent par ailleurs
     *  le calendrier grégorien pour les fêtes fixes. */
    fun orthodoxEasterGregorian(year: Int): DateYMD {
        val a = year % 4
        val b = year % 7
        val c = year % 19
        val d = (19 * c + 15) % 30
        val e = (2 * a + 4 * b - d + 34) % 7
        val julianMonth = (d + e + 114) / 31
        val julianDay = ((d + e + 114) % 31) + 1
        val julianEpoch = ymdToEpochDay(year, julianMonth, julianDay)
        return epochDayToYmd(julianEpoch + 13) // décalage julien -> grégorien, 1900-2099
    }

    private fun DateYMD.epoch() = ymdToEpochDay(year, month, day)
    private fun DateYMD.plusDays(n: Int) = epochDayToYmd(epoch() + n)

    /** Une période de jeûne calculée pour une année donnée. */
    data class FastPeriod(val start: DateYMD, val end: DateYMD) {
        fun contains(today: DateYMD): Boolean {
            val t = today.epoch()
            return t in start.epoch()..end.epoch()
        }
    }

    // ---- Périodes orthodoxes ---------------------------------------------------
    /** Grand Carême : du Lundi pur (Pâques - 48 j) au Samedi saint inclus (Pâques - 1 j). */
    fun greatLent(year: Int): FastPeriod {
        val easter = orthodoxEasterGregorian(year)
        return FastPeriod(easter.plusDays(-48), easter.plusDays(-1))
    }

    /** Jeûne des Apôtres : du lundi après le dimanche de Tous les Saints (Pâques + 57 j)
     *  jusqu'au 28 juin (veille des saints Pierre et Paul). Peut être très court certaines
     *  années où Pâques est tardive. */
    fun apostlesFast(year: Int): FastPeriod {
        val easter = orthodoxEasterGregorian(year)
        val start = easter.plusDays(57)
        val end = DateYMD(year, 6, 28)
        return if (start.epoch() > end.epoch()) FastPeriod(start, start) else FastPeriod(start, end)
    }

    /** Jeûne de la Dormition : 1er - 14 août. */
    fun dormitionFast(year: Int): FastPeriod = FastPeriod(DateYMD(year, 8, 1), DateYMD(year, 8, 14))

    /** Jeûne de la Nativité (« Carême des Philippes ») : 15 novembre - 24 décembre.
     *  Certaines Églises restées au calendrier julien pour les fêtes fixes vivent ces
     *  mêmes dates 13 jours plus tard en style grégorien : l'appli affiche la date
     *  « nouveau calendrier », majoritaire aujourd'hui. */
    fun nativityFast(year: Int): FastPeriod = FastPeriod(DateYMD(year, 11, 15), DateYMD(year, 12, 24))

    // ---- Périodes catholiques ---------------------------------------------------
    /** Carême : du Mercredi des Cendres (Pâques - 46 j) au Samedi saint (Pâques - 1 j). */
    fun lentCatholic(year: Int): FastPeriod {
        val easter = gregorianEaster(year)
        return FastPeriod(easter.plusDays(-46), easter.plusDays(-1))
    }

    /** Avent : du 4e dimanche avant Noël (dimanche le plus proche du 30 novembre)
     *  au 24 décembre inclus. */
    fun adventCatholic(year: Int): FastPeriod {
        val christmas = DateYMD(year, 12, 25)
        val cal = Calendar.getInstance()
        cal.set(year, 11, 25, 12, 0, 0)
        val weekday = cal.get(Calendar.DAY_OF_WEEK) // Sunday = 1
        val daysBack = if (weekday == Calendar.SUNDAY) 7 else weekday - 1
        val lastAdventSunday = christmas.plusDays(-daysBack)
        val firstAdventSunday = lastAdventSunday.plusDays(-21)
        return FastPeriod(firstAdventSunday, DateYMD(year, 12, 24))
    }

    /** Vendredis de l'année (abstinence, Code de droit canonique can. 1251) — vérification
     *  simple du jour de semaine, sans tenir compte des solennités qui en dispensent. */
    fun isFridayAbstinence(y: DateYMD): Boolean {
        val cal = Calendar.getInstance()
        cal.set(y.year, y.month - 1, y.day, 12, 0, 0)
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
    }

    /** Mercredis et vendredis de jeûne orthodoxe hebdomadaire — vérification simple du
     *  jour de semaine, sans les nombreuses semaines « sans jeûne » (Noël-Théophanie,
     *  semaine after Pâques, etc.) qu'il faut consulter dans le typikon local. */
    fun isWedFriFast(y: DateYMD): Boolean {
        val cal = Calendar.getInstance()
        cal.set(y.year, y.month - 1, y.day, 12, 0, 0)
        val wd = cal.get(Calendar.DAY_OF_WEEK)
        return wd == Calendar.WEDNESDAY || wd == Calendar.FRIDAY
    }

    /** Carême de saint Michel : dévotion traditionnelle (vécue par saint François
     *  d'Assise) du 15 août à la fête de saint Michel Archange, le 29 septembre —
     *  40 jours de jeûne et de prière. */
    fun saintMichaelFast(year: Int): FastPeriod =
        FastPeriod(DateYMD(year, 8, 15), DateYMD(year, 9, 29))

    /** Résout si une clé calculée est « active aujourd'hui » — mutualisé par le
     *  calendrier de jeûne, le suivi de jeûne et les pénitences datées. */
    fun isComputedActiveToday(key: String?, today: DateYMD): Boolean {
        val y = today.year
        return when (key) {
            "great_lent" -> greatLent(y).contains(today)
            "apostles_fast" -> apostlesFast(y).contains(today)
            "dormition_fast" -> dormitionFast(y).contains(today)
            "nativity_fast" -> nativityFast(y).contains(today)
            "lent_catholic" -> lentCatholic(y).contains(today)
            "advent_catholic" -> adventCatholic(y).contains(today)
            "saint_michael" -> saintMichaelFast(y).contains(today)
            "friday_weekly" -> isFridayAbstinence(today)
            "wedfri_weekly" -> isWedFriFast(today)
            else -> false
        }
    }

    fun formatDate(d: DateYMD, italian: Boolean): String {
        val moisFr = listOf("janv.", "févr.", "mars", "avr.", "mai", "juin", "juil.", "août", "sept.", "oct.", "nov.", "déc.")
        val moisIt = listOf("gen.", "feb.", "mar.", "apr.", "mag.", "giu.", "lug.", "ago.", "set.", "ott.", "nov.", "dic.")
        val mois = if (italian) moisIt else moisFr
        return "${d.day} ${mois[d.month - 1]} ${d.year}"
    }
}
