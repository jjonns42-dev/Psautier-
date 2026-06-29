# Précieux Sang — Dévotion Quotidienne

Applicazione **Android in Kotlin (Jetpack Compose, Material 3)** ispirata
all'app *CEI Liturgia delle Ore*, con tema **rosso liturgico** (il rosso del
Preziosissimo Sangue) e oro.

Il contenuto è tratto dal libro *« Dévotion Quotidienne au Précieux Sang de
Jésus »* (Les Adorateurs du Précieux Sang). Le **categorie** seguono le
divisioni dell'indice del PDF; è incluso un **calendario** dei giorni speciali
descritti nel libro.

---

## Come aprire ed eseguire

1. Apri **Android Studio** (Hedgehog 2023.1+ o più recente).
2. `File ▸ Open…` → seleziona la cartella `PrecieuxSang`.
3. Lascia che Android Studio sincronizzi Gradle (scarica le dipendenze).
   - Se chiede del *Gradle wrapper*, conferma: lo rigenera automaticamente.
4. Premi ▶ **Run** su un emulatore o dispositivo (Android 7.0 / API 24+).

> Versioni usate: AGP 8.5.2, Kotlin 1.9.24, Compose BOM 2024.06, Gradle 8.7.

---

## Struttura delle schermate

- **Prières** (Home): l'elenco delle sezioni; ogni sezione si espande nelle
  singole preghiere. Tocca una preghiera per leggerne il testo **integrale**.
- **Veillée**: il *gioco* dell'Ora Santa di Gethsémani (timer + livelli).
- **Calendrier**: giorni speciali della devozione.
- **Dettaglio preghiera**: testo formattato con versetti (℣/℟), rubriche,
  canti, mistero per mistero.

## Contenuto delle preghiere

Tutti i testi sono ora **completi**, estratti dal PDF. I testi lunghi sono in
`app/src/main/assets/prayers/*.txt` con un piccolo markup, letti a runtime da
`data/PrayerParser.kt`. Le preghiere principali (Chapelet, Litanie,
Consécration, Comment prier, Promesses) sono codificate in `PrayerData.kt`.

Markup degli asset:
```
# Titolo interno      -> intestazione
> (indication)        -> rubrica
~ refrain             -> canto
L| testo / R| testo   -> versetto (officiante / assemblea)
N| 1. testo           -> elemento numerato
testo                 -> paragrafo
```

## Veillée de Gethsémani (il gioco)

Schermata **Veillée** (`ui/screens/GethsemaniGameScreen.kt`):

- **Timer di un'ora** (Ora Santa) con anello di avanzamento, play/pausa/reset.
- **Livelli**: ogni **4 venerdì consecutivi** in cui si compie l'Ora Santa →
  **+1 livello**, e così via **all'infinito**.
- Saltare un venerdì azzera solo il progresso del livello in corso; i livelli
  acquisiti restano.
- Pulsante "Marquer cette heure comme accomplie" (o completamento automatico a
  fine timer). Statistiche: serie attuale, record, ore totali.
- Progressi salvati con `SharedPreferences` (`data/ProgressStore.kt`).

La logica del venerdì-di-riferimento e dei livelli è in `ProgressStore.kt`
(modificabile, es. `FRIDAYS_PER_LEVEL`).

## Categorie (dalle divisioni del PDF)

1. **Introduction** — Adresse de Jean XXIII · Comment prier cette dévotion
2. **Dévotion Quotidienne** — Rosaire · Litanie B.V.M. · **Chapelet du Précieux
   Sang** · **Litanie du Précieux Sang** · **Consécration**
3. **Heures de Gethsémani** — Consolation · Adoration · Appels Angoissés ·
   Prières Mystiques
4. **Prières de Réparation** — Père Éternel · Couronne d'Épines · 3ᵉ vendredis…
5. **Prières de Protection** — St Michel · Invocation · Contrôle de la langue
6. **Prières Spéciales** — Nouvel Israël · Roses du Règne · Litanie des Saints
7. **Annexes** — Appendice · **Les Promesses**

Le preghiere principali hanno il testo integrale codificato; tutte le altre
caricano il testo completo dagli asset. Nessuna sezione è più solo descrittiva.

## Calendario — giorni speciali

Calcolati in `data/LiturgicalCalendar.kt`:

- **Heures de Gethsémani** — ogni **giovedì 23:00 → venerdì 03:00**
  (l'app indica se sono *in corso* e la prossima data).
- **Réparation du 3ᵉ vendredi** — il **terzo venerdì di ogni mese**.
- **Grand mois de Juillet**:
  - **1–9 luglio** — Neuvaine du Précieux Sang (nove Cori degli Angeli)
  - **13–15 luglio** — tre giorni della Santissima Trinità
  - **20–31 luglio** — dodici giorni per il Nouvel Israël
- **1 luglio** — Fête du Précieux Sang.

I prossimi eventi sono ordinati per data con conto alla rovescia.

---

## Come modificare i testi

I testi lunghi sono in `app/src/main/assets/prayers/<id>.txt` (markup sopra).
Le preghiere principali sono in
`app/src/main/java/com/preciousblood/devotion/data/PrayerData.kt`, con blocchi
tipizzati definiti in `data/PrayerModels.kt` e renderizzati da
`ui/components/PrayerBlockView.kt`.

---

## Tema (rosso)

`ui/theme/Color.kt` e `Theme.kt`. Colore primario `BloodRed = #8B0000`,
accento oro `#C9A227`, sfondo pergamena. Tema chiaro e scuro inclusi.

---

*Compilato da fonti approvate dall'editore del libro di preghiera. Quest'app
è uno strumento di lettura/preghiera, non una pubblicazione ufficiale.*
