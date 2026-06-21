package com.byzantine.horologion.data

/** A single line/step of an office. */
data class OfficeStep(
    val kind: String,
    val role: String = "tous",          // "tous" | "pretre" | "laic"
    val ref: String? = null,            // id into common prayers / propers
    val fr: String? = null,
    val it: String? = null,
    val rubricFr: String? = null,
    val rubricIt: String? = null
) {
    fun text(lang: Lang): String? = if (lang == Lang.FR) fr else it
    fun rubric(lang: Lang): String? = if (lang == Lang.FR) rubricFr else rubricIt
}

data class Office(
    val id: String,
    val titleFr: String,
    val titleIt: String,
    val steps: List<OfficeStep>
) {
    fun title(lang: Lang) = if (lang == Lang.FR) titleFr else titleIt
}

data class Akathist(
    val id: String,
    val icon: String,
    val titleFr: String, val titleIt: String,
    val refrainFr: String, val refrainIt: String,
    val kontakion1Fr: String, val kontakion1It: String,
    val bodyFr: String, val bodyIt: String
) {
    fun title(l: Lang) = if (l == Lang.FR) titleFr else titleIt
    fun refrain(l: Lang) = if (l == Lang.FR) refrainFr else refrainIt
    fun kontakion1(l: Lang) = if (l == Lang.FR) kontakion1Fr else kontakion1It
    fun body(l: Lang) = if (l == Lang.FR) bodyFr else bodyIt
}

data class BibleBookMeta(
    val id: String, val nameFr: String, val nameIt: String,
    val testament: String, val chapters: Int
) {
    fun name(l: Lang) = if (l == Lang.FR) nameFr else nameIt
}

data class BibleBookFull(
    val id: String, val nameFr: String, val nameIt: String,
    val testament: String, val chapters: List<List<String>>
) {
    fun name(l: Lang) = if (l == Lang.FR) nameFr else nameIt
}

data class Chant(
    val id: String, val titleFr: String, val titleIt: String,
    val textRef: String?
) {
    fun title(l: Lang) = if (l == Lang.FR) titleFr else titleIt
}

data class Saint(
    val titleFr: String, val titleIt: String,
    val bioFr: String, val bioIt: String
) {
    fun title(l: Lang) = if (l == Lang.FR) titleFr else titleIt
    fun bio(l: Lang) = if (l == Lang.FR) bioFr else bioIt
}

data class Quote(val text: String, val src: String)

data class HolyWeekDay(
    val id: String, val titleFr: String, val titleIt: String,
    val fr: String, val it: String, val textRef: String? = null
) {
    fun title(l: Lang) = if (l == Lang.FR) titleFr else titleIt
    fun text(l: Lang) = if (l == Lang.FR) fr else it
}

/** Everything the engine derives for a given calendar day. */
data class DayInfo(
    val tone: Int,                 // 1..8, or 0 if special (Pascha/Bright Week)
    val heothinon: Int,            // 1..11 Sunday Matins Gospel cycle, 0 if N/A
    val weekdayKey: String,        // DIM..SAM
    val themeFr: String, val themeIt: String,
    val feastFr: String?, val feastIt: String?,
    val feastRank: Int,            // 0 none, 1 great, 2 feast, 3 memory
    val seasonKey: String,         // ete | hiver | careme
    val seasonLabelFr: String, val seasonLabelIt: String,
    val kathMatines: List<String>,
    val kathVepres: List<String>,
    val triodePrep: String? = null  // publicain|prodigue|apokreo|tyrini, null otherwise
) {
    fun theme(l: Lang) = if (l == Lang.FR) themeFr else themeIt
    fun feast(l: Lang) = if (l == Lang.FR) feastFr else feastIt
    fun seasonLabel(l: Lang) = if (l == Lang.FR) seasonLabelFr else seasonLabelIt
}
