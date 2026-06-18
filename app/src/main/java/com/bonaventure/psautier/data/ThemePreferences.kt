package com.bonaventure.psautier.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "psautier_prefs")

/**
 * Mode d'affichage du thème, à l'image du sélecteur de l'app CEI:
 * AUTO suit le système, CLAIR et SOMBRE sont forcés manuellement par l'utilisateur.
 */
enum class ThemeMode { AUTO, CLAIR, SOMBRE }

class ThemePreferences(private val context: Context) {

    private val THEME_MODE_KEY = intPreferencesKey("theme_mode")

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[THEME_MODE_KEY]) {
            1 -> ThemeMode.CLAIR
            2 -> ThemeMode.SOMBRE
            else -> ThemeMode.AUTO
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = when (mode) {
                ThemeMode.AUTO -> 0
                ThemeMode.CLAIR -> 1
                ThemeMode.SOMBRE -> 2
            }
        }
    }
}
