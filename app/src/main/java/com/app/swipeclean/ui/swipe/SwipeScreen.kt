package com.app.swipeclean.ui.swipe

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun SwipeScreen (
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
            progress = {
                if (state.photos.isEmpty()) 0f
                else state.currentIndex / state.photos.size.toFloat()
            },
            modifier = Modifier.fillMaxWidth().height(8.dp).border(2.dp, BrutalBlack),
            color = BrutalYellow,
            trackColor = Color.White
        )

        // Photo card (swipeable) — takes remaining height
        val currentPhoto = state.photos.getOrNull(state.currentIndex)
        if (currentPhoto != null) {
            PhotoSwipeCard(
                photo = currentPhoto,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
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

@Composable
private fun SwipeTopBar(
    currentIndex: Int,
    total: Int,
    canUndo: Boolean,
    onBack: () -> Unit,
    onUndo: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrutalButton(
            text = "← BACK",
            onClick = onBack,
            background = Color.White,
            textColor = BrutalBlack,
            modifier = Modifier.width(110.dp)
        )

        Text(
            text = "${(currentIndex + 1).coerceAtMost(total.coerceAtLeast(1))} / ${total.coerceAtLeast(1)}",
            style = MaterialTheme.typography.titleMedium,
            color = BrutalBlack
        )

        BrutalButton(
            text = "↶ UNDO",
            onClick = onUndo,
            background = if (canUndo) BrutalYellow else Color.LightGray,
            textColor = BrutalBlack,
            modifier = Modifier.width(110.dp)
        )
    }
}