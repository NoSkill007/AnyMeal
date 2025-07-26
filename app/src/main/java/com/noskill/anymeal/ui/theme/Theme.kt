package com.noskill.anymeal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = Background,
    onBackground = Text,
    surface = Surface,
    onSurface = Text,
    outline = BorderLight,
    error = ErrorRed,
    secondary = Accent,
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.Black,
    background = BackgroundDark,
    onBackground = TextDark,
    surface = SurfaceDark,
    onSurface = TextDark,
    outline = BorderDark,
    error = ErrorRed,
    secondary = AccentDark,
)

// ----------- EXTENSIONES PARA COLORES CUSTOM -----------

// Color para fondos de inputs
val ColorScheme.fieldBg: Color
    get() = if (isLight) FieldBg else FieldBgDark

// Texto secundario
val ColorScheme.textSecondary: Color
    get() = if (isLight) TextSecondary else TextSecondaryDark

// Color de acento
val ColorScheme.accent: Color
    get() = if (isLight) Accent else AccentDark

// Utilidad para detectar modo claro/oscuro de manera robusta
val ColorScheme.isLight: Boolean
    get() = background == Background

// ----------- THEME COMPOSABLE -----------

@Composable
fun AnyMealTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
