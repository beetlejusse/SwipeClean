package com.app.swipeclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.app.swipeclean.ui.navigation.NavGraph
import com.app.swipeclean.ui.theme.BrutalCream
import com.app.swipeclean.ui.theme.SwipeCleanTheme
import dagger.hilt.android.AndroidEntryPoint
// @AndroidEntryPoint enables Hilt injection in this Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge rendering with proper system bar colors
        enableEdgeToEdge()
        
        // Set system bar colors to match brutal theme
        window.statusBarColor = BrutalCream.toArgb()
        window.navigationBarColor = BrutalCream.toArgb()
        
        // Use dark icons on light background
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        
        setContent {
            SwipeCleanTheme {
                NavGraph() // The entire app UI lives here
            }
        }
    }
}