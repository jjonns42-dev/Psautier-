package com.bonaventure.psautier.data

import kotlinx.serialization.Serializable

@Serializable
data class Psautier(
    val titre: String,
    val auteur: String,
    val traducteur: String,
    val jours: List<Jour>
)

@Serializable
data class Jour(
    val jour: String,
    val ordre: Int,
    val heures: List<Heure>
)

@Serializable
data class Heure(
    val nom: String,
    val antienne: String,
    val sections: List<Section>
)

@Serializable
data class Section(
    val type: String,           // "psaume" | "cantique" | "priere" | "hymne" | "symbole" | "division"
    val titre: String,
    val soustitre: String? = null,
    val texte: String
)

/**
 * Résultat de recherche : pointe vers une section précise dans la hiérarchie
 * jour -> heure -> section, avec un court extrait mis en valeur.
 */
data class SearchResult(
    val jourNom: String,
    val jourOrdre: Int,
    val heureNom: String,
    val heureIndex: Int,
    val section: Section,
    val extrait: String
)
