package com.liturgia.monastica.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.TimeZone

/** Local calendar day index (days since epoch in the device's timezone). */
fun todayEpochDay(): Long {
    val cal = Calendar.getInstance()
    val zoneOffsetMs = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)
    return (cal.timeInMillis + zoneOffsetMs) / 86_400_000L
}

/**
 * Single source of truth for preferences and both prayer games.
 * Backed by SharedPreferences; exposes Compose state so screens recompose on change.
 */
class GameStore(context: Context) {

    private val sp = context.getSharedPreferences("liturgia_monastica", Context.MODE_PRIVATE)

    // ---- App preferences -----------------------------------------------------
    var lang by mutableStateOf(sp.getString("lang", "fr") ?: "fr")
        private set
    var night by mutableStateOf(sp.getBoolean("night", false))
        private set

    fun applyLang(v: String) { lang = v; sp.edit().putString("lang", v).apply() }
    fun toggleNight() { night = !night; sp.edit().putBoolean("night", night).apply() }

    // =========================================================================
    //  CANDLE GAME  (la bougie)
    //  Pray each day for a week. 7 days -> level up (+5 min) or stay.
    //  Miss a day -> back to start of the level. From 30 min: Benedict quote.
    // =========================================================================
    var candleLevel by mutableStateOf(sp.getInt("c_level", 1))
        private set
    var candleStreak by mutableStateOf(sp.getInt("c_streak", 0))
        private set
    var candleLastDay by mutableStateOf(sp.getLong("c_lastday", -1L))
        private set
    /** true when a full week is complete and the player must choose advance / stay. */
    var candleAwaitingChoice by mutableStateOf(sp.getBoolean("c_await", false))
        private set

    val candleMinutes: Int get() = candleLevel * 5

    /** Re-evaluate streak against the calendar; resets if a day was skipped. */
    fun candleRefresh() {
        if (candleAwaitingChoice) return
        if (candleLastDay < 0) return
        val gap = todayEpochDay() - candleLastDay
        if (gap >= 2) { // a full day was missed
            candleStreak = 0
            persistCandle()
        }
    }

    /** true if the timed prayer was already completed today. */
    fun candleDoneToday(): Boolean = candleLastDay == todayEpochDay()

    /** Call when the daily timer finishes successfully. */
    fun candleCompleteToday() {
        if (candleDoneToday() || candleAwaitingChoice) return
        candleStreak += 1
        candleLastDay = todayEpochDay()
        if (candleStreak >= 7) candleAwaitingChoice = true
        persistCandle()
    }

    fun candleAdvance() {            // climb to next level (+5 min)
        candleLevel += 1
        candleStreak = 0
        candleAwaitingChoice = false
        persistCandle()
    }

    fun candleStay() {              // remain at the same level for another week
        candleStreak = 0
        candleAwaitingChoice = false
        persistCandle()
    }

    fun candleReset() {
        candleLevel = 1; candleStreak = 0; candleLastDay = -1; candleAwaitingChoice = false
        persistCandle()
    }

    private fun persistCandle() {
        sp.edit()
            .putInt("c_level", candleLevel)
            .putInt("c_streak", candleStreak)
            .putLong("c_lastday", candleLastDay)
            .putBoolean("c_await", candleAwaitingChoice)
            .apply()
    }

    // =========================================================================
    //  1000 DAYS  (1000 jours)
    //  Each night pray 30 or 60 min with the intention of becoming a saint,
    //  with one chosen, locked devotion. Miss a day -> restart from day 0.
    // =========================================================================
    var thStarted by mutableStateOf(sp.getBoolean("t_started", false))
        private set
    var thDevotion by mutableStateOf(sp.getString("t_dev", "") ?: "")
        private set
    var thMinutes by mutableStateOf(sp.getInt("t_min", 30))
        private set
    var thDay by mutableStateOf(sp.getInt("t_day", 0))
        private set
    var thLastDay by mutableStateOf(sp.getLong("t_lastday", -1L))
        private set

    val THOUSAND = 1000

    fun thRefresh() {
        if (!thStarted) return
        if (thLastDay < 0) return
        val gap = todayEpochDay() - thLastDay
        if (gap >= 2) {            // a day was skipped -> start over
            thDay = 0
            thLastDay = -1
            persistThousand()
        }
    }

    fun thDoneToday(): Boolean = thLastDay == todayEpochDay()

