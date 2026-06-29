package com.preciousblood.devotion.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.preciousblood.devotion.ui.screens.CalendarScreen
import com.preciousblood.devotion.ui.screens.GethsemaniGameScreen
import com.preciousblood.devotion.ui.screens.HomeScreen
import com.preciousblood.devotion.ui.screens.PrayerDetailScreen

private sealed class Dest(val route: String, val label: String, val icon: ImageVector) {
    data object Prayers : Dest("prayers", "Prières", Icons.Filled.MenuBook)
    data object Veillee : Dest("veillee", "Veillée", Icons.Filled.Nightlight)
    data object Calendar : Dest("calendar", "Calendrier", Icons.Filled.CalendarMonth)
}

private const val DETAIL_ROUTE = "prayer/{prayerId}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val tabs = listOf(Dest.Prayers, Dest.Veillee, Dest.Calendar)

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val onDetail = currentRoute == DETAIL_ROUTE

    Scaffold(
        topBar = {
            if (!onDetail) {
                TopAppBar(
                    title = { Text("Précieux Sang") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (!onDetail) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    val dest = backStack?.destination
                    tabs.forEach { tab ->
                        val selected = dest?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                indicatorColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Prayers.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Dest.Prayers.route) {
                HomeScreen(
                    contentPadding = padding,
                    onPrayerClick = { id -> navController.navigate("prayer/$id") }
                )
            }
            composable(Dest.Calendar.route) {
                CalendarScreen(
                    contentPadding = padding,
                    onPrayerClick = { id -> navController.navigate("prayer/$id") }
                )
            }
            composable(Dest.Veillee.route) {
                GethsemaniGameScreen(
                    contentPadding = padding,
                    onOpenPrayer = { id -> navController.navigate("prayer/$id") }
                )
            }
            composable(DETAIL_ROUTE) { entry ->
                val id = entry.arguments?.getString("prayerId") ?: return@composable
                PrayerDetailScreen(prayerId = id, onBack = { navController.popBackStack() })
            }
        }
    }
}
