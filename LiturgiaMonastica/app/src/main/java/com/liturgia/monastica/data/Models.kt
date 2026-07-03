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
    val cathoCounsel: SinQuote,
    val extraCounsel: SinQuote? = null   // conseil supplémentaire, source non encore utilisée ailleurs dans ce combat
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
    val levels: List<SinLevel>,
    val furtherReading: List<String> = emptyList()
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

// =============================================================================
//  LES NEUVAINES — coins de neuvaine à durées multiples (9, 30, 40, 54 jours...)
//  Un seul "coin" actif à la fois. Chaque jour prié incrémente le compteur.
//  Un jour manqué remet le compteur à zéro (fidélité à la neuvaine classique).
//  Les neuvaines menées à terme sont conservées dans un petit répertoire.
// =============================================================================

/** Un type de neuvaine proposé (durée, thème, éventuellement jeûne associé). */
data class NovenaType(
    val id: String,
    val nameFr: String,
    val nameIt: String,
    val days: Int,
    val descFr: String,
    val descIt: String,
    val category: String,   // "classique" | "longue" | "jeune"
    val fasting: Boolean = false,
    val phases: List<NovenaPhase> = emptyList() // ex. 54 jours: pétition puis action de grâce
)

data class NovenaPhase(
    val fromDay: Int,   // 1-based, inclus
    val toDay: Int,     // inclus
    val labelFr: String,
    val labelIt: String
)

/** Une neuvaine achevée, conservée au répertoire. */
data class NovenaHistoryEntry(
    val typeId: String,
    val startEpochDay: Long,
    val endEpochDay: Long
)

data class NovenaProgress(
    val started: Boolean = false,
    val typeId: String = "",
    val day: Int = 0,
    val lastDay: Long = -1L,
    val startEpochDay: Long = -1L,
    val completed: List<NovenaHistoryEntry> = emptyList()
)

// =============================================================================
//  EXERCICES SPIRITUELS — pratiques fondées sur l'Écriture et les Pères,
//  réparties en deux répertoires : orthodoxe et catholique.
// =============================================================================

data class SpiritualExercise(
    val id: String,
    val family: String,     // "orthodoxe" | "catholique"
    val name: String,
    val source: String,
    val desc: String,
    val steps: List<String>,
    val scripture: List<String>
)

// =============================================================================
//  CALENDRIER DE JEÛNE — jeûnes universels et propres à chaque tradition/ordre,
//  catholiques et orthodoxes.
// =============================================================================

data class FastEntry(
    val id: String,
    val family: String,     // "orthodoxe" | "catholique"
    val scope: String,      // "Église universelle" ou nom de la tradition/ordre
    val name: String,
    val computed: String?,  // clé calculée par FastingCalendar, ou null si seulement descriptif
    val rule: String,
    val note: String
)

// =============================================================================
//  LE JEU DE LECTURE — niveaux infinis pour chaque livre de la Bible, et pour
//  les œuvres (théologie, spiritualité/mystique, Pères de l'Église) que le
//  lecteur ajoute lui-même. Chaque séance chronométrée (5 à 60 min) fait
//  progresser la jauge du livre/de l'œuvre ; les niveaux de tous les
//  livres/œuvres d'une catégorie se combinent en un niveau cumulé.
// =============================================================================

object ReadingCategory {
    const val BIBLE = "bible"
    const val THEOLOGIE = "theologie"
    const val MYSTIQUE = "mystique"
    const val PERES = "peres"
    val ALL = listOf(BIBLE, THEOLOGIE, MYSTIQUE, PERES)
}

/** Le jeu de lecture biblique couvre les 73 livres du canon, chacun avec son propre
 *  niveau — distinct du [BibleBook] (id, name, file, testament, chapters) qui sert
 *  à afficher le texte des quelques livres disponibles en lecture intégrale. */
data class BibleReadingBook(
    val id: String,
    val nameFr: String,
    val nameIt: String,
    val testament: String   // "AT" | "NT"
)

/** Une œuvre ajoutée par le lecteur lui-même, dans l'une des trois catégories
 *  théologie / spiritualité-mystique / Pères de l'Église. */
data class ReadingWork(
    val id: String,
    val category: String,   // ReadingCategory.THEOLOGIE | MYSTIQUE | PERES
    val title: String,
    val author: String = "",
    val xp: Long = 0L
)

/** Les 12 durées de séance proposées, en minutes. */
val READING_DURATIONS = listOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60)
