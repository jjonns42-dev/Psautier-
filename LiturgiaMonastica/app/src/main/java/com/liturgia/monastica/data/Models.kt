package com.liturgia.monastica.data

/** A single assembled section of an Office hour. */
data class OfficeSection(
    val kind: String,        // "intro","hymn","psalm","canticle","gospel_canticle","prayer","conclusion","label"
    val title: String,       // e.g. "Psaume 51" / "Cantique de Zacharie"
    val subtitle: String = "", // e.g. reference
    val body: String         // the prayer / psalm text
)

data class HourMeta(
    val id: String,
    val nameFr: String,
    val nameIt: String,
    val nameLa: String,
    val order: Int
)

data class Canticle(
    val id: String,
    val titleFr: String,
    val titleIt: String,
    val ref: String,
    val text: String
)

data class BenedictQuote(
    val fr: String,
    val it: String,
    val src: String
)

data class Devotion(
    val id: String,
    val nameFr: String,
    val nameIt: String,
    val desc: String,
    val promises: List<String>
)

data class BibleBook(
    val id: String,
    val name: String,
    val file: String,
    val testament: String,
    val chapters: Int
)

// =============================================================================
//  LE COMBAT — les huit pensées d'Évagre/Cassien + l'envie (saint Grégoire).
//  Chaque péché a 10 niveaux d'une semaine, une pratique orthodoxe et une
//  pratique catholique par niveau, une règle de prière propre, et 7 citations
//  bibliques qui tournent au fil des jours confirmés.
// =============================================================================

/** A short attributed text: a prayer or a piece of counsel, with its source. */
data class SinQuote(
    val source: String,
    val text: String
)

/** The prayer rule for one sin: one prayer and one counsel per tradition. */
data class SinPrayerRule(
    val orthoPrayer: SinQuote,
    val cathoPrayer: SinQuote,
    val orthoCounsel: SinQuote,
    val cathoCounsel: SinQuote
)

data class SinVerse(
    val ref: String,
    val text: String
)

/** One week-long level: a shared instruction plus a distinct Orthodox and Catholic practice. */
data class SinLevel(
    val title: String,
    val practice: String,
    val ortho: String,
    val catho: String
)

data class Sin(
    val id: String,
    val name: String,
    val latin: String,
    val accent: String,
    val virtue: String,
    val desc: String,
    val prayer: SinPrayerRule,
    val verses: List<SinVerse>,
    val levels: List<SinLevel>
)

/** Persisted progress for a single sin's combat. Immutable: always replaced, never mutated in place. */
data class SinProgress(
    val level: Int = 1,
    val daysConfirmed: Int = 0,
    val lastDay: Long = -1L
)

// =============================================================================
//  LA RÈGLE — règle de prière à niveaux infinis, choisie une fois (famille,
//  tradition/ordre, difficulté), puis progressive. Chaque niveau dure une
//  semaine ; tous les 4 niveaux (~1 mois), une dévotion nommée supplémentaire
//  s'ajoute à la règle. Un jour manqué fait redescendre d'un seul niveau,
//  qu'il faut revalider entièrement.
// =============================================================================

data class RuleDevotion(
    val name: String,
    val note: String,
    val signature: Boolean,   // true = dévotion emblématique propre à l'ordre/tradition
    val minutes: Int = 0,     // durée de base suggérée (à l'échelle Normal) ; 0 = non temporelle (jeûne, confession, geste concret...)
    val supersedes: Int = -1  // index (dans devotions) d'une dévotion antérieure que celle-ci remplace une fois active
)

data class RuleTradition(
    val id: String,
    val family: String,        // "orthodoxe" | "catholique" | "charismatique"
    val name: String,
    val subtitle: String,
    val patron: String,
    val accent: String,
    val desc: String,
    val dailyCore: List<String>,   // le socle fixe, présent dès le niveau 1
    val devotions: List<RuleDevotion> // dévotions nommées, débloquées progressivement
)

enum class RuleDifficulty(
    val label: String, val startCount: Int, val paceLevels: Int,
    val timeMultiplier: Double, val capFraction: Double
) {
    DEBUTANT("Débutant", startCount = 1, paceLevels = 8, timeMultiplier = 0.6, capFraction = 0.6),
    NORMAL("Normal", startCount = 2, paceLevels = 4, timeMultiplier = 1.0, capFraction = 0.85),
    DIFFICILE("Difficile", startCount = 3, paceLevels = 2, timeMultiplier = 1.6, capFraction = 1.0);

    companion object {
        fun fromKey(k: String) = entries.firstOrNull { it.name == k } ?: NORMAL
    }
}

/** Persisted state of the never-ending Rule game. */
/** Une entrée du répertoire des niveaux passés : quand, à quel niveau, et pour quel événement. */
data class RuleHistoryEntry(
    val level: Int,
    val epochDay: Long,
    val event: String   // "start" | "levelup" | "demotion" | "renewal"
)

data class RuleProgress(
    val started: Boolean = false,
    val traditionId: String = "",
    val difficulty: String = RuleDifficulty.NORMAL.name,
    val level: Int = 1,
    val daysConfirmed: Int = 0,
    val lastDay: Long = -1L,
    val lastRenewalYear: Int = 0,
    val history: List<RuleHistoryEntry> = emptyList()
)
