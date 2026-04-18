package com.app.swipeclean.ui.swipe

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.swipeclean.ui.theme.*
import com.app.swipeclean.ui.components.*

@Composable
fun swipeScreen (
    sessionGoal: Int = 0,
    onBack: () -> Unit,
    vm: SwipeViewModel = hiltViewModel()
) {

    val state by vm.state.collectAsStateWithLifecycle()

    // DisposableEffect calls saveSession when this composable leaves composition
    DisposableEffect(Unit) {onDispose { vm.saveSession() }}

    Column (
        modifier = Modifier.fillMaxSize().background(BrutalCream).padding(top = 52.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Top bar: back + counter + undo
        SwipeTopBar(
            currentIndex = state.currentIndex,
            total = state.photos.size,
            canUndo = state.canUndo,
            onBack = onBack,
            onUndo = { vm.onUndo() }
        )

        // Progress bar
        LinearProgressIndicator(
            progress = { if (state.photos.isEmpty()) 0f
            else state.currentIndex / state.photos.size.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp).border(2.dp, BrutalBlack),
            color = BrutalYellow,
            trackColor = Color.White
        )

        // Photo card (swipeable) — takes remaining height
        val currentPhoto = state.photos.getOrNull(state.currentIndex)
        if (currentPhoto != null) {
            PhotoSwipeCard(
                photo = currentPhoto,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                onSwipeLeft = { vm.onSwipeLeft() },
                onSwipeRight = { vm.onSwipeRight() },
            )
        }

        //Delete or Keep button
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BrutalButton(
                text = "✕ DELETE",
                onClick = { vm.onSwipeLeft() },
                background = BrutalRed,
                textColor = Color.White,
                modifier = Modifier.weight(1f)
            )
            BrutalButton(
                text = "✓ KEEP",
                onClick = { vm.onSwipeRight() },
                background = BrutalGreen,
                textColor = BrutalBlack,
                modifier = Modifier.weight(1f)
            )
        }
    }
}