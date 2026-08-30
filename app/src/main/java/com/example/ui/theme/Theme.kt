package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkVedicColorScheme = darkColorScheme(
    primary = VedicGold,
    onPrimary = CosmicDeepNavy,
    primaryContainer = Color(0xFF3A2E00),
    onPrimaryContainer = VedicGold,
    secondary = VedicTeal,
    onSecondary = CosmicDeepNavy,
    secondaryContainer = Color(0xFF00363D),
    onSecondaryContainer = VedicTeal,
    tertiary = VedicSaffron,
    onTertiary = CosmicDeepNavy,
    tertiaryContainer = Color(0xFF4A1A00),
    onTertiaryContainer = Color(0xFFFFDBCF),
    background = CosmicDeepNavy,
    onBackground = TextWhitePrimary,
    surface = CosmicMidnightSurface,
    onSurface = TextWhitePrimary,
    surfaceVariant = CosmicCardSurface,
    onSurfaceVariant = TextSilverSecondary,
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    error = SuryaCrimson,
    onError = Color.White
)

private val LightVedicColorScheme = lightColorScheme(
    primary = Color(0xFF8C6D00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE082),
    onPrimaryContainer = Color(0xFF281E00),
    secondary = Color(0xFF006874),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF97F0FF),
    onSecondaryContainer = Color(0xFF001F24),
    tertiary = Color(0xFF9C4300),
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to stunning Vedic cosmic dark theme
    dynamicColor: Boolean = false, // Keep authentic golden cosmic identity
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkVedicColorScheme else LightVedicColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
