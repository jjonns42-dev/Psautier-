package com.liturgia.monastica.data

import android.content.Context
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.Calendar

/**
 * Loads every JSON asset once and exposes typed accessors. The Office is assembled
 * on demand from the editable [office_structure.json] distribution + the real psalm
 * and canticle texts (Bible Crampon 1923).
 */
class ContentRepository(private val context: Context) {

    private fun readAsset(path: String): String =
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private fun obj(path: String): JsonObject =
        JsonParser.parseString(readAsset(path)).asJsonObject

    // ---- Lazy-loaded sources -------------------------------------------------
    private val psalms: JsonObject by lazy { obj("psalms_fr.json").getAsJsonObject("psalms") }
    private val canticlesRoot: JsonObject by lazy { obj("canticles.json").getAsJsonObject("canticles") }
    private val office: JsonObject by lazy { obj("office_structure.json") }
    private val quotesRoot by lazy { obj("benedict_quotes.json") }
    private val devotionsRoot by lazy { obj("devotions.json") }

    // ---- Psalms --------------------------------------------------------------
    fun psalmText(n: Int): String =
        if (psalms.has(n.toString())) psalms.get(n.toString()).asString
        else "(Psaume $n indisponible dans cette source.)"

    fun allPsalmNumbers(): List<Int> = (1..150).toList()

    // ---- Canticles -----------------------------------------------------------
    fun canticle(id: String): Canticle? {
        if (!canticlesRoot.has(id)) return null
        val c = canticlesRoot.getAsJsonObject(id)
        return Canticle(
            id = id,
            titleFr = c.get("title_fr").asString,
            titleIt = c.get("title_it").asString,
            ref = c.get("ref").asString,
            text = c.get("text").asString
        )
    }

    // ---- Benedict quotes -----------------------------------------------------
    fun benedictQuotes(): List<BenedictQuote> =
        quotesRoot.getAsJsonArray("quotes").map {
            val o = it.asJsonObject
            BenedictQuote(o.get("fr").asString, o.get("it").asString, o.get("src").asString)
        }

    fun randomBenedictQuote(): BenedictQuote = benedictQuotes().random()

    // ---- Devotions -----------------------------------------------------------
    fun devotions(): List<Devotion> =
        devotionsRoot.getAsJsonArray("devotions").map {
            val o = it.asJsonObject
            Devotion(
                id = o.get("id").asString,
                nameFr = o.get("name_fr").asString,
                nameIt = o.get("name_it").asString,
                desc = o.get("desc").asString,
                promises = o.getAsJsonArray("promises").map { p -> p.asString }
            )
        }

    fun devotion(id: String): Devotion? = devotions().firstOrNull { it.id == id }

    // ---- Hours ---------------------------------------------------------------
    fun hours(): List<HourMeta> {
        val h = office.getAsJsonObject("hours")
        return h.keySet().map { key ->
            val o = h.getAsJsonObject(key)
            HourMeta(key, o.get("name_fr").asString, o.get("name_it").asString,
                o.get("name_la").asString, o.get("order").asInt)
        }.sortedBy { it.order }
    }

    private fun fixed(field: String): String =
        office.getAsJsonObject("fixed").get(field).asString

    private fun introVersicle(lang: String): String {
        val f = office.getAsJsonObject("fixed").getAsJsonObject("introduction")
        return f.get(if (lang == "it") "versicle_it" else "versicle_fr").asString
    }

    private fun hymnFor(hourId: String, lang: String): OfficeSection? {
        val hymns = office.getAsJsonObject("hymns") ?: return null
        if (!hymns.has(hourId)) return null
        val h = hymns.getAsJsonObject(hourId)
        val title = if (h.has("title")) h.get("title").asString else "Hymne"
        val latin = h.get("latin").asString
        val fr = h.get("fr").asString
        // Latin is the sung text; French translation follows for understanding.
        val body = latin + "\n\n— " + fr
        return OfficeSection("hymn", if (lang == "it") "Inno" else "Hymne", title, body)
    }

