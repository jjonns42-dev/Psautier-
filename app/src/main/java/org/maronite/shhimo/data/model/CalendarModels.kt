package org.maronite.shhimo.data.model

import kotlinx.serialization.Serializable

/**
 * Les saisons (temps) de l'année liturgique maronite.
 * Le calendrier maronite est organisé autour de cycles propres, dont le
 * grand cycle qui va de la Consécration de l'Église (fin d'octobre) jusqu'à
 * la Pentecôte, suivi du temps après la Pentecôte.
 *
 * NOTE: l'ordre et les bornes exactes de ces temps doivent être renseignés
 * à partir du calendrier officiel maronite (Bkerké). Les valeurs ici servent
 * de structure ; les dates précises sont dans les fichiers calendar/calendar.json.
 */
enum class MaroniteSeason(val key: String) {
    CONSECRATION_CHURCH("consecration"),   // Qudash 'Idto - Consécration de l'Église
    RENEWAL_CHURCH("renewal"),             // Hudath 'Idto - Renouvellement de l'Église
    ANNUNCIATION("annunciation"),          // Subboro - temps de l'Annonciation (Avent maronite)
    NATIVITY("nativity"),                  // Yaldo - Nativité
    EPIPHANY("epiphany"),                  // Denho - Épiphanie
    GREAT_LENT("great_lent"),              // Sawmo Rabbo - Grand Carême
    PASSION_WEEK("passion"),               // Semaine de la Passion (Hasho)
    RESURRECTION("resurrection"),          // Qyomto - Résurrection
    PENTECOST("pentecost"),                // Pentecôte
    HOLY_CROSS("holy_cross"),              // Slibo - Sainte Croix
    ORDINARY("ordinary");                  // Temps après la Pentecôte / ordinaire
}

/**
 * Une entrée du calendrier : ce qui est célébré à une date donnée.
 * La date est exprimée soit en date fixe (mois/jour), soit calculée
 * relativement à Pâques (offsetFromEaster), soit relativement au début
 * d'une saison.
 */
@Serializable
data class CalendarEntry(
    val id: String,
    val title: LocalizedText,
    val rank: LiturgicalRank,
    val color: LiturgicalColor,
    val season: MaroniteSeason,

    /** Date fixe au format "MM-DD", ou null si mobile. */
    val fixedDate: String? = null,

    /** Décalage en jours par rapport à Pâques (peut être négatif), ou null. */
    val offsetFromEaster: Int? = null,

    /** Identifiant de l'office propre à utiliser ce jour-là, ou null
     *  pour retomber sur l'office férial du Šḥimo. */
    val officeId: String? = null
)

/**
 * Le jour liturgique résolu : le résultat du calcul du calendrier pour
 * une date civile donnée.
 */
data class LiturgicalDay(
    val civilDateIso: String,        // "2026-06-18"
    val weekday: Int,                // 1 = dimanche ... 7 = samedi
    val season: MaroniteSeason,
    val weekOfSeason: Int,           // semaine à l'intérieur de la saison
    val rank: LiturgicalRank,
    val color: LiturgicalColor,
    val title: LocalizedText,
    val officeId: String,            // office à charger pour ce jour
    val commemorations: List<CalendarEntry> = emptyList()
)
