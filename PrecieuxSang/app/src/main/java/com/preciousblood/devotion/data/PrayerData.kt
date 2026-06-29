package com.preciousblood.devotion.data

import com.preciousblood.devotion.data.PrayerBlock.*

/**
 * Contenu de la dévotion, organisé selon les divisions de la
 * « Table des matières » du livre « Dévotion Quotidienne au
 * Précieux Sang de Jésus » (Les Adorateurs du Précieux Sang).
 *
 * Les prières centrales (Chapelet, Litanie, Consécration,
 * Comment prier, Promesses) ont leur texte intégral.
 * Les autres présentent une description et la page d'origine ;
 * il suffit d'ajouter les blocs `body` pour les compléter.
 */
object PrayerData {

    val sections: List<Section> = listOf(
        introduction(),
        devotionQuotidienne(),
        heuresGethsemani(),
        reparation(),
        protection(),
        prieresSpeciales(),
        annexes()
    )

    fun findPrayer(id: String): Prayer? =
        sections.flatMap { it.prayers }.firstOrNull { it.id == id }

    fun findSection(id: String): Section? =
        sections.firstOrNull { it.id == id }

    // ----------------------------------------------------------------
    // 1. INTRODUCTION
    // ----------------------------------------------------------------
    private fun introduction() = Section(
        id = "intro",
        title = "Introduction",
        subtitle = "Pour bien commencer",
        prayers = listOf(
            Prayer(
                id = "adresse-jean-xxiii",
                title = "Adresse de Jean XXIII sur le Précieux Sang",
                source = "30 juin 1960",
                summary = "Extrait de la lettre apostolique « Inde a Primis » du Pape Jean XXIII.",
                page = 6,
                assetFile = "prayers/adresse-jean-xxiii.txt"
            ),
            Prayer(
                id = "comment-prier",
                title = "Comment prier cette dévotion",
                summary = "Les trois volets : Dévotion quotidienne, Heures de Gethsémani, Grand mois de Juillet.",
                page = 10,
                body = listOf(
                    Paragraph(
                        "Ceci est la dévotion au Précieux Sang de Notre Seigneur " +
                        "Jésus-Christ telle que donnée par Notre Seigneur et Notre Dame " +
                        "à Barnabas Nwoye, dans le village d'Olo, État d'Enugu, Nigéria, " +
                        "de 1995 à 2003."
                    ),
                    Heading("(1) La Dévotion Quotidienne"),
                    Paragraph("Les prières quotidiennes minimums pour un dévot sont :"),
                    Numbered(1, "Un mystère du Rosaire (Joyeux, Lumineux, Douloureux ou Glorieux)"),
                    Numbered(2, "Chapelet du Précieux Sang"),
                    Numbered(3, "Litanie du Précieux Sang"),
                    Numbered(4, "Consécration au Précieux Sang"),
                    Numbered(5, "Toutes autres prières de la Dévotion Quotidienne Complète que vous pouvez ajouter à votre journée sans entraver vos devoirs de famille et de travail."),
                    Heading("(2) Les Heures de Gethsémani"),
                    Paragraph(
                        "Chaque Jeudi soir de 23:00 à Vendredi matin 3:00, ce sont les " +
                        "Heures de Gethsémani : la période exacte pendant laquelle Notre " +
                        "Seigneur a souffert dans le Jardin. Le mieux est de prier en " +
                        "présence de Jésus exposé dans le Saint Sacrement. Si vous ne " +
                        "pouvez faire que l'heure minimum, qu'elle soit entre minuit et " +
                        "3:00 du Vendredi."
                    ),
                    Heading("(3) Le Grand mois de Juillet"),
                    Paragraph(
                        "Chaque Juillet (et le troisième Vendredi de chaque mois), " +
                        "l'Église honore le Précieux Sang. Trois périodes importantes :"
                    ),
                    Numbered(1, "Juillet 1–9 : neuvaine du Précieux Sang en l'honneur des neuf Chœurs des Anges"),
                    Numbered(2, "Juillet 13–15 : trois jours de prière en l'honneur de la Très Sainte Trinité"),
                    Numbered(3, "Juillet 20–31 : douze jours de prière pour le Nouvel Israël"),
                    Rubric("Voir l'onglet Calendrier pour les prochaines dates.")
                )
            )
        )
    )

