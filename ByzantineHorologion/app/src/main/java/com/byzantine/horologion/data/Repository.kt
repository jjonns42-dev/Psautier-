package com.byzantine.horologion.data

import android.content.Context
import org.json.JSONObject

/** Loads liturgical content from /assets and caches parsed results. */
class Repository(private val context: Context) {

    private fun read(path: String): String =
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private val offices: Map<String, Office> by lazy { parseOffices() }
    val officeOrder: List<String> by lazy {
        val root = JSONObject(read("offices/offices.json"))
        val arr = root.getJSONArray("order")
        (0 until arr.length()).map { arr.getString(it) }
    }

    private val commonPrayers: JSONObject by lazy { JSONObject(read("propers/common_prayers.json")) }
    val akathists: List<Akathist> by lazy { parseAkathists() }
    val akathistNoteFr: String by lazy { akathistNote("fr") }
    val akathistNoteIt: String by lazy { akathistNote("it") }
    val chants: List<Chant> by lazy { parseChants() }
    val troparia: JSONObject by lazy { JSONObject(read("propers/troparia_resurrection.json")) }
    val kontakia: JSONObject by lazy { JSONObject(read("propers/kontakia_resurrection.json")) }
    private val hymnesTrinitairesTon: JSONObject by lazy { JSONObject(read("propers/hymnes_trinitaires_ton.json")) }
    private val trinikaDimanche: JSONObject by lazy { JSONObject(read("propers/trinika_dimanche_mesonyktikon.json")) }
    private val evlogitairesResurrection: JSONObject by lazy { JSONObject(read("propers/evlogitaires_resurrection.json")) }
    private val tropaireEpoux: JSONObject by lazy { JSONObject(read("propers/tropaire_epoux.json")) }
    private val priereEustrate: JSONObject by lazy { JSONObject(read("propers/priere_eustrate.json")) }
    private val canonTrinitaireTon1: JSONObject by lazy { JSONObject(read("propers/canon_trinitaire_ton1_metrophane.json")) }
    private val canonTrinitaireTon2: JSONObject by lazy { JSONObject(read("propers/canon_trinitaire_ton2.json")) }
    private val canonTrinitaireTon3: JSONObject by lazy { JSONObject(read("propers/canon_trinitaire_ton3.json")) }
    private val canonTrinitaireTon4: JSONObject by lazy { JSONObject(read("propers/canon_trinitaire_ton4.json")) }
    private val canonTrinitaireTon5: JSONObject by lazy { JSONObject(read("propers/canon_trinitaire_ton5.json")) }
    private val canonTrinitaireTon6: JSONObject by lazy { JSONObject(read("propers/canon_trinitaire_ton6.json")) }
    private val canonTrinitaireTon7: JSONObject by lazy { JSONObject(read("propers/canon_trinitaire_ton7.json")) }
    private val canonTrinitaireTon8: JSONObject by lazy { JSONObject(read("propers/canon_trinitaire_ton8.json")) }
    private val canonResurrectionTon1: JSONObject by lazy { JSONObject(read("propers/canon_resurrection_ton1.json")) }
    private val canonResurrectionTon2: JSONObject by lazy { JSONObject(read("propers/canon_resurrection_ton2.json")) }
    private val canonResurrectionTon3: JSONObject by lazy { JSONObject(read("propers/canon_resurrection_ton3.json")) }
    private val canonResurrectionTon4: JSONObject by lazy { JSONObject(read("propers/canon_resurrection_ton4.json")) }
    private val canonResurrectionTon5: JSONObject by lazy { JSONObject(read("propers/canon_resurrection_ton5.json")) }
    private val canonResurrectionTon6: JSONObject by lazy { JSONObject(read("propers/canon_resurrection_ton6.json")) }
    private val canonPaques: JSONObject by lazy { JSONObject(read("propers/canon_paques.json")) }
    private val triodePrep: JSONObject by lazy { JSONObject(read("propers/triode_dimanches_preparatoires.json")) }
    private val eothinaAnastasima: JSONObject by lazy { JSONObject(read("propers/eothina_anastasima.json")) }
    private val exapostilairesDominicaux: JSONObject by lazy { JSONObject(read("propers/exapostilaires_dominicaux.json")) }
    private val canonResurrectionTon7: JSONObject by lazy { JSONObject(read("propers/canon_resurrection_ton7.json")) }
    private val canonResurrectionTon8: JSONObject by lazy { JSONObject(read("propers/canon_resurrection_ton8.json")) }
    private val exapostilaireLundiTon1: JSONObject by lazy { JSONObject(read("propers/exapostilaire_lundi_anges_ton1.json")) }
    private val exapostilaireLundiTon2: JSONObject by lazy { JSONObject(read("propers/exapostilaire_lundi_anges_ton2.json")) }
    private val canonAngesLundiTon3: JSONObject by lazy { JSONObject(read("propers/canon_anges_lundi_ton3.json")) }
    private val canonAngesLundiTon4: JSONObject by lazy { JSONObject(read("propers/canon_anges_lundi_ton4.json")) }
    private val canonAngesLundiTon5: JSONObject by lazy { JSONObject(read("propers/canon_anges_lundi_ton5.json")) }
    private val canonAngesLundiTon6: JSONObject by lazy { JSONObject(read("propers/canon_anges_lundi_ton6.json")) }
    private val canonAngesLundiTon7: JSONObject by lazy { JSONObject(read("propers/canon_anges_lundi_ton7.json")) }
    private val canonAngesLundiTon8: JSONObject by lazy { JSONObject(read("propers/canon_anges_lundi_ton8.json")) }
    private val sticheronMardiTon1: JSONObject by lazy { JSONObject(read("propers/sticheron_mardi_prodrome_ton1.json")) }
    private val canonProdromeMardiTon2: JSONObject by lazy { JSONObject(read("propers/canon_prodrome_mardi_ton2.json")) }
    private val canonProdromeMardiTon3: JSONObject by lazy { JSONObject(read("propers/canon_prodrome_mardi_ton3.json")) }
    private val canonProdromeMardiTon4: JSONObject by lazy { JSONObject(read("propers/canon_prodrome_mardi_ton4.json")) }
    private val canonProdromeMardiTon5: JSONObject by lazy { JSONObject(read("propers/canon_prodrome_mardi_ton5.json")) }
    private val canonProdromeMardiTon6: JSONObject by lazy { JSONObject(read("propers/canon_prodrome_mardi_ton6.json")) }
    private val canonProdromeMardiTon7: JSONObject by lazy { JSONObject(read("propers/canon_prodrome_mardi_ton7.json")) }
    private val canonProdromeMardiTon8: JSONObject by lazy { JSONObject(read("propers/canon_prodrome_mardi_ton8.json")) }
    private val tropaireSamediTon1: JSONObject by lazy { JSONObject(read("propers/tropaire_samedi_defunts_ton1.json")) }
    private val tropaireSamediTon2: JSONObject by lazy { JSONObject(read("propers/tropaire_samedi_defunts_ton2.json")) }
    private val tropaireSamediTon3: JSONObject by lazy { JSONObject(read("propers/tropaire_samedi_defunts_ton3.json")) }
    private val tropaireSamediTon4: JSONObject by lazy { JSONObject(read("propers/tropaire_samedi_defunts_ton4.json")) }
    private val tropaireSamediTon5: JSONObject by lazy { JSONObject(read("propers/tropaire_samedi_defunts_ton5.json")) }
    private val tropaireSamediTon6: JSONObject by lazy { JSONObject(read("propers/tropaire_samedi_defunts_ton6.json")) }
    private val tropaireSamediTon7: JSONObject by lazy { JSONObject(read("propers/tropaire_samedi_defunts_ton7.json")) }
    private val tropaireSamediTon8: JSONObject by lazy { JSONObject(read("propers/tropaire_samedi_defunts_ton8.json")) }
    private val sticheronMercrediTon1: JSONObject by lazy { JSONObject(read("propers/sticheron_mercredi_croix_ton1.json")) }
    private val sticheronMercrediTon2: JSONObject by lazy { JSONObject(read("propers/sticheron_mercredi_croix_ton2.json")) }
    private val sticheronMercrediTon3: JSONObject by lazy { JSONObject(read("propers/sticheron_mercredi_croix_ton3.json")) }
    private val sticheronMercrediTon4: JSONObject by lazy { JSONObject(read("propers/sticheron_mercredi_croix_ton4.json")) }
    private val sticheronMercrediTon5: JSONObject by lazy { JSONObject(read("propers/sticheron_mercredi_croix_ton5.json")) }
    private val sticheronMercrediTon6: JSONObject by lazy { JSONObject(read("propers/sticheron_mercredi_croix_ton6.json")) }
    private val sticheronMercrediTon7: JSONObject by lazy { JSONObject(read("propers/sticheron_mercredi_croix_ton7.json")) }
    private val sticheronMercrediTon8: JSONObject by lazy { JSONObject(read("propers/sticheron_mercredi_croix_ton8.json")) }
    private val sticheronJeudiTon1: JSONObject by lazy { JSONObject(read("propers/sticheron_jeudi_apotres_ton1.json")) }
    private val sticheronJeudiTon2: JSONObject by lazy { JSONObject(read("propers/sticheron_jeudi_apotres_ton2.json")) }
    private val sticheronJeudiTon3: JSONObject by lazy { JSONObject(read("propers/sticheron_jeudi_apotres_ton3.json")) }
    private val sticheronJeudiTon4: JSONObject by lazy { JSONObject(read("propers/sticheron_jeudi_apotres_ton4.json")) }
    private val sticheronJeudiTon5: JSONObject by lazy { JSONObject(read("propers/sticheron_jeudi_apotres_ton5.json")) }
    private val sticheronJeudiTon6: JSONObject by lazy { JSONObject(read("propers/sticheron_jeudi_apotres_ton6.json")) }
    private val sticheronJeudiTon7: JSONObject by lazy { JSONObject(read("propers/sticheron_jeudi_apotres_ton7.json")) }
    private val sticheronJeudiTon8: JSONObject by lazy { JSONObject(read("propers/sticheron_jeudi_apotres_ton8.json")) }
    private val sticheronVendrediTon1: JSONObject by lazy { JSONObject(read("propers/sticheron_vendredi_croix_ton1.json")) }
    private val sticheronVendrediTon2: JSONObject by lazy { JSONObject(read("propers/sticheron_vendredi_croix_ton2.json")) }
    private val sticheronVendrediTon3: JSONObject by lazy { JSONObject(read("propers/sticheron_vendredi_croix_ton3.json")) }
    private val sticheronVendrediTon4: JSONObject by lazy { JSONObject(read("propers/sticheron_vendredi_croix_ton4.json")) }
    private val sticheronVendrediTon5: JSONObject by lazy { JSONObject(read("propers/sticheron_vendredi_croix_ton5.json")) }
    private val sticheronVendrediTon6: JSONObject by lazy { JSONObject(read("propers/sticheron_vendredi_croix_ton6.json")) }
    private val sticheronVendrediTon7: JSONObject by lazy { JSONObject(read("propers/sticheron_vendredi_croix_ton7.json")) }
    private val sticheronVendrediTon8: JSONObject by lazy { JSONObject(read("propers/sticheron_vendredi_croix_ton8.json")) }

