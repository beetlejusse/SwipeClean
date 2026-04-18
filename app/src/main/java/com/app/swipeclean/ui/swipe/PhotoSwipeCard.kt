package com.app.swipeclean.ui.swipe

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.swipeclean.data.model.Photo
import com.app.swipeclean.ui.theme.*
import kotlin.math.roundToInt

private const val SWIPE_THRESHOLD = 200f // px — how far to drag before triggering
private const val MAX_ROTATION = 12f // degrees of tilt at full drag

@Composable
fun PhotoSwipeCard (
    photo: Photo,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {

    //offsetX is how far the use has dragged the card horizontally
    var offsetX by remember { mutableFloatStateOf(0f) }

    //Rotation is proportional to drag distance, with a max tilt at SWIPE_THRESHOLD - feels physical
    val rotation by animateFloatAsState(
        targetValue = (offsetX / SWIPE_THRESHOLD) * MAX_ROTATION,
        label = "card-rotation"
    )

    // Show DELETE hint when dragging left, KEEP hint when dragging right
    val showDelete = offsetX < -60f
    val showKeep = offsetX > 60f
    Box(
        modifier = modifier.offset{IntOffset(offsetX.roundToInt(), 0)}.rotate(rotation)
            .border(2.5.dp, when {
                showDelete -> BrutalRed
                showKeep -> BrutalGreen
                else -> BrutalBlack
            })
            .background(BrutalBlack).pointerInput(photo.uri) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX < -SWIPE_THRESHOLD -> { onSwipeLeft(); offsetX = 0f }
                            offsetX > SWIPE_THRESHOLD -> { onSwipeRight(); offsetX = 0f }
                            else -> offsetX = 0f // snap back to center
                        }
                    },
                    onDragCancel = { offsetX = 0f },
                    onHorizontalDrag = { _, delta -> offsetX += delta }
                )
            }
    ) {
        // In a real app, load the photo thumbnail here
        AsyncImage(
            model = photo.uri,
            contentDescription = photo.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        //Delete Hint Overlay i.e. on top left
        if(showDelete) {
            Box(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp).background(BrutalRed)
                    .border(2.dp, Color.White).padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text("DELETE", color = Color.White,
                style = MaterialTheme.typography.bodyMedium)
            }
        }

        // KEEP hint overlay (top-right)
        if (showKeep) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(BrutalGreen)
                    .border(2.dp, BrutalBlack)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text("KEEP", color = BrutalBlack,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

        // File info at bottom
        Row (
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(BrutalBlack)
                .border(2.dp, BrutalBlack)
                .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(photo.displayName.take(22), color = BrutalYellow,
                style = MaterialTheme.typography.labelSmall)
            Text("% .1f MB".format(photo.sizeBytes / 1_048_576f),
                color = BrutalYellow, style = MaterialTheme.typography.labelSmall)
        }
    }
}