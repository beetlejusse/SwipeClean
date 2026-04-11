package com.app.swipeclean.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SwipeCleanColorScheme =
        lightColorScheme(
                primary = ColorBorder,
                onPrimary = ColorOnDark,
                primaryContainer = ColorAccent,
                background = ColorBackground,
                onBackground = BrutalBlack,
                surface = ColorSurface,
                onSurface = BrutalBlack,
                error = ColorDelete,
                onError = Color.White,
        )

@Composable
fun SwipeCleanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
            colorScheme = SwipeCleanColorScheme,
            typography = SwipeCleanTypography,
            shapes =
                    Shapes( // Zero corner radius everywhere
                            small = androidx.compose.foundation.shape.CircleShape,
                            medium = androidx.compose.foundation.shape.CircleShape,
                            large = androidx.compose.foundation.shape.CircleShape,
                            extraLarge = androidx.compose.foundation.shape.CircleShape,
                    ),
            content = content
    )
}