    fun office(id: String): Office? = offices[id]
    fun allOffices(): List<Office> = officeOrder.mapNotNull { offices[it] }

    /** Resolve a common-prayer reference to its text in the chosen language. */
    fun commonPrayer(ref: String, lang: Lang): String? {
        if (!commonPrayers.has(ref)) return null
        val o = commonPrayers.getJSONObject(ref)
        return o.optString(if (lang == Lang.FR) "fr" else "it").ifBlank { null }
    }

    fun jesusPrayer(): Triple<String, String, String> {
        val o = JSONObject(read("propers/jesus_prayer.json"))
        return Triple(o.getString("el"), o.getString("fr"), o.getString("it"))
    }

    fun resurrectionTroparion(tone: Int, lang: Lang): String? {
        val key = tone.toString()
        if (!troparia.has(key)) return null
        return troparia.getJSONObject(key).optString(if (lang == Lang.FR) "fr" else "it")
    }

    fun resurrectionKontakion(tone: Int, lang: Lang): String? {
        val key = tone.toString()
        if (!kontakia.has(key)) return null
        return kontakia.getJSONObject(key).optString(if (lang == Lang.FR) "fr" else "it")
    }

    /** The 8 fixed Trinitarian hymns of Orthros, one per Octoechos tone (1..8). */
    fun trinitarianHymnByTone(tone: Int, lang: Lang): String? {
        val key = tone.toString()
        if (!hymnesTrinitairesTon.has(key)) return null
        return hymnesTrinitairesTon.getJSONObject(key).optString(if (lang == Lang.FR) "fr" else "it")
    }

