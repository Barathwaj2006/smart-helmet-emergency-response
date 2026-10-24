package com.smarthelmet.emergency.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SurakshaColorScheme = lightColorScheme(
    primary = PrimaryGolden,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = TextSecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = SurfaceContainerHighest,
    onSecondaryContainer = OnSurface,
    tertiary = TertiaryRed,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Surface,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariant,
    outline = OutlineVariant
)

@Composable
fun SmartHelmetTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SurakshaColorScheme,
        typography = Typography,
        content = content
    )
}
