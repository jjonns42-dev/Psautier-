package org.maronite.shhimo.data.source

import android.content.Context
import kotlinx.serialization.json.Json
import org.maronite.shhimo.data.model.CalendarEntry
import org.maronite.shhimo.data.model.DailyOffice
import org.maronite.shhimo.data.model.HymnBank

/**
 * Lit les données liturgiques empaquetées dans le dossier assets/.
 *
 * Organisation des assets :
 *   assets/calendar/calendar.json       -> liste des CalendarEntry
 *   assets/offices/<officeId>.json       -> un DailyOffice par fichier
 *   assets/hymns/hymns.json              -> la banque d'hymnes (HymnBank)
 *
 * Le format JSON est documenté dans docs/DATA_FORMAT.md. C'est ce format
 * que devra respecter le contenu réel du Šḥimo une fois saisi.
 */
class AssetLiturgicalSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    fun loadCalendar(): List<CalendarEntry> {
        val raw = readAssetOrNull("calendar/calendar.json") ?: return emptyList()
        return runCatching { json.decodeFromString<List<CalendarEntry>>(raw) }
            .getOrDefault(emptyList())
    }

    fun loadOffice(officeId: String): DailyOffice? {
        val raw = readAssetOrNull("offices/$officeId.json") ?: return null
        return runCatching { json.decodeFromString<DailyOffice>(raw) }.getOrNull()
    }

    fun loadHymnBank(): HymnBank {
        val raw = readAssetOrNull("hymns/hymns.json") ?: return HymnBank()
        return runCatching { json.decodeFromString<HymnBank>(raw) }
            .getOrDefault(HymnBank())
    }

    /** Liste les identifiants d'offices disponibles dans assets/offices/. */
    fun listOfficeIds(): List<String> =
        runCatching {
            context.assets.list("offices")
                ?.filter { it.endsWith(".json") }
                ?.map { it.removeSuffix(".json") }
                ?: emptyList()
        }.getOrDefault(emptyList())

    private fun readAssetOrNull(path: String): String? =
        runCatching {
            context.assets.open(path).bufferedReader().use { it.readText() }
        }.getOrNull()
}
