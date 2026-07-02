package org.maronite.shhimo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.maronite.shhimo.data.model.AppLanguage
import org.maronite.shhimo.ui.common.ShhimoTheme
import org.maronite.shhimo.ui.office.OfficeScreen
import org.maronite.shhimo.ui.office.OfficeViewModel
import org.maronite.shhimo.ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {

    private val officeViewModel: OfficeViewModel by viewModels {
        val app = application as ShhimoApp
        OfficeViewModel.Factory(app.liturgyRepository, app.settingsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by officeViewModel.state.collectAsStateWithLifecycle()
            ShhimoTheme(fontScale = state.fontScale) {
                // L'arabe s'affiche de droite à gauche.
                val direction = if (state.language == AppLanguage.ARABIC) {
                    LayoutDirection.Rtl
                } else {
                    LayoutDirection.Ltr
                }
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Surface(Modifier.fillMaxSize()) {
                        RootScaffold(officeViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun RootScaffold(officeViewModel: OfficeViewModel) {
    val navController = rememberNavController()
    val state by officeViewModel.state.collectAsStateWithLifecycle()
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = current == "office",
                    onClick = { navController.navigate("office") { launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = null) },
                    label = { Text(officeLabel(state.language)) }
                )
                NavigationBarItem(
                    selected = current == "settings",
                    onClick = { navController.navigate("settings") { launchSingleTop = true } },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(settingsLabel(state.language)) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "office",
            modifier = Modifier.padding(padding)
        ) {
            composable("office") {
                OfficeScreen(officeViewModel, onOpenSettings = { navController.navigate("settings") })
            }
            composable("settings") {
                SettingsScreen(
                    currentLanguage = state.language,
                    fontScale = state.fontScale,
                    onLanguageChange = officeViewModel::setLanguage,
                    onFontScaleChange = officeViewModel::setFontScale
                )
            }
        }
    }
}

private fun officeLabel(l: AppLanguage) = when (l) {
    AppLanguage.ARABIC -> "المكتب"; AppLanguage.ITALIAN -> "Ufficio"; AppLanguage.SYRIAC -> "Office"
}
private fun settingsLabel(l: AppLanguage) = when (l) {
    AppLanguage.ARABIC -> "الإعدادات"; AppLanguage.ITALIAN -> "Impostazioni"; AppLanguage.SYRIAC -> "Settings"
}
