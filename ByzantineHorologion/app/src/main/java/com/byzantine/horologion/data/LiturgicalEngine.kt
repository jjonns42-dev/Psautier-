package com.byzantine.horologion.data

import android.content.Context
import com.byzantine.horologion.util.JavaLiturgicalHelper
import org.json.JSONObject
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Derives the liturgical character of a civil date: tone (Octoechos),
 * weekday theme, season (for the kathismata cursus), and fixed feast.
 *
 * The tone is computed from the weekly cycle anchored on Pascha and is
 * INDICATIVE: during the Triodion/Pentecostarion and great feasts the
 * Typikon overrides the running tone. The UI labels it as such.
 */
class LiturgicalEngine(context: Context) {

    private val assets = context.assets
    private fun read(p: String) = assets.open(p).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private val weekdayThemes = JSONObject(read("calendar/weekday_themes.json"))
    private val fixedFeasts = JSONObject(read("calendar/fixed_feasts.json"))
    private val kathismata = JSONObject(read("calendar/kathismata.json"))

    private val weekdayKeys = arrayOf("DIM", "LUN", "MAR", "MER", "JEU", "VEN", "SAM")

    fun dayInfo(year: Int, month: Int, day: Int): DayInfo {
        // weekday
        val cal = GregorianCalendar(year, month - 1, day)
        val dow = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday..7=Saturday
        val wkey = weekdayKeys[dow - 1]
        val themeObj = weekdayThemes.getJSONObject(wkey)

        // Pascha governing this date (this year's, or previous year's if earlier)
        val paschaThis = JavaLiturgicalHelper.orthodoxPaschaGregorian(year)
        val before = JavaLiturgicalHelper.daysBetween(
            paschaThis[0], paschaThis[1], paschaThis[2], year, month, day
        ) < 0
        val govPascha = if (before) JavaLiturgicalHelper.orthodoxPaschaGregorian(year - 1) else paschaThis
        val daysSincePascha = JavaLiturgicalHelper.daysBetween(
            govPascha[0], govPascha[1], govPascha[2], year, month, day
        )

        // tone: Bright Week (0..6 days) special -> 0; Antipascha week = tone 1
        val tone: Int = if (daysSincePascha in 0L..6L) {
            0
        } else {
            val weeks = Math.floorDiv(daysSincePascha, 7L) // 1 at Antipascha
            (((weeks - 1) % 8 + 8) % 8 + 1).toInt()
        }

        // heothinon: the 11-week cycle of Sunday Matins resurrection Gospels,
        // beginning at 1 on Antipascha (Thomas Sunday) and advancing every
        // Sunday until the next Pascha. Only meaningful on Sundays; 0 otherwise.
        val heothinon: Int = if (wkey != "DIM" || daysSincePascha < 7L) {
            0
        } else {
            val weeks = Math.floorDiv(daysSincePascha, 7L)
            (((weeks - 1) % 11 + 11) % 11 + 1).toInt()
        }

        // season for kathismata
        val seasonKey = season(year, month, day, paschaThis)
        val seasonObj = kathismata.getJSONObject("seasons").getJSONObject(seasonKey)
        val matObj = seasonObj.getJSONObject("matines")
        val vepObj = seasonObj.getJSONObject("vepres")
        val kathMat = jsonArr(matObj, wkey)
        val kathVep = jsonArr(vepObj, wkey)

        // fixed feast
        val mmdd = String.format("%02d-%02d", month, day)
        var feastFr: String? = null; var feastIt: String? = null; var rank = 0
        if (fixedFeasts.has(mmdd)) {
            val f = fixedFeasts.getJSONObject(mmdd)
            feastFr = f.getString("fr"); feastIt = f.getString("it"); rank = f.getInt("rank")
        }

        return DayInfo(
            tone = tone,
            heothinon = heothinon,
            weekdayKey = wkey,
            themeFr = themeObj.getString("fr"),
            themeIt = themeObj.getString("it"),
            feastFr = feastFr, feastIt = feastIt, feastRank = rank,
            seasonKey = seasonKey,
            seasonLabelFr = seasonObj.getString("label_fr"),
            seasonLabelIt = seasonObj.getString("label_it"),
            kathMatines = kathMat,
            kathVepres = kathVep,
            triodePrep = triodePreparatorySunday(year, month, day, paschaThis)
        )
    }

    private fun jsonArr(o: JSONObject, key: String): List<String> {
        if (!o.has(key)) return emptyList()
        val a = o.getJSONArray(key)
        return (0 until a.length()).map { a.getString(it) }
    }

