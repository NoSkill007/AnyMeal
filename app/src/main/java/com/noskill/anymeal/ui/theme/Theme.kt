/**
 * Theme.kt
 *
 * Propósito: Define el sistema de temas de la aplicación AnyMeal utilizando Material 3.
 * Configura esquemas de colores para temas claro y oscuro, extensiones de propiedades
 * personalizadas y el composable principal para aplicar el tema a toda la aplicación.
 * Esta es la pieza central del sistema de diseño de la aplicación.
 */
package com.noskill.anymeal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Esquema de colores para el tema claro de la aplicación.
 * Define los colores estándar de Material Design adaptados a la identidad visual de AnyMeal.
 */
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

/**
 * Esquema de colores para el tema oscuro de la aplicación.
 * Adapta los colores para proporcionar una experiencia confortable en condiciones de poca luz,
 * manteniendo la identidad visual de la aplicación.
 */
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

/**
 * ----------- EXTENSIONES PARA COLORES PERSONALIZADOS -----------
 * Las siguientes propiedades extienden ColorScheme para añadir colores adicionales
 * que no están incluidos en Material 3 por defecto, pero son necesarios para la UI de AnyMeal.
 */

/**
 * Color de fondo para campos de entrada (inputs, text fields).
 * Cambia automáticamente entre modo claro y oscuro.
 */
val ColorScheme.fieldBg: Color
    get() = if (isLight) FieldBg else FieldBgDark

/**
 * Color para textos secundarios y de menor importancia visual.
 * Cambia automáticamente entre modo claro y oscuro.
 */
val ColorScheme.textSecondary: Color
    get() = if (isLight) TextSecondary else TextSecondaryDark

/**
 * Color de acento para elementos destacados.
 * Cambia automáticamente entre modo claro y oscuro.
 */
val ColorScheme.accent: Color
    get() = if (isLight) Accent else AccentDark

/**
 * Propiedad de utilidad que determina si el tema current es claro u oscuro.
 * Utilizada internamente por otras propiedades de extensión.
 */
val ColorScheme.isLight: Boolean
    get() = background == Background

/**
 * ----------- COMPOSABLE PRINCIPAL DEL TEMA -----------
 */

/**
 * Función composable que aplica el tema de AnyMeal a todo su contenido.
 * Configura el esquema de colores, tipografía y formas utilizadas en toda la aplicación.
 *
 * @param darkTheme Determina si se debe usar el tema oscuro. Por defecto, sigue la configuración del sistema.
 * @param content El contenido composable al que se aplicará el tema.
 */
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
