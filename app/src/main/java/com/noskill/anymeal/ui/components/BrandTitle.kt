/**
 * BrandTitle.kt
 *
 * Este archivo define un componente Composable para mostrar el título de la marca de la aplicación
 * (AnyMeal) con colores personalizables para cada parte del nombre. Permite una presentación
 * visualmente atractiva del logo textual de la aplicación.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

/**
 * Componente que muestra el título de la marca con dos partes diferenciadas por color.
 * Por defecto muestra "AnyMeal" con colores predefinidos, pero todos los aspectos son personalizables.
 *
 * @param first Primera parte del título (por defecto "Any")
 * @param second Segunda parte del título (por defecto "Meal")
 * @param firstColor Color para la primera parte (por defecto gris azulado)
 * @param secondColor Color para la segunda parte (por defecto verde turquesa)
 * @param fontSize Tamaño de la fuente para todo el título
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun BrandTitle(
    first: String = "Any",
    second: String = "Meal",
    firstColor: Color = Color(0xFF90A4AE),
    secondColor: Color = Color(0xFF26A69A),
    fontSize: TextUnit = 32.sp,
    modifier: Modifier = Modifier
) {
    Text(
        // Construcción de un string anotado que permite aplicar estilos diferentes a partes del texto
        buildAnnotatedString {
            // Estilo para la primera parte del título
            withStyle(
                SpanStyle(
                    color = firstColor,
                    fontWeight = FontWeight.Bold
                )
            ) { append(first) }
            // Estilo para la segunda parte del título
            withStyle(
                SpanStyle(
                    color = secondColor,
                    fontWeight = FontWeight.Bold
                )
            ) { append(second) }
        },
        fontSize = fontSize,
        modifier = modifier,
        textAlign = TextAlign.Center,    // Centra el texto horizontalmente
        letterSpacing = 1.5.sp,          // Aumenta el espaciado entre letras para mejor legibilidad
        lineHeight = 38.sp,              // Define la altura de línea para el texto
    )
}
