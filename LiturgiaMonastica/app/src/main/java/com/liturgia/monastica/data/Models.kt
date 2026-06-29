package com.liturgia.monastica.data

/** A single assembled section of an Office hour. */
data class OfficeSection(
    val kind: String,        // "intro","hymn","psalm","canticle","gospel_canticle","prayer","conclusion","label"
    val title: String,       // e.g. "Psaume 51" / "Cantique de Zacharie"
    val subtitle: String = "", // e.g. reference
    val body: String         // the prayer / psalm text
)

data class HourMeta(
    val id: String,
    val nameFr: String,
    val nameIt: String,
    val nameLa: String,
    val order: Int
)

data class Canticle(
    val id: String,
    val titleFr: String,
    val titleIt: String,
    val ref: String,
    val text: String
)

data class BenedictQuote(
    val fr: String,
    val it: String,
    val src: String
)

data class Devotion(
    val id: String,
    val nameFr: String,
    val nameIt: String,
    val desc: String,
    val promises: List<String>
)

data class BibleBook(
    val id: String,
    val name: String,
    val file: String,
    val testament: String,
    val chapters: Int
)