    /** The full, authentic monastic Compline (Breviarium Monasticum, Pars II). */
    private fun buildCompline(lang: String): List<OfficeSection> {
        val it = lang == "it"
        val c = office.getAsJsonObject("completorium_full")
        fun seg(key: String) = c.getAsJsonObject(key)
        val s = mutableListOf<OfficeSection>()
        val gloria = if (it) fixed("gloria_it") else fixed("gloria_fr")

        s += OfficeSection("intro", if (it) "Apertura" else "Ouverture", "",
            seg("opening").get(if (it) "fr" else "latin").asString)
        s += OfficeSection("reading", if (it) "Lettura breve" else "Lecture brève",
            seg("lectio_brevis").get("ref").asString,
            seg("lectio_brevis").get(if (it) "fr" else "latin").asString)
        s += OfficeSection("prayer", "", "", seg("adjutorium").get(if (it) "fr" else "latin").asString)
        s += OfficeSection("prayer", if (it) "Confesso" else "Confiteor", "",
            seg("confiteor").get(if (it) "fr" else "latin").asString)
        s += OfficeSection("prayer", "", "", seg("converte").get(if (it) "fr" else "latin").asString)
        s += OfficeSection("label", "", "", seg("antiphon").get(if (it) "fr" else "latin").asString)

        val psLabel = if (it) "Salmo" else "Psaume"
        for (n in c.getAsJsonArray("psalms").map { it2 -> it2.asInt }) {
            s += OfficeSection("psalm", "$psLabel $n", "", psalmText(n) + "\n\n" + gloria)
        }

        hymnFor("completorium", lang)?.let { s += it }

        s += OfficeSection("reading", if (it) "Capitolo" else "Chapitre",
            seg("chapter").get("ref").asString, seg("chapter").get(if (it) "fr" else "latin").asString)
        s += OfficeSection("prayer", if (it) "Responsorio" else "Répons", "",
            seg("responsory").get(if (it) "fr" else "latin").asString)

        canticle("nunc_dimittis")?.let { nd ->
            s += OfficeSection("label", "", "", seg("nunc_antiphon").get(if (it) "fr" else "latin").asString)
            s += OfficeSection("gospel_canticle", if (it) nd.titleIt else nd.titleFr, nd.ref, nd.text + "\n\n" + gloria)
        }
        s += OfficeSection("prayer", if (it) "Padre nostro" else "Notre Père", "",
            if (it) fixed("our_father_it") else fixed("our_father_fr"))
        s += OfficeSection("prayer", if (it) "Orazione" else "Oraison", "",
            seg("collect").get(if (it) "fr" else "latin").asString)
        s += OfficeSection("conclusion", if (it) "Benedizione" else "Bénédiction", "",
            seg("blessing").get(if (it) "fr" else "latin").asString)

        // Final Marian antiphon
        val salve = office.getAsJsonObject("marian_antiphons").getAsJsonObject("salve_regina")
        s += OfficeSection("gospel_canticle", salve.get("title").asString,
            if (it) "Antifona finale alla Vergine" else "Antienne finale à la Vierge",
            salve.get(if (it) "fr" else "latin").asString)
        return s
    }

    private fun distributionFor(hourId: String, weekday: Int): List<Int> {
        val dist = office.getAsJsonObject("distribution")
        if (!dist.has(hourId)) return emptyList()
        val hourDist = dist.getAsJsonObject(hourId)
        val arr = when {
            hourDist.has("all") -> hourDist.getAsJsonArray("all")
            hourDist.has(weekday.toString()) -> hourDist.getAsJsonArray(weekday.toString())
            else -> return emptyList()
        }
        return arr.map { it.asInt }
    }

    /**
     * Assemble a full hour for the given weekday (Calendar.SUNDAY..SATURDAY).
     * lang = "fr" or "it" — affects the fixed prayers and labels.
     */
    fun buildHour(hourId: String, weekday: Int, lang: String): List<OfficeSection> {
        // Compline has its own complete, authentic ordinary.
        if (hourId == "completorium") return buildCompline(lang)

        val sections = mutableListOf<OfficeSection>()
        val dayIndex = weekday - 1 // Calendar.SUNDAY(1) -> 0

        val psLabel = if (lang == "it") "Salmo" else "Psaume"
        val gloria = if (lang == "it") fixed("gloria_it") else fixed("gloria_fr")

        // 1. Opening versicle
        sections += OfficeSection(
            kind = "intro",
            title = if (lang == "it") "Introduzione" else "Ouverture",
            body = introVersicle(lang)
        )

        // 2. Hymn (authentic Latin + French)
        hymnFor(hourId, lang)?.let { sections += it }

        // 2. Psalmody
        val psalmNums = distributionFor(hourId, dayIndex)
        for (n in psalmNums) {
            sections += OfficeSection(
                kind = "psalm",
                title = "$psLabel $n",
                body = psalmText(n) + "\n\n" + gloria
            )
        }

        // 3. Gospel canticle for Lauds / Vespers / Compline
        val hourObj = office.getAsJsonObject("hours").getAsJsonObject(hourId)
        if (!hourObj.get("uses_canticle").isJsonNull) {
            val cantId = hourObj.get("uses_canticle").asString
            canticle(cantId)?.let { c ->
                sections += OfficeSection(
                    kind = "gospel_canticle",
                    title = if (lang == "it") c.titleIt else c.titleFr,
                    subtitle = c.ref,
                    body = c.text + "\n\n" + gloria
                )
            }
        }

        // 4. Our Father (at the principal hours)
        if (hourId in listOf("laudes", "vesperae", "completorium")) {
            sections += OfficeSection(
                kind = "prayer",
                title = if (lang == "it") "Padre nostro" else "Notre Père",
                body = if (lang == "it") fixed("our_father_it") else fixed("our_father_fr")
            )
        }

        // 5. Conclusion
        sections += OfficeSection(
            kind = "conclusion",
            title = if (lang == "it") "Conclusione" else "Conclusion",
            body = if (lang == "it") fixed("conclusion_it") else fixed("conclusion_fr")
        )
        return sections
    }

