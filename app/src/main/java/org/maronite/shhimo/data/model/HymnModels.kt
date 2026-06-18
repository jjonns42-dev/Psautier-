package org.maronite.shhimo.data.model

import kotlinx.serialization.Serializable

/**
 * Une mélodie type (qolo) du répertoire maronite. Le chant maronite est
 * organisé selon un système de modes/mélodies types ; un grand nombre de
 * textes se chantent sur un petit nombre de mélodies de référence.
 *
 * [audioAsset] pointe éventuellement vers un fichier audio dans assets/hymns/.
 */
@Serializable
data class Melody(
    val id: String,
    val name: LocalizedText,
    val mode: Int = 0,                 // ton / mode (0 si non spécifié)
    val incipit: LocalizedText = LocalizedText(),
    val audioAsset: String? = null
)

/**
 * Une hymne complète : texte chantable rattaché à une mélodie.
 */
@Serializable
data class Hymn(
    val id: String,
    val title: LocalizedText,
    val melodyId: String? = null,
    val stanzas: List<LocalizedText> = emptyList(),
    val season: MaroniteSeason? = null
)

/** Conteneur sérialisé pour le fichier assets/hymns/hymns.json. */
@Serializable
data class HymnBank(
    val melodies: List<Melody> = emptyList(),
    val hymns: List<Hymn> = emptyList()
)
