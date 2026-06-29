package com.preciousblood.devotion.data

/**
 * Modèle de données de la dévotion au Précieux Sang.
 *
 * Hiérarchie :
 *   Section  ->  Prayer (prière individuelle)
 *
 * Les sections reprennent les grandes divisions de la
 * « Table des matières » du livre de prière, regroupées
 * de façon logique à la manière de la Liturgie des Heures.
 */

data class Section(
    val id: String,
    val title: String,
    val subtitle: String,
    val prayers: List<Prayer>
)

data class Prayer(
    val id: String,
    val title: String,
    /** Date de révélation ou source, ex. « 15 mars 1997 ». Peut être vide. */
    val source: String = "",
    /** Courte description affichée dans la liste. */
    val summary: String,
    /** Page d'origine dans le livre imprimé (référence). */
    val page: Int = 0,
    /** Fichier d'assets contenant le texte balisé, ex. « prayers/consolation.txt ». */
    val assetFile: String? = null,
    /** Corps complet de la prière (si défini en dur, sinon chargé depuis [assetFile]). */
    val body: List<PrayerBlock> = emptyList()
)

/** Un bloc de texte typé, pour rendre la mise en page liturgique. */
sealed interface PrayerBlock {
    /** Titre de section interne (ex. « Le Premier Mystère »). */
    data class Heading(val text: String) : PrayerBlock

    /** Paragraphe de prière normal. */
    data class Paragraph(val text: String) : PrayerBlock

    /** Réplique de l'officiant (L:) et de l'assemblée (R:). */
    data class Versicle(val leader: String, val response: String) : PrayerBlock

    /** Chant / refrain, affiché en italique centré. */
    data class Chant(val text: String) : PrayerBlock

    /** Indication rubricale entre parenthèses, ex. « (Baissez la tête) ». */
    data class Rubric(val text: String) : PrayerBlock

    /** Élément d'une liste numérotée (promesses, etc.). */
    data class Numbered(val number: Int, val text: String) : PrayerBlock
}
