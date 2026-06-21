package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings

@Composable
fun BasilRuleScreen() {
    val repo = LocalRepo.current
    val lang = LocalSettings.current.lang
    var tab by remember { mutableIntStateOf(0) }

    val grandes = remember { repo.basilGrandes() }
    val petites = remember { repo.basilPetites() }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 },
                text = { Text(if (lang == Lang.FR) "Grandes Règles" else "Grandi Regole") })
            Tab(selected = tab == 1, onClick = { tab = 1 },
                text = { Text(if (lang == Lang.FR) "Petites Règles" else "Piccole Regole") })
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text(
                if (lang == Lang.FR)
                    "Saint Basile le Grand — texte intégral (français)."
                else
                    "San Basilio Magno — testo integrale (in francese).",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(10.dp))
            Text(
                if (tab == 0) grandes else petites,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(30.dp))
        }
    }
}
