package com.byzantine.horologion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byzantine.horologion.R
import com.byzantine.horologion.data.Lang
import com.byzantine.horologion.ui.LocalRepo
import com.byzantine.horologion.ui.LocalSettings
import java.time.LocalDate

/**
 * Opening screen shown each time the app is launched: a single quote that
 * changes every day, set over the skull-and-cross background. A double tap
 * dismisses both the quote and the background, revealing the app.
 */
@Composable
fun SplashScreen(onDismiss: () -> Unit) {
    val repo = LocalRepo.current
    val lang = LocalSettings.current.lang
    val pool = repo.quotePool("splash")
    val q = if (pool.isEmpty()) null else pool[LocalDate.now().dayOfYear % pool.size]

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onDoubleTap = { onDismiss() }) }
    ) {
        Image(
            painter = painterResource(R.drawable.bg_splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xCC000000), Color(0x99000000), Color(0xEE000000)))
            )
        )
        Column(
            Modifier.fillMaxSize().padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (q != null) {
                Text("« ${q.text} »", color = Color(0xFFF3ECE0), textAlign = TextAlign.Center,
                    fontSize = 21.sp, lineHeight = 30.sp, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(14.dp))
                Text(q.src, color = Color(0xFFC9A24B), fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(40.dp))
            Text(
                if (lang == Lang.FR) "Touchez deux fois pour entrer"
                else "Tocca due volte per entrare",
                color = Color(0x99FFFFFF), style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
