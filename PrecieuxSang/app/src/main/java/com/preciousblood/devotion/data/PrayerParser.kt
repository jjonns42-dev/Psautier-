package com.preciousblood.devotion.data

import android.content.Context
import com.preciousblood.devotion.data.PrayerBlock.*

/**
 * Convertit le balisage léger des fichiers d'assets en blocs typés.
 *
 * Balisage :
 *   `# texte`     -> Heading
 *   `> texte`     -> Rubric (entre parenthèses, indication rubricale)
 *   `~ texte`     -> Chant
 *   `L| texte`    -> début de Versicle (officiant) ; apparié au `R|` suivant
 *   `R| texte`    -> réponse du Versicle
 *   `N| 1. texte` -> élément numéroté
 *   (ligne simple) -> Paragraph
 */
object PrayerParser {

    private val cache = HashMap<String, List<PrayerBlock>>()

    fun load(context: Context, assetFile: String): List<PrayerBlock> =
        cache.getOrPut(assetFile) {
            runCatching {
                context.assets.open(assetFile).bufferedReader().use { it.readText() }
            }.map { parse(it) }.getOrElse {
                listOf(Paragraph("Texte indisponible ($assetFile)."))
            }
        }

    fun parse(text: String): List<PrayerBlock> {
        val out = ArrayList<PrayerBlock>()
        val lines = text.split("\n")
        var i = 0
        while (i < lines.size) {
            val raw = lines[i].trim()
            if (raw.isEmpty()) { i++; continue }
            when {
                raw.startsWith("# ") ->
                    out += Heading(raw.removePrefix("# ").trim())

                raw.startsWith("> ") ->
                    out += Rubric(raw.removePrefix("> ").trim())

                raw.startsWith("~ ") ->
                    out += Chant(raw.removePrefix("~ ").trim())

                raw.startsWith("N| ") -> {
                    val item = raw.removePrefix("N| ").trim()
                    val m = Regex("""^(\d{1,2})[.)]\s*(.*)$""").find(item)
                    if (m != null) {
                        out += Numbered(m.groupValues[1].toInt(), m.groupValues[2].trim())
                    } else {
                        out += Paragraph(item)
                    }
                }

                raw.startsWith("L| ") -> {
                    val leader = raw.removePrefix("L| ").trim()
                    val next = lines.getOrNull(i + 1)?.trim().orEmpty()
                    if (next.startsWith("R| ")) {
                        out += Versicle(leader, next.removePrefix("R| ").trim())
                        i++ // consume the response line
                    } else {
                        out += Paragraph("℣ $leader")
                    }
                }

                raw.startsWith("R| ") ->
                    out += Paragraph("℟ ${raw.removePrefix("R| ").trim()}")

                else -> out += Paragraph(raw)
            }
            i++
        }
        return out
    }
}
