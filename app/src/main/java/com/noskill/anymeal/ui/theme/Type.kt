/**
 * Type.kt
 *
 * Propósito: Define la escala tipográfica de la aplicación AnyMeal.
 * Establece los estilos de texto (tamaño, peso, espaciado) que se
 * usarán de manera consistente a través de MaterialTheme en toda la aplicación.
 * Esta configuración garantiza una jerarquía visual clara y legibilidad óptima.
 */
package com.noskill.anymeal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Definición de la tipografía para toda la aplicación AnyMeal.
 *
 * Este objeto define los estilos para cada rol de texto semántico siguiendo
 * el sistema de Material Design. Al usar `MaterialTheme.typography.titleLarge`
 * en un Composable, se aplicará el estilo definido aquí, facilitando la
 * consistencia y adaptación a futuras actualizaciones del tema.
 */
val AppTypography = Typography(
    /**
     * displayLarge: Estilo para los títulos más grandes y destacados.
     * Utilizado en pantallas de bienvenida, splash y encabezados principales.
     */
    displayLarge = TextStyle(
        // fontFamily = Poppins, // Ejemplo de uso de fuente personalizada
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        letterSpacing = (-1).sp
    ),

    /**
     * displaySmall: Estilo para títulos secundarios de gran importancia.
     * Utilizado en encabezados de secciones principales y títulos destacados.
     */
    displaySmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
    ),

    /**
     * titleLarge: Estilo para títulos de pantalla estándar.
     * Ejemplos: "Plan Semanal", "Mis Recetas", "Perfil".
     */
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    ),

    /**
     * bodyLarge: Estilo principal para el texto del cuerpo de la aplicación.
     * Utilizado en descripciones, párrafos informativos y contenido general.
     */
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),

    /**
     * labelMedium: Estilo para etiquetas y texto informativo con énfasis.
     * Utilizado en nombres de recetas en tarjetas, etiquetas de categorías,
     * y elementos de interfaz que requieren atención moderada.
     */
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    )
)
