package com.example.a221505_cikgu_izwan_plantlogs.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

// COLOUR SCHEMES
private val LightColorScheme = lightColorScheme(
    primary            = Green40,
    onPrimary          = PLWhite,
    primaryContainer   = PLLight,
    onPrimaryContainer = PLTextDark,
    secondary          = GreenGrey40,
    onSecondary        = PLWhite,
    background         = Neutral99,
    onBackground       = PLTextDark,
    surface            = PLWhite,
    onSurface          = PLTextDark,
    onSurfaceVariant   = PLTextMid,
    error              = Color(0xFFB00020)
)

// DARK MODE
private val DarkColorScheme = darkColorScheme(
    primary            = DarkGreen80,
    onPrimary          = DarkNavy10,
    primaryContainer   = Color(0xFF1F3A30),
    onPrimaryContainer = DarkGreen80,
    secondary          = PLAccent,
    onSecondary        = DarkNavy10,
    background         = DarkNavy10,
    onBackground       = DarkOnSurface,
    surface            = DarkSurface,
    onSurface          = DarkOnSurface,
    onSurfaceVariant   = PLTextMidDarkMode,
    error              = Color(0xFFFF8A80)
)

// TYPOGRAPHY
val PlantLogTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize   = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize   = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize   = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize   = 10.sp,
        lineHeight = 14.sp
    )
)

// SHAPES
val PlantLogShapes = Shapes(
    small  = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large  = RoundedCornerShape(24.dp)
)

// THEME
@Composable
fun PlantLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = PlantLogTypography,
        shapes      = PlantLogShapes,
        content     = content
    )
}