package com.smarthelmet.emergency.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    primaryContainer = EmergencyRedDark,
    onPrimaryContainer = Color.White,
    secondary = AccentCyan,
    onSecondary = NavyDeep,
    secondaryContainer = NavyCard,
    onSecondaryContainer = TextPrimary,
    tertiary = WarningAmber,
    onTertiary = NavyDeep,
    background = NavyDeep,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder
)

@Composable
fun SmartHelmetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Professional dark navy theme with high visibility emergency accents
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
