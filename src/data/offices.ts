export interface OfficeHour {
  id: string;
  name: string;
  nameGreek: string;
  nameFr: string;
  hour: number; // heure de la journée (0-23)
  description: string;
  tropaire: string;
  psaumes: Psaume[];
  lectures: Lecture[];
  priereFin: string;
}

export interface Psaume {
  numero: number;
  titre: string;
  texte: string;
}

export interface Lecture {
  reference: string;
  texte: string;
}

export const OFFICES: OfficeHour[] = [
  {
    id: 'mesonikion',
    name: 'Mésonyktikon',
    nameGreek: 'Μεσονυκτικόν',
    nameFr: 'Office de Minuit',
    hour: 0,
    description:
      'L\'office de minuit prépare l\'âme à la venue du Seigneur, comme les vierges sages veillant pour l\'Époux.',
    tropaire:
      'Voici que l\'Époux vient au milieu de la nuit, et bienheureux est le serviteur qu\'il trouvera en éveil ; mais celui qu\'il trouvera nonchalant est indigne. Prends garde donc, ô mon âme, de ne pas t\'abandonner au sommeil, de peur d\'être livrée à la mort et d\'être exclue du Royaume.',
    psaumes: [
      {
        numero: 50,
        titre: 'Miserere',
        texte:
          'Aie pitié de moi, ô Dieu, selon ta grande miséricorde ; selon la multitude de tes compassions, efface mes transgressions. Lave-moi complètement de mon iniquité, et purifie-moi de mon péché. Car je reconnais mes transgressions, et mon péché est constamment devant moi. Contre toi seul j\'ai péché et j\'ai fait ce qui est mal à tes yeux.',
      },
      {
        numero: 117,
        titre: 'Louange au Seigneur',
        texte:
          'Rendez grâce au Seigneur car il est bon, car sa miséricorde est éternelle. Que dise Israël : sa miséricorde est éternelle. Que dise la maison d\'Aaron : sa miséricorde est éternelle. Que le disent ceux qui craignent le Seigneur : sa miséricorde est éternelle.',
      },
    ],
    lectures: [
      {
        reference: '1 Thessaloniciens 5, 1-11',
        texte:
          'Pour ce qui est des temps et des moments, frères, vous n\'avez pas besoin qu\'on vous en écrive. Vous savez vous-mêmes parfaitement que le Jour du Seigneur vient comme un voleur dans la nuit. Vous, frères, vous n\'êtes pas dans les ténèbres, pour que ce Jour vous surprenne comme un voleur. Vous êtes tous des enfants de la lumière et des enfants du jour.',
      },
    ],
    priereFin:
      'Seigneur, fortifie-nous pour la veille et préserve nos âmes du sommeil de l\'indifférence. Que nos lampes brûlent dans l\'attente de ta venue glorieuse.',
  },
  {
    id: 'orthros',
    name: 'Orthros',
    nameGreek: 'Ὄρθρος',
    nameFr: 'Matines',
    hour: 6,
    description:
      'L\'Orthros est le grand office du matin, sommet de la louange matinale. Il célèbre la Résurrection et la lumière du Christ qui dissipe les ténèbres.',
    tropaire:
      'Gloire à toi qui as montré la Lumière ! Gloire à Dieu au plus haut des cieux, et sur la terre paix aux hommes de bonne volonté. Nous te louons, nous te bénissons, nous t\'adorons, nous te glorifions, nous te rendons grâces pour ta grande gloire.',
    psaumes: [
      {
        numero: 3,
        titre: 'Confiance en Dieu',
        texte:
          'Seigneur, que mes ennemis se multiplient ! Beaucoup se lèvent contre moi. Beaucoup disent de mon âme : il n\'y a point de salut pour lui en Dieu. Mais toi, Seigneur, tu es mon bouclier, ma gloire, et tu relèves ma tête. De ma voix je crie vers le Seigneur, et il me répond de sa sainte montagne.',
      },
      {
        numero: 37,
        titre: 'Prière du pénitent',
        texte:
          'Seigneur, ne me reprends pas dans ta colère, et ne me châtie pas dans ta fureur. Car tes flèches se sont enfoncées en moi, et ta main s\'est appesantie sur moi. Il n\'y a rien de sain dans ma chair à cause de ton indignation ; il n\'y a point de paix dans mes os à cause de mon péché.',
      },
      {
        numero: 62,
        titre: 'Soif de Dieu',
        texte:
          'Ô Dieu, tu es mon Dieu, je te cherche dès l\'aurore, mon âme a soif de toi, ma chair soupire après toi, dans une terre aride, desséchée, sans eau. C\'est ainsi que je t\'ai contemplé dans le sanctuaire, voyant ta puissance et ta gloire.',
      },
    ],
    lectures: [
      {
        reference: 'Évangile de la Résurrection',
        texte:
          'Le premier jour de la semaine, Marie de Magdala vint au sépulcre de grand matin, lorsqu\'il faisait encore obscur, et elle vit que la pierre avait été ôtée du sépulcre. Elle courut donc et vint trouver Simon-Pierre et l\'autre disciple que Jésus aimait, et elle leur dit : On a enlevé le Seigneur du sépulcre, et nous ne savons pas où on l\'a mis.',
      },
    ],
    priereFin:
      'Tu es la lumière véritable qui illumines tout homme venant en ce monde. Seigneur, marque-nous du signe de ta lumière pour que nous voyions ta lumière.',
  },
  {
    id: 'premiere-heure',
    name: 'Première Heure',
    nameGreek: 'Πρώτη Ὥρα',
    nameFr: 'Prime',
    hour: 7,
    description:
      'La première heure sanctifie le début de la journée, offrant à Dieu les prémices de nos activités et implorant sa protection pour les heures à venir.',
    tropaire:
      'Au matin exauce ma voix, ô mon Roi et mon Dieu. Au matin je me présente devant toi, et tu me regardes. Car tu n\'es pas un Dieu qui prend plaisir à l\'iniquité ; le méchant ne séjourne pas près de toi.',
    psaumes: [
      {
        numero: 5,
        titre: 'Prière du matin',
        texte:
          'Écoute mes paroles, ô Seigneur, considère ma plainte. Prête l\'oreille à la voix de mon cri, ô mon Roi et mon Dieu, car c\'est à toi que je prie. Seigneur, dès le matin tu entends ma voix ; dès le matin je te présente ma requête, et j\'attends.',
      },
      {
        numero: 89,
        titre: 'Éternité de Dieu',
        texte:
          'Seigneur, tu as été pour nous un refuge de génération en génération. Avant que les montagnes fussent nées, avant que tu eusses créé la terre et le monde, de toute éternité à toute éternité tu es Dieu. Tu fais retourner l\'homme à la poussière, et tu dis : Retournez, fils des hommes !',
      },
    ],
    lectures: [
      {
        reference: 'Romains 13, 11-14',
        texte:
          'C\'est le moment d\'être tirés du sommeil, car maintenant le salut est plus près de nous que quand nous avons cru. La nuit est avancée, le jour est proche. Dépouillons donc les œuvres des ténèbres, et revêtons les armes de la lumière.',
      },
    ],
    priereFin:
      'Toi qui à toute heure et à tout moment es adoré et glorifié dans les cieux et sur la terre, ô Christ notre Dieu, ô longanimité, grande miséricorde et grande bonté, toi qui aimes les justes et as pitié des pécheurs, qui appelles tous les hommes au salut par la promesse des biens à venir : accueille, Seigneur, nos supplications à cette heure.',
  },
  {
    id: 'troisieme-heure',
    name: 'Troisième Heure',
    nameGreek: 'Τρίτη Ὥρα',
    nameFr: 'Tierce',
    hour: 9,
    description:
      'La troisième heure commémore la descente de l\'Esprit Saint sur les Apôtres à la Pentecôte, et la condamnation de notre Seigneur devant Pilate.',
    tropaire:
      'Seigneur, toi qui à la troisième heure as envoyé ton très saint Esprit à tes apôtres, ne nous enlève pas ce don, ô Tout-Bon, mais renouvelle-le en nous qui te supplions.',
    psaumes: [
      {
        numero: 16,
        titre: 'Prière de l\'innocent',
        texte:
          'Seigneur, entends la justice, sois attentif à mon cri, prête l\'oreille à ma prière faite de lèvres sans fausseté. Que ta sentence pour moi vienne de ta face, que tes yeux regardent l\'équité. Tu sondes mon cœur, tu le visites de nuit, tu m\'éprouves et tu ne trouves rien.',
      },
      {
        numero: 24,
        titre: 'Confiance et supplication',
        texte:
          'Vers toi, Seigneur, j\'élève mon âme. Mon Dieu, en toi je me confie, que je ne sois pas confondu, que mes ennemis ne triomphent pas de moi. Oui, tous ceux qui t\'espèrent ne seront pas confondus ; ils seront confondus ceux qui trahissent sans raison.',
      },
    ],
    lectures: [
      {
        reference: 'Actes 2, 1-4',
        texte:
          'Le jour de la Pentecôte, ils étaient tous ensemble dans le même lieu. Tout à coup il vint du ciel un bruit comme celui d\'un vent impétueux, et il remplit toute la maison où ils étaient assis. Des langues, semblables à des langues de feu, leur apparurent, séparées les unes des autres, et se posèrent sur chacun d\'eux. Et ils furent tous remplis du Saint-Esprit.',
      },
    ],
    priereFin:
      'Seigneur Dieu tout-puissant, qui à la troisième heure envoyâtes sur vos apôtres votre très saint Esprit : ne le retirez pas de nous, mais renouvelez-le en nous qui vous supplions.',
  },
  {
    id: 'sixieme-heure',
    name: 'Sixième Heure',
    nameGreek: 'Ἕκτη Ὥρα',
    nameFr: 'Sexte',
    hour: 12,
    description:
      'La sixième heure — midi — commémore la Crucifixion du Christ sur le Golgotha, lorsque les ténèbres couvrirent toute la terre.',
    tropaire:
      'Toi qui à la sixième heure fus crucifié, ô Christ notre Dieu, mets à mort en nous l\'ardeur de notre chair, et accorde-nous le pardon.',
    psaumes: [
      {
        numero: 53,
        titre: 'Appel au secours',
        texte:
          'O Dieu, sauve-moi par ton nom, et défends-moi par ta puissance ! O Dieu, entends ma prière, prête l\'oreille aux paroles de ma bouche. Car des étrangers se sont levés contre moi, des hommes violents en veulent à ma vie ; ils ne placent pas Dieu devant eux.',
      },
      {
        numero: 54,
        titre: 'Trahison et confiance',
        texte:
          'Prête l\'oreille, ô Dieu, à ma prière, et ne te cache pas devant ma supplication ! Écoute-moi et réponds-moi ! Je me tourmente en soupirant, à cause de la clameur de l\'ennemi, à cause de l\'oppression du méchant ; car ils font peser sur moi l\'iniquité, et dans leur colère ils me poursuivent.',
      },
    ],
    lectures: [
      {
        reference: 'Luc 23, 44-46',
        texte:
          'Il était environ la sixième heure, et il y eut des ténèbres sur toute la terre jusqu\'à la neuvième heure. Le soleil s\'obscurcit, et le voile du temple se déchira par le milieu. Puis Jésus, criant d\'une voix forte, dit : Père, je remets mon esprit entre tes mains ! En disant ces paroles, il expira.',
      },
    ],
    priereFin:
      'Toi qui as daigné être cloué en croix à la sixième heure pour le péché qu\'Adam commit au Paradis, déchire le billet de nos péchés, ô Christ notre Dieu, et sauve-nous.',
  },
  {
    id: 'neuvieme-heure',
    name: 'Neuvième Heure',
    nameGreek: 'Ἐνάτη Ὥρα',
    nameFr: 'None',
    hour: 15,
    description:
      'La neuvième heure commémore la mort du Seigneur sur la Croix et sa descente aux enfers pour délivrer les âmes des justes.',
    tropaire:
      'Toi qui à la neuvième heure goûtas la mort en ta chair pour notre salut, mets à mort notre chair selon sa prudence charnelle, ô Christ notre Dieu, et sauve-nous.',
    psaumes: [
      {
        numero: 83,
        titre: 'Désir du Temple',
        texte:
          'Qu\'ils sont aimables, tes demeures, Seigneur des armées ! Mon âme soupire et languit après les parvis du Seigneur ; mon cœur et ma chair crient vers le Dieu vivant. Le moineau même trouve une maison, et l\'hirondelle un nid où elle dépose ses petits : tes autels, Seigneur des armées, mon Roi et mon Dieu !',
      },
      {
        numero: 84,
        titre: 'Prière pour le salut',
        texte:
          'Seigneur, tu as été favorable à ton pays, tu as ramené les captifs de Jacob. Tu as pardonné l\'iniquité de ton peuple, tu as couvert tous ses péchés. Tu as retiré tout ton courroux, tu t\'es détourné de ton ardente colère.',
      },
    ],
    lectures: [
      {
        reference: 'Jean 19, 28-30',
        texte:
          'Après cela, Jésus, sachant que tout était déjà accompli, dit, afin que l\'Écriture fût accomplie : J\'ai soif. Il y avait là un vase plein de vinaigre. Les soldats en imbibèrent une éponge, et, l\'ayant fixée à une branche d\'hysope, ils l\'approchèrent de sa bouche. Quand Jésus eut pris le vinaigre, il dit : Tout est accompli. Et, baissant la tête, il rendit l\'esprit.',
      },
    ],
    priereFin:
      'O Maître Seigneur Jésus-Christ notre Dieu, qui supportas avec patience les tourments et la croix pour notre salut, accorde-nous ta paix et ta lumière.',
  },
  {
    id: 'hesperinos',
    name: 'Hespérinon',
    nameGreek: 'Ἑσπερινός',
    nameFr: 'Vêpres',
    hour: 18,
    description:
      'Les Vêpres sont l\'office du soir, charnière entre le jour qui s\'achève et la nuit qui commence. Elles chantent la lumière du Christ, « Lumière joyeuse ».',
    tropaire:
      'Lumière joyeuse de la sainte gloire du Père immortel, céleste, saint et bienheureux, ô Jésus-Christ ! Arrivés au coucher du soleil, voyant la lumière du soir, nous chantons le Père, le Fils et le Saint-Esprit, Dieu. Tu es digne en tout temps d\'être chanté par des voix saintes, ô Fils de Dieu, toi qui donnes la vie ; c\'est pourquoi le monde te glorifie.',
    psaumes: [
      {
        numero: 103,
        titre: 'Hymne à la Création',
        texte:
          'Bénis le Seigneur, ô mon âme ! Seigneur mon Dieu, tu es si grand ! Tu es revêtu d\'éclat et de majesté. Il s\'enveloppe de lumière comme d\'un manteau, il étend les cieux comme un tapis. Il établit ses chambres hautes dans les eaux, il prend les nuées pour son char, il marche sur les ailes du vent.',
      },
      {
        numero: 1,
        titre: 'Les deux voies',
        texte:
          'Heureux l\'homme qui ne marche pas selon le conseil des méchants, qui ne s\'arrête pas sur la voie des pécheurs, et qui ne s\'assied pas en compagnie des moqueurs, mais qui trouve son plaisir dans la loi de l\'Éternel, et qui la médite jour et nuit !',
      },
    ],
    lectures: [
      {
        reference: 'Genèse 1, 1-5',
        texte:
          'Au commencement, Dieu créa les cieux et la terre. La terre était informe et vide : il y avait des ténèbres à la surface de l\'abîme, et l\'esprit de Dieu se mouvait au-dessus des eaux. Dieu dit : Que la lumière soit ! Et la lumière fut. Dieu vit que la lumière était bonne ; et Dieu sépara la lumière d\'avec les ténèbres.',
      },
    ],
    priereFin:
      'Accordez-nous, Seigneur, de passer sans péché cette soirée. Béni sois-tu, Seigneur Dieu de nos pères, et loué et glorifié est ton nom à jamais.',
  },
  {
    id: 'apodipnon',
    name: 'Apodipnon',
    nameGreek: 'Ἀπόδειπνον',
    nameFr: 'Complies',
    hour: 21,
    description:
      'L\'Apodipnon — les Complies — est l\'office du soir tardif, confiant l\'âme et le corps à Dieu pour la nuit, implorant la protection des anges.',
    tropaire:
      'Avec nous est Dieu ! Comprenez, nations, et soumettez-vous, car Dieu est avec nous ! Écoutez jusqu\'aux extrémités de la terre, car Dieu est avec nous ! Vous puissants, rendez-vous, car Dieu est avec nous !',
    psaumes: [
      {
        numero: 4,
        titre: 'Prière du soir',
        texte:
          'Réponds-moi quand j\'appelle, Dieu de ma justice ! Dans la détresse tu m\'as mis au large ; aie pitié de moi, exauce ma prière. Fils des hommes, jusques à quand ma gloire sera-t-elle déshonorée ? Vous aimez la vanité, vous cherchez le mensonge ? Sachez que l\'Éternel a mis à part l\'homme pieux ; l\'Éternel exaucera quand je crierai à lui.',
      },
      {
        numero: 90,
        titre: 'Protection divine',
        texte:
          'Celui qui demeure sous l\'abri du Très-Haut repose à l\'ombre du Tout-Puissant. Je dis à l\'Éternel : Mon refuge et ma forteresse, Mon Dieu en qui je me confie ! Car c\'est lui qui te délivrera du filet de l\'oiseleur, De la peste et de ses ravages.',
      },
      {
        numero: 133,
        titre: 'Bénédiction nocturne',
        texte:
          'Alléluia ! Bénissez l\'Éternel, vous tous, serviteurs de l\'Éternel, Qui vous tenez dans la maison de l\'Éternel pendant les nuits ! Levez vos mains vers le sanctuaire, Et bénissez l\'Éternel. Que l\'Éternel te bénisse de Sion, Lui qui a fait les cieux et la terre !',
      },
    ],
    lectures: [
      {
        reference: 'Isaïe 8, 13-14',
        texte:
          'C\'est le Seigneur des armées que vous devez regarder comme saint, c\'est lui qui doit être l\'objet de votre crainte et de votre frayeur. Il sera un sanctuaire, et il sera une pierre d\'achoppement et un rocher de scandale pour les deux maisons d\'Israël, un piège et un filet pour les habitants de Jérusalem.',
      },
    ],
    priereFin:
      'Éclaire mes yeux, ô Christ notre Dieu, de peur que je ne m\'endorme dans la mort, de peur que mon ennemi ne dise : J\'ai eu le dessus sur lui.',
  },
];

export function getOfficeByHour(currentHour: number): OfficeHour {
  const sorted = [...OFFICES].sort((a, b) => {
    const diffA = (currentHour - a.hour + 24) % 24;
    const diffB = (currentHour - b.hour + 24) % 24;
    return diffA - diffB;
  });
  return sorted[0];
}

export function getNextOffice(currentHour: number): OfficeHour {
  const future = OFFICES.filter((o) => {
    const diff = (o.hour - currentHour + 24) % 24;
    return diff > 0;
  }).sort((a, b) => {
    const diffA = (a.hour - currentHour + 24) % 24;
    const diffB = (b.hour - currentHour + 24) % 24;
    return diffA - diffB;
  });
  return future[0] ?? OFFICES[0];
}