    // ----------------------------------------------------------------
    // 2. DÉVOTION QUOTIDIENNE
    // ----------------------------------------------------------------
    private fun devotionQuotidienne() = Section(
        id = "quotidienne",
        title = "Dévotion Quotidienne",
        subtitle = "Les prières de chaque jour",
        prayers = listOf(
            Prayer(
                id = "rosaire",
                title = "Le Saint Rosaire",
                summary = "Récité avant le Chapelet, suivi immédiatement de celui-ci.",
                page = 19,
                assetFile = "prayers/rosaire.txt"
            ),
            Prayer(
                id = "litanie-bvm",
                title = "Litanie de la Bienheureuse Vierge Marie",
                summary = "Litanie mariale récitée comme partie du Rosaire.",
                page = 28,
                assetFile = "prayers/litanie-bvm.txt"
            ),
            chapeletDuPrecieuxSang(),
            litanieDuPrecieuxSang(),
            consecration()
        )
    )

    private fun chapeletDuPrecieuxSang() = Prayer(
        id = "chapelet",
        title = "Chapelet du Précieux Sang",
        source = "15 mars 1997",
        summary = "Le chapelet en cinq mystères des plaies, cœur de la dévotion.",
        page = 32,
        body = listOf(
            Chant(
                "Très Précieux Sang, Sang de Jésus-Christ (×4)\n" +
                "Très Précieux Sang, sauvez le monde."
            ),
            Heading("Prière à l'Esprit Saint"),
            Paragraph(
                "Viens Esprit Saint, remplis les cœurs de tes fidèles et allume en " +
                "eux le feu de Ton Amour."
            ),
            Versicle("Envoie Ton Esprit et tout sera créé.", "Et Tu renouvelleras la face de la terre."),
            Paragraph(
                "Ô Dieu qui as instruit les cœurs de tes fidèles par la lumière de " +
                "Ton Esprit, fais que le même Esprit nous donne le goût et l'amour " +
                "du bien et qu'Il nous remplisse toujours de la joie de ses divines " +
                "consolations. Par Jésus-Christ Notre Seigneur. Amen."
            ),
            Rubric("Symbole des Apôtres (sur le Crucifix) — Baissez la tête"),
            Paragraph(
                "Que le Précieux Sang qui jaillit de la Tête Sacrée de Notre Seigneur " +
                "Jésus-Christ, le Temple de la Divine Sagesse, le Tabernacle de la " +
                "Divine Connaissance et le Soleil Brillant du Ciel et de la Terre, " +
                "nous couvre maintenant et toujours. Amen."
            ),
            Versicle("Ô Très Précieux Sang de Jésus-Christ", "Guérissez les blessures dans le Cœur Très Sacré de Jésus."),
            Rubric("Notre Père… Je vous salue Marie (3 fois)… Gloire au Père…"),

            Heading("Le Premier Mystère — On cloue la main droite"),
            Paragraph(
                "Par la précieuse plaie dans votre main droite et par la douleur du " +
                "clou qui perça votre main droite, que le Précieux Sang qui en jaillit " +
                "convertisse plusieurs âmes et sauve les pécheurs du monde entier. Amen."
            ),
            Versicle("Précieux Sang de Jésus-Christ", "Sauvez-nous et le monde entier. (12 fois)"),

            Heading("Deuxième Mystère — On cloue la main gauche"),
            Paragraph(
                "Par la précieuse plaie dans votre main gauche et par la douleur du " +
                "clou qui perça votre main gauche, que le Précieux Sang qui en jaillit " +
                "sauve les âmes du Purgatoire et protège les mourants contre les " +
                "attaques des esprits infernaux. Amen."
            ),
            Versicle("Précieux Sang de Jésus-Christ", "Sauvez-nous et le monde entier. (12 fois)"),

            Heading("Troisième Mystère — On cloue le pied droit"),
            Paragraph(
                "Par la précieuse plaie dans votre pied droit et par la douleur du " +
                "clou qui perça votre pied droit, que le Précieux Sang qui en jaillit " +
                "couvre les fondations de l'Église Catholique contre les plans du " +
                "royaume occulte et des méchants. Amen."
            ),
            Versicle("Précieux Sang de Jésus-Christ", "Sauvez-nous et le monde entier. (12 fois)"),

            Heading("Quatrième Mystère — On cloue le pied gauche"),
            Paragraph(
                "Par la précieuse plaie dans votre pied gauche et par la douleur du " +
                "clou qui perça votre pied gauche, que le Précieux Sang qui en jaillit " +
                "nous protège sur tous nos chemins contre les plans et les attaques " +
                "des esprits mauvais et leurs agents. Amen."
            ),
            Versicle("Précieux Sang de Jésus-Christ", "Sauvez-nous et le monde entier. (12 fois)"),

            Heading("Le Cinquième Mystère — On transperce le côté sacré"),
            Paragraph(
                "Par la précieuse plaie dans votre côté sacré et par la douleur de la " +
                "lance qui perça votre côté sacré, que le Précieux Sang et l'eau qui " +
                "en jaillissent guérissent les malades, ressuscitent les morts, " +
                "solutionnent nos problèmes actuels et nous enseignent la route de " +
                "notre Dieu vers la gloire éternelle. Amen."
            ),
            Versicle("Précieux Sang de Jésus-Christ", "Sauvez-nous et le monde entier. (12 fois)"),

            Heading("Conclusion"),
            Versicle("Ô Très Précieux Sang de Jésus-Christ", "Guérissez les blessures dans le Cœur Très Sacré de Jésus. (3 fois)"),
            Rubric("Salut, ô Reine…"),
            Paragraph(
                "Ô Très Précieux Sang de Jésus-Christ, nous Vous honorons, Vous " +
                "servons et Vous adorons à cause de votre alliance éternelle qui " +
                "apporte la paix au genre humain. Guérissez les blessures dans le " +
                "Cœur Très Sacré de Jésus, consolez le Père Tout-Puissant sur Son " +
                "Trône et lavez les péchés du monde entier. Que tous Vous vénèrent, " +
                "Ô Précieux Sang. Ayez pitié de nous. Amen."
            )
        )
    )

