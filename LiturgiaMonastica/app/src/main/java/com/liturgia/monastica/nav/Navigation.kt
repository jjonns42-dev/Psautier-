package com.liturgia.monastica.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.ui.screens.*

object Routes {
    const val HOME = "home"
    const val OFFICE = "office"
    const val OFFICE_HOUR = "office/{hourId}"
    const val BIBLE = "bible"
    const val CANDLE = "candle"
    const val THOUSAND = "thousand"
    const val COMBAT = "combat"
    const val COMBAT_SIN = "combat/{sinId}"
    const val COMBAT_LEVEL = "combat/{sinId}/{level}"
    const val COMBAT_INFO = "combat_info"
    const val RULE = "rule"
    const val NOVENA = "novena"
    const val FASTING = "fasting"
    const val EXERCISES = "exercises"
    const val READING = "reading"
    const val PENANCE = "penance"
    fun hour(id: String) = "office/$id"
    fun combatSin(id: String) = "combat/$id"
    fun combatLevel(id: String, level: Int) = "combat/$id/$level"
}

@Composable
fun AppNavGraph(repo: ContentRepository, store: GameStore) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                store = store,
                onOffice = { nav.navigate(Routes.OFFICE) },
                onBible = { nav.navigate(Routes.BIBLE) },
                onCandle = { nav.navigate(Routes.CANDLE) },
                onThousand = { nav.navigate(Routes.THOUSAND) },
                onCombat = { nav.navigate(Routes.COMBAT) },
                onRule = { nav.navigate(Routes.RULE) },
                onNovena = { nav.navigate(Routes.NOVENA) },
                onFasting = { nav.navigate(Routes.FASTING) },
                onExercises = { nav.navigate(Routes.EXERCISES) },
                onReading = { nav.navigate(Routes.READING) },
                onPenance = { nav.navigate(Routes.PENANCE) }
            )
        }
        composable(Routes.OFFICE) {
            OfficeListScreen(repo, store, onBack = { nav.popBackStack() },
                onHour = { id -> nav.navigate(Routes.hour(id)) })
        }
        composable(
            Routes.OFFICE_HOUR,
            arguments = listOf(navArgument("hourId") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("hourId") ?: "laudes"
            OfficeHourScreen(repo, store, id, onBack = { nav.popBackStack() })
        }
        composable(Routes.BIBLE) {
            BibleScreen(repo, store, onBack = { nav.popBackStack() })
        }
        composable(Routes.CANDLE) {
            CandleGameScreen(repo, store, onBack = { nav.popBackStack() })
        }
        composable(Routes.THOUSAND) {
            ThousandDaysScreen(repo, store, onBack = { nav.popBackStack() })
        }
        composable(Routes.COMBAT) {
            CombatListScreen(repo, store, onBack = { nav.popBackStack() },
                onSin = { id -> nav.navigate(Routes.combatSin(id)) },
                onInfo = { nav.navigate(Routes.COMBAT_INFO) })
        }
        composable(Routes.COMBAT_INFO) {
            CombatInfoScreen(store, onBack = { nav.popBackStack() })
        }
        composable(
            Routes.COMBAT_SIN,
            arguments = listOf(navArgument("sinId") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("sinId") ?: return@composable
            CombatSinScreen(repo, store, id, onBack = { nav.popBackStack() },
                onLevel = { lvl -> nav.navigate(Routes.combatLevel(id, lvl)) })
        }
        composable(
            Routes.COMBAT_LEVEL,
            arguments = listOf(
                navArgument("sinId") { type = NavType.StringType },
                navArgument("level") { type = NavType.IntType }
            )
        ) { entry ->
            val id = entry.arguments?.getString("sinId") ?: return@composable
            val lvl = entry.arguments?.getInt("level") ?: 1
            CombatLevelScreen(repo, store, id, lvl, onBack = { nav.popBackStack() })
        }
        composable(Routes.RULE) {
            RuleScreen(repo, store, onBack = { nav.popBackStack() })
        }
        composable(Routes.NOVENA) {
            NovenaScreen(repo, store, onBack = { nav.popBackStack() })
        }
        composable(Routes.FASTING) {
            FastingScreen(repo, store, onBack = { nav.popBackStack() })
        }
        composable(Routes.EXERCISES) {
            SpiritualExercisesScreen(repo, store, onBack = { nav.popBackStack() })
        }
        composable(Routes.READING) {
            ReadingGameScreen(repo, store, onBack = { nav.popBackStack() })
        }
        composable(Routes.PENANCE) {
            PenanceScreen(store, onBack = { nav.popBackStack() })
        }
    }
}
