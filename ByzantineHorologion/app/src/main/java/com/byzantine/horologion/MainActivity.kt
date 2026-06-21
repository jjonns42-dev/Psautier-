package com.byzantine.horologion

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.byzantine.horologion.data.AppSettings
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.data.LiturgicalEngine
import com.byzantine.horologion.data.Repository
import com.byzantine.horologion.ui.AppRoot
import com.byzantine.horologion.ui.LocalEngine
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings
import com.byzantine.horologion.ui.theme.HorologionTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    /** Apply the saved language to the resource configuration before inflation. */
    override fun attachBaseContext(newBase: Context) {
        val code = AppSettings.savedLang(newBase).lowercase(Locale.ROOT)
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = AppSettings(this)
        val repo = Repository(this)
        val engine = LiturgicalEngine(this)

        setContent {
            HorologionTheme(themeMode = settings.themeMode, accent = settings.accent) {
                CompositionLocalProvider(
                    LocalSettings provides settings,
                    LocalRepo provides repo,
                    LocalEngine provides engine
                ) {
                    AppRoot(
                        onLanguageChange = { lang: Lang ->
                            settings.setLangPersisted(lang)
                            recreate() // re-resolve string resources in the new locale
                        }
                    )
                }
            }
        }
    }
}
