# Format des données liturgiques

Cette application sépare **le moteur (code)** et **le contenu (textes)**.
Tout le contenu liturgique vit dans `app/src/main/assets/`, en JSON. On peut
donc remplir, corriger ou compléter les textes **sans toucher au code**.

> ⚠️ Les textes livrés ici sont **provisoires** (marqués entre crochets).
> Le contenu réel doit provenir d'une édition autorisée du Šḥimo et du
> calendrier maronite, et être validé par l'autorité ecclésiastique
> compétente avant tout usage liturgique.

## 1. Les langues

Chaque texte est un objet à trois champs :

```json
{ "ar": "العربية اللبنانية", "it": "Italiano", "syr": "ܣܘܪܝܝܐ" }
```

- `ar` : arabe libanais
- `it` : italien
- `syr` : syriaque (caractères syriaques ou translittération) — facultatif

Un champ laissé vide (`""`) est traité comme « non encore traduit » et
l'application bascule sur une autre langue disponible.

## 2. Le calendrier — `assets/calendar/calendar.json`

Un tableau d'entrées. Chaque entrée :

| Champ              | Obligatoire | Description                                          |
|--------------------|-------------|------------------------------------------------------|
| `id`               | oui         | identifiant unique                                   |
| `title`            | oui         | texte multilingue                                    |
| `rank`             | oui         | `FERIA`, `COMMEMORATION`, `FEAST`, `SOLEMNITY`, `SUNDAY` |
| `color`            | oui         | `WHITE`, `RED`, `GREEN`, `VIOLET`, `ROSE`, `BLACK`   |
| `season`           | oui         | voir la liste des saisons ci-dessous                 |
| `fixedDate`        | non         | date fixe `"MM-DD"` (ex. `"12-25"`)                  |
| `offsetFromEaster` | non         | décalage en jours par rapport à Pâques (ex. `49`)    |
| `officeId`         | non         | office propre à charger ; sinon office férial du jour |

Une entrée est soit à date fixe (`fixedDate`), soit mobile
(`offsetFromEaster`).

### Saisons (`season`)

`CONSECRATION_CHURCH`, `RENEWAL_CHURCH`, `ANNUNCIATION`, `NATIVITY`,
`EPIPHANY`, `GREAT_LENT`, `PASSION_WEEK`, `RESURRECTION`, `PENTECOST`,
`HOLY_CROSS`, `ORDINARY`.

## 3. Les offices — `assets/offices/<officeId>.json`

Un fichier par office. La convention pour les offices fériaux est
`feria_<jour>` (`feria_monday`, `feria_tuesday`, …).

```json
{
  "id": "feria_monday",
  "title": { "ar": "...", "it": "...", "syr": "" },
  "hours": [
    {
      "hour": "RAMCHO",
      "elements": [
        {
          "type": "PSALM",
          "reference": "Ps 141 / 140",
          "title": { "ar": "...", "it": "...", "syr": "" },
          "body":  { "ar": "...", "it": "...", "syr": "" },
          "melodyId": null
        }
      ]
    }
  ]
}
```

### Heures (`hour`)

`RAMCHO` (Vêpres), `SOUTORO` (Complies), `LILIO` (Vigiles),
`SAFRO` (Matines/Laudes), `THIRD_HOUR`, `SIXTH_HOUR`, `NINTH_HOUR`.

### Types d'élément (`type`)

`RUBRIC` (affiché en rouge, non lu), `OPENING`, `PSALM`, `QOLO`,
`HOUSSOYO`, `SEDRO`, `PROCLAMATION`, `PRAYER`, `RESPONSE`, `DOXOLOGY`.

## 4. Les hymnes — `assets/hymns/hymns.json`

Deux listes : `melodies` (les mélodies types / modes) et `hymns`
(les textes chantables rattachés à une mélodie par `melodyId`).
Un fichier audio facultatif peut être référencé via `audioAsset`
(placé dans `assets/hymns/`).

## 5. Comment remplir le contenu réel

1. Choisir l'office à compléter (ex. `feria_monday.json`).
2. Pour chaque heure, ajouter les `elements` dans l'ordre liturgique.
3. Remplir `ar`, `it` et si possible `syr` pour chaque texte.
4. Pour le calendrier, saisir les fêtes maronites avec leurs dates.
5. Recompiler : les nouveaux textes apparaissent automatiquement.

Aucune modification de code n'est nécessaire pour le contenu.