    private fun litanieDuPrecieuxSang() = Prayer(
        id = "litanie",
        title = "Litanie du Précieux Sang de Jésus-Christ",
        summary = "Litanie d'invocations au Sang du salut.",
        page = 41,
        body = listOf(
            Versicle("Seigneur, prends pitié de nous", "Seigneur, prends pitié de nous"),
            Versicle("Ô Christ, prends pitié de nous", "Ô Christ, prends pitié de nous"),
            Versicle("Ô Christ, écoute-nous", "Ô Christ, écoute-nous gracieusement"),
            Paragraph("Dieu le Père du ciel, prends pitié de nous."),
            Paragraph("Dieu le Fils, Rédempteur du monde, prends pitié de nous."),
            Paragraph("Dieu le Saint Esprit, prends pitié de nous."),
            Paragraph("Trinité Sainte, un seul Dieu, prends pitié de nous."),
            Versicle("Ô Très Précieux Sang de Jésus-Christ, le Sang du salut", "Couvrez-nous et le monde entier"),
            Heading("Libérez-nous"),
            Paragraph("Océan du Sang de Jésus-Christ — libérez-nous"),
            Paragraph("Sang rempli de sainteté et de compassion — libérez-nous"),
            Paragraph("Notre force et notre puissance — libérez-nous"),
            Paragraph("Alliance Éternelle — libérez-nous"),
            Paragraph("Fondation de la Foi Chrétienne — libérez-nous"),
            Paragraph("Bouclier de Dieu — libérez-nous"),
            Paragraph("Divine Charité — libérez-nous"),
            Paragraph("Ennemi des démons — libérez-nous"),
            Paragraph("Secours des esclaves du Malin — libérez-nous"),
            Paragraph("Le Vin Sacré — libérez-nous"),
            Paragraph("La Puissance des Chrétiens — libérez-nous"),
            Paragraph("Défenseur du Mur Catholique — libérez-nous"),
            Paragraph("Vraie Foi du Chrétien — libérez-nous"),
            Heading("Sauvez-nous"),
            Paragraph("Sang Guérisseur — sauvez-nous"),
            Paragraph("Sang qui oint — sauvez-nous"),
            Paragraph("Audace des enfants de Dieu — sauvez-nous"),
            Paragraph("Commandeur des militants Chrétiens — sauvez-nous"),
            Paragraph("Le Sang de la Résurrection — sauvez-nous"),
            Paragraph("La Boisson des Anges du Ciel — sauvez-nous"),
            Paragraph("La Consolation de Dieu le Père — sauvez-nous"),
            Paragraph("Puissance de l'Esprit Saint — sauvez-nous"),
            Paragraph("Paix du monde — sauvez-nous"),
            Paragraph("Soleil du ciel et de la terre — sauvez-nous"),
            Paragraph("Arc-en-ciel dans le Paradis — sauvez-nous"),
            Paragraph("Espoir des enfants innocents — sauvez-nous"),
            Paragraph("Parole de Dieu dans nos cœurs — sauvez-nous"),
            Paragraph("Arme Céleste — sauvez-nous"),
            Paragraph("Divine Sagesse — sauvez-nous"),
            Paragraph("Fondation du monde — sauvez-nous"),
            Paragraph("Miséricorde de Dieu le Père — sauvez-nous"),
            Heading("Conclusion"),
            Versicle("Ô Très Précieux Sang de Jésus-Christ", "Lavez les péchés du monde"),
            Versicle("Ô Très Précieux Sang de Jésus-Christ", "Purifiez le monde"),
            Versicle("Ô Très Précieux Sang de Jésus-Christ", "Montrez-nous comment consoler Jésus"),
            Paragraph(
                "Ô Précieux Sang de notre salut, nous croyons, espérons et avons " +
                "confiance en Vous. Délivrez tous ceux qui sont esclaves des esprits " +
                "infernaux. Protégez les mourants contre les attaques des esprits " +
                "mauvais et accueillez-les dans votre gloire éternelle. Ayez pitié du " +
                "monde entier et donnez-nous la force d'adorer et de consoler le Sacré " +
                "Cœur. Nous Vous adorons, Ô Précieux Sang de Miséricorde. Amen."
            ),
            Chant("Sang de Jésus, Sang de Jésus, Sang de Jésus, couvrez-nous. (3 fois)")
        )
    )

