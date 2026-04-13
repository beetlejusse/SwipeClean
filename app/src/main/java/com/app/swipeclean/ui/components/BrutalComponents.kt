package com.app.swipeclean.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.swipeclean.ui.theme.*

// The signature offset shadow — draws black rect behind the element
fun Modifier.brutalShadow(offset: Dp = 5.dp, color: Color = BrutalBlack) =
    drawBehind {
        drawRect(
            color = color,
            topLeft = Offset(offset.toPx(), offset.toPx()),
            size = size,
        )
    }

// Standard card with brutal border + shadow
@Composable
fun BrutalCard(
    modifier: Modifier = Modifier,
    background: Color = Color.White,
    shadowOffset: Dp = 5.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .brutalShadow(shadowOffset)
            .border(2.dp, BrutalBlack)
            .background(background)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

// Stat card: big number + small label
@Composable
fun BrutalStatCard(
    value: String, label: String,
    background: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    BrutalCard(modifier = modifier, background = background) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = BrutalBlack.copy(alpha = 0.5f))
    }
}
// Full-width clickable button with brutal border
@Composable
fun BrutalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.White,
    textColor: Color = BrutalBlack,
) {
    Box(
        modifier = modifier
            .brutalShadow(4.dp)
            .border(2.dp, BrutalBlack)
            .background(background)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium, color = textColor)
    }
}
// Streak banner: black background with yellow text
@Composable
fun StreakBanner(streak: Int, modifier: Modifier = Modifier) {
    BrutalCard(modifier = modifier.fillMaxWidth(), background = BrutalBlack) {
        Text("CURRENT STREAK",
            style = MaterialTheme.typography.bodyMedium,
            color = BrutalYellow.copy(alpha = 0.7f))
        Text("$streak DAY${if (streak != 1) "S" else ""}",
            style = MaterialTheme.typography.headlineMedium,
            color = BrutalYellow)
    }
}