    /** Approximate season selector for the kathismata cursus. Indicative. */
    private fun season(y: Int, m: Int, d: Int, pascha: IntArray): String {
        // Lenten window: Pascha-48 (Clean Monday) .. Pascha-1 (Holy Saturday)
        val lentStart = JavaLiturgicalHelper.jdnToGregorian(
            JavaLiturgicalHelper.gregorianToJdn(pascha[0], pascha[1], pascha[2]) - 48
        )
        val holySat = JavaLiturgicalHelper.jdnToGregorian(
            JavaLiturgicalHelper.gregorianToJdn(pascha[0], pascha[1], pascha[2]) - 1
        )
        if (inRange(y, m, d, lentStart, holySat)) return "careme"

        // Nativity / Theophany window 20 Dec .. 14 Jan uses summer scheme
        if ((m == 12 && d >= 20) || (m == 1 && d <= 14)) return "ete"

        // Civil-date summer 22 Sep boundary: summer = before 22 Sep (and after Pascha season)
        // winter = 22 Sep .. 19 Dec and 15 Jan .. pre-Lent
        return if (m in 1..9 && !(m == 9 && d >= 22)) "ete" else "hiver"
    }

    private fun inRange(y: Int, m: Int, d: Int, lo: IntArray, hi: IntArray): Boolean {
        val j = JavaLiturgicalHelper.gregorianToJdn(y, m, d)
        val jl = JavaLiturgicalHelper.gregorianToJdn(lo[0], lo[1], lo[2])
        val jh = JavaLiturgicalHelper.gregorianToJdn(hi[0], hi[1], hi[2])
        return j in jl..jh
    }

    fun paschaOf(year: Int): IntArray = JavaLiturgicalHelper.orthodoxPaschaGregorian(year)

    /** Returns which of the four pre-Lenten Triodion Sundays this date is, or null.
     *  Offsets are days before Pascha: Publican&Pharisee=70, Prodigal Son=63,
     *  Meatfare/Last Judgment=56, Cheesefare/Forgiveness=49. Clean Monday is 48. */
    fun triodePreparatorySunday(y: Int, m: Int, d: Int, pascha: IntArray): String? {
        val j = JavaLiturgicalHelper.gregorianToJdn(y, m, d)
        val jp = JavaLiturgicalHelper.gregorianToJdn(pascha[0], pascha[1], pascha[2])
        return when (jp - j) {
            70 -> "publicain"
            63 -> "prodigue"
            56 -> "apokreo"
            49 -> "tyrini"
            else -> null
        }
    }

    private val holyWeekIds = arrayOf(
        "lundi_saint","mardi_saint","mercredi_saint","jeudi_saint","vendredi_saint","samedi_saint","paques"
    )

    /** Returns the Holy Week day id (lundi_saint..paques) for this date, or null. */
    fun holyWeekDayId(year: Int, month: Int, day: Int): String? {
        val pascha = JavaLiturgicalHelper.orthodoxPaschaGregorian(year)
        val diff = JavaLiturgicalHelper.daysBetween(pascha[0], pascha[1], pascha[2], year, month, day)
        // diff == 0 at Pascha; Holy Monday is diff == -6 ... Holy Saturday diff == -1
        if (diff < -6L || diff > 0L) return null
        val idx = (diff + 6L).toInt() // 0..6
        return holyWeekIds[idx]
    }

    /**
     * Name of the movable pre-Lenten or Lenten Sunday, if [year]-[month]-[day]
     * falls on one (Publican & Pharisee through Palm Sunday). Pure date
     * arithmetic from Pascha; valid for any year.
     */
    fun movableSundayName(year: Int, month: Int, day: Int): Pair<String, String>? {
        val pascha = JavaLiturgicalHelper.orthodoxPaschaGregorian(year)
        val diff = JavaLiturgicalHelper.daysBetween(pascha[0], pascha[1], pascha[2], year, month, day)
        return when (diff) {
            -70L -> "Dimanche du Publicain et du Pharisien" to "Domenica del Pubblicano e del Fariseo"
            -63L -> "Dimanche du Fils prodigue" to "Domenica del Figlio prodigo"
            -56L -> "Dimanche du Jugement dernier (Carnaval)" to "Domenica del Giudizio universale (Carnevale)"
            -49L -> "Dimanche du Pardon (fin du Carnaval)" to "Domenica del Perdono (fine del Carnevale)"
            -42L -> "1er dimanche de Carême — Triomphe de l'Orthodoxie" to "1ª domenica di Quaresima — Trionfo dell'Ortodossia"
            -35L -> "2e dimanche de Carême — Saint Grégoire Palamas" to "2ª domenica di Quaresima — San Gregorio Palamas"
            -28L -> "3e dimanche de Carême — Vénération de la Croix" to "3ª domenica di Quaresima — Venerazione della Croce"
            -21L -> "4e dimanche de Carême — Saint Jean Climaque" to "4ª domenica di Quaresima — San Giovanni Climaco"
            -14L -> "5e dimanche de Carême — Sainte Marie l'Égyptienne" to "5ª domenica di Quaresima — Santa Maria Egiziaca"
            -7L -> "Dimanche des Rameaux (entrée à Jérusalem)" to "Domenica delle Palme (ingresso a Gerusalemme)"
            else -> null
        }
    }
}
