package com.liturgia.monastica.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.Brightness3
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.liturgia.monastica.R
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.ui.components.AppScaffold

@Composable
fun HomeScreen(
    store: GameStore,
    onOffice: () -> Unit,
    onBible: () -> Unit,
    onCandle: () -> Unit,
    onThousand: () -> Unit,
    onCombat: () -> Unit,
    onRule: () -> Unit,
    onNovena: () -> Unit,
    onFasting: () -> Unit,
    onExercises: () -> Unit,
    onReading: () -> Unit
) {
    val it = store.lang == "it"
    AppScaffold(
        title = "Liturgia Monastica",
        night = store.night,
        onToggleNight = { store.toggleNight() }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(18.dp))
            Image(
                painter = painterResource(R.drawable.benedict_medal),
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                if (it) "Ufficio monastico benedettino" else "Office monastique bénédictin",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                "« Ora et labora »",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(Modifier.height(16.dp))

            // Language toggle
            LanguageToggle(store)
            Spacer(Modifier.height(16.dp))

            Tile(
                title = if (it) "Liturgia delle Ore" else "Liturgie des Heures",
                subtitle = if (it) "Le otto ore monastiche" else "Les huit heures monastiques",
                icon = Icons.Filled.WbTwilight, onClick = onOffice
            )
            Tile(
                title = if (it) "Bibbia" else "La Bible",
                subtitle = if (it) "Salmi e Vangeli (Crampon)" else "Psaumes et Évangiles (Crampon)",
                icon = Icons.AutoMirrored.Filled.MenuBook, onClick = onBible
            )
            Tile(
                title = if (it) "La Candela" else "La Bougie",
                subtitle = if (it) "Pregare ogni giorno, una settimana" else "Prier chaque jour, une semaine",
                icon = Icons.Filled.LocalFireDepartment, onClick = onCandle
            )
            Tile(
                title = if (it) "1000 Giorni" else "1000 Jours",
                subtitle = if (it) "Una devozione, mille giorni" else "Une dévotion, mille jours",
                icon = Icons.Outlined.Brightness3, onClick = onThousand
            )
            Tile(
                title = if (it) "Il Combattimento" else "Le Combat",
                subtitle = if (it) "Le otto passioni, poi vigilanza perpetua" else "Les huit pensées, puis vigilance perpétuelle",
                icon = Icons.Filled.Shield, onClick = onCombat
            )
            Tile(
                title = if (it) "La Regola" else "La Règle",
                subtitle = if (it) "Regola di preghiera, livelli infiniti" else "Règle de prière, niveaux infinis",
                icon = Icons.Filled.SelfImprovement, onClick = onRule
            )
            Tile(
                title = if (it) "Novena" else "Neuvaine",
                subtitle = if (it) "9, 30, 40 o 54 giorni" else "9, 30, 40 ou 54 jours",
                icon = Icons.Filled.CalendarMonth, onClick = onNovena
            )
            Tile(
                title = if (it) "Calendario dei digiuni" else "Calendrier de jeûne",
                subtitle = if (it) "Tradizioni ortodosse e cattoliche" else "Traditions orthodoxes et catholiques",
                icon = Icons.Filled.NoFood, onClick = onFasting
            )
            Tile(
                title = if (it) "Esercizi spirituali" else "Exercices spirituels",
                subtitle = if (it) "Scrittura ed insegnamento dei Padri" else "Écriture et enseignement des Pères",
                icon = Icons.Filled.AutoStories, onClick = onExercises
            )
            Tile(
                title = if (it) "Gioco di lettura" else "Jeu de lecture",
                subtitle = if (it) "Bibbia, teologia, mistica, Padri — livelli infiniti" else "Bible, théologie, mystique, Pères — niveaux infinis",
                icon = Icons.Filled.Stairs, onClick = onReading
            )
        }
    }
}

@Composable
private fun LanguageToggle(store: GameStore) {
    Row(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp)
    ) {
        listOf("fr" to "Français", "it" to "Italiano").forEach { (code, label) ->
            val selected = store.lang == code
            Text(
                label,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { store.applyLang(code) }
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun Tile(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
