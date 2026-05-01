package com.app.swipeclean.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.swipeclean.ui.components.*
import com.app.swipeclean.ui.theme.*
import com.app.swipeclean.util.formatFileSize

@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel(),
    onStartCleaning: (Int) -> Unit,
    onOpenTrash: () -> Unit,
    onOpenStats: () -> Unit,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    
    // Refresh stats when screen becomes visible
    LaunchedEffect(Unit) {
        vm.refreshStats()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrutalCream)
            .padding(top = 52.dp) // Below status bar
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // App wordmark
        Text("SWIPE\nCLEAN", style = MaterialTheme.typography.displayLarge)
        HorizontalDivider(thickness = 2.dp, color = BrutalBlack)

        // Stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BrutalStatCard(
                label = "PHOTOS",
                value = state.totalPhotos.toString(),
                modifier = Modifier.weight(1f)
            )
            BrutalStatCard(
                label = "TOTAL SIZE",
                value = formatFileSize(state.totalGalleryBytes),
                background = BrutalYellow,
                modifier = Modifier.weight(1f)
            )
        }

        // Streak card
        StreakBanner(streak = state.streak)

        // CTA
        BrutalButton(
            text = "START CLEANING →",
            background = BrutalGreen,
            onClick = { onStartCleaning(0) },
            modifier = Modifier.fillMaxWidth()
        )

        // Trash / Stats
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BrutalButton(
                text = "TRASH (${state.trashCount})",
                background = BrutalRed,
                textColor = Color.White,
                onClick = onOpenTrash,
                modifier = Modifier.weight(1f)
            )
            BrutalButton(
                text = "STATS",
                background = BrutalBlack,
                textColor = BrutalYellow,
                onClick = onOpenStats,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}