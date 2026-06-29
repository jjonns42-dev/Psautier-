# Liturgia Monastica

Application Android (Kotlin / Jetpack Compose) pour l'**Office monastique bénédictin**,
en français et en italien, avec une section biblique et deux « jeux » de prière :
la **Bougie** et les **1000 Jours**.

L'esthétique (parchemin, encre bénédictine, or, vert) s'inspire des icônes fournies :
la *Madone du Silence*, *saint Séraphin de Sarov* et la *médaille de saint Benoît*.

---

## Ce qui est inclus et fonctionne

- **Liturgie des Heures** — les 8 heures monastiques (Vigiles, Laudes, Prime, Tierce,
  Sexte, None, Vêpres, Complies). L'office est assemblé automatiquement pour le jour
  courant : ouverture, hymne, psalmodie, cantique évangélique, Notre Père, conclusion.
  - **Complies** : ordinaire complet et authentique (hymne *Te lucis ante terminum*,
    Confiteor monastique nommant *« beato Patre nostro Benedicto »*, psaumes 4, 91, 134,
    capitule *Jr 14, 9*, répons *In manus tuas*, *Nunc dimittis*, oraison *Visita quæsumus*,
    antienne finale *Salve Regina*) — texte latin + traduction française, recoupé par OCR
    du *Breviarium Monasticum, Pars II*.
  - **Hymnes** des heures en latin traditionnel avec traduction (*Iam lucis*, *Nunc Sancte*,
    *Rector potens*, *Rerum Deus*, *O lux beata Trinitas*, *Splendor paternæ*, *Te lucis*).
- **Bible** — texte intégral des **150 Psaumes** et des **4 Évangiles**
  (Matthieu, Marc, Luc, Jean) d'après la **Bible Crampon 1923**.
- **La Bougie** 🕯️ — prier chaque jour pendant une semaine (timer). 7 jours → on monte
  d'un niveau (+5 min) **ou** on choisit de rester. Un jour manqué → retour au début du
  niveau. À partir de 30 min, une **citation de saint Benoît** (tirée de sa Règle) s'affiche.
  Fond : la *Madone du Silence*.
- **1000 Jours** — chaque jour (la nuit), prier 30 min ou 1 h avec l'intention de
  devenir un saint, selon **une seule dévotion choisie et verrouillée**. Chaque jour il faut
  cocher « Je fais ce jour ». Un jour manqué → on recommence au jour 0. 19 dévotions avec
  leurs **promesses** (Rosaire, Divine Miséricorde, Saintes Plaies, 7 Douleurs, Larmes de
  Marie, Couronne angélique, Manteau de saint Joseph, etc.). Fond : *saint Séraphin de Sarov*.
- **Bilingue** (FR / IT) et **mode nuit** (Modalità Notturna), commutables en haut de l'écran.

---

## Sources utilisées

| Source fournie | Utilisation |
|---|---|
| **Bible Crampon 1923** (texte) | 150 Psaumes + 4 Évangiles (Bible et psalmodie de l'office) |
| **Règle de saint Benoît** (Solesmes) | citations de la Bougie (sélection authentique) |
| **Breviarium Monasticum, Pars II** (scan) | Complies + hymnes (OCR + texte liturgique traditionnel) |
| **Antiphonale Monasticum 1934** (scan) | référence (non extractible en texte : images) |
| Icônes (Madone du Silence, st Séraphin, médaille st Benoît) | fonds et identité visuelle |

> Les grands bréviaires fournis sont des **scans d'images** sans texte sélectionnable.
> Les parties latines (Complies, hymnes) ont été reconstituées à partir du texte liturgique
> traditionnel **recoupé** par OCR de la source. Le **psautier complet** et les **propres
> italiens (CEI)** demandent des sources que je n'avais pas en texte (les bréviaires sont
> scannés ; le texte CEI est sous droits et n'était pas fourni). Tout est donc **modifiable**.

---

## Ce qui reste à vérifier / compléter (honnêteté)

- **Distribution du psautier** : `app/src/main/assets/office_structure.json` → clé
  `distribution`. C'est le schéma bénédictin traditionnel (numérotation hébraïque, comme la
  CEI). Vous pouvez corriger librement chaque liste de psaumes par heure et par jour pour la
  faire correspondre exactement à votre bréviaire.
- **Textes italiens** : l'interface est entièrement bilingue, mais le **corps des psaumes et
  de la Bible est en français** (source Crampon fournie). Pour ajouter l'italien, dupliquez
  `psalms_fr.json` en `psalms_it.json` (même schéma) — l'app est prête à les recevoir.
- **Antiennes propres et propre du temps/des saints** : non inclus (demandent l'OCR fiable
  des bréviaires scannés). Le cadre de l'office les accepte facilement en JSON.

---

## Ouvrir et compiler (APK)

1. **Android Studio** (Hedgehog 2023.1+ ou plus récent) → *Open* → choisir le dossier
   `LiturgiaMonastica`.
2. Laisser Android Studio **configurer le wrapper Gradle** (le `.jar` du wrapper n'est pas
   inclus ; Android Studio le régénère, ou exécutez `gradle wrapper` si Gradle est installé).
3. Versions : AGP 8.5.2 · Gradle 8.9 · Kotlin 1.9.24 · Compose Compiler 1.5.14 · JDK 17 ·
   compileSdk 34 · minSdk 24.
4. *Build → Build Bundle(s) / APK(s) → Build APK(s)*. L'APK debug se trouve dans
   `app/build/outputs/apk/debug/`.

> Données de jeu (niveaux, jours, langue, mode nuit) sauvegardées en local
> (SharedPreferences). Aucune connexion réseau requise.

---

## Structure

```
app/src/main/
├── assets/
│   ├── psalms_fr.json            # 150 psaumes (Crampon)
│   ├── canticles.json            # Benedictus, Magnificat, Nunc dimittis
│   ├── benedict_quotes.json      # citations de la Règle
│   ├── devotions.json            # 19 dévotions + promesses
│   ├── office_structure.json     # ordinaire, hymnes, Complies, distribution (ÉDITABLE)
│   └── bible/                     # index + psaumes + 4 évangiles
├── java/com/liturgia/monastica/
│   ├── MainActivity.kt
│   ├── data/                     # Models, ContentRepository, GameStore
│   ├── nav/Navigation.kt
│   └── ui/ (theme, components, screens)
└── res/ (drawables, mipmaps, values)
```
