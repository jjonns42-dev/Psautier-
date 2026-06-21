package com.byzantine.horologion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.byzantine.horologion.R
import com.byzantine.horologion.data.AppSettings
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.data.LiturgicalEngine
import com.byzantine.horologion.data.Repository
import com.byzantine.horologion.ui.components.OrthodoxCross
import com.byzantine.horologion.ui.screens.*
import kotlinx.coroutines.launch

val LocalSettings = staticCompositionLocalOf<AppSettings> { error("no settings") }
val LocalRepo = staticCompositionLocalOf<Repository> { error("no repo") }
val LocalEngine = staticCompositionLocalOf<LiturgicalEngine> { error("no engine") }

data class Dest(val route: String, val labelRes: Int, val icon: ImageVector)

private val DESTS = listOf(
    Dest("home", R.string.nav_home, Icons.Filled.Home),
    Dest("office", R.string.nav_office, Icons.Filled.WbTwilight),
    Dest("calendar", R.string.nav_calendar, Icons.Filled.CalendarMonth),
    Dest("jesus", R.string.nav_jesus, Icons.Filled.SelfImprovement),
    Dest("akathist", R.string.nav_akathist, Icons.Filled.AutoAwesome),
    Dest("basil", R.string.nav_basil, Icons.Filled.Gavel),
    Dest("bible", R.string.nav_bible, Icons.AutoMirrored.Filled.MenuBook),
    Dest("chants", R.string.nav_chants, Icons.Filled.MusicNote),
    Dest("settings", R.string.nav_settings, Icons.Filled.Settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(onLanguageChange: (Lang) -> Unit) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showSplash by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    OrthodoxCross(
                        Modifier.size(34.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
                }
                HorizontalDivider()
                val current by nav.currentBackStackEntryAsState()
                val route = current?.destination?.route
                DESTS.forEach { d ->
                    NavigationDrawerItem(
                        icon = { Icon(d.icon, null) },
                        label = { Text(stringResource(d.labelRes)) },
                        selected = route == d.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (route != d.route) nav.navigate(d.route) {
                                launchSingleTop = true
                                popUpTo("home")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
            }
        }
    ) {
        val current by nav.currentBackStackEntryAsState()
        val routeNow = current?.destination?.route ?: "home"
        val titleRes = DESTS.firstOrNull { it.route == routeNow }?.labelRes ?: R.string.app_name

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(titleRes)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { pad ->
            NavHost(
                navController = nav,
                startDestination = "home",
                modifier = Modifier.padding(pad)
            ) {
                composable("home") { HomeScreen(nav) }
                composable("office") { OfficeScreen() }
                composable("calendar") { CalendarScreen() }
                composable("jesus") { JesusPrayerScreen() }
                composable("akathist") { AkathistScreen() }
                composable("basil") { BasilRuleScreen() }
                composable("bible") { BibleScreen() }
                composable("chants") { ChantScreen() }
                composable("settings") { SettingsScreen(onLanguageChange) }
            }
        }
    }
        if (showSplash) SplashScreen { showSplash = false }
    }
}

/** Small helper used across screens. */
@Composable
fun ScrollColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        content = content
    )
}
