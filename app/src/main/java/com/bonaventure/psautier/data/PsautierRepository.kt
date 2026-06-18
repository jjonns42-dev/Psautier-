package com.bonaventure.psautier.data

import android.content.Context
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Charge et met en cache le contenu du Psautier depuis les assets.
 * Le JSON est en lecture seule, donc un simple cache mémoire (lazy) suffit:
 * pas besoin de base de données pour ce volume de contenu.
 */
class PsautierRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private var cache: Psautier? = null

    fun getPsautier(): Psautier {
        cache?.let { return it }
        val text = try {
            context.assets.open("psautier.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        } catch (e: IOException) {
            throw IllegalStateException("Impossible de lire psautier.json dans les assets", e)
        }
        val parsed = json.decodeFromString<Psautier>(text)
        cache = parsed
        return parsed
    }

    fun getJour(ordre: Int): Jour? = getPsautier().jours.firstOrNull { it.ordre == ordre }

    fun getHeure(jourOrdre: Int, heureNom: String): Heure? =
        getJour(jourOrdre)?.heures?.firstOrNull { it.nom == heureNom }

    /**
     * Recherche plein texte insensible à la casse et aux accents simples,
     * à travers les titres, sous-titres, antiennes et textes de toutes les sections.
     */
    fun search(query: String): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val needle = normalize(query)
        val results = mutableListOf<SearchResult>()

        for (jour in getPsautier().jours) {
            for ((heureIndex, heure) in jour.heures.withIndex()) {
                for (section in heure.sections) {
                    val haystack = normalize("${section.titre} ${section.soustitre.orEmpty()} ${section.texte}")
                    if (haystack.contains(needle)) {
                        results += SearchResult(
                            jourNom = jour.jour,
                            jourOrdre = jour.ordre,
                            heureNom = heure.nom,
                            heureIndex = heureIndex,
                            section = section,
                            extrait = buildExtrait(section.texte, query)
                        )
                    }
                }
            }
        }
        return results
    }

    private fun buildExtrait(texte: String, query: String): String {
        val idx = normalize(texte).indexOf(normalize(query))
        if (idx < 0) return texte.take(120)
        val start = (idx - 40).coerceAtLeast(0)
        val end = (idx + query.length + 60).coerceAtMost(texte.length)
        val prefix = if (start > 0) "…" else ""
        val suffix = if (end < texte.length) "…" else ""
        return prefix + texte.substring(start, end).replace("\n", " ") + suffix
    }

    private fun normalize(s: String): String {
        val lower = s.lowercase()
        val sb = StringBuilder(lower.length)
        for (c in lower) {
            sb.append(
                when (c) {
                    'à', 'â', 'ä' -> 'a'
                    'é', 'è', 'ê', 'ë' -> 'e'
                    'î', 'ï' -> 'i'
                    'ô', 'ö' -> 'o'
                    'ù', 'û', 'ü' -> 'u'
                    'ç' -> 'c'
                    else -> c
                }
            )
        }
        return sb.toString()
    }
}
