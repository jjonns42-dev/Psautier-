package org.maronite.shhimo.data.model

import kotlinx.serialization.Serializable

/**
 * Les heures canoniques de l'office maronite (Šḥimo / Shhimo).
 * L'ordre suit le déroulement liturgique de la journée, qui dans la
 * tradition syro-antiochienne commence au soir (Ramcho).
 */
enum class CanonicalHour(val syriacKey: String) {
    RAMCHO("ramcho"),     // Vêpres (le soir) — début du jour liturgique
    SOUTORO("soutoro"),   // Complies (avant le repos)
    LILIO("lilio"),       // Vigiles nocturnes
    SAFRO("safro"),       // Matines / Laudes (le matin)
    THIRD_HOUR("tloth"),  // Tierce  (heure intermédiaire, optionnelle)
    SIXTH_HOUR("sheth"),  // Sexte   (heure intermédiaire, optionnelle)
    NINTH_HOUR("tsha");   // None    (heure intermédiaire, optionnelle)

    companion object {
        /** Les heures principales, toujours présentes dans le Šḥimo. */
        val principal = listOf(RAMCHO, SOUTORO, LILIO, SAFRO)
    }
}

/**
 * Rang liturgique d'une célébration, qui détermine quel office prévaut
 * lorsque plusieurs coïncident le même jour.
 */
enum class LiturgicalRank {
    FERIA,            // Jour de semaine ordinaire -> office du Šḥimo
    COMMEMORATION,    // Mémoire d'un saint
    FEAST,            // Fête
    SOLEMNITY,        // Solennité (ex. Noël, Pâques)
    SUNDAY            // Dimanche (a son propre propre du Fenqitho)
}

/**
 * Couleur liturgique. Le rite maronite a son propre usage des couleurs,
 * proche mais non identique au rite romain.
 */
enum class LiturgicalColor {
    WHITE, RED, GREEN, VIOLET, ROSE, BLACK
}

/**
 * Un texte multilingue. Chaque champ correspond à une langue cible.
 * - [ar]  : arabe libanais
 * - [it]  : italien
 * - [syr] : syriaque translittéré ou en caractères syriaques (facultatif)
 *
 * Tout champ vide signifie "traduction non encore saisie".
 */
@Serializable
data class LocalizedText(
    val ar: String = "",
    val it: String = "",
    val syr: String = ""
) {
    fun forLanguage(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> ar.ifBlank { it }
        AppLanguage.ITALIAN -> it.ifBlank { ar }
        AppLanguage.SYRIAC -> syr.ifBlank { it }
    }

    val isEmpty: Boolean get() = ar.isBlank() && it.isBlank() && syr.isBlank()
}

enum class AppLanguage(val code: String) {
    ARABIC("ar"),
    ITALIAN("it"),
    SYRIAC("syr")
}

/**
 * Type d'élément composant un office. Permet à l'UI d'appliquer un style
 * différent (rubrique en rouge, psaume indenté, etc.).
 */
enum class OfficeElementType {
    RUBRIC,        // Indication rituelle (affichée en rouge, non lue)
    OPENING,       // Verset d'ouverture
    PSALM,         // Psaume
    QOLO,          // Hymne strophique chantée (qolo / madrosho)
    HOUSSOYO,      // Prière de propitiation (houssoyo)
    SEDRO,         // Sedro (longue prière)
    PROCLAMATION,  // Lecture / proclamation
    PRAYER,        // Oraison
    RESPONSE,      // Répons de l'assemblée
    DOXOLOGY       // Doxologie (Gloire au Père...)
}

/**
 * Un élément atomique d'un office : un psaume, une hymne, une rubrique...
 * [reference] est une référence libre (ex. "Ps 51", "Qolo - ton 1").
 */
@Serializable
data class OfficeElement(
    val type: OfficeElementType,
    val reference: String = "",
    val title: LocalizedText = LocalizedText(),
    val body: LocalizedText = LocalizedText(),
    /** Identifiant d'une mélodie dans la banque d'hymnes (voir HymnBank). */
    val melodyId: String? = null
)

/**
 * Une heure canonique entièrement composée pour un jour donné.
 */
@Serializable
data class OfficeHour(
    val hour: CanonicalHour,
    val elements: List<OfficeElement> = emptyList()
)

/**
 * L'ensemble des offices d'une journée liturgique.
 */
@Serializable
data class DailyOffice(
    /** Clé de l'office, ex. "feria_monday_week1" ou "nativity". */
    val id: String,
    val title: LocalizedText,
    val hours: List<OfficeHour> = emptyList()
) {
    fun hour(target: CanonicalHour): OfficeHour? = hours.firstOrNull { it.hour == target }
}