    private fun consecration() = Prayer(
        id = "consecration",
        title = "Consécration au Précieux Sang de Jésus-Christ",
        summary = "Acte de dédicace personnelle au Précieux Sang.",
        page = 46,
        body = listOf(
            Paragraph(
                "Miséricordieux Jésus, conscient de ma petitesse et de votre " +
                "Sublimité, je me prosterne à vos pieds et Vous remercie pour les " +
                "multiples preuves de faveur que Vous m'avez montrées, moi, votre " +
                "ingrate créature. Je Vous remercie spécialement de m'avoir délivré " +
                "par Votre Précieux Sang du pouvoir destructif de Satan."
            ),
            Paragraph(
                "En présence de ma tendre Mère Marie, de mon bon Ange gardien, de mon " +
                "saint Patron et de toute la Cour Céleste, je me dédie volontairement, " +
                "avec un cœur sincère, Ô Très doux Jésus, à Votre Précieux Sang, par " +
                "lequel Vous avez racheté le monde du péché, de la mort et de l'Enfer."
            ),
            Paragraph(
                "Je Vous promets, avec l'aide de votre grâce et jusqu'à la limite de " +
                "mes forces, de promouvoir et de nourrir la dévotion à Votre Précieux " +
                "Sang, le prix de notre rédemption, afin que Votre Sang adorable soit " +
                "honoré et glorifié par tous."
            ),
            Paragraph(
                "Aspergez-moi, Ô Divin Sauveur, ainsi que tous les hommes, avec Votre " +
                "Précieux Sang, afin que nous puissions désormais, Ô Amour Crucifié, " +
                "Vous aimer de tout notre cœur et dignement honorer le Prix de notre " +
                "rédemption. Amen."
            ),
            Rubric("Pour tous les Bienfaiteurs de cette Dévotion : Notre Père… Je vous salue Marie… Gloire au Père…")
        )
    )

