package com.byzantine.horologion.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

/**
 * The optional Jesus-Prayer game played in the candle space.
 *
 * Rules (from the user's specification):
 *  - Level N requires N×5 minutes of the Jesus Prayer (level 1 = 5 min).
 *  - A level is held for one week; the user confirms each day, near the end of
 *    the candle, whether they prayed for the required time. Days are counted.
 *  - After 7 confirmed days the level rises by 5 minutes — unless the user has
 *    chosen to FREEZE the level (allowed from 30 min = level 6). Levels are infinite.
 *  - If a day is missed, the week restarts at day 1 of the SAME level.
 *  - If a whole week passes without using the game, one level is lost.
 *  - From level 12 (1 h) once a week, on a random day, a quote shows for 90 s.
 *  - Backgrounds: ≥ level 24 (2 h) → Death-to-the-World, alternating daily;
 *                 ≥ level 12 (1 h) → the Theotokos icon; otherwise the Virgin of Silence.
 */
class GameState(context: Context) {

    private val prefs = context.getSharedPreferences("horologion_game", Context.MODE_PRIVATE)

    var level by mutableIntStateOf(prefs.getInt("level", 1)); private set
    var frozen by mutableStateOf(prefs.getBoolean("frozen", false)); private set
    var daysThisWeek by mutableIntStateOf(prefs.getInt("days", 0)); private set
    private var weekStartDay = prefs.getLong("weekStart", -1L)
    private var lastCompletedDay = prefs.getLong("lastDone", -1L)
    private var lastActivityDay = prefs.getLong("lastAct", -1L)
    private var weeklyQuoteShownDay = prefs.getLong("qShown", -1L)

    fun requiredMinutes(): Int = level * 5
    fun canFreeze(): Boolean = level >= 6      // 30 minutes

    private fun persist() {
        prefs.edit()
            .putInt("level", level).putBoolean("frozen", frozen)
            .putInt("days", daysThisWeek).putLong("weekStart", weekStartDay)
            .putLong("lastDone", lastCompletedDay).putLong("lastAct", lastActivityDay)
            .putLong("qShown", weeklyQuoteShownDay)
            .apply()
    }

    fun applyFrozen(v: Boolean) { frozen = v; persist() }

    private fun resetWeek(today: Long) {
        daysThisWeek = 0
        weekStartDay = today
    }

    /** Called every time the candle screen opens, with today's epoch-day. */
    fun reconcile(today: Long) {
        if (weekStartDay < 0L) {                 // first ever run
            weekStartDay = today
            lastActivityDay = today
            persist(); return
        }
        if (lastActivityDay >= 0L && today - lastActivityDay >= 7L) {
            // a whole week went unused -> drop one level
            if (level > 1) level -= 1
            resetWeek(today)
        } else if (daysThisWeek in 1..6 && lastCompletedDay >= 0L &&
            today - lastCompletedDay >= 2L
        ) {
            // a day was skipped mid-week -> restart the week at day 1
            resetWeek(today)
        }
        lastActivityDay = today
        persist()
    }

    /** Confirmation shown near the end of the candle. */
    fun confirm(success: Boolean, today: Long) {
        lastActivityDay = today
        if (!success) { resetWeek(today); persist(); return }
        if (lastCompletedDay != today) {
            daysThisWeek += 1
            lastCompletedDay = today
        }
        if (daysThisWeek >= 7) {
            if (!frozen) level += 1
            resetWeek(today)
        }
        persist()
    }

    // ---- weekly quote ----
    private fun weeklyQuoteDay(): Long =
        if (weekStartDay < 0) -1L else weekStartDay + Random(weekStartDay).nextInt(7)

    fun shouldShowQuoteToday(today: Long): Boolean =
        level >= 12 && today == weeklyQuoteDay() && weeklyQuoteShownDay != today

    fun markQuoteShown(today: Long) { weeklyQuoteShownDay = today; persist() }

    /** Which quote pool to draw from: patristic from 2 h, scriptural from 1 h. */
    fun quotePool(): String = if (level >= 24) "level2h" else "level1h"

    /** Drawable name for the background, given today's epoch-day. */
    fun backgroundDrawable(today: Long): String = when {
        level >= 24 -> if (today % 2L == 0L) "bg_dttw_cross" else "bg_dttw_skull"
        level >= 12 -> "bg_madonna"
        else -> "icon_virgin_silence"
    }
}
