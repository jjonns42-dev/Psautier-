package com.liturgia.monastica.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
}
