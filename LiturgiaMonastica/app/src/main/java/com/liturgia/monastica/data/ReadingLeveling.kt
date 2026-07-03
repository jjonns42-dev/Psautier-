package com.liturgia.monastica.data

/**
 * Formules du jeu de lecture : gain d'XP par séance chronométrée, et conversion
 * XP -> niveau infini, utilisées à la fois pour chaque livre/œuvre individuel
 * et pour le niveau cumulé d'une catégorie entière (Bible, Théologie,
 * Spiritualité/Mystique, Pères de l'Église).
 */
object ReadingLeveling {

    /**
     * XP gagnée pour une séance de [minutes] minutes (doit appartenir à READING_DURATIONS).
     * Plus la séance choisie est longue, plus elle rapporte d'XP par minute :
     * la jauge de niveau progresse donc plus vite pour les temps de lecture les plus longs.
     */
    fun sessionXp(minutes: Int): Long {
        val tierIndex = ((minutes / 5) - 1).coerceIn(0, 11)   // 5 min -> 0 ... 60 min -> 11
        val multiplier = 1.0 + tierIndex * 0.06                // 1.00x .. 1.66x
        return Math.round(minutes * multiplier).coerceAtLeast(1L)
    }

    /** Coût en XP du niveau [level] (le niveau 1 coûte 100 XP, le niveau 2 en coûte 200, etc. — coût croissant, niveau infini). */
    private fun levelCost(level: Int): Long = 100L * level

    /** Niveau atteint pour un total de [xp] cumulée. */
    fun levelFromXp(xp: Long): Int {
        var level = 1
        var total = 0L
        while (true) {
            val needed = levelCost(level)
            if (total + needed > xp) return level
            total += needed
            level++
        }
    }

    /** (progression dans le niveau courant, XP nécessaire pour le niveau courant) — pour la jauge. */
    fun progressWithinLevel(xp: Long): Pair<Long, Long> {
        var level = 1
        var total = 0L
        while (true) {
            val needed = levelCost(level)
            if (total + needed > xp) return (xp - total) to needed
            total += needed
            level++
        }
    }
}
