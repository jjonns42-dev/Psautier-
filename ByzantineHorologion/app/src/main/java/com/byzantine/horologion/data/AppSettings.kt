package com.byzantine.horologion.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode { DAY, NIGHT, SYSTEM }
enum class Accent { RED, PINK }
enum class Lang { FR, IT }
enum class Role { PRIEST, LAYPERSON }

/**
 * Central, observable settings store. Backed by SharedPreferences so choices
 * survive restarts; exposes Compose-observable state so screens recompose.
 */
class AppSettings(context: Context) {

    private val prefs = context.getSharedPreferences("horologion", Context.MODE_PRIVATE)

    var themeMode by mutableStateOf(
        ThemeMode.valueOf(prefs.getString("theme", ThemeMode.SYSTEM.name)!!)
    )
        private set

    var accent by mutableStateOf(
        Accent.valueOf(prefs.getString("accent", Accent.RED.name)!!)
    )
        private set

    var lang by mutableStateOf(
        Lang.valueOf(prefs.getString("lang", Lang.FR.name)!!)
    )
        private set

    var role by mutableStateOf(
        Role.valueOf(prefs.getString("role", Role.LAYPERSON.name)!!)
    )
        private set

    var showScores by mutableStateOf(prefs.getBoolean("scores", false))
        private set

    var gameEnabled by mutableStateOf(prefs.getBoolean("game", false))
        private set

    fun setTheme(v: ThemeMode) { themeMode = v; prefs.edit().putString("theme", v.name).apply() }
    fun applyAccent(v: Accent) { accent = v; prefs.edit().putString("accent", v.name).apply() }
    fun applyRole(v: Role) { role = v; prefs.edit().putString("role", v.name).apply() }
    fun applyShowScores(v: Boolean) { showScores = v; prefs.edit().putBoolean("scores", v).apply() }
    fun applyGameEnabled(v: Boolean) { gameEnabled = v; prefs.edit().putBoolean("game", v).apply() }

    /** Language change requires an Activity recreate to re-resolve resources. */
    fun setLangPersisted(v: Lang) { lang = v; prefs.edit().putString("lang", v.name).apply() }

    companion object {
        fun savedLang(context: Context): String =
            context.getSharedPreferences("horologion", Context.MODE_PRIVATE)
                .getString("lang", Lang.FR.name)!!
    }
}
