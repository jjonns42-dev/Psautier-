package com.bonaventure.psautier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bonaventure.psautier.data.ThemeMode
import com.bonaventure.psautier.ui.PsautierViewModel
import com.bonaventure.psautier.ui.screens.DaysListScreen
import com.bonaventure.psautier.ui.screens.HourDetailScreen
import com.bonaventure.psautier.ui.screens.HoursListScreen
import com.bonaventure.psautier.ui.screens.SearchScreen
import com.bonaventure.psautier.ui.screens.SettingsScreen
import com.bonaventure.psautier.ui.theme.PsautierTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PsautierApp()
        }
    }
}

private object Routes {
    const val DAYS = "days"
    const val HOURS = "hours/{jourOrdre}"
    const val HOUR_DETAIL = "hour/{jourOrdre}/{heureIndex}"
    const val SEARCH = "search"
    const val SETTINGS = "settings"

    fun hours(jourOrdre: Int) = "hours/$jourOrdre"
    // On route par index plutôt que par nom d'heure, pour éviter tout souci
    // d'encodage d'URL avec les accents ("Vêpres", etc.) dans Navigation Compose.
    fun hourDetail(jourOrdre: Int, heureIndex: Int) = "hour/$jourOrdre/$heureIndex"
}

@Composable
fun PsautierApp() {
    val viewModel: PsautierViewModel = viewModel()
    val themeMode by viewModel.themeMode.collectAsState()

    val darkTheme = when (themeMode) {
        ThemeMode.AUTO -> isSystemInDarkTheme()
        ThemeMode.CLAIR -> false
        ThemeMode.SOMBRE -> true
    }

    PsautierTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            val psautier by viewModel.psautier.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val searchResults by viewModel.searchResults.collectAsState()

            // Tant que le JSON n'est pas chargé, on ne construit pas le NavHost
            // pour éviter les écrans vides; ce chargement est quasi instantané.
            if (psautier == null) return@Surface

            NavHost(navController = navController, startDestination = Routes.DAYS) {

                composable(Routes.DAYS) {
                    DaysListScreen(
                        jours = psautier!!.jours,
                        onDayClick = { jour -> navController.navigate(Routes.hours(jour.ordre)) },
                        onSearchClick = { navController.navigate(Routes.SEARCH) },
                        onSettingsClick = { navController.navigate(Routes.SETTINGS) }
                    )
                }

                composable(
                    route = Routes.HOURS,
                    arguments = listOf(navArgument("jourOrdre") { type = NavType.IntType })
                ) { backStackEntry ->
                    val jourOrdre = backStackEntry.arguments?.getInt("jourOrdre") ?: 1
                    val jour = psautier!!.jours.firstOrNull { it.ordre == jourOrdre }
                    if (jour != null) {
                        HoursListScreen(
                            jour = jour,
                            onBack = { navController.popBackStack() },
                            onHourClick = { heure ->
                                val idx = jour.heures.indexOf(heure)
                                navController.navigate(Routes.hourDetail(jourOrdre, idx))
                            }
                        )
                    }
                }

                composable(
                    route = Routes.HOUR_DETAIL,
                    arguments = listOf(
                        navArgument("jourOrdre") { type = NavType.IntType },
                        navArgument("heureIndex") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val jourOrdre = backStackEntry.arguments?.getInt("jourOrdre") ?: 1
                    val heureIndex = backStackEntry.arguments?.getInt("heureIndex") ?: 0
                    val jour = psautier!!.jours.firstOrNull { it.ordre == jourOrdre }
                    val heure = jour?.heures?.getOrNull(heureIndex)
                    if (jour != null && heure != null) {
                        HourDetailScreen(
                            jourNom = jour.jour,
                            heure = heure,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }

                composable(Routes.SEARCH) {
                    SearchScreen(
                        query = searchQuery,
                        results = searchResults,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onBack = {
                            viewModel.clearSearch()
                            navController.popBackStack()
                        },
                        onResultClick = { result ->
                            navController.navigate(Routes.hourDetail(result.jourOrdre, result.heureIndex)) {
                                // On revient à la liste des jours en arrière-plan de la pile,
                                // pour permettre un retour cohérent depuis le détail.
                            }
                        }
                    )
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        currentMode = themeMode,
                        onBack = { navController.popBackStack() },
                        onModeSelected = { mode -> viewModel.setThemeMode(mode) }
                    )
                }
            }
        }
    }
}
