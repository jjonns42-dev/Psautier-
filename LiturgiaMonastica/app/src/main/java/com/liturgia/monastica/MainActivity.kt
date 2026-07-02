package com.liturgia.monastica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.liturgia.monastica.data.ContentRepository
import com.liturgia.monastica.data.GameStore
import com.liturgia.monastica.nav.AppNavGraph
import com.liturgia.monastica.ui.theme.LiturgiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val store = remember { GameStore(applicationContext) }
            val repo = remember { ContentRepository(applicationContext) }
            // Keep streaks honest across launches.
            LaunchedEffect(Unit) {
                store.candleRefresh()
                store.thRefresh()
                store.combatRefresh()
                store.ruleRefresh()
            }
            LiturgiaTheme(night = store.night) {
                AppNavGraph(repo, store)
            }
        }
    }
}
