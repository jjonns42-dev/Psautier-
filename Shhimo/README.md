# Shḥimo — Liturgie des Heures maronite (Android)

Application Android (Kotlin / Jetpack Compose) pour la **liturgie des heures
maronite** (le Šḥimo / شحيمو), couvrant l'ensemble de l'année liturgique,
en **arabe libanais** et **italien** (le **syriaque** est prévu en option).

L'architecture s'inspire de celle d'une application de *Liturgia delle Ore* :
un moteur calcule le jour liturgique, puis charge l'office correspondant, que
l'utilisateur consulte heure par heure.

## ⚠️ Au sujet du contenu liturgique

Ce dépôt contient **le moteur complet et fonctionnel**, mais **pas** les textes
intégraux du Šḥimo. Les textes des éditions de référence (Šḥimto, Kaslik 1982 ;
livre rituel de Bkerké, 1942) sont sous droits et doivent provenir d'une
**édition autorisée**, puis être **validés par l'autorité ecclésiastique
maronite** avant tout usage liturgique réel.

Les quelques textes présents sont des **exemples provisoires** (entre crochets),
là uniquement pour que l'application se lance et que l'on voie le rendu et la
navigation. Le format pour saisir le contenu réel est décrit dans
[`docs/DATA_FORMAT.md`](docs/DATA_FORMAT.md).

## Ouvrir le projet

1. Android Studio (Koala ou plus récent).
2. *Open* → sélectionner le dossier `shhimo/`.
3. Laisser Gradle synchroniser, puis *Run*.

Prérequis : JDK 17, Android SDK 34.

## Structure

```
app/src/main/
├─ java/org/maronite/shhimo/
│  ├─ data/
│  │  ├─ model/        modèles (offices, calendrier, hymnes, langues)
│  │  ├─ source/       lecture des assets JSON
│  │  └─ repository/   accès données + préférences
│  ├─ domain/calendar/ calcul de Pâques + résolution du jour maronite
│  ├─ ui/
│  │  ├─ office/        écran de l'office + ViewModel
│  │  ├─ settings/      réglages (langue, taille du texte)
│  │  └─ common/        thème
│  ├─ MainActivity.kt   navigation + gestion RTL (arabe)
│  └─ ShhimoApp.kt      conteneur de dépendances
├─ assets/
│  ├─ calendar/         calendar.json
│  ├─ offices/          un fichier JSON par office
│  └─ hymns/            hymns.json
└─ res/
   ├─ values/           italien (défaut)
   ├─ values-ar/        arabe
   └─ values-it/        italien explicite
```

## Ce qui fonctionne

- Calcul du jour liturgique (saison, semaine, rang, couleur) pour **toute date**.
- Calcul de Pâques (grégorien, avec option julienne).
- Navigation jour précédent / aujourd'hui / jour suivant.
- Sélection des heures (Ramcho, Soutoro, Lilio, Safro).
- Bascule de langue **arabe ↔ italien** avec **passage automatique en RTL**.
- Réglage de la taille du texte.
- Chargement du contenu depuis des fichiers JSON, sans recompiler le code.

## À compléter

- Saisie du contenu liturgique réel (voir `docs/DATA_FORMAT.md`).
- Délimitation fine des saisons maronites selon le calendrier de Bkerké
  (le squelette est dans `domain/calendar/MaroniteCalendar.kt`, commenté).
- Police syriaque dédiée dans `res/font/` pour un meilleur rendu du `syr`.
- Audio des mélodies (qole) dans `assets/hymns/`.

## Licence

Le **code** peut être placé sous la licence de votre choix.
Le **contenu liturgique** que vous y ajouterez relève des droits de ses
éditeurs et des autorisations ecclésiastiques applicables.