    // ---- Le Combat (huit pensées + envie) -------------------------------------
    private val sinsRoot: JsonObject by lazy { obj("sins.json") }

    private fun quoteOf(o: JsonObject, field: String): SinQuote {
        val q = o.getAsJsonObject(field)
        return SinQuote(q.get("source").asString, q.get("text").asString)
    }

    fun sins(): List<Sin> =
        sinsRoot.getAsJsonArray("sins").map { el ->
            val o = el.asJsonObject
            val prayerObj = o.getAsJsonObject("prayer")
            Sin(
                id = o.get("id").asString,
                name = o.get("name").asString,
                latin = o.get("latin").asString,
                accent = o.get("accent").asString,
                virtue = o.get("virtue").asString,
                desc = o.get("desc").asString,
                prayer = SinPrayerRule(
                    orthoPrayer = quoteOf(prayerObj, "orthoPrayer"),
                    cathoPrayer = quoteOf(prayerObj, "cathoPrayer"),
                    orthoCounsel = quoteOf(prayerObj, "orthoCounsel"),
                    cathoCounsel = quoteOf(prayerObj, "cathoCounsel"),
                    extraCounsel = if (prayerObj.has("extraCounsel")) quoteOf(prayerObj, "extraCounsel") else null
                ),
                verses = o.getAsJsonArray("verses").map { v ->
                    val vo = v.asJsonObject
                    SinVerse(vo.get("ref").asString, vo.get("text").asString)
                },
                levels = o.getAsJsonArray("levels").map { l ->
                    val lo = l.asJsonObject
                    SinLevel(
                        title = lo.get("title").asString,
                        practice = lo.get("practice").asString,
                        ortho = lo.get("ortho").asString,
                        catho = lo.get("catho").asString
                    )
                },
                furtherReading = if (o.has("furtherReading")) o.getAsJsonArray("furtherReading").map { it.asString } else emptyList()
            )
        }

    fun sin(id: String): Sin? = sins().firstOrNull { it.id == id }

    // ---- La Règle (traditions de prière) --------------------------------------
    private val traditionsRoot: JsonObject by lazy { obj("traditions.json") }

    fun ruleTraditions(): List<RuleTradition> =
        traditionsRoot.getAsJsonArray("traditions").map { el ->
            val o = el.asJsonObject
            RuleTradition(
                id = o.get("id").asString,
                family = o.get("family").asString,
                name = o.get("name").asString,
                subtitle = o.get("subtitle").asString,
                patron = o.get("patron").asString,
                accent = o.get("accent").asString,
                desc = o.get("desc").asString,
                dailyCore = o.getAsJsonArray("dailyCore").map { it2 -> it2.asString },
                devotions = o.getAsJsonArray("devotions").map { d ->
                    val dO = d.asJsonObject
                    RuleDevotion(
                        name = dO.get("name").asString,
                        note = dO.get("note").asString,
                        signature = dO.get("signature").asBoolean,
                        minutes = if (dO.has("minutes")) dO.get("minutes").asInt else 0,
                        supersedes = if (dO.has("supersedes")) dO.get("supersedes").asInt else -1
                    )
                }
            )
        }

    fun ruleTradition(id: String): RuleTradition? = ruleTraditions().firstOrNull { it.id == id }

    fun ruleTraditionsByFamily(family: String): List<RuleTradition> =
        ruleTraditions().filter { it.family == family }

    // ---- Neuvaines -------------------------------------------------------------
    private val novenasRoot: JsonObject by lazy { obj("novenas.json") }