    // ----------------------------------------------------------------
    // 3. HEURES DE GETHSÉMANI
    // ----------------------------------------------------------------
    private fun heuresGethsemani() = Section(
        id = "gethsemani",
        title = "Heures de Gethsémani",
        subtitle = "Jeudi 23h → Vendredi 3h",
        prayers = listOf(
            descPrayer("consolation", "Prières de Consolation",
                "Prières pour consoler Notre Seigneur dans son agonie.", 48),
            descPrayer("adoration", "Prières d'Adoration",
                "Prières d'adoration du Précieux Sang.", 57),
            descPrayer("appels-angoisses", "Les Appels Angoissés",
                "Les cris d'appel de Notre Seigneur agonisant.", 70),
            descPrayer("prieres-mystiques", "Les Prières Mystiques",
                "Prières mystiques offertes durant l'agonie.", 96)
        )
    )

    // ----------------------------------------------------------------
    // 4. PRIÈRES DE RÉPARATION
    // ----------------------------------------------------------------
    private fun reparation() = Section(
        id = "reparation",
        title = "Prières de Réparation",
        subtitle = "Réparer les offenses",
        prayers = listOf(
            descPrayer("reparation-pere-eternel", "Prière de Réparation au Père Éternel",
                "Réparation offerte au Père Éternel.", 110),
            descPrayer("louange-divine", "Prières de louange Divine et d'Adoration",
                "Louanges et adoration divines.", 111),
            descPrayer("reparation-precieux-sang", "Prière de réparation pour le Précieux Sang",
                "Réparation des outrages faits au Précieux Sang.", 111),
            descPrayer("reparation-couronne", "Prière de Réparation avec la Couronne d'Épines",
                "Réparation par la méditation de la Couronne d'Épines.", 118),
            descPrayer("offrandes-couronne", "Offrandes de la Couronne d'Épines",
                "Offrandes liées à la Couronne d'Épines.", 123),
            descPrayer("troisiemes-vendredis", "Réparation des troisièmes vendredis",
                "Prières du troisième vendredi de chaque mois.", 125)
        )
    )

    // ----------------------------------------------------------------
    // 5. PRIÈRES DE PROTECTION
    // ----------------------------------------------------------------
    private fun protection() = Section(
        id = "protection",
        title = "Prières de Protection",
        subtitle = "Sous le bouclier du Sang",
        prayers = listOf(
            descPrayer("st-michel-original", "Prière Originale de St. Michel Archange",
                "La prière originale à saint Michel Archange.", 112),
            descPrayer("invocation-protection", "Invocation Puissante de Protection",
                "Invocation pour la protection contre le mal.", 115),
            descPrayer("benediction-st-michel", "Bénédiction solennelle de St Michel Archange",
                "Bénédiction solennelle (prêtres seulement).", 124),
            descPrayer("controle-langue", "Prière pour le contrôle de la langue",
                "Prière pour maîtriser la parole.", 123)
        )
    )

    // ----------------------------------------------------------------
    // 6. PRIÈRES SPÉCIALES
    // ----------------------------------------------------------------
    private fun prieresSpeciales() = Section(
        id = "speciales",
        title = "Prières Spéciales",
        subtitle = "Pour les grandes périodes",
        prayers = listOf(
            descPrayer("nouvel-israel", "Prière pour le Nouvel Israël",
                "Récitée du 20 au 31 juillet (douze jours).", 116),
            descPrayer("roses-regne-glorieux", "Les Roses du règne Glorieux",
                "Offrandes de roses pour le Règne Glorieux.", 128),
            descPrayer("litanie-saints", "Litanie des Saints (Latin / Français)",
                "Litanie des Saints, bilingue.", 136),
            descPrayer("douze-tribus", "Une alerte aux douze Tribus d'Israël",
                "Message d'alerte aux douze Tribus d'Israël.", 146)
        )
    )

