package com.smarthelmet.emergency.ui.theme

import androidx.compose.ui.graphics.Color

// Suraksha Helmet Safety Theme Tokens (from HTML design specification)
val Surface = Color(0xFFF9F9FA)
val SurfaceContainer = Color(0xFFEEEEEF)
val SurfaceContainerLow = Color(0xFFF3F3F4)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerHigh = Color(0xFFE8E8E9)
val SurfaceContainerHighest = Color(0xFFE2E2E3)

val OnSurface = Color(0xFF1A1C1D)
val OnSurfaceVariant = Color(0xFF4F4632)
val OutlineVariant = Color(0xFFD2C5AB)
val Outline = Color(0xFF81765F)
val TextSecondaryColor = Color(0xFF5F5E5E)
val TextMutedColor = Color(0xFF81765F)

val PrimaryContainer = Color(0xFFFFC700) // Tactile Safety Yellow
val OnPrimaryContainer = Color(0xFF6E5400)
val PrimaryGolden = Color(0xFF765B00)
val PrimaryFixedDim = Color(0xFFF5BF00)

val TertiaryRed = Color(0xFFBA1A20) // High-G Impact / Emergency Red
val TertiaryContainer = Color(0xFFFFBFB9)
val OnTertiaryContainer = Color(0xFFB0111A)
val ErrorContainer = Color(0xFFFFDAD6)

val InverseSurface = Color(0xFF2F3132) // Dark Charcoal
val InverseOnSurface = Color(0xFFF0F1F2)

val EmeraldLive = Color(0xFF059669) // Connected BLE / Active Trip
val EmeraldLight = Color(0xFFD1FAE5)
val EmeraldDark = Color(0xFF065F46)

// Compatibility aliases
val NavyDeep = InverseSurface
val NavySurface = SurfaceContainerLowest
val NavyCard = SurfaceContainerLow
val NavyBorder = SurfaceContainerHigh
val EmergencyRed = TertiaryRed
val EmergencyRedDark = Color(0xFF930010)
val EmergencyRedLight = Color(0xFFFF5449)
val WarningAmber = PrimaryContainer
val SafeGreen = EmeraldLive
val TextPrimary = OnSurface
val TextSecondary = TextSecondaryColor
val TextMuted = TextMutedColor
val AccentCyan = Color(0xFF0284C7)
