package com.bonaventure.psautier.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bonaventure.psautier.data.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentMode: ThemeMode,
    onBack: () -> Unit,
    onModeSelected: (ThemeMode) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réglages", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "APPARENCE",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 1.dp
            ) {
                Column {
                    ThemeOptionRow(
                        icon = Icons.Filled.Brightness4,
                        label = "Automatique (système)",
                        selected = currentMode == ThemeMode.AUTO,
                        onClick = { onModeSelected(ThemeMode.AUTO) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    ThemeOptionRow(
                        icon = Icons.Filled.LightMode,
                        label = "Clair",
                        selected = currentMode == ThemeMode.CLAIR,
                        onClick = { onModeSelected(ThemeMode.CLAIR) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    ThemeOptionRow(
                        icon = Icons.Filled.DarkMode,
                        label = "Sombre",
                        selected = currentMode == ThemeMode.SOMBRE,
                        onClick = { onModeSelected(ThemeMode.SOMBRE) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(14.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = "Sélectionné", tint = MaterialTheme.colorScheme.primary)
        }
    }
}
