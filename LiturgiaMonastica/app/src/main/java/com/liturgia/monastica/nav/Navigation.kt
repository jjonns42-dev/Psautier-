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
    fun hour(id: String) = "office/$id"
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
                onThousand = { nav.navigate(Routes.THOUSAND) }
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
    }
}
