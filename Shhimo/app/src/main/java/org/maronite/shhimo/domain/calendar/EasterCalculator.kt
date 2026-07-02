package org.maronite.shhimo.domain.calendar

import java.time.LocalDate

/**
 * Calcul de la date de Pâques.
 *
 * L'Église maronite, en communion avec Rome, célèbre Pâques selon le
 * comput grégorien (algorithme de Meeus/Butcher). Une option pour le comput
 * julien (Pâques "orientale") est fournie car certaines communautés et
 * certains calendriers de référence l'utilisent encore régionalement.
 */
object EasterCalculator {

    /** Pâques grégorienne (comput occidental). Algorithme de Meeus/Jones/Butcher. */
    fun gregorianEaster(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return LocalDate.of(year, month, day)
    }

    /** Pâques julienne convertie au calendrier grégorien (comput oriental). */
    fun julianEaster(year: Int): LocalDate {
        val a = year % 4
        val b = year % 7
        val c = year % 19
        val d = (19 * c + 15) % 30
        val e = (2 * a + 4 * b - d + 34) % 7
        val month = (d + e + 114) / 31
        val day = ((d + e + 114) % 31) + 1
        // Date julienne -> grégorienne : on ajoute le décalage (13 jours pour 1900-2099).
        val julianDate = LocalDate.of(year, month, day)
        return julianDate.plusDays(julianGregorianOffset(year).toLong())
    }

    private fun julianGregorianOffset(year: Int): Int {
        val century = year / 100
        return century - (century / 4) - 2
    }
}
