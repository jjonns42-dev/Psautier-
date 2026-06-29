package com.preciousblood.devotion.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.preciousblood.devotion.data.PrayerBlock
import com.preciousblood.devotion.data.PrayerData
import com.preciousblood.devotion.data.PrayerParser
import com.preciousblood.devotion.ui.components.PrayerBlockView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerDetailScreen(prayerId: String, onBack: () -> Unit) {
    val prayer = PrayerData.findPrayer(prayerId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        prayer?.title ?: "Prière",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (prayer == null) {
            Text(
                "Prière introuvable.",
                modifier = Modifier.padding(padding).padding(24.dp)
            )
            return@Scaffold
        }
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                prayer.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            if (prayer.source.isNotBlank()) {
                Text(
                    "Révélé le ${prayer.source}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            if (prayer.page > 0) {
                Text(
                    "Page ${prayer.page} du livre imprimé",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))

            val context = LocalContext.current
            val blocks: List<PrayerBlock> by produceState(
                initialValue = prayer.body,
                key1 = prayer.id
            ) {
                value = if (prayer.assetFile != null) {
                    PrayerParser.load(context, prayer.assetFile)
                } else {
                    prayer.body
                }
            }

            blocks.forEach { block ->
                PrayerBlockView(block, modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
