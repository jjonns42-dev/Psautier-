package org.maronite.shhimo.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.maronite.shhimo.data.model.AppLanguage

private val Context.dataStore by preferencesDataStore(name = "shhimo_prefs")

/**
 * Préférences persistées : langue d'affichage et échelle de police.
 * L'arabe libanais est la langue par défaut, l'italien la seconde.
 */
class SettingsRepository(private val context: Context) {

    private val keyLang = stringPreferencesKey("language")
    private val keyFontScale = floatPreferencesKey("font_scale")

    val language: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        when (prefs[keyLang]) {
            "it" -> AppLanguage.ITALIAN
            "syr" -> AppLanguage.SYRIAC
            else -> AppLanguage.ARABIC
        }
    }

    val fontScale: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[keyFontScale] ?: 1.0f
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[keyLang] = language.code }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[keyFontScale] = scale }
    }
}
