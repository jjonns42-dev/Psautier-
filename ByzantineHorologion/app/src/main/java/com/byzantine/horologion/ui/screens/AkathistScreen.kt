package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.byzantine.horologion.R
import com.byzantine.horologion.data.Akathist
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings

private fun iconRes(name: String): Int = when (name) {
    "icon_christ" -> R.drawable.icon_christ
    "icon_theotokos" -> R.drawable.icon_theotokos
    "icon_joseph" -> R.drawable.icon_joseph
    "icon_michael" -> R.drawable.icon_michael
    else -> R.drawable.ic_cross_red
}

@Composable
fun AkathistScreen() {
    val repo = LocalRepo.current
    val lang = LocalSettings.current.lang
    val list = repo.akathists
    var selected by remember { mutableStateOf<Akathist?>(null) }

    if (selected == null) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            list.forEach { a ->
                Card(
                    onClick = { selected = a },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(iconRes(a.icon)),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(14.dp))
                        Text(a.title(lang), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    } else {
        val a = selected!!
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            TextButton(onClick = { selected = null }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                Spacer(Modifier.width(6.dp))
                Text(if (lang == com.byzantine.horologion.data.Lang.FR) "Retour" else "Indietro")
            }

            // Saint icon at the top of the text, as requested
            Image(
                painter = painterResource(iconRes(a.icon)),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(10.dp))
            Text(
                a.title(lang),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))
            // Standing 30-days note, emphasised
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        if (lang == com.byzantine.horologion.data.Lang.FR)
                            "Prière debout — 30 jours" else "Preghiera in piedi — 30 giorni",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        if (lang == com.byzantine.horologion.data.Lang.FR) repo.akathistNoteFr else repo.akathistNoteIt,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Kondakion 1", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)
            Text(a.kontakion1(lang), style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(12.dp))
            Text(
                a.refrain(lang),
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))
            Text(a.body(lang), style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(30.dp))
        }
    }
}