    fun novenaTypes(): List<NovenaType> =
        novenasRoot.getAsJsonArray("novenas").map { el ->
            val o = el.asJsonObject
            NovenaType(
                id = o.get("id").asString,
                nameFr = o.get("name_fr").asString,
                nameIt = o.get("name_it").asString,
                days = o.get("days").asInt,
                descFr = o.get("desc_fr").asString,
                descIt = o.get("desc_it").asString,
                category = o.get("category").asString,
                fasting = o.get("fasting").asBoolean,
                phases = o.getAsJsonArray("phases").map { p ->
                    val pO = p.asJsonObject
                    NovenaPhase(
                        fromDay = pO.get("from_day").asInt,
                        toDay = pO.get("to_day").asInt,
                        labelFr = pO.get("label_fr").asString,
                        labelIt = pO.get("label_it").asString
                    )
                }
            )
        }

    fun novenaType(id: String): NovenaType? = novenaTypes().firstOrNull { it.id == id }

    // ---- Exercices spirituels ---------------------------------------------------
    private val exercisesRoot: JsonObject by lazy { obj("spiritual_exercises.json") }

    fun spiritualExercises(): List<SpiritualExercise> =
        exercisesRoot.getAsJsonArray("exercises").map { el ->
            val o = el.asJsonObject
            SpiritualExercise(
                id = o.get("id").asString,
                family = o.get("family").asString,
                name = o.get("name").asString,
                source = o.get("source").asString,
                desc = o.get("desc").asString,
                steps = o.getAsJsonArray("steps").map { it2 -> it2.asString },
                scripture = o.getAsJsonArray("scripture").map { it2 -> it2.asString }
            )
        }

    fun spiritualExercisesByFamily(family: String): List<SpiritualExercise> =
        spiritualExercises().filter { it.family == family }

    // ---- Calendrier de jeûne -----------------------------------------------------
    private val fastingRoot: JsonObject by lazy { obj("fasting_traditions.json") }

    fun fastEntries(): List<FastEntry> =
        fastingRoot.getAsJsonArray("fasts").map { el ->
            val o = el.asJsonObject
            FastEntry(
                id = o.get("id").asString,
                family = o.get("family").asString,
                scope = o.get("scope").asString,
                name = o.get("name").asString,
                computed = if (o.has("computed") && !o.get("computed").isJsonNull) o.get("computed").asString else null,
                rule = o.get("rule").asString,
                note = o.get("note").asString
            )
        }

    fun fastEntriesByFamily(family: String): List<FastEntry> = fastEntries().filter { it.family == family }

    // ---- Jeûnes suivables (espace de jeûne) -------------------------------------
    private val fastTracksRoot: JsonObject by lazy { obj("fast_tracks.json") }

    fun fastTracks(): List<FastTrackType> =
        fastTracksRoot.getAsJsonArray("tracks").map { el ->
            val o = el.asJsonObject
            FastTrackType(
                id = o.get("id").asString,
                family = o.get("family").asString,
                nameFr = o.get("name_fr").asString,
                nameIt = o.get("name_it").asString,
                days = o.get("days").asInt,
                rule = o.get("rule").asString,
                descFr = o.get("desc_fr").asString,
                descIt = o.get("desc_it").asString,
                computed = if (o.has("computed") && !o.get("computed").isJsonNull) o.get("computed").asString else null
            )
        }

    fun fastTrack(id: String): FastTrackType? = fastTracks().firstOrNull { it.id == id }

    // ---- Pénitences (catalogue par niveaux, jours assignés) ---------------------
    private val penancesRoot: JsonObject by lazy { obj("penances.json") }

    fun penances(): List<Penance> =
        penancesRoot.getAsJsonArray("penances").map { el ->
            val o = el.asJsonObject
            Penance(
                id = o.get("id").asString,
                level = o.get("level").asInt,
                family = o.get("family").asString,
                name = o.get("name").asString,
                desc = o.get("desc").asString,
                weekdays = if (o.has("weekdays")) o.getAsJsonArray("weekdays").map { it2 -> it2.asInt } else emptyList(),
                computed = if (o.has("computed") && !o.get("computed").isJsonNull) o.get("computed").asString else null,
                note = if (o.has("note")) o.get("note").asString else "",
                custom = false
            )
        }

    // ---- Œuvres de miséricorde --------------------------------------------------
    private val mercyRoot: JsonObject by lazy { obj("works_of_mercy.json") }

    fun worksOfMercy(): List<WorkOfMercy> =
        mercyRoot.getAsJsonArray("works").map { el ->
            val o = el.asJsonObject
            WorkOfMercy(
                id = o.get("id").asString,
                category = o.get("category").asString,
                nameFr = o.get("name_fr").asString,
                nameIt = o.get("name_it").asString,
                scripture = o.get("scripture").asString,
                desc = o.get("desc").asString,
                examples = o.getAsJsonArray("examples").map { it2 -> it2.asString }
            )
        }