    fun sundayMesonyktikonTrinika(lang: Lang): String? =
        trinikaDimanche.optString(if (lang == Lang.FR) "fr" else "it").ifBlank { null }

    fun resurrectionEvlogitaria(lang: Lang): String? =
        evlogitairesResurrection.optString(if (lang == Lang.FR) "fr" else "it").ifBlank { null }

    fun bridegroomTroparion(lang: Lang): String? =
        tropaireEpoux.optString(if (lang == Lang.FR) "fr" else "it").ifBlank { null }

    fun eustratiusPrayer(lang: Lang): String? =
        priereEustrate.optString(if (lang == Lang.FR) "fr" else "it").ifBlank { null }

    /** Mesonyktikon Trinitarian Canon — tones 1-4 translated so far. */
    fun trinitarianCanonText(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        if (tone == 1) {
            val ode1 = canonTrinitaireTon1.getJSONObject("ode1").optString(key)
            val ode3 = canonTrinitaireTon1.getJSONObject("ode3").optString(key)
            val ode4 = canonTrinitaireTon1.getJSONObject("ode4").optString(key)
            val kath = canonTrinitaireTon1.getJSONObject("kathisma").optString(key)
            return "Ode 1.\n$ode1\n\nOde 3.\n$ode3\n\nOde 4.\n$ode4\n\n" +
                (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath
        }
        if (tone == 2) {
            val ode4 = canonTrinitaireTon2.getJSONObject("ode4").optString(key)
            val ode5 = canonTrinitaireTon2.getJSONObject("ode5").optString(key)
            val kath = canonTrinitaireTon2.getJSONObject("kathisma").optString(key)
            return "Ode 4.\n$ode4\n\nOde 5.\n$ode5\n\n" +
                (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath
        }
        if (tone == 3) {
            val ode1 = canonTrinitaireTon3.getJSONObject("ode1").optString(key)
            val kath = canonTrinitaireTon3.getJSONObject("kathisma").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath
        }
        if (tone == 4) {
            val ode1 = canonTrinitaireTon4.getJSONObject("ode1").optString(key)
            val ode3 = canonTrinitaireTon4.getJSONObject("ode3").optString(key)
            val kath = canonTrinitaireTon4.getJSONObject("kathisma").optString(key)
            val theo = canonTrinitaireTon4.getJSONObject("theotokion").optString(key)
            return "Ode 1.\n$ode1\n\nOde 3.\n$ode3\n\n" +
                (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath +
                "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo
        }
        if (tone == 5) {
            val ode1 = canonTrinitaireTon5.getJSONObject("ode1").optString(key)
            val ode3 = canonTrinitaireTon5.getJSONObject("ode3").optString(key)
            val kath = canonTrinitaireTon5.getJSONObject("kathisma").optString(key)
            val theo1 = canonTrinitaireTon5.getJSONObject("theotokion_ode1").optString(key)
            val theo3 = canonTrinitaireTon5.getJSONObject("theotokion_ode3").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo1 +
                "\n\nOde 3.\n$ode3\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo3 +
                "\n\n" + (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath
        }
        if (tone == 6) {
            val ode1 = canonTrinitaireTon6.getJSONObject("ode1").optString(key)
            val ode3 = canonTrinitaireTon6.getJSONObject("ode3").optString(key)
            val kath = canonTrinitaireTon6.getJSONObject("kathisma").optString(key)
            val theo1 = canonTrinitaireTon6.getJSONObject("theotokion_ode1").optString(key)
            val theo3 = canonTrinitaireTon6.getJSONObject("theotokion_ode3").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo1 +
                "\n\nOde 3.\n$ode3\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo3 +
                "\n\n" + (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath
        }
        if (tone == 7) {
            val ode1 = canonTrinitaireTon7.getJSONObject("ode1").optString(key)
            val ode3 = canonTrinitaireTon7.getJSONObject("ode3").optString(key)
            val kath = canonTrinitaireTon7.getJSONObject("kathisma").optString(key)
            val theoK = canonTrinitaireTon7.getJSONObject("theotokion_kathisma").optString(key)
            val theo1 = canonTrinitaireTon7.getJSONObject("theotokion_ode1").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo1 +
                "\n\nOde 3.\n$ode3\n\n" + (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath +
                "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theoK
        }
        if (tone == 8) {
            val ode1 = canonTrinitaireTon8.getJSONObject("ode1").optString(key)
            val ode3 = canonTrinitaireTon8.getJSONObject("ode3").optString(key)
            val kath = canonTrinitaireTon8.getJSONObject("kathisma").optString(key)
            val theoK = canonTrinitaireTon8.getJSONObject("theotokion_kathisma").optString(key)
            val theo1 = canonTrinitaireTon8.getJSONObject("theotokion_ode1").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo1 +
                "\n\nOde 3.\n$ode3\n\n" + (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath +
                "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theoK
        }
        return null
    }
    fun resurrectionCanonText(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        if (tone == 1) {
            val ode1 = canonResurrectionTon1.getJSONObject("ode1").optString(key)
            val kath = canonResurrectionTon1.getJSONObject("kathisma").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Cathisme.\n" else "Kathisma.\n") + kath
        }
        if (tone == 2) {
            val ode1 = canonResurrectionTon2.getJSONObject("ode1").optString(key)
            return "Ode 1.\n$ode1"
        }
        if (tone == 3) {
            val ode1 = canonResurrectionTon3.getJSONObject("ode1").optString(key)
            return "Ode 1.\n$ode1"
        }
        if (tone == 4) {
            val ode1 = canonResurrectionTon4.getJSONObject("ode1").optString(key)
            val theo = canonResurrectionTon4.getJSONObject("theotokion").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo
        }
        if (tone == 5) {
            val ode1 = canonResurrectionTon5.getJSONObject("ode1").optString(key)
            val theo = canonResurrectionTon5.getJSONObject("theotokion").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo
        }
        if (tone == 6) {
            val ode1 = canonResurrectionTon6.getJSONObject("ode1").optString(key)
            val theo = canonResurrectionTon6.getJSONObject("theotokion").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo
        }
        if (tone == 7) {
            val ode1 = canonResurrectionTon7.getJSONObject("ode1").optString(key)
            val theo = canonResurrectionTon7.getJSONObject("theotokion").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo
        }
        if (tone == 8) {
            val ode1 = canonResurrectionTon8.getJSONObject("ode1").optString(key)
            val theo = canonResurrectionTon8.getJSONObject("theotokion").optString(key)
            return "Ode 1.\n$ode1\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo
        }
        return null
    }

    /** Monday Exapostilarion to the Angels — tones 1 and 2 translated so far. */
    fun mondayAngelsExapostilarion(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        if (tone == 1) return exapostilaireLundiTon1.optString(key).ifBlank { null }
        if (tone == 2) return exapostilaireLundiTon2.optString(key).ifBlank { null }
        return null
    }

    /** Expanded Paschal Canon (John Damascene): Hypakoe, Kontakion, Odes 1, 3-9 (complete). */
    fun paschaCanonText(lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        val ode1 = canonPaques.optJSONObject("ode1")?.optString(key) ?: return null
        val hyp = canonPaques.optJSONObject("hypakoe")?.optString(key).orEmpty()
        val kont = canonPaques.optJSONObject("kontakion")?.optString(key).orEmpty()
        val hL = if (lang == Lang.FR) "Hypakoè." else "Ipakoì."
        val kL = if (lang == Lang.FR) "Kontakion." else "Kontakion."
        val sb = StringBuilder()
        sb.append("Ode 1.\n").append(ode1).append("\n\n").append(hL).append("\n").append(hyp)
        sb.append("\n\n").append(kL).append("\n").append(kont)
        for (n in listOf(3, 4, 5, 6, 7, 8, 9)) {
            val ode = canonPaques.optJSONObject("ode$n")?.optString(key)
            if (!ode.isNullOrBlank()) sb.append("\n\nOde $n.\n").append(ode)
        }
        return sb.toString()
    }

    /** Pre-Lenten Triodion content for one of the four preparatory Sundays:
     *  "publicain", "prodigue", "apokreo", "tyrini". */
    fun triodePreparatoryText(which: String, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        val parts = when (which) {
            "publicain" -> listOf("publicain_kontakion", "publicain_doxastikon")
            "prodigue" -> listOf("prodigue_kontakion", "prodigue_doxastikon")
            "apokreo" -> listOf("apokreo_apolytikion", "apokreo_idiomelon")
            "tyrini" -> listOf("tyrini_sticheron")
            else -> return null
        }
        val sb = StringBuilder()
        for (p in parts) {
            val t = triodePrep.optJSONObject(p)?.optString(key)
            if (!t.isNullOrBlank()) {
                if (sb.isNotEmpty()) sb.append("\n\n")
                sb.append(t)
            }
        }
        return if (sb.isEmpty()) null else sb.toString()
    }

    /** Sunday Matins Eothinon Doxastikon (dawn Resurrection hymn), n=1..11.
     *  Only n=1,2,4,5 translated so far. */
    fun eothinonText(n: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        val roman = mapOf(1 to "I", 2 to "II", 4 to "IV", 5 to "V")[n] ?: return null
        return eothinaAnastasima.optJSONObject(roman)?.optString(key)?.ifBlank { null }
    }

    /** Sunday closing Exapostilarion paired with the Eothinon, n=1..11.
     *  Only n=1,2,5 translated so far. */
    fun sundayExapostilarionText(n: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        val roman = mapOf(1 to "I", 2 to "II", 5 to "V")[n] ?: return null
        return exapostilairesDominicaux.optJSONObject(roman)?.optString(key)?.ifBlank { null }
    }

    /** Monday Angels Canon excerpt — tones 3, 4, 5, 7, 8 translated so far.
     *  (Tone 6 pending: the source OCR for its Monday section is too degraded to translate
     *  reliably; it will be revisited with a cleaner source in a future session.) */
    fun mondayAngelsCanonText(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        val obj = when (tone) {
            3 -> canonAngesLundiTon3
            4 -> canonAngesLundiTon4
            5 -> canonAngesLundiTon5
            6 -> canonAngesLundiTon6
            7 -> canonAngesLundiTon7
            8 -> canonAngesLundiTon8
            else -> return null
        }
        val main = obj.optString(key).ifBlank { null } ?: return null
        val theoKey = "theotokion1" + (if (lang == Lang.IT) "_it" else "")
        val theo1 = obj.optString(theoKey)
        val martKey = "martyrikon" + (if (lang == Lang.IT) "_it" else "")
        val mart = obj.optString(martKey)
        val theo2Key = "theotokion2" + (if (lang == Lang.IT) "_it" else "")
        val theo2 = obj.optString(theo2Key)
        val theoKeySimple = "theotokion" + (if (lang == Lang.IT) "_it" else "")
        val theoSimple = obj.optString(theoKeySimple)
        var result = main
        if (theo1.isNotBlank()) result += "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo1
        if (mart.isNotBlank()) result += "\n\n" + (if (lang == Lang.FR) "Martyrikon.\n" else "Martyrikon.\n") + mart
        if (theo2.isNotBlank()) result += "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo2
        if (theoSimple.isNotBlank()) result += "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theoSimple
        return result
    }

    /** Tuesday Forerunner sticheron/canon — tones 1 and 2. */
    fun tuesdayForerunnerSticheron(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        if (tone == 1) return sticheronMardiTon1.optString(key).ifBlank { null }
        if (tone == 2) {
            val main = canonProdromeMardiTon2.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = canonProdromeMardiTon2.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 3) {
            val main = canonProdromeMardiTon3.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = canonProdromeMardiTon3.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 4) {
            val main = canonProdromeMardiTon4.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = canonProdromeMardiTon4.optString(theoKey)
            val martKey = "martyrikon" + (if (lang == Lang.IT) "_it" else "")
            val mart = canonProdromeMardiTon4.optString(martKey)
            var result = main
            if (theo.isNotBlank()) result += "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo
            if (mart.isNotBlank()) result += "\n\n" + (if (lang == Lang.FR) "Martyrikon.\n" else "Martyrikon.\n") + mart
            return result
        }
        if (tone == 5) {
            val main = canonProdromeMardiTon5.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = canonProdromeMardiTon5.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 6) return canonProdromeMardiTon6.optString(key).ifBlank { null }
        if (tone == 7) {
            val main = canonProdromeMardiTon7.optString(key).ifBlank { null } ?: return null
            val theo1 = canonProdromeMardiTon7.optString("theotokion1" + (if (lang == Lang.IT) "_it" else ""))
            val theo2 = canonProdromeMardiTon7.optString("theotokion2" + (if (lang == Lang.IT) "_it" else ""))
            var r = main
            if (theo1.isNotBlank()) r += "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo1
            if (theo2.isNotBlank()) r += "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo2
            return r
        }
        if (tone == 8) {
            val main = canonProdromeMardiTon8.optString(key).ifBlank { null } ?: return null
            val theo1 = canonProdromeMardiTon8.optString("theotokion1" + (if (lang == Lang.IT) "_it" else ""))
            val theo2 = canonProdromeMardiTon8.optString("theotokion2" + (if (lang == Lang.IT) "_it" else ""))
            var r = main
            if (theo1.isNotBlank()) r += "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo1
            if (theo2.isNotBlank()) r += "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo2
            return r
        }
        return null
    }

    /** Saturday funeral troparion — tones 1 and 2. */
    fun saturdayFuneralTroparion(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        if (tone == 1) return tropaireSamediTon1.optString(key).ifBlank { null }
        if (tone == 2) return tropaireSamediTon2.optString(key).ifBlank { null }
        if (tone == 3) return tropaireSamediTon3.optString(key).ifBlank { null }
        if (tone == 4) {
            val main = tropaireSamediTon4.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = tropaireSamediTon4.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 5) {
            val main = tropaireSamediTon5.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = tropaireSamediTon5.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 6) {
            val main = tropaireSamediTon6.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = tropaireSamediTon6.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 7) {
            val main = tropaireSamediTon7.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = tropaireSamediTon7.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 8) {
            val main = tropaireSamediTon8.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = tropaireSamediTon8.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        return null
    }

    /** Wednesday Cross sticheron — tones 1 and 2. */
    fun wednesdayCrossSticheron(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        if (tone == 1) return sticheronMercrediTon1.optString(key).ifBlank { null }
        if (tone == 2) return sticheronMercrediTon2.optString(key).ifBlank { null }
        if (tone == 3) return sticheronMercrediTon3.optString(key).ifBlank { null }
        if (tone == 4) {
            val main = sticheronMercrediTon4.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronMercrediTon4.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 5) {
            val main = sticheronMercrediTon5.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronMercrediTon5.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 6) {
            val main = sticheronMercrediTon6.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronMercrediTon6.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 7) {
            val main = sticheronMercrediTon7.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronMercrediTon7.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 8) {
            val main = sticheronMercrediTon8.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronMercrediTon8.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        return null
    }

    /** Thursday Apostles sticheron — tones 1 and 2. */
    fun thursdayApostlesSticheron(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        if (tone == 1) return sticheronJeudiTon1.optString(key).ifBlank { null }
        if (tone == 2) {
            val main = sticheronJeudiTon2.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronJeudiTon2.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 3) return sticheronJeudiTon3.optString(key).ifBlank { null }
        if (tone == 4) {
            val main = sticheronJeudiTon4.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronJeudiTon4.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 5) {
            val main = sticheronJeudiTon5.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronJeudiTon5.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 6) {
            val main = sticheronJeudiTon6.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronJeudiTon6.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 7) {
            val main = sticheronJeudiTon7.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronJeudiTon7.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        if (tone == 8) {
            val main = sticheronJeudiTon8.optString(key).ifBlank { null } ?: return null
            val theoKey = "theotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronJeudiTon8.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Théotokion.\n" else "Teotochio.\n") + theo else main
        }
        return null
    }

    /** Friday Cross sticheron — tones 1 and 2. */
    fun fridayCrossSticheron(tone: Int, lang: Lang): String? {
        val key = if (lang == Lang.FR) "fr" else "it"
        if (tone == 1) return sticheronVendrediTon1.optString(key).ifBlank { null }
        if (tone == 2) {
            val main = sticheronVendrediTon2.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronVendrediTon2.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 3) {
            val main = sticheronVendrediTon3.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronVendrediTon3.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 4) {
            val main = sticheronVendrediTon4.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronVendrediTon4.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 5) {
            val main = sticheronVendrediTon5.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronVendrediTon5.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 6) {
            val main = sticheronVendrediTon6.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronVendrediTon6.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 7) {
            val main = sticheronVendrediTon7.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronVendrediTon7.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        if (tone == 8) {
            val main = sticheronVendrediTon8.optString(key).ifBlank { null } ?: return null
            val theoKey = "stauротheotokion" + (if (lang == Lang.IT) "_it" else "")
            val theo = sticheronVendrediTon8.optString(theoKey)
            return if (theo.isNotBlank()) main + "\n\n" + (if (lang == Lang.FR) "Stavrothéotokion.\n" else "Stavroteotochio.\n") + theo else main
        }
        return null
    }


    /**
     * Generic lookup used by screens that only have a bare ref string (e.g. a
     * Holy Week text_ref): tries the common-prayers pool first, then each of
     * the standalone fixed-content files added from the 1851 Horologion.
     */
    fun resolveAnyText(ref: String, lang: Lang): String? {
        commonPrayer(ref, lang)?.let { return it }
        if (ref == "tropaire_epoux") return bridegroomTroparion(lang)
        if (ref == "priere_eustrate") return eustratiusPrayer(lang)
        if (ref == "evlogitaires_resurrection") return resurrectionEvlogitaria(lang)
        if (ref == "trinika_dimanche_mesonyktikon") return sundayMesonyktikonTrinika(lang)
        return null
    }

    fun basilGrandes(): String = read("basil/grandes_regles.txt")
    fun basilPetites(): String = read("basil/petites_regles.txt")

    // ---- Holy Week structure ----
    val holyWeek: List<HolyWeekDay> by lazy {
        val root = JSONObject(read("calendar/holy_week.json"))
        val a = root.getJSONArray("days")
        (0 until a.length()).map {
            val d = a.getJSONObject(it)
            HolyWeekDay(d.getString("id"), d.getString("title_fr"), d.getString("title_it"),
                d.getString("fr"), d.getString("it"), d.optString("text_ref").ifBlank { null })
        }
    }

    /** The Palm Sunday entry (kept separate: it is not part of Holy Week proper). */
    val palmSunday: HolyWeekDay? by lazy {
        val root = JSONObject(read("calendar/holy_week.json"))
        if (!root.has("rameaux")) return@lazy null
        val d = root.getJSONObject("rameaux")
        HolyWeekDay("rameaux", d.getString("title_fr"), d.getString("title_it"), "", "",
            d.optString("text_ref").ifBlank { null })
    }

    // ---- saint of the day ----
    private val saints: JSONObject by lazy { JSONObject(read("calendar/saints.json")).getJSONObject("items") }
    fun saintOfDay(month: Int, day: Int): Saint? {
        val key = String.format("%02d-%02d", month, day)
        if (!saints.has(key)) return null
        val o = saints.getJSONObject(key)
        return Saint(o.getString("title_fr"), o.getString("title_it"),
            o.getString("fr"), o.getString("it"))
    }

    // ---- quote pools ----
    private val quotes: JSONObject by lazy { JSONObject(read("game/quotes.json")) }
    fun quotePool(name: String): List<Quote> {
        if (!quotes.has(name)) return emptyList()
        val a = quotes.getJSONArray(name)
        return (0 until a.length()).map {
            val q = a.getJSONObject(it); Quote(q.getString("text"), q.getString("src"))
        }
    }

    // ---- parsers ----
    private fun parseOffices(): Map<String, Office> {
        val root = JSONObject(read("offices/offices.json"))
        val items = root.getJSONObject("items")
        val map = LinkedHashMap<String, Office>()
        for (id in items.keys()) {
            val o = items.getJSONObject(id)
            val stepsArr = o.getJSONArray("steps")
            val steps = (0 until stepsArr.length()).map { i ->
                val s = stepsArr.getJSONObject(i)
                OfficeStep(
                    kind = s.getString("kind"),
                    role = s.optString("role", "tous"),
                    ref = s.optString("ref").ifBlank { null },
                    fr = s.optString("fr").ifBlank { null },
                    it = s.optString("it").ifBlank { null },
                    rubricFr = s.optString("rubric_fr").ifBlank { null },
                    rubricIt = s.optString("rubric_it").ifBlank { null }
                )
            }
            map[id] = Office(id, o.getString("title_fr"), o.getString("title_it"), steps)
        }
        return map
    }

    private fun parseAkathists(): List<Akathist> {
        val root = JSONObject(read("akathists/akathists.json"))
        val arr = root.getJSONArray("items")
        return (0 until arr.length()).map { i ->
            val a = arr.getJSONObject(i)
            Akathist(
                a.getString("id"), a.getString("icon"),
                a.getString("title_fr"), a.getString("title_it"),
                a.getString("refrain_fr"), a.getString("refrain_it"),
                a.getString("kontakion1_fr"), a.getString("kontakion1_it"),
                a.getString("body_fr"), a.getString("body_it")
            )
        }
    }

    private fun akathistNote(l: String): String =
        JSONObject(read("akathists/akathists.json")).getJSONObject("note_standing").getString(l)

    // ---- Bible (full Crampon, per-book on demand) ----
    val bibleIndex: List<BibleBookMeta> by lazy {
        val root = JSONObject(read("bible/index.json"))
        val a = root.getJSONArray("books")
        (0 until a.length()).map {
            val b = a.getJSONObject(it)
            BibleBookMeta(b.getString("id"), b.getString("fr"), b.getString("it"),
                b.getString("testament"), b.getInt("chapters"))
        }
    }
    val bibleVersion: String by lazy {
        JSONObject(read("bible/index.json")).optString("version", "Crampon 1923")
    }
    fun bibleBook(id: String): BibleBookFull {
        val o = JSONObject(read("bible/books/$id.json"))
        val ch = o.getJSONArray("chapters")
        val chapters = (0 until ch.length()).map { ci ->
            val v = ch.getJSONArray(ci)
            (0 until v.length()).map { v.getString(it) }
        }
        return BibleBookFull(o.getString("id"), o.getString("fr"), o.getString("it"),
            o.getString("testament"), chapters)
    }

    private fun parseChants(): List<Chant> {
        val root = JSONObject(read("chants/chants.json"))
        val arr = root.getJSONArray("items")
        return (0 until arr.length()).map { i ->
            val c = arr.getJSONObject(i)
            Chant(c.getString("id"), c.getString("title_fr"), c.getString("title_it"),
                c.optString("text_ref").ifBlank { null })
        }
    }
}
