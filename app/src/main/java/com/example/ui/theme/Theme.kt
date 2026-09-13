package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Navy900,
    onPrimary = Color.White,
    primaryContainer = SurfaceVariantLight,
    onPrimaryContainer = Navy900,
    secondary = BlueAccent,
    onSecondary = Color.White,
    secondaryContainer = BlueLight.copy(alpha = 0.15f),
    onSecondaryContainer = Navy900,
    tertiary = BlueLight,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = Navy900,
    primaryContainer = Navy800,
    onPrimaryContainer = Color.White,
    secondary = BlueAccent,
    onSecondary = Color.White,
    background = Navy900,
    onBackground = Color.White,
    surface = Navy800,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
