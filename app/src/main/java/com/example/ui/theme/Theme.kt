package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GeoPrimaryDark,
    secondary = GeoSecondaryDark,
    tertiary = GeoTertiaryDark,
    background = GeoBackgroundDark,
    surface = GeoSurfaceDark,
    onPrimary = Color(0xFF001D36),
    onSecondary = Color(0xFF001D36),
    onTertiary = Color(0xFF001D36),
    onBackground = GeoOnBackgroundDark,
    onSurface = GeoOnSurfaceDark,
    surfaceVariant = GeoSurfaceVariantDark,
    onSurfaceVariant = GeoOnSurfaceVariantDark,
    outline = GeoOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = GeoPrimaryLight,
    secondary = GeoSecondaryLight,
    tertiary = GeoTertiaryLight,
    background = GeoBackgroundLight,
    surface = GeoSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = GeoOnBackgroundLight,
    onSurface = GeoOnSurfaceLight,
    surfaceVariant = GeoSurfaceVariantLight,
    onSurfaceVariant = GeoOnSurfaceVariantLight,
    outline = GeoOutlineLight
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
