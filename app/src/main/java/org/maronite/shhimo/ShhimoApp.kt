package org.maronite.shhimo

import android.app.Application
import org.maronite.shhimo.data.repository.LiturgyRepository
import org.maronite.shhimo.data.repository.SettingsRepository
import org.maronite.shhimo.data.source.AssetLiturgicalSource

/**
 * Application + conteneur de dépendances minimal (sans framework DI, pour
 * garder le projet simple à lire et à reprendre).
 */
class ShhimoApp : Application() {

    lateinit var liturgyRepository: LiturgyRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val source = AssetLiturgicalSource(this)
        liturgyRepository = LiturgyRepository(source)
        settingsRepository = SettingsRepository(this)
    }
}
