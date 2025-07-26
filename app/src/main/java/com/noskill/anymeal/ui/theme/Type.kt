/* --------------------------------------------------------------------
 * Archivo: Type.kt
 * Descripción: Define la escala tipográfica de la aplicación.
 * Establece los estilos de texto (tamaño, peso, espaciado) que se
 * usarán de manera consistente a través de MaterialTheme.
 * --------------------------------------------------------------------
 */
package com.noskill.anymeal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// El objeto 'AppTypography' define los estilos para cada rol de texto semántico.
// Al usar `MaterialTheme.typography.titleLarge` en un Composable, se aplicará
// el estilo definido aquí, adaptándose a cualquier futura actualización del tema.
val AppTypography = Typography(
    // Usado para los títulos más grandes y destacados (ej. en pantallas de bienvenida).
    displayLarge = TextStyle(
        // fontFamily = Poppins, // Ejemplo de uso de fuente personalizada
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        letterSpacing = (-1).sp
    ),
    // Para títulos secundarios de gran importancia.
    displaySmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
    ),
    // Títulos de pantalla estándar (ej. "Plan Semanal").
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    ),
    // El estilo de texto principal para el cuerpo de la app (descripciones, párrafos).
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    // Para etiquetas o texto informativo que necesita un ligero énfasis (ej. nombres en tarjetas).
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    )
)