    /** Begin a new game: locks the devotion and the duration. */
    fun thStart(devotionId: String, minutes: Int) {
        thStarted = true
        thDevotion = devotionId
        thMinutes = if (minutes == 60) 60 else 30
        thDay = 0
        thLastDay = -1
        persistThousand()
    }

    /** Mark today as prayed. */
    fun thCheckToday() {
        if (!thStarted || thDoneToday()) return
        thDay += 1
        thLastDay = todayEpochDay()
        persistThousand()
    }

    /** Full restart: unlocks devotion choice again. */
    fun thRestart() {
        thStarted = false; thDevotion = ""; thDay = 0; thLastDay = -1
        persistThousand()
    }

    private fun persistThousand() {
        sp.edit()
            .putBoolean("t_started", thStarted)
            .putString("t_dev", thDevotion)
            .putInt("t_min", thMinutes)
            .putInt("t_day", thDay)
            .putLong("t_lastday", thLastDay)
            .apply()
    }

    // =========================================================================
    //  LE COMBAT  (les huit pensées d'Évagre/Cassien + l'envie)
    //  Un niveau dure une semaine (7 confirmations). Chaque jour doit être
    //  confirmé manuellement une fois la pratique orthodoxe et/ou catholique
    //  accomplie. Une seule journée sans confirmation remet le niveau à zéro.
    //  Chaque péché progresse indépendamment ; jusqu'à 10 niveaux.
    // =========================================================================
    private val gson = Gson()
    private val combatType = object : TypeToken<Map<String, SinProgress>>() {}.type

    var combatProgress by mutableStateOf(loadCombatProgress())
        private set

    /** Set of sinIds whose current level was just reset by a missed day (consumed once by the UI). */
    var combatJustReset by mutableStateOf(setOf<String>())
        private set

    private fun loadCombatProgress(): Map<String, SinProgress> {
        val raw = sp.getString("combat_progress", null) ?: return emptyMap()
        return try {
            gson.fromJson<Map<String, SinProgress>>(raw, combatType) ?: emptyMap()
        } catch (e: Exception) { emptyMap() }
    }

    private fun persistCombat() {
        sp.edit().putString("combat_progress", gson.toJson(combatProgress)).apply()
    }

    private fun progressFor(sinId: String): SinProgress = combatProgress[sinId] ?: SinProgress()

    fun combatLevel(sinId: String): Int = progressFor(sinId).level
    fun combatDaysConfirmed(sinId: String): Int = progressFor(sinId).daysConfirmed
    fun combatDoneToday(sinId: String): Boolean = progressFor(sinId).lastDay == todayEpochDay()

    private fun updateProgress(sinId: String, transform: (SinProgress) -> SinProgress) {
        val updated = transform(progressFor(sinId))
        combatProgress = combatProgress + (sinId to updated)
        persistCombat()
    }

    private fun isFullyAccomplished(p: SinProgress) = p.level >= 10 && p.daysConfirmed >= 7

    /** Re-evaluate every sin's streak against the calendar; called on app launch and on entering a combat screen. */
    fun combatRefresh() {
        val today = todayEpochDay()
        var changed = false
        val justReset = mutableSetOf<String>()
        val updated = combatProgress.mapValues { (id, p) ->
            if (!isFullyAccomplished(p) && p.lastDay >= 0) {
                val gap = today - p.lastDay
                if (gap >= 2) {
                    changed = true
                    justReset += id
                    p.copy(daysConfirmed = 0, lastDay = -1L)
                } else p
            } else p
        }
        if (changed) {
            combatProgress = updated
            persistCombat()
        }
        if (justReset.isNotEmpty()) combatJustReset = justReset
    }

    /** The UI calls this once it has shown the reset notice, so it doesn't reappear. */
    fun combatConsumeReset(sinId: String) {
        if (sinId in combatJustReset) combatJustReset = combatJustReset - sinId
    }

    /** Confirm today's practice for a sin. Advances the level automatically at 7/7. */
    fun combatConfirmToday(sinId: String) {
        if (combatDoneToday(sinId)) return
        if (isFullyAccomplished(progressFor(sinId))) return
        updateProgress(sinId) { p ->
            var level = p.level
            var days = p.daysConfirmed + 1
            var lastDay: Long = todayEpochDay()
            if (days >= 7) {
                if (level < 10) {
                    level += 1
                    days = 0
                    lastDay = -1L
                } else {
                    days = 7 // combat achevé : reste plein, ne se remet plus à zéro
                }
            }
            p.copy(level = level, daysConfirmed = days, lastDay = lastDay)
        }
    }