    // ----------------------------------------------------------------
    // 7. ANNEXES
    // ----------------------------------------------------------------
    private fun annexes() = Section(
        id = "annexes",
        title = "Annexes",
        subtitle = "Appendice et promesses",
        prayers = listOf(
            descPrayer("appendice", "Appendice des Prières",
                "Prières supplémentaires en appendice.", 150),
            promesses()
        )
    )

    private fun promesses() = Prayer(
        id = "promesses",
        title = "Les Promesses",
        summary = "Les promesses de Notre Seigneur à ceux qui prient cette dévotion.",
        page = 154,
        body = listOf(
            Heading("À ceux qui prient dévotement le Chapelet du Précieux Sang"),
            Numbered(1, "Je promets de protéger contre les attaques du Malin toute personne qui prie dévotement ce chapelet."),
            Numbered(2, "Je garderai ses cinq sens."),
            Numbered(3, "Je la protégerai contre toute mort subite."),
            Numbered(4, "Douze heures avant sa mort, elle boira mon Précieux Sang et mangera mon Corps."),
            Numbered(5, "Vingt-quatre heures avant sa mort, Je lui montrerai mes cinq plaies pour qu'elle ait une contrition profonde de ses péchés."),
            Numbered(6, "Toute personne qui fait une neuvaine avec le chapelet verra la réalisation de ses intentions."),
            Numbered(7, "Je ferai plusieurs miracles merveilleux avec ce chapelet."),
            Numbered(8, "Par ce chapelet, Je détruirai plusieurs sociétés secrètes et libérerai plusieurs âmes en captivité."),
            Numbered(9, "Par lui, Je sauverai beaucoup d'âmes du Purgatoire."),
            Numbered(10, "Je montrerai mon chemin à celui qui honore mon Précieux Sang par ce chapelet."),
            Numbered(11, "J'aurai pitié de ceux qui ont pitié de mes Précieuses Blessures et de mon Précieux Sang."),
            Numbered(12, "Toute personne qui enseignera cette prière à une autre aura une indulgence de quatre ans."),
            Heading("À ceux qui récitent les Prières de Consolation et d'Adoration"),
            Numbered(1, "Je promets de protéger contre les attaques du mal toute personne qui dévotement me console et m'adore. Elle ne décédera pas par une mort subite."),
            Numbered(2, "Je promets de protéger contre les attaques des mauvais esprits toute personne qui me console et m'adore."),
            Numbered(3, "Tout soldat qui dit cette prière avant le combat ne sera pas mis en déroute."),
            Numbered(4, "Récitée pour une femme en travail, elle aura moins de douleur et délivrera son bébé en sécurité."),
            Numbered(5, "Placez cette prière sur la tête de tout enfant troublé par des mauvais esprits : mon Chérubin le protégera."),
            Numbered(6, "Je promets de protéger toute famille contre les éclairs et le tonnerre, et toute maison contre les tempêtes."),
            Numbered(7, "Récitée auprès d'un mourant, je promets que son âme ne sera pas perdue."),
            Numbered(8, "Tout pécheur qui me console et m'adore avec cette prière obtiendra la conversion."),
            Numbered(9, "Je promets de cacher dans mes Saintes Blessures tous ceux qui me consolent. Aucun poison n'aura d'effet sur eux."),
            Numbered(10, "Je promets de baptiser les enfants avortés et de mettre une profonde contrition dans le cœur de leurs parents."),
            Numbered(11, "Tous ceux qui me consolent jusqu'à leur mort rejoindront les Armées Célestes. Je leur donnerai l'Étoile du Matin.")
        )
    )

    // ----------------------------------------------------------------
    // Helper : prière dont le texte intégral est dans un fichier d'assets
    // ----------------------------------------------------------------
    private fun descPrayer(id: String, title: String, summary: String, page: Int) =
        Prayer(
            id = id,
            title = title,
            summary = summary,
            page = page,
            assetFile = "prayers/$id.txt"
        )
}