    fun worksOfMercyByCategory(category: String): List<WorkOfMercy> =
        worksOfMercy().filter { it.category == category }

    // ---- Bibliothèque commune de dévotions (pour la règle personnelle) ----------
    /** Toutes les dévotions de toutes les traditions, dédupliquées par nom, pour
     *  que l'utilisateur puisse composer sa propre règle. */
    fun ruleLibraryDevotions(): List<LibraryDevotion> {
        val byName = LinkedHashMap<String, LibraryDevotion>()
        ruleTraditions().forEach { trad ->
            trad.devotions.forEach { d ->
                val existing = byName[d.name]
                if (existing == null) {
                    byName[d.name] = LibraryDevotion(
                        name = d.name, note = d.note, signature = d.signature,
                        minutes = d.minutes, fromTraditions = listOf(trad.name)
                    )
                } else {
                    byName[d.name] = existing.copy(fromTraditions = existing.fromTraditions + trad.name)
                }
            }
        }
        return byName.values.sortedBy { it.name.lowercase() }
    }

    // ---- Livres du jeu de lecture biblique (73 livres, niveaux) -------------------
    private val bibleReadingRoot: JsonObject by lazy { obj("bible_reading_books.json") }

    fun bibleReadingBooks(): List<BibleReadingBook> =
        bibleReadingRoot.getAsJsonArray("books").map { el ->
            val o = el.asJsonObject
            BibleReadingBook(
                id = o.get("id").asString,
                nameFr = o.get("name_fr").asString,
                nameIt = o.get("name_it").asString,
                testament = o.get("testament").asString
            )
        }

    // ---- Bible ---------------------------------------------------------------
    fun bibleBooks(): List<BibleBook> =
        obj("bible/index.json").getAsJsonArray("books").map {
            val o = it.asJsonObject
            BibleBook(o.get("id").asString, o.get("name").asString,
                o.get("file").asString, o.get("testament").asString, o.get("chapters").asInt)
        }

    fun bibleChapter(file: String, chapter: Int): String {
        val o = obj(file).getAsJsonObject("chapters")
        return if (o.has(chapter.toString())) o.get(chapter.toString()).asString
        else "(Chapitre indisponible.)"
    }

    fun bibleChapterCount(file: String): Int =
        obj(file).getAsJsonObject("chapters").keySet().mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0

    // ---- Liturgie des Heures « Ora et labora » -------------------------------
    private val verseRegex = Regex("(\\d+)\\s+(.*?)(?=\\s+\\d+\\s|\\Z)", RegexOption.DOT_MATCHES_ALL)

    /** Découpe un chapitre (texte Crampon « 1 … 2 … ») en (numéro, texte). */
    fun bibleVerses(file: String, chapter: Int): List<Pair<Int, String>> {
        val txt = bibleChapter(file, chapter)
        if (txt.isBlank()) return emptyList()
        return verseRegex.findAll(txt).mapNotNull { m ->
            val n = m.groupValues[1].toIntOrNull() ?: return@mapNotNull null
            n to m.groupValues[2].trim()
        }.toList()
    }

    private val hoursRoot: JsonObject by lazy { obj("hours_prayers.json") }

    fun hoursPrayers(): HoursPrayers {
        val h = hoursRoot.getAsJsonObject("hymns")
        val hymns = h.keySet().associateWith { key ->
            val o = h.getAsJsonObject(key)
            HoursHymn(
                id = key,
                titleFr = o.get("title_fr").asString,
                titleIt = o.get("title_it").asString,
                lines = o.getAsJsonArray("lines").map { it2 -> it2.asString }
            )
        }
        fun arr(name: String) = hoursRoot.getAsJsonArray(name).map { it2 -> it2.asString }
        val inter = hoursRoot.getAsJsonObject("intercession")
        val orai = hoursRoot.getAsJsonObject("oraison")
        return HoursPrayers(
            hymns = hymns,
            notrePere = arr("notre_pere"),
            intercessionTitleFr = inter.get("title_fr").asString,
            intercessionTitleIt = inter.get("title_it").asString,
            intercession = inter.getAsJsonArray("paragraphs").map { it2 -> it2.asString },
            oraisonTitleFr = orai.get("title_fr").asString,
            oraisonTitleIt = orai.get("title_it").asString,
            oraison = orai.getAsJsonArray("paragraphs").map { it2 -> it2.asString },
            invocations = arr("invocations")
        )
    }

    companion object {
        fun todayWeekday(): Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    }
}
