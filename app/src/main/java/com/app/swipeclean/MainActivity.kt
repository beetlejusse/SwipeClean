package com.app.swipeclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.app.swipeclean.ui.navigation.NavGraph
import com.app.swipeclean.ui.theme.SwipeCleanTheme
import dagger.hilt.android.AndroidEntryPoint
// @AndroidEntryPoint enables Hilt injection in this Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge rendering — content draws under status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SwipeCleanTheme {
                NavGraph() // The entire app UI lives here
            }
        }
    }
}