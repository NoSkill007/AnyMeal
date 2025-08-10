/**
 * Color.kt
 * 
 * Propósito: Define la paleta de colores utilizada en toda la aplicación AnyMeal.
 * Contiene definiciones para los colores de los temas claro y oscuro, incluyendo
 * colores primarios, de fondo, superficie, texto y otros elementos de la interfaz.
 * Estos colores son utilizados por el sistema de temas de Jetpack Compose.
 */
package com.noskill.anymeal.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ---------- COLORES DEL TEMA CLARO ----------
 * Definición de colores para el modo de tema claro de la aplicación.
 */

/** Color primario verde - usado para elementos principales de la UI, botones y barras */
val Primary = Color(0xFF43A047)

/** Variante oscura del color primario - usada para estados pressed y destacados */
val PrimaryDark = Color(0xFF00701A)

/** Color de acento amarillo - usado para elementos de atención y destacados */
val Accent = Color(0xFFFFC107)

/** Color de fondo principal - blanco puro para la base de la UI */
val Background = Color(0xFFFFFFFF)

/** Color de superficie - usado en tarjetas y elementos elevados */
val Surface = Color(0xFFF8F9FA)

/** Color de fondo para campos de entrada y áreas interactivas */
val FieldBg = Color(0xFFF3F6FA)

/** Color primario para textos - casi negro para máxima legibilidad */
val Text = Color(0xFF18191A)

/** Color secundario para textos - gris oscuro para información menos importante */
val TextSecondary = Color(0xFF50505A)

/** Color para bordes y contornos en tema claro */
val BorderLight = Color(0xFFE0E0E0)

/** Color rojo para indicar errores y alertas */
val ErrorRed = Color(0xFFDB2C2C)

/**
 * ---------- COLORES DEL TEMA OSCURO ----------
 * Definición de colores para el modo de tema oscuro de la aplicación.
 */

/** Color de fondo principal para tema oscuro - negro con tinte gris */
val BackgroundDark = Color(0xFF111113)

/** Color de superficie para tema oscuro - usado en tarjetas y elementos elevados */
val SurfaceDark = Color(0xFF232429)

/** Color de fondo para campos de entrada en tema oscuro */
val FieldBgDark = Color(0xFF202124)

/** Color primario para textos en tema oscuro - casi blanco para máxima legibilidad */
val TextDark = Color(0xFFF8F8FF)

/** Color secundario para textos en tema oscuro - gris claro para información menos importante */
val TextSecondaryDark = Color(0xFFA8ADB7)

/** Color para bordes y contornos en tema oscuro */
val BorderDark = Color(0xFF393A41)

/** Variante del color de acento adaptada para el tema oscuro - amarillo suave */
val AccentDark = Color(0xFFFFE066)