    /** Full manual restart of a single sin's combat (level 1, day 0). */
    fun combatResetSin(sinId: String) {
        updateProgress(sinId) { SinProgress() }
    }

    // =========================================================================
    //  LA RÈGLE  (règle de prière à niveaux infinis)
    //  Une seule règle active à la fois : famille + tradition/ordre + difficulté
    //  choisis une fois, verrouillés jusqu'à un « recommencer à zéro ».
    //  Un niveau dure une semaine. Un jour manqué fait redescendre d'UN SEUL
    //  niveau (à revalider entièrement) ; le niveau ne descend jamais sous 1.
    // =========================================================================
    private val ruleType = object : TypeToken<RuleProgress>() {}.type

    var ruleProgress by mutableStateOf(loadRuleProgress())
        private set

    /** true right after a missed day has demoted the level (consumed once by the UI). */
    var ruleJustDemoted by mutableStateOf(false)
        private set

    private fun loadRuleProgress(): RuleProgress {
        val raw = sp.getString("rule_progress", null) ?: return RuleProgress()
        return try { gson.fromJson(raw, ruleType) ?: RuleProgress() } catch (e: Exception) { RuleProgress() }
    }

    private fun persistRule() {
        sp.edit().putString("rule_progress", gson.toJson(ruleProgress)).apply()
    }

    fun ruleDoneToday(): Boolean = ruleProgress.started && ruleProgress.lastDay == todayEpochDay()

    /** Locks the tradition and difficulty; begins at level 1. */
    fun ruleStart(traditionId: String, difficulty: RuleDifficulty) {
        val today = todayEpochDay()
        ruleProgress = RuleProgress(
            started = true, traditionId = traditionId, difficulty = difficulty.name,
            level = 1, daysConfirmed = 0, lastDay = -1L,
            history = listOf(RuleHistoryEntry(level = 1, epochDay = today, event = "start"))
        )
        ruleJustDemoted = false
        persistRule()
    }

    /** Re-evaluate the streak against the calendar; demotes by exactly one level on a missed day. */
    fun ruleRefresh() {
        val p = ruleProgress
        if (!p.started || p.lastDay < 0) return
        val gap = todayEpochDay() - p.lastDay
        if (gap >= 2) {
            val newLevel = if (p.level > 1) p.level - 1 else 1
            val entry = RuleHistoryEntry(level = newLevel, epochDay = todayEpochDay(), event = "demotion")
            ruleProgress = p.copy(level = newLevel, daysConfirmed = 0, lastDay = -1L, history = p.history + entry)
            ruleJustDemoted = true
            persistRule()
        }
    }

    fun ruleConsumeDemoted() { if (ruleJustDemoted) ruleJustDemoted = false }

    /** Confirm today's rule. Advances one level at 7/7 — levels are unbounded. */
    fun ruleConfirmToday() {
        val p = ruleProgress
        if (!p.started || ruleDoneToday()) return
        var level = p.level
        var days = p.daysConfirmed + 1
        var lastDay = todayEpochDay()
        var history = p.history
        if (days >= 7) {
            level += 1
            days = 0
            lastDay = -1L
            history = history + RuleHistoryEntry(level = level, epochDay = todayEpochDay(), event = "levelup")
        }
        ruleProgress = p.copy(level = level, daysConfirmed = days, lastDay = lastDay, history = history)
        persistRule()
    }

    /** Renouvellement des vœux : appelé quand l'utilisateur reconnaît consciemment une année de fidélité. */
    fun ruleAcknowledgeRenewal(year: Int) {
        val p = ruleProgress
        if (!p.started || year <= p.lastRenewalYear) return
        val entry = RuleHistoryEntry(level = p.level, epochDay = todayEpochDay(), event = "renewal")
        ruleProgress = p.copy(lastRenewalYear = year, history = p.history + entry)
        persistRule()
    }

    /** Abandon the current rule entirely; returns to the choice screen. */
    fun ruleReset() {
        ruleProgress = RuleProgress()
        ruleJustDemoted = false
        persistRule()
    }
}
