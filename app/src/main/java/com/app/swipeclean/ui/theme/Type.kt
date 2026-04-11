package com.app.swipeclean.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Mono = FontFamily.Monospace
val SwipeCleanTypography = Typography(
// App title
    displayLarge = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Bold,
        fontSize = 34.sp, lineHeight = 38.sp,
        letterSpacing = (-0.5).sp
    ),
// Section headers 
    headlineMedium = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
// Metric numbers 
    headlineSmall = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 26.sp
    ),
// Body / labels
    bodyMedium = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Normal,
        fontSize = 10.sp, lineHeight = 14.sp,
        letterSpacing = 1.5.sp
    ),
// Captions / file metadata
    labelSmall = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Normal,
        fontSize = 8.sp, lineHeight = 11.sp,
        letterSpacing = 0.8.sp
    ),
)
