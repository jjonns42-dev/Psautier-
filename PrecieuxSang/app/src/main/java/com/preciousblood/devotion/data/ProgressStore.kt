package com.preciousblood.devotion.data

import android.content.Context
import java.time.DayOfWeek
import java.time.LocalDate

/** État de progression de la Veillée de Gethsémani. */
data class GethsemaniProgress(
    val level: Int = 1,
    /** Vendredis consécutifs accumulés vers le niveau suivant (0..3). */
    val progressInLevel: Int = 0,
    /** Série de vendredis consécutifs (pour l'affichage). */
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalHolyHours: Int = 0,
    /** Jour (epochDay) du dernier vendredi accompli, ou -1. */
    val lastFridayEpochDay: Long = -1L
) {
    companion object {
        const val FRIDAYS_PER_LEVEL = 4
    }
}

/** Résultat d'un enregistrement d'heure accomplie. */
data class CompletionResult(
    val progress: GethsemaniProgress,
    val alreadyDoneThisWeek: Boolean,
    val leveledUp: Boolean,
    val streakBroken: Boolean
)

/**
 * Stockage simple (SharedPreferences) de la progression.
 *
 * Règle : 4 vendredis consécutifs = +1 niveau, à l'infini.
 * Le niveau est permanent ; rompre la série ne fait perdre que la
 * progression en cours dans le niveau, jamais les niveaux acquis.
 */
class ProgressStore(context: Context) {

    private val prefs = context.getSharedPreferences("gethsemani", Context.MODE_PRIVATE)

    fun load(): GethsemaniProgress = GethsemaniProgress(
        level = prefs.getInt("level", 1),
        progressInLevel = prefs.getInt("progressInLevel", 0),
        currentStreak = prefs.getInt("currentStreak", 0),
        bestStreak = prefs.getInt("bestStreak", 0),
        totalHolyHours = prefs.getInt("total", 0),
        lastFridayEpochDay = prefs.getLong("lastFriday", -1L)
    )

    private fun save(p: GethsemaniProgress) {
        prefs.edit()
            .putInt("level", p.level)
            .putInt("progressInLevel", p.progressInLevel)
            .putInt("currentStreak", p.currentStreak)
            .putInt("bestStreak", p.bestStreak)
            .putInt("total", p.totalHolyHours)
            .putLong("lastFriday", p.lastFridayEpochDay)
            .apply()
    }

    fun reset() {
        prefs.edit().clear().apply()
    }

    /** Le vendredi auquel rattacher une heure accomplie aujourd'hui. */
    fun fridayFor(today: LocalDate): LocalDate = when (today.dayOfWeek) {
        DayOfWeek.FRIDAY -> today
        DayOfWeek.SATURDAY -> today.minusDays(1)
        DayOfWeek.SUNDAY -> today.minusDays(2)
        else -> {
            // lundi..jeudi : le vendredi à venir de cette semaine
            val diff = (DayOfWeek.FRIDAY.value - today.dayOfWeek.value + 7) % 7
            today.plusDays(diff.toLong())
        }
    }

    /** Enregistre une heure accomplie pour le vendredi donné. */
    fun recordCompletion(friday: LocalDate): CompletionResult {
        val cur = load()
        val epoch = friday.toEpochDay()

        if (epoch == cur.lastFridayEpochDay) {
            return CompletionResult(cur, alreadyDoneThisWeek = true,
                leveledUp = false, streakBroken = false)
        }

        val consecutive = cur.lastFridayEpochDay >= 0 &&
                (epoch - cur.lastFridayEpochDay) == 7L
        val streakBroken = cur.lastFridayEpochDay >= 0 && !consecutive

        val newStreak = if (consecutive) cur.currentStreak + 1 else 1
        var newProgress = if (consecutive) cur.progressInLevel + 1 else 1
        var newLevel = cur.level
        var leveledUp = false
        if (newProgress >= GethsemaniProgress.FRIDAYS_PER_LEVEL) {
            newLevel += 1
            newProgress = 0
            leveledUp = true
        }

        val updated = cur.copy(
            level = newLevel,
            progressInLevel = newProgress,
            currentStreak = newStreak,
            bestStreak = maxOf(cur.bestStreak, newStreak),
            totalHolyHours = cur.totalHolyHours + 1,
            lastFridayEpochDay = epoch
        )
        save(updated)
        return CompletionResult(updated, alreadyDoneThisWeek = false,
            leveledUp = leveledUp, streakBroken = streakBroken)
    }
}
