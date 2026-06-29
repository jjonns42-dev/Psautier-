package com.preciousblood.devotion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.preciousblood.devotion.data.PrayerBlock
import com.preciousblood.devotion.data.PrayerBlock.*

@Composable
fun PrayerBlockView(block: PrayerBlock, modifier: Modifier = Modifier) {
    when (block) {
        is Heading -> Text(
            text = block.text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 6.dp)
        )

        is Paragraph -> Text(
            text = block.text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        )

        is Versicle -> Column(
            modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            VersicleLine("℣", block.leader, FontWeight.Normal)
            VersicleLine("℟", block.response, FontWeight.SemiBold)
        }

        is Chant -> Text(
            text = block.text,
            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )

        is Rubric -> Text(
            text = block.text,
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        )

        is Numbered -> Row(
            modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text(
                text = "${block.number}.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(28.dp)
            )
            Text(
                text = block.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun VersicleLine(symbol: String, text: String, weight: FontWeight) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 6.dp)
        )
        Text(
            text = "  $text",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = weight,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
