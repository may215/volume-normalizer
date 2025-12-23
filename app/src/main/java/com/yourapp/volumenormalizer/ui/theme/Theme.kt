package com.yourapp.volumenormalizer.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Defines the custom Compose theme used by the app. Relies on a dark palette
 * reminiscent of volume booster apps. If you wish to adjust accent colors
 * simply modify the values here.
 */
private val DarkColorPalette = darkColors(
    primary = Color(0xFF1F1F5F),
    secondary = Color(0xFFFF4081),
    background = Color(0xFF121212),
    surface = Color(0xFF1F1F5F),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun VolumeNormalizerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
