package com.applicforge.umbalarm.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF007AFF),
    secondary = Color(0xFF34C759),
    tertiary = Color(0xFFFF9500)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007AFF),
    secondary = Color(0xFF34C759),
    tertiary = Color(0xFFFF9500)
)

@Composable
fun TestAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 