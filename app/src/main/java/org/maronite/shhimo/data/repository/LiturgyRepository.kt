package org.maronite.shhimo.data.repository

import org.maronite.shhimo.data.model.DailyOffice
import org.maronite.shhimo.data.model.HymnBank
import org.maronite.shhimo.data.model.LiturgicalDay
import org.maronite.shhimo.data.source.AssetLiturgicalSource
import org.maronite.shhimo.domain.calendar.MaroniteCalendar
import java.time.LocalDate

/**
 * Point d'accès unique de la couche données pour le reste de l'application.
 * Cache en mémoire le calendrier et la banque d'hymnes (petits) ; charge
 * les offices à la demande (potentiellement nombreux et volumineux).
 */
class LiturgyRepository(private val source: AssetLiturgicalSource) {

    private val calendarEntries by lazy { source.loadCalendar() }
    private val calendar by lazy { MaroniteCalendar(calendarEntries) }
    val hymnBank: HymnBank by lazy { source.loadHymnBank() }

    private val officeCache = mutableMapOf<String, DailyOffice?>()

    /** Le jour liturgique pour une date civile. */
    fun liturgicalDay(date: LocalDate): LiturgicalDay = calendar.resolve(date)

    /** L'office complet correspondant à un jour liturgique. */
    fun officeFor(day: LiturgicalDay): DailyOffice? = office(day.officeId)

    fun office(officeId: String): DailyOffice? =
        officeCache.getOrPut(officeId) { source.loadOffice(officeId) }

    fun availableOfficeIds(): List<String> = source.listOfficeIds()
}
